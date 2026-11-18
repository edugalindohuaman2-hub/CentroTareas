package com.galindo.centrotareas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface PrimosUi {
    object Inicial : PrimosUi
    object Calculando : PrimosUi
    data class Listo(val cantidad: Int, val ms: Long) : PrimosUi
    object Cancelado : PrimosUi
}

class TareasViewModel : ViewModel() {
    private val repository = TareasRepositoryLocal()

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    private val _primosUi = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primosUi: StateFlow<PrimosUi> = _primosUi.asStateFlow()

    private var trabajo: Job? = null
    private var primosJob: Job? = null

    fun tareaLarga() {
        if (_corriendo.value) return

        trabajo = viewModelScope.launch {
            _corriendo.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")

            try {
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f
                    }
                }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    fun contar(hasta: Int) {
        if (hasta < 2) return
        primosJob?.cancel()

        primosJob = viewModelScope.launch {
            _primosUi.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repository.contarPrimos(hasta)
                val duracion = System.currentTimeMillis() - inicio
                _primosUi.value = PrimosUi.Listo(cantidad, duracion)
            } catch (e: CancellationException) {
                _primosUi.value = PrimosUi.Cancelado
                throw e
            } catch (e: Exception) {
                _primosUi.value = PrimosUi.Cancelado
            }
        }
    }

    fun cancelarPrimos() {
        primosJob?.cancel()
        _primosUi.value = PrimosUi.Cancelado
    }
}
