package com.example.supfood.data

data class APISearchResponse(
    val count: Int,
    val results: List<Recipe>
)