# Registro de Prompts y Mejoras con IA (CentroTareas)

Este documento registra las interacciones y solicitudes realizadas a la IA durante el desarrollo y optimización de la aplicación **CentroTareas** utilizando Kotlin, Jetpack Compose, Material 3 y Arquitectura MVVM + Repository con Corrutinas.

## Resumen de Mejoras Implementadas

1. **Gestión de hilos y bloqueo de UI (`Thread.sleep`)**:
   - Creación de `tareaLargaMala()` en el ViewModel.
   - Análisis y redacción en el `README.md` sobre por qué la UI no muestra `"Trabajando..."` al bloquear el hilo principal y las consecuencias de un ANR a los 10 segundos.

2. **Migración a Corrutinas y Estado con `StateFlow`**:
   - Reemplazo de bloqueos síncronos por `viewModelScope.launch`, `withContext(Dispatchers.Default)` y `delay(40)`.
   - Implementación de la barra de progreso reactiva y monitoreo de hilos con `Log.d("HILO", ...)`.

3. **Cancelación Estructurada y Concurrencia**:
   - Almacenamiento de referencias a corrutinas mediante `private var trabajo: Job? = null`.
   - Exposición del estado `corriendo` y creación de `cancelar()` sin reiniciar el progreso.

4. **Operaciones CPU-intensivas (Conteo de Primos)**:
   - Implementación del repositorio `TareasRepositoryLocal` (implementando `TareasRepository`).
   - Cálculo eficiente de números primos con `withContext(Dispatchers.Default)`, `ensureActive()` para cancelación cooperativa, y algoritmo optimizado con `while (d.toLong() * d <= n)`.
   - Gestión de estados con la interfaz sellada `PrimosUi`.

5. **Cargas Concurrentes y Manejo de Errores**:
   - Implementación de `cargarUnaPorUna()` (secuencial) y `cargarALaVez()` (concurrente usando `coroutineScope` y `async`/`await`).
   - Simulación de errores con `Switch`, manejo robusto de excepciones relanzando `CancellationException`, y recuadro de error con opción de reintentar.

6. **Documentación y Capturas de Pantalla**:
   - Redacción completa del `README.md` con descripción, árbol de paquetes en subcarpetas (`data`, `viewmodel`, `ui`), diagrama MVVM, respuestas a la sección *"Para pensar"*, e incorporación de capturas de pantalla reales capturadas del dispositivo.
