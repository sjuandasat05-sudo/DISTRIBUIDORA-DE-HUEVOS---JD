package com.example.distribuidora_de_huevos_jd.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.distribuidora_de_huevos_jd.data.TipoHuevo
import com.example.distribuidora_de_huevos_jd.viewmodel.PedidoViewModel

/** Pantalla 3: resumen de ventas y avance hacia la meta de la prueba piloto. */
@Composable
fun ResumenVentasScreen(viewModel: PedidoViewModel, modifier: Modifier = Modifier) {
    val resumen by viewModel.resumen.collectAsState()
    val meta = PedidoViewModel.META_CUBETAS
    val progreso = (resumen.totalCubetas.toFloat() / meta).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Resumen de ventas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaDato("Pedidos", "${resumen.totalPedidos}", Modifier.weight(1f))
            TarjetaDato("Pendientes", "${resumen.pendientes}", Modifier.weight(1f))
        }
        TarjetaDato("Cubetas vendidas", "${resumen.totalCubetas}", Modifier.fillMaxWidth())
        TarjetaDato("Total en ventas", formatoPesos(resumen.totalVentas), Modifier.fillMaxWidth())

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Meta de la prueba piloto", style = MaterialTheme.typography.titleMedium)
                Text("${resumen.totalCubetas} de $meta cubetas (${(progreso * 100).toInt()} %)")
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Cubetas por tipo de huevo", style = MaterialTheme.typography.titleMedium)
                TipoHuevo.entries.forEach { tipo ->
                    Row {
                        Text(tipo.etiqueta, modifier = Modifier.weight(1f))
                        Text(
                            text = "${resumen.cubetasPorTipo[tipo] ?: 0}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaDato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}