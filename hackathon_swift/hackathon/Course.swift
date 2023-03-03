//
//  Course.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 02/03/2023.
//

import Foundation
import Firebase

struct Course{
    var id : String
    var idConducteur: String
    var passagerId: [String]
    var nbPlace: Int
    var plaque: String
    var lieuDepart: String
    var lieuArrive: String
    var dateHeure: Timestamp
}
