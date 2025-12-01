package com.example.gastroconectaaplicacion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gastroconectaaplicacion.ui.components.RecipeCard
import com.example.gastroconectaaplicacion.ui.navigation.AppScreens
import com.example.gastroconectaaplicacion.ui.viewmodel.AuthViewModel
import com.example.gastroconectaaplicacion.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    navController: NavController,
    userId: Long,
    authViewModel: AuthViewModel,
    recipeViewModel: RecipeViewModel
) {
    // 1. Cargar datos del usuario al entrar
    LaunchedEffect(userId) {
        authViewModel.loadPublicProfile(userId)
    }

    val publicUser by authViewModel.publicProfileUser.collectAsState()
    val allRecipes by recipeViewModel.recipes.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // 2. Filtrar recetas de este usuario
    val userRecipes = remember(allRecipes, userId) {
        allRecipes.filter { it.autorId == userId }
    }

    // Lógica Seguir
    val isFollowing = remember(currentUser, userId) {
        currentUser?.siguiendo?.contains(userId) == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(publicUser?.name ?: "Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (publicUser == null) {
                CircularProgressIndicator()
            } else {
                Text(publicUser!!.name, style = MaterialTheme.typography.headlineMedium)
                Text(publicUser!!.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Seguir (Solo si no soy yo mismo)
                if (currentUser != null && currentUser!!.id != userId) {
                    Button(onClick = { authViewModel.toggleFollow(userId) }) {
                        Text(if (isFollowing) "Dejar de seguir" else "Seguir")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Recetas Publicadas (${userRecipes.size})", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userRecipes) { recipe ->
                        RecipeCard(recipe = recipe, onClick = {
                            navController.navigate(AppScreens.RecipeDetailScreen.createRoute(recipe.id ?: 0L))
                        })
                    }
                }
            }
        }
    }
}