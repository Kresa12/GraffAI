package com.esa.graffai.data.repository

import com.esa.graffai.data.model.PredictedResponeseModel
import com.esa.graffai.data.remote.api.APIService
import okhttp3.MultipartBody
import javax.inject.Inject

class RoboflowRepository @Inject constructor(
    private val apiService : APIService
) {

    suspend fun classyfyImage(file : MultipartBody.Part): PredictedResponeseModel{
        val modelId = "masterpiece-nograffiti-tags-throwups-9j0tt/1"
        val apiKey = "pTa8NoUR9zdDsE1h4lc1"
        return apiService.classifyImage(modelId = modelId, apiKey = apiKey, file = file)
    }
}