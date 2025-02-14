package com.amos_tech_code.foodhub.data

import com.amos_tech_code.foodhub.data.model.request.AddToCartRequest
import com.amos_tech_code.foodhub.data.model.request.OauthRequest
import com.amos_tech_code.foodhub.data.model.request.SignInRequest
import com.amos_tech_code.foodhub.data.model.request.SignUpRequest
import com.amos_tech_code.foodhub.data.model.request.UpdateCartItemRequest
import com.amos_tech_code.foodhub.data.model.response.AddToCartResponse
import com.amos_tech_code.foodhub.data.model.response.AddressResponse
import com.amos_tech_code.foodhub.data.model.response.AuthResponse
import com.amos_tech_code.foodhub.data.model.response.CartResponse
import com.amos_tech_code.foodhub.data.model.response.CategoriesResponse
import com.amos_tech_code.foodhub.data.model.response.GenericMsgResponse
import com.amos_tech_code.foodhub.data.model.response.FoodItemResponse
import com.amos_tech_code.foodhub.data.model.response.RestaurantsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {

    @GET("/categories")
    suspend fun getCategories() : Response<CategoriesResponse>

    @GET("/restaurants")
    suspend fun getRestaurants(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ) : Response<RestaurantsResponse>

    @GET("/restaurants/{id}/menu")
    suspend fun getRestaurantFoodItems(@Path("id") id: String) : Response<FoodItemResponse>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest) : Response<AuthResponse>

    @POST("/auth/login")
    suspend fun signIn(@Body request: SignInRequest) : Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun socialSignIn(@Body request: OauthRequest) : Response<AuthResponse>

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCartRequest) : Response<AddToCartResponse>

    @GET("/cart")
    suspend fun getCartItems() : Response<CartResponse>

    @DELETE("/cart/{cartItemId}")
    suspend fun deleteCartItem(@Path("cartItemId") cartItemId: String) : Response<GenericMsgResponse>

    @PATCH("/cart")
    suspend fun updateCart(@Body request: UpdateCartItemRequest) : Response<GenericMsgResponse>

    @GET("/addresses")
    suspend fun getAddress() : Response<AddressResponse>
}