package com.galindo.centrotareas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class TareasRepositoryLocal {
    suspend fun contarPrimos(hasta: Int): Int = withContext(Dispatchers.Default) {
        var contador = 0
        for (n in 2..hasta) {
            ensureActive()
            if (esPrimo(n)) {
                contador++
            }
        }
        contador
    }

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }
}
