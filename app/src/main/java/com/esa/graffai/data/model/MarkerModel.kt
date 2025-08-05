package com.esa.graffai.data.model

data class MarkerModel(
    val id : String = "",
    val lat : Double = 0.0,
    val lng : Double = 0.0,
    val label : String = "",
    val confidences : Float = 0f,
    val imageUrl : String = ""
)
