//
//  AlertUtilities.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 03/03/2023.
//

import Foundation
import  UIKit

class AlertUtilities{
    
    static func displayAlert(_ title: String,_ message: String,VC: UIViewController){
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        VC.present(alert, animated: true, completion: nil)
        }
}
