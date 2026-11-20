package com.example.distribuidora_de_huevos_jd.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local de la app (SQLite a través de Room).
 * Se crea una sola vez y se reutiliza en toda la app.
 */
@Database(entities = [Pedido::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtenerBaseDeDatos(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "distribuidora_jd.db"
                ).build().also { INSTANCIA = it }
            }
        }
    }
}