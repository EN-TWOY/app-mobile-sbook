package com.app.sbook

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.sbook.model.Libro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.app.sbook.adapter.LibroAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var lblLibro: TextView
    private lateinit var lblAutor: TextView
    private lateinit var lblAnio: TextView

    private lateinit var modelList: MutableList<Libro>
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var pd: ProgressDialog
    private lateinit var adapter: LibroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager

        pd = ProgressDialog(this)
        showData()
    }

    private fun showData() {
        if (::modelList.isInitialized) {
            pd.setTitle("Obteniendo datos...")
            pd.show()

            firestore.collection("Libros").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        modelList.clear()
                        pd.dismiss()
                        for (doc in task.result) {
                            val libro = Libro(
                                doc.getString("id") ?: "",
                            doc.getString("nombre") ?: "",
                            doc.getString("autor") ?: "",
                            doc.getLong("anio")?.toInt() ?: 0
                            )
                            modelList.add(libro)
                        }
                        adapter = LibroAdapter(this, modelList)
                        mRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this, "Error al obtener los datos!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    pd.dismiss()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // modelList inicializada
            modelList = ArrayList()
            showData()
        }
    }

    public fun deleteData(index: Int) {
        pd.setTitle("Eliminando libro en Firestore")
        pd.show()

        firestore.collection("Libros")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val documentSnapshot = querySnapshot.documents[index]

                val documentRef = firestore.collection("Libros").document(documentSnapshot.id)
                documentRef.delete()
                    .addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(this, "Libro eliminado exitosamente", Toast.LENGTH_LONG).show()
                        showData()
                    }
                    .addOnFailureListener { exception ->
                        pd.dismiss()
                        Toast.makeText(this, "Error al eliminar el libro: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { exception ->
                pd.dismiss()
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun searchData(s: String) {
        pd.setTitle("Buscando Libro...")
        pd.show()

        firestore.collection("Libros").whereEqualTo("search", s.lowercase())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    modelList.clear()
                    pd.dismiss()
                    if (task.result.isEmpty) {
                        val msjBuscar = "No se encontraron libros con el nombre: \"$s\""
                        Toast.makeText(this, msjBuscar, Toast.LENGTH_LONG).show()
                        showData()
                    } else {
                        for (doc in task.result) {
                            val libro = Libro(
                                doc.getString("id") ?: "",
                                doc.getString("nombre") ?: "",
                                doc.getString("autor") ?: "",
                                doc.getLong("anio")?.toInt() ?: 0
                            )
                            modelList.add(libro)
                        }
                        adapter = LibroAdapter(this, modelList)
                        mRecyclerView.adapter = adapter
                    }
                } else {
                    val exception = task.exception
                    pd.dismiss()
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Show Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val item: MenuItem? = menu?.findItem(R.id.buscar_libro)
        val searchView: SearchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchData(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    // Show MenuItems
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.registrar_libro -> {
                nextActivityAddBook()
            }
            R.id.menu_salir -> {
                showConfirmationLogout()
            }
            R.id.action_settings -> {
                Toast.makeText(this, "En desarrollo...", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmationLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmacion")
        builder.setMessage("¿Estas seguro de que deseas cerrar la sesion?")
        builder.setPositiveButton("Si") { dialog, which ->
            cerrarSesion()
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun cerrarSesion(){
        firebaseAuth.signOut()
        Toast.makeText(baseContext, "Sesion Cerrada Exitosamente", Toast.LENGTH_LONG).show()
        nextActivityLogin()
    }

    private fun nextActivityLogin() {
        val nextActivity = Intent(this, LoginActivity::class.java);
        this.startActivity(nextActivity);
    }

    private fun nextActivityAddBook() {
        val nextActivity = Intent(this, AddBookActivity::class.java);
        this.startActivity(nextActivity);
    }

    override fun onBackPressed() {
        return
    }
}