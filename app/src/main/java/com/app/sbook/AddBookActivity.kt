package com.app.sbook

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class AddBookActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var editTextNombre: EditText
    private lateinit var editTextAutor: EditText
    private lateinit var editTextAnio: EditText
    private lateinit var btnGuardarLibro: Button
    private lateinit var btnCancelarRegistro: Button

    private lateinit var pd: ProgressDialog

    private var pId: String? = null
    private var pNombre: String? = null
    private var pAutor: String? = null
    private var pAnio: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        editTextNombre = findViewById(R.id.idNombreLibro)
        editTextAutor = findViewById(R.id.idAutorLibro)
        editTextAnio = findViewById(R.id.idAnioLibro)
        btnGuardarLibro = findViewById(R.id.idGuardarLibro)

        val bundle = intent.extras
        if (bundle != null) {
            // Actualizar Datos
            btnGuardarLibro.setText("Actualizar Libro")
            // Get Data
            pId = bundle.getString("pId")
            pNombre = bundle.getString("pNombre")
            pAutor = bundle.getString("pAutor")
            pAnio = bundle.getInt("pAnio")
            // Set Data
            editTextNombre.setText(pNombre)
            editTextAutor.setText(pAutor)
            editTextAnio.setText(pAnio.toString())
        } else {
            // Agregar Datos
            btnGuardarLibro.setText("Registrar Libro")
        }

        pd = ProgressDialog(this)
        firestore = FirebaseFirestore.getInstance()

        // Cancelar Registro(-> MainActivity)
        val btnCancelarRegistro = findViewById<TextView>(R.id.idCancelarRegistro);
        btnCancelarRegistro.setOnClickListener {
            nextActivityMain()
        }

        // Registrar Libro(-> None)
        btnGuardarLibro.setOnClickListener { view ->
            val nombre = editTextNombre.text.toString()
            val autor = editTextAutor.text.toString()
            val anioText = editTextAnio.text.toString()
            val id = pId.toString()

            if (nombre.isNotEmpty() && autor.isNotEmpty() && anioText.isNotEmpty()) {
                val anio = anioText.toIntOrNull()

                if (anio != null && anioText.length == 4) {
                    val bundle = intent.extras
                    if (bundle != null) {
                        actualizarDatos(id, nombre, autor, anio)
                    } else {
                        // subirDatos(nombre, autor, anio)
                        showConfirmationAddBook(nombre, autor, anio)
                    }
                } else {
                    Toast.makeText(this, "Ingrese un anio valido de 4 digitos", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showConfirmationAddBook(nombre: String, autor: String, anio: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmacion")
        builder.setMessage("¿Deseas registrar el siguiente libro?\n\n" + "Titulo: ${nombre.toUpperCase()}")
        builder.setPositiveButton("Si") { dialog, which ->
            subirDatos(nombre, autor, anio)
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun actualizarDatos(id: String, nombre: String, autor: String, anio: Int) {
        pd.setTitle("Actualizando libro en Firestore")
        pd.show()
        firestore.collection("Libros")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = firestore.batch()

                for (documentSnapshot in querySnapshot.documents) {
                    val documentRef = firestore.collection("Libros").document(documentSnapshot.id)

                    val data = hashMapOf<String, Any>(
                        "nombre" to nombre,
                        "search" to nombre.lowercase(),
                        "autor" to autor,
                        "anio" to anio
                    )
                    batch.update(documentRef, data)
                }
                batch.commit()
                    .addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(this, "¡Libro actualizado exitosamente!", Toast.LENGTH_LONG).show()
                        // Eye!!
                        nextActivityMain()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener { exception ->
                        pd.dismiss()
                        Toast.makeText(this, "Error al actualizar el libro!: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun subirDatos(nombre: String, autor: String, anio: Int) {
        pd.setTitle("Guardando libro en Firestore")
        pd.show()
        val id = UUID.randomUUID().toString()
        val doc: MutableMap<String, Any> = HashMap()
        doc["id"] = id
        doc["nombre"] = nombre
        // For Search
        doc["search"] = nombre.lowercase()
        doc["autor"] = autor
        doc["anio"] = anio

        firestore.collection("Libros").document().set(doc)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pd.dismiss()
                    Toast.makeText(this, "¡Registro exitoso del libro!", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                } else {
                    val exception = task.exception
                    Toast.makeText(baseContext, "Error al cargar los datos. Por favor, intenta nuevamente.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar los datos: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun limpiarCampos() {
        editTextNombre.text.clear()
        editTextAutor.text.clear()
        editTextAnio.text.clear()
        editTextNombre.requestFocus()
    }

    private fun nextActivityMain() {
        val nextActivity = Intent(this, MainActivity::class.java)
        this.startActivity(nextActivity)
    }

    override fun onBackPressed() {
        return
    }
}