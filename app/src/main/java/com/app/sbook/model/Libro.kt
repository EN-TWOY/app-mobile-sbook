package com.app.sbook.model

import java.io.Serializable

class Libro(
    var id: String? = null,
    var nombre: String? = null,
    var autor: String? = null,
    var anio: Int? = null
    // var imageUrl: String? = null
) : Serializable