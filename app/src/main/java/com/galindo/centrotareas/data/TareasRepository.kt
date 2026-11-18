package com.galindo.centrotareas.data

interface TareasRepository {
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
    suspend fun contarPrimos(hasta: Int): Int
}
