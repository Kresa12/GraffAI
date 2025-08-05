package com.esa.graffai.data.repository

import com.esa.graffai.data.model.MarkerModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MarkerRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val markersRef = firestore.collection("markers")

    suspend fun addMarker(marker : MarkerModel) {
        val docRef = markersRef.document()
        val markerWithId = marker.copy(id = docRef.id)
        docRef.set(markerWithId).await()
    }

    suspend fun getAllMarkers() : List<MarkerModel> {
        val snapshot = markersRef.get().await()
        return snapshot.documents.mapNotNull { it.toObject(MarkerModel::class.java) }
    }
}