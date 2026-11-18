package com.galindo.centrotareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

@Composable
fun PantallaTareas(
    modifier: Modifier = Modifier,
    viewModel: TareasViewModel = viewModel()
) {
    var contador by remember { mutableIntStateOf(0) }
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()

    var textoHasta by remember { mutableStateOf("100000") }
    val primosUi by viewModel.primosUi.collectAsState()
    val hastaInt = textoHasta.toIntOrNull()
    val hastaValido = hastaInt != null && hastaInt >= 2
    val estaCalculando = primosUi is PrimosUi.Calculando

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { contador++ }) {
            Text(text = "Tócame: $contador", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.tareaLarga() },
                enabled = !corriendo
            ) {
                Text(text = "Iniciar", fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = { viewModel.cancelar() },
                enabled = corriendo
            ) {
                Text(text = "Cancelar", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "${(progreso * 100).toInt()}%", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = mensaje, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de cálculo de primos
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = textoHasta,
                    onValueChange = { textoHasta = it },
                    label = { Text("Número hasta") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            hastaInt?.let { viewModel.contar(it) }
                        },
                        enabled = hastaValido && !estaCalculando
                    ) {
                        Text("Contar")
                    }

                    OutlinedButton(
                        onClick = { viewModel.cancelarPrimos() },
                        enabled = estaCalculando
                    ) {
                        Text("Cancelar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val resultadoTexto = when (val ui = primosUi) {
                    is PrimosUi.Inicial -> "Listo para calcular primos"
                    is PrimosUi.Calculando -> "Calculando primos..."
                    is PrimosUi.Listo -> "Primos encontrados: ${ui.cantidad} (${ui.ms} ms)"
                    is PrimosUi.Cancelado -> "Cálculo cancelado"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                ) {
                    Text(
                        text = resultadoTexto,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF1B5E20),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
