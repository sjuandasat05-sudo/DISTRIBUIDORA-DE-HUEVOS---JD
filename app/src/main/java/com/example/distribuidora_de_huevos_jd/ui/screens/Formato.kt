package com.example.distribuidora_de_huevos_jd.ui.screens

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeColombia: Locale = Locale.forLanguageTag("es-CO")

/** Convierte un valor en pesos a texto, por ejemplo 19500 -> "$ 19.500". */
fun formatoPesos(valor: Long): String {
    val formato = NumberFormat.getCurrencyInstance(localeColombia)
    formato.maximumFractionDigits = 0
    return formato.format(valor)
}

/** Convierte una fecha guardada en milisegundos a texto, por ejemplo "19/11/2026 18:30". */
fun formatoFecha(milisegundos: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", localeColombia).format(Date(milisegundos))
}