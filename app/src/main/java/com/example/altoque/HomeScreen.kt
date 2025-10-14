package com.example.altoque

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() },
        containerColor = Color(0xFF2E2E2E)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ExpandingBubbleMenu(
                isExpanded = isExpanded,
                onToggle = { isExpanded = !isExpanded }
            )
        }
    }
}

@Composable
fun ExpandingBubbleMenu(isExpanded: Boolean, onToggle: () -> Unit) {
    val animationProgress by animateFloatAsState(targetValue = if (isExpanded) 1f else 0f)

    val distance = 120.dp
    val bubbleCount = 4


    Box(contentAlignment = Alignment.Center) {
        for (i in 0 until bubbleCount) {
            val angleDegrees = i * 90.0
            val angleRadians = Math.toRadians(angleDegrees)

            val offsetX = (distance.value * animationProgress * cos(angleRadians)).dp
            val offsetY = (distance.value * animationProgress * sin(angleRadians)).dp

            ServiceBubble(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .alpha(animationProgress)
            )
        }

        CentralBubble(onClick = onToggle)
    }
}

// Hacemos la burbuja central clicable
@Composable
fun CentralBubble(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color(0xFF00B2FF))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¿Qué servicio necesitas?",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun ServiceBubble(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(Color(0xFF00B2FF))
    ) {
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* TODO: Acción */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chamba),
                    contentDescription = "Perfil",
                    tint = Color.Unspecified
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Acción */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.robot_1),
                    contentDescription = "ia",
                    tint = Color.Unspecified
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF1F1F1F)
        )
    )
}

@Composable
fun BottomBar() {
    BottomAppBar(
        containerColor = Color(0xFFD9D9D9)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Navegar a Chat */ }) {
                Icon(Icons.Default.Email, contentDescription = "Chat")
            }
            IconButton(onClick = { /* TODO: Navegar a Perfil */ }) {
                Icon(Icons.Default.Person, contentDescription = "Perfil")
            }
            IconButton(onClick = { /* TODO: Navegar a Notificaciones */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
            }
        }
    }
}