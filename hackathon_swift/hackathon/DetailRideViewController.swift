//
//  DetailRideViewController.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 02/03/2023.
//

import UIKit
import Firebase
import FirebaseFirestore
import FirebaseAuth
import Kingfisher
import MapKit

class DetailRideViewController: UIViewController {
    
    @IBOutlet weak var imgProfile: UIImageView!
    
    @IBOutlet weak var fullnameLabel: UILabel!
    @IBOutlet weak var phoneButton: UIButton!
    
    @IBOutlet weak var adresseDepartLabel: UILabel!
    @IBOutlet weak var adresseArriveLabel: UILabel!
    @IBOutlet weak var heureLabel: UILabel!
    @IBOutlet weak var plaqueLabel: UILabel!
    @IBOutlet weak var nbPlaceLabel: UILabel!
    
    @IBOutlet weak var participeButton: UIButton!
    @IBOutlet weak var mapView: MKMapView!
    
    var userId: String = ""
    
    private var db = Firestore.firestore()
    
    var selectedRide: Course!

    var driverSelectedRide: Employe?
    override func viewDidLoad() {
        super.viewDidLoad()
        setUpVC()
    }
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    func setUpVC(){
        adresseArriveLabel.text = selectedRide.lieuArrive
        adresseDepartLabel.text = selectedRide.lieuDepart
        plaqueLabel.text = selectedRide.plaque
        guard let userID = Auth.auth().currentUser?.uid else { return }
        self.userId = userID
        fetchData()
        if !selectedRide.passagerId.contains(self.userId){
            setupParticipeButton()
        }else{
            setupParticipePlusButton()
        }
        mapView.delegate = self
        createItineraire()
    }
    
    func fetchData(){
        self.db.collection("Employe").getDocuments {querySnapshot, err in
            if let err = err {
                print("Error getting documents: \(err)")
            } else {
                for document in querySnapshot!.documents{
                    let dd = document.data()
                    let id = document.documentID
                    if self.selectedRide.idConducteur == id{
                        let nom = dd["nom"] as? String ?? ""
                        let prenom = dd["prenom"] as? String ?? ""
                        let mail = dd["mail"] as? String ?? ""
                        let tel = dd["tel"] as? String ?? ""
                        let photo = dd["photo"] as? String ?? ""
                        let adresse = dd["adresse"] as? String ?? ""
                        let driver = Employe(id: id, nom: nom, prenom: prenom, mail: mail, tel: tel, photo: photo, adresse: adresse)
                        self.driverSelectedRide = driver
                        self.fullnameLabel.text = "\(nom) \(prenom)"
                        let date = self.selectedRide.dateHeure.dateValue()
                        let dateFormatter = DateFormatter()
                        dateFormatter.dateFormat = "EEEE dd MMMM, HH:mm"
                        let heureString = dateFormatter.string(from: date)
                        self.heureLabel.text = heureString
                        self.phoneButton.setTitle(tel, for: .normal)
                        self.imgProfile.kf.setImage(with: URL(string: photo))
                        self.nbPlaceLabel.text = String(self.selectedRide.nbPlace-self.selectedRide.passagerId.count)
                    }
                }
            }
        }
    }
    func retrieveLocation(address: String, completion: @escaping (CLLocation?) -> Void) {
        let geocoder = CLGeocoder()

        geocoder.geocodeAddressString(address) { (placemarks, error) in
            guard let placemark = placemarks?.first, error == nil else {
                completion(nil)
                return
            }
            completion(placemark.location)
        }
    }
    
    func createItineraire(){
        let adressDepart = selectedRide.lieuDepart
        let adressArrive = selectedRide.lieuArrive
        
        
        retrieveLocation(address: adressDepart) { locD in
            guard let locD = locD else {return}
            self.retrieveLocation(address: adressArrive) { locA in
                guard let locA = locA else {return}
                let sourcePlacemark = MKPlacemark(
                      coordinate: locD.coordinate,
                      addressDictionary: nil)
                let destinationPlacemark = MKPlacemark(
                    coordinate: locA.coordinate,
                    addressDictionary: nil)
                
                let sourceMapItem = MKMapItem(placemark: sourcePlacemark)
                let destinationMapItem = MKMapItem(placemark: destinationPlacemark)
                
                let directionRequest = MKDirections.Request()
                directionRequest.source = sourceMapItem
                directionRequest.destination = destinationMapItem
                directionRequest.transportType = .automobile
                
                let directions = MKDirections(request: directionRequest)
                directions.calculate { (response, error) in
                    guard let response = response else {
                        if let error = error {
                            print("Error: \(error)")
                        }
                        return
                    }
                    let route = response.routes[0]
                    self.mapView.addOverlay(route.polyline, level: .aboveRoads)
                    self.mapView.setVisibleMapRect(route.polyline.boundingMapRect, animated: true)
                }
            }
        }
    }
    @IBAction func callDriver(_ sender: UIButton) {
        guard let driver = self.driverSelectedRide else { return  }
        let phone = driver.tel
        if let phoneCallURL = URL(string: "tel://\(phone)") {
            let application:UIApplication = UIApplication.shared
            if (application.canOpenURL(phoneCallURL)) {
                application.open(phoneCallURL, options: [:], completionHandler: nil)
            }
          }
    }
    
    public class func newInstance(selectedRide: Course) -> DetailRideViewController{
        let cvc = DetailRideViewController()
        cvc.selectedRide = selectedRide
        return cvc
    }
    func setupParticipePlusButton(){
        participeButton.setTitle("Je ne participe plus", for: .normal)
        participeButton.setTitleColor(UIColor.systemPink, for: .normal)
        participeButton.backgroundColor = UIColor.white
    }
    func setupParticipeButton(){
        participeButton.setTitle("Je participe", for: .normal)
        participeButton.setTitleColor(UIColor.white, for: .normal)
        participeButton.backgroundColor = UIColor.systemPink
    }
    
    @IBAction func participeTapped(_ sender: UIButton) {
        if self.selectedRide.nbPlace > self.selectedRide.passagerId.count || self.selectedRide.passagerId.contains(userId) {
            var passengers: [String] = []
            if !selectedRide.passagerId.contains(self.userId){
                passengers = selectedRide.passagerId
                passengers.append(self.userId)
                setupParticipePlusButton()
            }else {
                setupParticipeButton()
                passengers = selectedRide.passagerId.filter{$0 != self.userId}
            }
            self.selectedRide.passagerId = passengers
            updateParticipationCourse(passengers: passengers)
        }else{
            AlertUtilities.displayAlert("Plus de place", "Il n'y a plus de place pour ce covoiturage", VC: self)
        }
        
    }
    func updateParticipationCourse(passengers : [String]){
        self.db.collection("Course").document(self.selectedRide!.id).updateData(["passagerId":passengers])
    }
}

extension DetailRideViewController: MKMapViewDelegate{
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        let renderer = MKPolylineRenderer(overlay: overlay)
        renderer.strokeColor = .systemPink
        renderer.lineWidth = 3.0
        return renderer
    }

}
