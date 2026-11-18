package com.galindo.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.centrotareas.data.TareasRepository
import com.galindo.centrotareas.data.TareasRepositoryLocal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface PrimosUi {
    object Inicial : PrimosUi
    object Calculando : PrimosUi
    data class Listo(val cantidad: Int, val milisegundos: Long) : PrimosUi {
        val ms: Long get() = milisegundos
    }
    object Cancelado : PrimosUi
}

sealed interface CargaUi {
    object Inicial : CargaUi
    object Cargando : CargaUi
    data class Exito(val modo: String, val perfil: String, val pedidos: Int, val milisegundos: Long) : CargaUi {
        val ms: Long get() = milisegundos
    }
    data class Error(val mensaje: String) : CargaUi
}

class TareasViewModel(
    private val repository: TareasRepository = TareasRepositoryLocal()
) : ViewModel() {

    // Tarjeta 1: Tarea con progreso
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    private var trabajo: Job? = null

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

    // Tarjeta 2: Contar números primos
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private var primosJob: Job? = null

    fun contar(hasta: Int) {
        primosJob?.cancel()
        _primos.value = PrimosUi.Calculando

        primosJob = viewModelScope.launch {
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repository.contarPrimos(hasta)
                val ms = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, ms)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    _primos.value = PrimosUi.Cancelado
                    throw e
                }
                _primos.value = PrimosUi.Cancelado
            }
        }
    }

    fun cancelarPrimos() {
        primosJob?.cancel()
        _primos.value = PrimosUi.Cancelado
    }

    // Tarjeta 3: Dos cargas
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    private var cargaJob: Job? = null

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cargarUnaPorUna() {
        if (_carga.value is CargaUi.Cargando) return
        cargaJob?.cancel()

        cargaJob = viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val perfil = repository.cargarPerfil()
                val pedidos = repository.cargarPedidos(_simularError.value)
                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("Una por una", perfil, pedidos.size, ms)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }

    fun cargarALaVez() {
        if (_carga.value is CargaUi.Cargando) return
        cargaJob?.cancel()

        cargaJob = viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = coroutineScope {
                    val a = async { repository.cargarPerfil() }
                    val b = async { repository.cargarPedidos(_simularError.value) }
                    Pair(a.await(), b.await())
                }
                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("A la vez", perfil, pedidos.size, ms)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }
}
