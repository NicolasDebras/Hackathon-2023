package com.ysifre.projet.model

data class Employe (
    var _id: String,
    var nom: String,
    val prenom: String,
    var mail: String,
    var phone: String,
    var photo: String,
)