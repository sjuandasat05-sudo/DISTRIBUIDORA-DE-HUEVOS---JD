package com.example.distribuidora_de_huevos_jd.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "pedidos" de la base de datos.
 * Cada fila es un pedido de un habitante de la urbanización Tricentenario.
 */
@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cliente: String,
    val telefono: String,
    val bloque: Int,                 // 1 a 90
    val apartamento: String,
    val tipoHuevo: TipoHuevo,        // Room guarda el enum como texto
    val cantidad: Int,               // número de cubetas de 30
    val total: Int,                  // precio total en pesos
    val esDomicilio: Boolean,        // true = domicilio, false = punto fijo
    val entregado: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)