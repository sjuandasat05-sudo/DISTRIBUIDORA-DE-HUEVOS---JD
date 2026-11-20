package com.example.distribuidora_de_huevos_jd.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.distribuidora_de_huevos_jd.data.Pedido
import com.example.distribuidora_de_huevos_jd.viewmodel.PedidoViewModel

/** Pantalla 2: lista de pedidos con filtro por bloque (ruta de domicilios). */
@Composable
fun ListaPedidosScreen(viewModel: PedidoViewModel, modifier: Modifier = Modifier) {
    val pedidos by viewModel.pedidosFiltrados.collectAsState()
    val bloques by viewModel.bloquesConPedidos.collectAsState()
    val filtro by viewModel.filtroBloque.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Pedidos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Filtra por bloque para armar la ruta de domicilios",
            style = MaterialTheme.typography.bodyMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = filtro == null,
                    onClick = { viewModel.cambiarFiltro(null) },
                    label = { Text("Todos") }
                )
            }
            items(bloques) { bloque ->
                FilterChip(
                    selected = filtro == bloque,
                    onClick = { viewModel.cambiarFiltro(bloque) },
                    label = { Text("Bloque $bloque") }
                )
            }
        }

        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Todavía no hay pedidos registrados.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(pedidos, key = { it.id }) { pedido ->
                    TarjetaPedido(
                        pedido = pedido,
                        onCambiarEstado = { viewModel.cambiarEstado(pedido) },
                        onEliminar = { viewModel.eliminarPedido(pedido) },
                        onWhatsApp = { abrirWhatsApp(context, pedido) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaPedido(
    pedido: Pedido,
    onCambiarEstado: () -> Unit,
    onEliminar: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pedido.cliente,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (pedido.entregado) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = if (pedido.entregado) "Entregado" else "Pendiente",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (pedido.entregado) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Text("Bloque ${pedido.bloque} · Apto ${pedido.apartamento}")
            Text("${pedido.cantidad} cubeta(s) de ${pedido.tipoHuevo.etiqueta}")
            Text(if (pedido.esDomicilio) "Domicilio" else "Punto fijo")
            Text(
                text = formatoFecha(pedido.fecha),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Total: ${formatoPesos(pedido.total.toLong())}",
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Button(onClick = onCambiarEstado, modifier = Modifier.weight(1f)) {
                    Text(if (pedido.entregado) "Deshacer" else "Entregado")
                }
                if (pedido.telefono.isNotBlank()) {
                    OutlinedButton(onClick = onWhatsApp, modifier = Modifier.weight(1f)) {
                        Text("WhatsApp")
                    }
                }
            }
            TextButton(onClick = onEliminar) {
                Text("Eliminar pedido", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/** Abre WhatsApp con un mensaje listo para el cliente. */
private fun abrirWhatsApp(context: Context, pedido: Pedido) {
    val numero = pedido.telefono.filter { it.isDigit() }
    val numeroConIndicativo = if (numero.length == 10) "57$numero" else numero
    val estado = if (pedido.entregado) {
        "fue entregado. ¡Gracias por tu compra!"
    } else {
        "va en camino al bloque ${pedido.bloque}, apto ${pedido.apartamento}."
    }
    val mensaje = "Hola ${pedido.cliente}, tu pedido de ${pedido.cantidad} cubeta(s) de " +
            "${pedido.tipoHuevo.etiqueta} por ${formatoPesos(pedido.total.toLong())} $estado " +
            "- Distribuidora de Huevos JD"
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://wa.me/$numeroConIndicativo?text=${Uri.encode(mensaje)}")
    )
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
    }
}