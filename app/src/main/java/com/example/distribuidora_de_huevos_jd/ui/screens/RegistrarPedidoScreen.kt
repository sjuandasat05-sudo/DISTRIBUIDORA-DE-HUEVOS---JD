package com.example.distribuidora_de_huevos_jd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.distribuidora_de_huevos_jd.data.TipoHuevo
import com.example.distribuidora_de_huevos_jd.viewmodel.PedidoViewModel

/** Pantalla 1: formulario para registrar un pedido nuevo. */
@Composable
fun RegistrarPedidoScreen(viewModel: PedidoViewModel, modifier: Modifier = Modifier) {
    var cliente by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var bloque by rememberSaveable { mutableStateOf("") }
    var apartamento by rememberSaveable { mutableStateOf("") }
    var tipoHuevo by rememberSaveable { mutableStateOf(TipoHuevo.AA) }
    var cantidad by rememberSaveable { mutableStateOf(1) }
    var esDomicilio by rememberSaveable { mutableStateOf(true) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val total = tipoHuevo.precioCubeta.toLong() * cantidad

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registrar pedido",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = cliente,
            onValueChange = { cliente = it },
            label = { Text("Nombre del cliente") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { nuevo -> telefono = nuevo.filter { it.isDigit() }.take(10) },
            label = { Text("Celular (WhatsApp)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = bloque,
                onValueChange = { nuevo -> bloque = nuevo.filter { it.isDigit() }.take(2) },
                label = { Text("Bloque (1-90)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = apartamento,
                onValueChange = { apartamento = it.take(6) },
                label = { Text("Apartamento") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Text("Tipo de huevo", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TipoHuevo.entries.forEach { tipo ->
                FilterChip(
                    selected = tipoHuevo == tipo,
                    onClick = { tipoHuevo = tipo },
                    label = { Text(tipo.etiqueta) }
                )
            }
        }
        Text(
            text = "Precio por cubeta de 30: ${formatoPesos(tipoHuevo.precioCubeta.toLong())}",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Cubetas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(onClick = { if (cantidad > 1) cantidad-- }) { Text("−") }
            Text(
                text = "$cantidad",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OutlinedButton(onClick = { if (cantidad < 99) cantidad++ }) { Text("+") }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (esDomicilio) "Entrega a domicilio" else "Recoge en punto fijo",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = esDomicilio, onCheckedChange = { esDomicilio = it })
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a pagar", modifier = Modifier.weight(1f))
                Text(
                    text = formatoPesos(total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val numeroBloque = bloque.toIntOrNull()
                error = when {
                    cliente.isBlank() -> "Escribe el nombre del cliente."
                    numeroBloque == null || numeroBloque !in 1..PedidoViewModel.TOTAL_BLOQUES ->
                        "El bloque debe estar entre 1 y ${PedidoViewModel.TOTAL_BLOQUES}."
                    apartamento.isBlank() -> "Escribe el número de apartamento."
                    telefono.isNotEmpty() && telefono.length != 10 ->
                        "El celular debe tener 10 dígitos (o déjalo vacío)."
                    else -> null
                }
                if (error == null && numeroBloque != null) {
                    viewModel.registrarPedido(
                        cliente = cliente.trim(),
                        telefono = telefono,
                        bloque = numeroBloque,
                        apartamento = apartamento.trim(),
                        tipoHuevo = tipoHuevo,
                        cantidad = cantidad,
                        esDomicilio = esDomicilio
                    )
                    Toast.makeText(context, "Pedido registrado", Toast.LENGTH_SHORT).show()
                    cliente = ""
                    telefono = ""
                    bloque = ""
                    apartamento = ""
                    cantidad = 1
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar pedido")
        }
    }
}