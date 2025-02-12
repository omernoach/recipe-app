package com.example.recipe_app.Data.api

import com.example.recipe_app.Data.model.ProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NutritionixService {
    @Headers("X-Api-Key: DKUy3opBB+UijUdXBhe0tw==d50PMkLJQswN4e4y")
    @GET("nutrition")
    fun searchProductByName(
        @Query("query") productName: String
    ): Call<ProductResponse>
}

