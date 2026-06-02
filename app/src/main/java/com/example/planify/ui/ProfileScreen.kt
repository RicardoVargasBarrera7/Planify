package com.example.planify.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.planify.R

/**
 * Pantalla de bienvenida / perfil del usuario.
 * Muestra datos personales y opciones de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToTasks: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Mi Perfil",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector        = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint               = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Encabezado con gradiente ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // ── Foto de perfil ─────────────────────────────────────
                    Surface(
                        shape  = CircleShape,
                        color  = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape),
                        shadowElevation = 8.dp
                    ) {
                        Image(
                            painter            = painterResource(id = R.drawable.profile_photo),
                            contentDescription = "Foto de perfil",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Nombre ─────────────────────────────────────────────
                    Text(
                        text       = "Ricardo García",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ── Mensaje de bienvenida ──────────────────────────────
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text      = "¡Bienvenido de vuelta! 👋",
                            style     = MaterialTheme.typography.bodyMedium,
                            color     = MaterialTheme.colorScheme.onPrimary,
                            modifier  = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tarjeta de datos personales ────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text       = "Información personal",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileItem(
                        icon  = Icons.Default.Person,
                        label = "Nombre completo",
                        value = "Ricardo García"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    ProfileItem(
                        icon  = Icons.Default.Cake,
                        label = "Edad",
                        value = "21 años"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    ProfileItem(
                        icon  = Icons.Default.School,
                        label = "Carrera",
                        value = "Ingeniería en Sistemas Computacionales"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    ProfileItem(
                        icon  = Icons.Default.Email,
                        label = "Correo electrónico",
                        value = "admin@planify.com"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón: Ir a Tareas ─────────────────────────────────────────
            Button(
                onClick  = onNavigateToTasks,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Assignment,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Ver mis tareas",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Botón: Cerrar sesión ───────────────────────────────────────
            OutlinedButton(
                onClick  = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector        = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Cerrar sesión",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Componente reutilizable para mostrar un dato del perfil con ícono.
 */
@Composable
private fun ProfileItem(
    icon  : ImageVector,
    label : String,
    value : String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text  = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
