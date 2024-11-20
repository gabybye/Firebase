package com.example.firebasegabi

import android.R
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasegabi.ui.theme.FirebaseGabiTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.MaterialTheme.colorScheme as colorScheme1

class MainActivity : ComponentActivity() {

    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseGabiTheme {
                Surface(
                    Modifier.fillMaxSize(),
                    color = colorScheme1.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var clientes by remember { mutableStateOf(listOf<Map<String, String>>()) }

    LaunchedEffect(Unit) {
        db.collection("Clientes").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Erro ao carregar documentos", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                updateClientes(snapshot) { listaAtualizada ->
                    clientes = listaAtualizada
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Gabriela Correia - 3 DS MTEC",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_menu_gallery), // Substitua pelo ID correto
            contentDescription = "Imagem Personalizada",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "App Firebase Firestore", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Nome:")
            }
            Column {
                TextField(value = nome, onValueChange = { nome = it })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(value = telefone, onValueChange = { telefone = it })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val cliente = hashMapOf("nome" to nome, "telefone" to telefone)
            db.collection("Clientes")
                .add(cliente)
                .addOnSuccessListener { Log.d("Firestore", "Documento salvo!") }
                .addOnFailureListener { e -> Log.w("Firestore", "Erro ao salvar documento", e) }
        }) {
            Text(text = "Cadastrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(clientes) { cliente ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = cliente["nome"] ?: "", modifier = Modifier.weight(1f))
                    Text(text = cliente["telefone"] ?: "", modifier = Modifier.weight(1f))
                }
                Divider()
            }
        }
    }
}

private fun updateClientes(snapshot: QuerySnapshot, onClientesUpdated: (List<Map<String, String>>) -> Unit) {
    val listaClientes = snapshot.documents.map { doc ->
        mapOf(
            "nome" to (doc.getString("nome") ?: ""),
            "telefone" to (doc.getString("telefone") ?: "")
        )
    }
    onClientesUpdated(listaClientes)
}
