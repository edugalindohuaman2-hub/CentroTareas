# CentroTareas

## Observación del Paso 1

### ¿Por qué no se ve "Trabajando..."?
Cuando se ejecuta la función `tareaLargaMala()`, se invoca `Thread.sleep(4000)` directamente en el hilo principal (Main Thread / UI Thread). 
Aunque actualizamos el valor de `_mensaje` a `"Trabajando..."` justo antes de pausar el hilo, el bucle de eventos (`Looper`) del hilo principal queda completamente bloqueado y congelado durante esos 4 segundos. Como resultado, la interfaz de usuario no puede procesar recomposiciones ni redibujar los cambios en pantalla. La UI salta directamente de mostrar `"Listo"` a mostrar `"Terminó"` una vez que el hilo principal se libera de la pausa.

### ¿Qué pasaría si esperara 10 segundos?
Si el hilo principal se bloquea durante 10 segundos mediante operaciones síncronas o de bloqueo pesado (`Thread.sleep(10000)`), el sistema operativo Android detectará que la aplicación no está respondiendo a eventos táctiles ni de dibujo tras aproximadamente 5 segundos. Esto provocará un **ANR (Application Not Responding)**, mostrando un diálogo en pantalla al usuario indicando que "La aplicación no responde" y ofreciendo la opción de cerrarla de forma forzosa.
