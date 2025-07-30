package com.esa.graffai.data.repository

import com.esa.graffai.data.model.PredictedResponeseModel
import com.esa.graffai.data.remote.api.APIService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import okhttp3.MultipartBody
import javax.inject.Inject

class RoboflowRepository @Inject constructor(
    private val apiService: APIService,
    private val firestore: FirebaseFirestore
) {

    suspend fun classyfyImage(file: MultipartBody.Part): PredictedResponeseModel {
        val snapshot = firestore.collection("roboflow_config").document("LivzQbViX6Zo8A8qhzbD").get().await()

        val modelId = snapshot.getString("modelId") ?: error("Model ID missing")
        val apiKey = snapshot.getString("apiKey") ?: error("API key missing")

        return apiService.classifyImage(modelId = modelId, apiKey = apiKey, file = file)
    }

}
