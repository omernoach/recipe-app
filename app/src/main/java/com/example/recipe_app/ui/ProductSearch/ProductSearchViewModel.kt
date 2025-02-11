package com.example.recipe_app.ui.ProductSearch

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipe_app.Data.api.RetrofitClient
import com.example.recipe_app.Data.api.NutritionixService
import com.example.recipe_app.Data.model.ProductItem
import com.example.recipe_app.Data.model.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch

class ProductSearchViewModel(application: Application) : AndroidViewModel(application) {

    val searchResults = MutableLiveData<List<ProductItem>>()

    private val nutritionixService = RetrofitClient.instance.create(NutritionixService::class.java)

    // פונקציה לשליחת הבקשה ל-API
    fun searchProduct(query: String) {
        val url = "https://api.calorieninjas.com/v1/nutrition?query=$query"
        Log.d("ProductSearchViewModel", "Request URL: $url")  // לוג של ה-URL שנשלח
        val call = nutritionixService.searchProductByName(query)
        call.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    Log.d("ProductSearchViewModel", "API Success: ${response.body()?.items?.size} items found")
                    searchResults.postValue(response.body()?.items)
                } else {
                    Log.e("ProductSearchViewModel", "API Error: ${response.code()} - ${response.message()}")
                    searchResults.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("ProductSearchViewModel", "API Request failed: ${t.message}", t)
                searchResults.postValue(emptyList())
            }
        })
    }
}
