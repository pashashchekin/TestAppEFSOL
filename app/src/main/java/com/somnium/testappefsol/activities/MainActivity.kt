package com.somnium.testappefsol.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.somnium.testappefsol.App
import com.somnium.testappefsol.utils.Shake
import com.somnium.testappefsol.models.EnergyModel
import com.somnium.testappefsol.api.ObserveOnMainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import retrofit2.Response
import android.app.NotificationManager
import android.support.v4.app.NotificationCompat
import android.app.PendingIntent
import android.content.Intent
import com.somnium.testappefsol.R

const val PREFS_FIRST_SEND_ENERGY = "FirstSendEnergy"
const val KEY_FIRST_SEND_ENERGY_VIEWED = "send_energy_viewed"

class MainActivity : AppCompatActivity(), Shake.OnShakeListener{

    private var disposables = CompositeDisposable()

    private lateinit var text : TextView
    private lateinit var  sensorManager: SensorManager
    private lateinit var mSensorListener: Shake

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkFirstSend()
        text = findViewById(R.id.textView)
        text.text = "Пожалуйста потратьте энергию"
        mSensorListener = Shake()
        mSensorListener.setOnShakeListener(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(mSensorListener)
        super.onPause()
    }

    override fun onShakeDetected() {
        text.text = "Вы тратите энергию"
    }

    override fun onShakeStopped() {
        text.text = "Пожалуйста потратьте энергию"
    }


    private fun sendEnergy() {
        disposables.add(App.efsolApi!!.sendEnergy(EnergyModel(1))
                .compose(ObserveOnMainThread())
                .subscribeBy(
                        onNext = ::onSendEnergySuccess,
                        onError = ::onSendEnergyError
                )
        )
    }

    private fun onSendEnergySuccess(response: Response<EnergyModel>) {
        if (response.isSuccessful){
            Toast.makeText(this, "Показания успешно отправлены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSendEnergyError(throwable: Throwable) {
        Toast.makeText(this, "Показания не отправлены", Toast.LENGTH_SHORT).show()
    }

    private fun checkFirstSend() {
        if (isViewed(this)) {
            setViewed(this)
        } else {
            sendNotification()
        }
    }

    private fun sendNotification(){
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Уведомление")
                .setContentText("Пожалуйста потратьте энергию")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)

        val notification = builder.build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
        setViewed(this)
    }

    companion object Main{
        fun isViewed(context: Context): Boolean{
            val prefs = context.getSharedPreferences(PREFS_FIRST_SEND_ENERGY, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_FIRST_SEND_ENERGY_VIEWED, false)
        }

        fun setViewed(context: Context){
            val prefs = context.getSharedPreferences(PREFS_FIRST_SEND_ENERGY, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_FIRST_SEND_ENERGY_VIEWED, true).apply()
        }
    }
}
