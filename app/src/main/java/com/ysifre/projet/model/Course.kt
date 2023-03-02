package com.ysifre.projet.model

data class Course (
    var _id: String,
    var conducteurID: String,
    val passagerID: ArrayList<String>,
    var nbPlaces: Int,
    var plaque: String,
    var depart: String,
    var arrivee: String,
    var heureDepart: String,
)