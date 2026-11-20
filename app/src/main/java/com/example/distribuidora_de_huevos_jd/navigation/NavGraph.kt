package com.example.distribuidora_de_huevos_jd.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.distribuidora_de_huevos_jd.ui.screens.ListaPedidosScreen
import com.example.distribuidora_de_huevos_jd.ui.screens.RegistrarPedidoScreen
import com.example.distribuidora_de_huevos_jd.ui.screens.ResumenVentasScreen
import com.example.distribuidora_de_huevos_jd.viewmodel.PedidoViewModel

/** Las tres pantallas de la app, con su ruta, título e ícono. */
sealed class Pantalla(val ruta: String, val titulo: String, val icono: String) {
    data object Registrar : Pantalla("registrar", "Pedido", "📝")
    data object Lista : Pantalla("lista", "Pedidos", "📋")
    data object Resumen : Pantalla("resumen", "Ventas", "💰")
}

private val pantallas = listOf(Pantalla.Registrar, Pantalla.Lista, Pantalla.Resumen)

/** Barra inferior y navegación entre las pantallas. */
@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    // Un solo ViewModel compartido por las tres pantallas
    val pedidoViewModel: PedidoViewModel = viewModel()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                pantallas.forEach { pantalla ->
                    NavigationBarItem(
                        selected = rutaActual == pantalla.ruta,
                        onClick = {
                            navController.navigate(pantalla.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(pantalla.icono) },
                        label = { Text(pantalla.titulo) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Pantalla.Registrar.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Pantalla.Registrar.ruta) { RegistrarPedidoScreen(pedidoViewModel) }
            composable(Pantalla.Lista.ruta) { ListaPedidosScreen(pedidoViewModel) }
            composable(Pantalla.Resumen.ruta) { ResumenVentasScreen(pedidoViewModel) }
        }
    }
}