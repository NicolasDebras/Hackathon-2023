//
//  SignInViewController.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 02/03/2023.
//

import UIKit
import Firebase
import FirebaseAuth

class SignInViewController: UIViewController {

    @IBOutlet weak var usernameTF: UITextField!
    @IBOutlet weak var passworfTF: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()
        setupVC()
    }
    
    func setupVC(){
        usernameTF.delegate = self
        passworfTF.delegate = self
    }

    @IBAction func forgetPasswordTapped(_ sender: UIButton) {
        if(usernameTF.text?.trimmingCharacters(in: .whitespacesAndNewlines) == ""){
            AlertUtilities.displayAlert("Champs Manquants","Veuillez rentrer votre mail", VC: self)
            return
        }
        Auth.auth().sendPasswordReset(withEmail: self.usernameTF.text!) { error in
            if(error == nil){
                AlertUtilities.displayAlert("Réinitialisation de mot de passe", "Un mot de passe a été envoyé dans votre boite mail : \(self.usernameTF.text!) afin de réinitialiser votre mot de passe", VC: self)
            }
            else{
                AlertUtilities.displayAlert(NSLocalizedString("Error mail", comment: ""),NSLocalizedString("Email doesn't exist", comment: ""), VC: self)
                print("error sent mail")
            }
        }
    }
    
    @IBAction func signInHandle(_ sender: UIButton) {
        let email = self.usernameTF.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        let pwd = self.passworfTF.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        Auth.auth().signIn(withEmail: email, password: pwd) { (result, error) in
            if error != nil {
                AlertUtilities.displayAlert("Erreur d'authentification", "email ou mot de passe incorrect", VC: self)
            } else {
                self.navigationController?.pushViewController(HomeViewController(), animated: true)
            }
        }
    }

}
extension SignInViewController: UITextFieldDelegate{
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
