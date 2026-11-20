package com.example.distribuidora_de_huevos_jd.data

/**
 * Tipos de huevo que vende la distribuidora (RF-001).
 * El precio es por cubeta de 30 unidades, en pesos colombianos.
 * Los precios son de ejemplo: cámbialos por los precios reales del negocio.
 */
enum class TipoHuevo(val etiqueta: String, val precioCubeta: Int) {
    AA("Huevo AA", 21000),
    A("Huevo A", 19500),
    B("Huevo B", 17500)
}