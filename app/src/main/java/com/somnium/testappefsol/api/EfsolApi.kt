package com.somnium.testappefsol.api

import com.somnium.testappefsol.models.EnergyModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface EfsolApi {

    @PUT("https://betaapi.nasladdin.club/api/energy/operationStatus/")
    fun sendEnergy(@Body energyModel: EnergyModel): Observable<Response<EnergyModel>>

}