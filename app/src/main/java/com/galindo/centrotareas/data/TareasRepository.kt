package com.galindo.centrotareas.data

import kotlinx.coroutines.flow.Flow

interface TareasRepository {
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
    suspend fun contarPrimos(hasta: Int, onProgress: (Float) -> Unit = {}): Int
    fun ejecutarTareaConProgreso(): Flow<Float>
}
