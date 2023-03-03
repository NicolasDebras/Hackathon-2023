//
//  CreateCourseViewController.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 02/03/2023.
//

import UIKit
import Firebase
import FirebaseAuth
import FirebaseFirestore

class CreateCourseViewController: UIViewController {

    @IBOutlet weak var lieuDepart: UITextField!
    @IBOutlet weak var nbPlaceLabel: UILabel!
    @IBOutlet weak var stepper: UIStepper!
    @IBOutlet weak var lieuArrive: UITextField!
    @IBOutlet weak var immatLabel: UITextField!
    @IBOutlet weak var datePicker: UIDatePicker!
    @IBOutlet weak var createCourseButton: UIButton!
    
    private var db = Firestore.firestore()
    
    var userId : String = ""
    var adresseTravail = "1 avenue de la cristallerie 92310 Sèvre"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUpVC()
    }
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    
    func setUpVC(){
        self.nbPlaceLabel.text = String(Int(self.stepper.value))
        let currentDate = Date()
        let minDate = currentDate
        let maxDate = Calendar.current.date(byAdding: .month, value: 1, to: currentDate)
        datePicker.maximumDate = maxDate
        datePicker.minimumDate = minDate
        self.lieuArrive.delegate = self
        self.lieuDepart.delegate = self
        self.immatLabel.delegate = self
        guard let userID = Auth.auth().currentUser?.uid else { return }
        self.userId = userID
    }

    @IBAction func stepperDidChange(_ sender: UIStepper) {
        self.nbPlaceLabel.text = String(Int(self.stepper.value))
    }
    
    @IBAction func createCourseTapped(_ sender: UIButton) {
        if areFieldOk(){
            createCourseFirebase()
            self.navigationController?.pushViewController(HomeViewController(), animated: true)
        }
    }
    
    func createCourseFirebase(){
        let docRef = self.db.collection("Course").document()
        docRef.setData([
            "conducteurId": self.userId,
            "dateHeure": Timestamp(date: self.datePicker.date),
            "lieuDepart":self.lieuDepart.text!,
            "lieuArrive": self.lieuArrive.text!,
            "nbPlace": self.stepper.value,
            "passagerId": [],
            "plaque": self.immatLabel.text!
        ]){ error in
            if let error = error {
                AlertUtilities.displayAlert("Erreur\(error)", "Erreur", VC: self)
            } else {
                AlertUtilities.displayAlert("Course crée", "", VC: self)
            }
        }
    }
    func areFieldOk() ->Bool{
        if self.lieuDepart.text?.trimmingCharacters(in: .whitespacesAndNewlines) != "" || self.lieuArrive.text?.trimmingCharacters(in: .whitespacesAndNewlines) != "" || self.immatLabel.text?.trimmingCharacters(in: .whitespacesAndNewlines) != ""
        {
            return true
            
        }
        AlertUtilities.displayAlert("Champs manquants", "Veuillez remplir tous les champs", VC: self)
        return false
    }
}

extension CreateCourseViewController: UITextFieldDelegate{
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    func textFieldDidEndEditing(_ textField: UITextField) {
        if textField == self.lieuDepart && self.lieuDepart.text != ""{
            self.lieuArrive.text = adresseTravail
        }else if textField == self.lieuArrive && self.lieuArrive.text != ""{
            self.lieuDepart.text = adresseTravail
        }else{
            if textField != self.immatLabel{
                self.lieuArrive.text = ""
                self.lieuDepart.text = ""
            }
        }
    }
}
