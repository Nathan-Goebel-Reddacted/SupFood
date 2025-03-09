package com.example.supfood.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoadScreen(navController: NavController) {
    val primaryColor = Color(0xFFFFD8A8)
    val secondaryColor = Color(0xFF1971C2)
    LaunchedEffect(Unit) {
        navController.navigate("home") {
            popUpTo("loader") { inclusive = true }
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = com.example.supfood.R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 20.dp)
            )
            CircularProgressIndicator(
                color = secondaryColor,
                strokeWidth = 4.dp
            )
        }
    }
}