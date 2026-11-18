# CentroTareas

## Descripción de la aplicación
**CentroTareas** es una aplicación de Android desarrollada con **Jetpack Compose** y arquitectura **MVVM (Model-View-ViewModel) + Repository**, diseñada para demostrar el uso avanzado de Corrutinas de Kotlin, gestión de hilos, manejo de estados con `StateFlow`, cancelación cooperativa, operaciones CPU-intensivas y manejo concurrente y síncrono de operaciones asíncronas.

---

## Árbol de paquetes
```text
com.galindo.centrotareas/
├── MainActivity.kt
├── data/
│   ├── TareasRepository.kt
│   └── TareasRepositoryLocal.kt
├── viewmodel/
│   └── TareasViewModel.kt
└── ui/
    ├── PantallaTareas.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## Arquitectura MVVM + Repository
```text
┌──────────────┐         ViewModel          ┌──────────────────────────┐
│              │ ◄───────────────────────── │                          │
│ Pantalla     │          StateFlow         │      TareasViewModel     │
│ Tareas (UI)  │                            │                          │
│              │ ───────────────►           │                          │
└──────────────┘      User Actions          └────────────┬─────────────┘
                                                         │
                                               suspend functions / jobs
                                                         │
                                                         ▼
                                            ┌──────────────────────────┐
                                            │                          │
                                            │ TareasRepositoryLocal    │
                                            │ (CPU / Network tasks)    │
                                            │                          │
                                            └──────────────────────────┘
```

---

## Observación del Paso 1

### ¿Por qué no se ve "Trabajando..."?
Cuando se ejecuta la función `tareaLargaMala()`, se invoca `Thread.sleep(4000)` directamente en el hilo principal (Main Thread / UI Thread). 
Aunque actualizamos el valor de `_mensaje` a `"Trabajando..."` justo antes de pausar el hilo, el bucle de eventos (`Looper`) del hilo principal queda completamente bloqueado y congelado durante esos 4 segundos. Como resultado, la interfaz de usuario no puede procesar recomposiciones ni redibujar los cambios en pantalla. La UI salta directamente de mostrar `"Listo"` a mostrar `"Terminó"` una vez que el hilo principal se libera de la pausa.

### ¿Qué pasaría si esperara 10 segundos?
Si el hilo principal se bloquea durante 10 segundos mediante operaciones síncronas o de bloqueo pesado (`Thread.sleep(10000)`), el sistema operativo Android detectará que la aplicación no está respondiendo a eventos táctiles ni de dibujo tras aproximadamente 5 segundos. Esto provocará un **ANR (Application Not Responding)**, mostrando un diálogo en pantalla al usuario indicando que "La aplicación no responde" y ofreciendo la opción de cerrarla de forma forzosa.

---

## Capturas de pantalla
1. **Paso 1 - Bloqueo de UI (`Thread.sleep`):**
   ![Captura 1](file:///C:/Users/User/CentroTareas/screenshots/paso1_bloqueo.png)

2. **Paso 2 - Barra de Progreso y Corrutinas (`delay`):**
   ![Captura 2](file:///C:/Users/User/CentroTareas/screenshots/paso2_progreso.png)

3. **Paso 3 - Concurrencia y Cancelación (`Job` y `cancelar()`):**
   ![Captura 3](file:///C:/Users/User/CentroTareas/screenshots/paso3_cancelacion.png)

4. **Paso 4 - Conteo de Primos con `Dispatchers.Default` y `ensureActive()`:**
   ![Captura 4](file:///C:/Users/User/CentroTareas/screenshots/paso4_primos.png)

5. **Paso 5 - Carga Concurrente (`async`/`await`) y Manejo de Errores con Switch:**
   ![Captura 5](file:///C:/Users/User/CentroTareas/screenshots/paso5_cargas_errores.png)

---

## Sección: Para pensar

### 1. ¿Por qué bloquear el hilo principal es peligroso en Android?
El hilo principal (UI Thread) es el único responsable de medir, diseñar y dibujar las vistas en pantalla, así como de despachar los eventos táctiles y de entrada del usuario. Si se bloquea con operaciones pesadas o esperas síncronas, la app se congela visualmente (pérdida de fluidez / jank) y si supera los ~5 segundos, el sistema emite un diálogo de ANR (*Application Not Responding*), cerrando la aplicación por mala experiencia de usuario.

### 2. ¿Cuál es la diferencia fundamental entre `Thread.sleep()` y `delay()`?
- `Thread.sleep()` **bloquea físicamente** el hilo en el que se ejecuta (incluso si es el hilo principal), paralizando su `Looper` y congelando la UI o impidiendo que otras tareas se ejecuten en ese hilo.
- `delay()` es una función suspendida (`suspend`) que **suspende** la corrutina sin bloquear el hilo subyacente. Libera el hilo para que procese otras tareas u otras corrutinas y reanuda la ejecución transcurrido el tiempo especificado.

### 3. ¿Cuándo debemos usar `Dispatchers.Default` frente a `Dispatchers.IO`?
- **`Dispatchers.Default`**: Optimizado para tareas **intensivas de CPU** (cálculos matemáticos, procesamiento de imágenes, filtrado de grandes colecciones, conteo de números primos). Utiliza un pool de hilos basado en el número de núcleos de CPU disponibles.
- **`Dispatchers.IO`**: Optimizado para operaciones de **entrada/salida (I/O)** (lectura/escritura de bases de datos Room, llamadas a APIs de red, lectura/escritura de archivos en disco). Utiliza un pool compartido de hilos amplio diseñado para bloquearse esperando respuestas de red o disco sin saturar la CPU.

### 4. ¿Qué ocurre con las corrutinas lanzadas en `viewModelScope` cuando se cierra la pantalla (ViewModel destruido)?
`viewModelScope` está atado al ciclo de vida del `ViewModel`. Cuando el usuario sale de la pantalla y el `ViewModel` es destruido (por ejemplo, al finalizar la actividad o fragmento asociado), `viewModelScope` cancela automáticamente todas las corrutinas que se lanzaron dentro de él. Esto previene fugas de memoria (*memory leaks*) y operaciones innecesarias en segundo plano.

### 5. ¿Cómo funciona `async` / `await` para tareas concurrentes y cómo se propagan o manejan los fallos?
- `async` lanza una corrutina concurrente de tipo `Deferred` que representa un cálculo futuro con un resultado. Permite iniciar múltiples tareas en paralelo y esperar sus resultados invocando `await()`.
- **Manejo de fallos**: Si una corrutina lanzada con `async` lanza una excepción no atrapada, esa excepción se encapsula en el `Deferred` y se vuelve a lanzar en el momento en que se invoca `await()`. Si no se usa `coroutineScope` o un bloque de manejo de excepciones (`try/catch`), la excepción puede propagarse hacia arriba en la jerarquía de corrutinas y cancelar el ámbito padre. Por ello, se recomienda envolver bloques `async/await` en `coroutineScope` con `try/catch` para capturar y manejar errores de manera controlada.
