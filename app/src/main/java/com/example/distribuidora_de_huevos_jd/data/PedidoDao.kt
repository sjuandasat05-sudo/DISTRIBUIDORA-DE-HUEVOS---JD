package com.example.distribuidora_de_huevos_jd.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Consultas a la tabla "pedidos".
 * Room genera automáticamente el código que ejecuta cada consulta.
 */
@Dao
interface PedidoDao {

    @Insert
    suspend fun insertar(pedido: Pedido)

    @Update
    suspend fun actualizar(pedido: Pedido)

    @Delete
    suspend fun eliminar(pedido: Pedido)

    /** Todos los pedidos, del más reciente al más antiguo. Se actualiza solo (Flow). */
    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<Pedido>>

    /** Total de cubetas vendidas. */
    @Query("SELECT IFNULL(SUM(cantidad), 0) FROM pedidos")
    fun totalCubetas(): Flow<Int>

    /** Total de dinero vendido, en pesos. */
    @Query("SELECT IFNULL(SUM(total), 0) FROM pedidos")
    fun totalVentas(): Flow<Long>
}