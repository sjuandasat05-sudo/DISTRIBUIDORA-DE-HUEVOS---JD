package com.example.distribuidora_de_huevos_jd.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.distribuidora_de_huevos_jd.data.AppDatabase
import com.example.distribuidora_de_huevos_jd.data.Pedido
import com.example.distribuidora_de_huevos_jd.data.TipoHuevo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Datos que muestra la pantalla de Resumen de ventas. */
data class ResumenVentas(
    val totalPedidos: Int = 0,
    val totalCubetas: Int = 0,
    val totalVentas: Long = 0,
    val pendientes: Int = 0,
    val cubetasPorTipo: Map<TipoHuevo, Int> = emptyMap()
)

/**
 * Lógica de la app. Lo usan las tres pantallas:
 * Registrar pedido, Lista de pedidos y Resumen de ventas.
 */
class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.obtenerBaseDeDatos(application).pedidoDao()

    /** Todos los pedidos guardados en la base de datos. */
    val pedidos: StateFlow<List<Pedido>> = dao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Bloque seleccionado en el filtro. null = todos los bloques. */
    private val _filtroBloque = MutableStateFlow<Int?>(null)
    val filtroBloque: StateFlow<Int?> = _filtroBloque.asStateFlow()

    /** Pedidos que se muestran según el filtro por bloque (ruta de domicilios). */
    val pedidosFiltrados: StateFlow<List<Pedido>> =
        combine(pedidos, _filtroBloque) { lista, bloque ->
            if (bloque == null) lista else lista.filter { it.bloque == bloque }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Bloques que tienen al menos un pedido, ordenados. */
    val bloquesConPedidos: StateFlow<List<Int>> = pedidos
        .map { lista -> lista.map { it.bloque }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Resumen de ventas: usa consultas SUM de la base de datos y la lista de pedidos. */
    val resumen: StateFlow<ResumenVentas> =
        combine(dao.totalCubetas(), dao.totalVentas(), pedidos) { cubetas, ventas, lista ->
            ResumenVentas(
                totalPedidos = lista.size,
                totalCubetas = cubetas,
                totalVentas = ventas,
                pendientes = lista.count { !it.entregado },
                cubetasPorTipo = TipoHuevo.entries.associateWith { tipo ->
                    lista.filter { it.tipoHuevo == tipo }.sumOf { it.cantidad }
                }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResumenVentas())

    fun cambiarFiltro(bloque: Int?) {
        _filtroBloque.value = bloque
    }

    fun registrarPedido(
        cliente: String,
        telefono: String,
        bloque: Int,
        apartamento: String,
        tipoHuevo: TipoHuevo,
        cantidad: Int,
        esDomicilio: Boolean
    ) {
        val pedido = Pedido(
            cliente = cliente,
            telefono = telefono,
            bloque = bloque,
            apartamento = apartamento,
            tipoHuevo = tipoHuevo,
            cantidad = cantidad,
            total = tipoHuevo.precioCubeta * cantidad,
            esDomicilio = esDomicilio
        )
        viewModelScope.launch { dao.insertar(pedido) }
    }

    /** Cambia el pedido entre "Pendiente" y "Entregado". */
    fun cambiarEstado(pedido: Pedido) {
        viewModelScope.launch { dao.actualizar(pedido.copy(entregado = !pedido.entregado)) }
    }

    fun eliminarPedido(pedido: Pedido) {
        viewModelScope.launch { dao.eliminar(pedido) }
    }

    companion object {
        /** Bloques de la urbanización Tricentenario. */
        const val TOTAL_BLOQUES = 90

        /** Meta de la prueba piloto (MVP): más de 500 cubetas en 4 semanas. */
        const val META_CUBETAS = 500
    }
}