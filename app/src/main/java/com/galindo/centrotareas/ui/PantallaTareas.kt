package com.galindo.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galindo.centrotareas.viewmodel.CargaUi
import com.galindo.centrotareas.viewmodel.PrimosUi
import com.galindo.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    modifier: Modifier = Modifier,
    viewModel: TareasViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    // Estados ViewModel Card 1
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    var toques by remember { mutableIntStateOf(0) }

    // Estados ViewModel Card 2
    var textoHasta by remember { mutableStateOf("500000") }
    val primos by viewModel.primos.collectAsState()
    val hastaInt = textoHasta.toIntOrNull()
    val hastaValido = hastaInt != null && hastaInt >= 2
    val estaCalculandoPrimos = primos is PrimosUi.Calculando

    // Estados ViewModel Card 3
    val carga by viewModel.carga.collectAsState()
    val simularError by viewModel.simularError.collectAsState()
    val estaCargando = carga is CargaUi.Cargando

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ================= CARD 1: Tarea con progreso =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tarea con progreso",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = { toques++ }) {
                        Text(text = "Tócame: $toques", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.tareaLarga() },
                            enabled = !corriendo
                        ) {
                            Text(text = "Iniciar", fontSize = 16.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.cancelar() },
                            enabled = corriendo
                        ) {
                            Text(text = "Cancelar", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "${(progreso * 100).toInt()} %", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = mensaje, fontSize = 16.sp)
                }
            }

            // ================= CARD 2: Contar números primos =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Contar números primos",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = textoHasta,
                        onValueChange = { textoHasta = it },
                        label = { Text("Hasta el número") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { hastaInt?.let { viewModel.contar(it) } },
                            enabled = hastaValido && !estaCalculandoPrimos
                        ) {
                            Text("Contar")
                        }

                        OutlinedButton(
                            onClick = { viewModel.cancelarPrimos() },
                            enabled = estaCalculandoPrimos
                        ) {
                            Text("Cancelar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val estado = primos) {
                        is PrimosUi.Inicial -> {
                            Text("Ingrese un número y presione Contar", fontSize = 14.sp)
                        }
                        is PrimosUi.Calculando -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Calculando primos: ${(estado.progreso * 100).toInt()}%", fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { estado.progreso },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        is PrimosUi.Listo -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Primos hasta $hastaInt: ${estado.cantidad}",
                                        color = Color(0xFF1B5E20),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tiempo: ${estado.milisegundos} ms",
                                        color = Color(0xFF1B5E20),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                        is PrimosUi.Cancelado -> {
                            Text("Cancelado", color = Color.Red, fontSize = 16.sp)
                        }
                    }
                }
            }

            // ================= CARD 3: Dos cargas =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Dos cargas",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simular error", fontSize = 16.sp)
                        Switch(
                            checked = simularError,
                            onCheckedChange = { viewModel.cambiarSimularError(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.cargarUnaPorUna() },
                            enabled = !estaCargando
                        ) {
                            Text("Una por una")
                        }

                        Button(
                            onClick = { viewModel.cargarALaVez() },
                            enabled = !estaCargando
                        ) {
                            Text("A la vez")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (estaCargando) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    when (val estado = carga) {
                        is CargaUi.Inicial -> {
                            Text("Listo para cargar", fontSize = 14.sp)
                        }
                        is CargaUi.Cargando -> {
                            Text("Cargando datos...", fontSize = 14.sp)
                        }
                        is CargaUi.Exito -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Modo: ${estado.modo}", color = Color(0xFF1B5E20), fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Perfil: ${estado.perfil}", color = Color(0xFF1B5E20), fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Pedidos: ${estado.pedidos}", color = Color(0xFF1B5E20), fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Tiempo: ${estado.milisegundos} ms", color = Color(0xFF1B5E20), fontSize = 16.sp)
                                }
                            }
                        }
                        is CargaUi.Error -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = estado.mensaje,
                                        color = Color(0xFFB71C1C),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { viewModel.cargarALaVez() }) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
