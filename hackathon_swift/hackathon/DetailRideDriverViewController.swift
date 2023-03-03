//
//  DetailRideDriverViewController.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 03/03/2023.
//

import UIKit
import Firebase
import FirebaseFirestore
import FirebaseAuth
import Kingfisher
import MapKit

class DetailRideDriverViewController: UIViewController {

    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var nomLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var mapView: MKMapView!
    var selectedRide: Course!
    var passengers : [Employe] = []
    
    var userId: String = ""
    
    private var db = Firestore.firestore()
    
    @IBOutlet weak var addressPassengerLabel: UILabel!
    @IBOutlet weak var phoneButton: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        setupVC()
        // Do any additional setup after loading the view.
    }
    func setupVC(){
        tableView.dataSource = self
        tableView.delegate = self
        fetchData()
        mapView.delegate = self
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
    
    func createItineraire(adressArrive: String){
        let adressDepart = selectedRide.lieuDepart
        
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
    
    func fetchData(){
        self.db.collection("Employe").getDocuments {querySnapshot, err in
            if let err = err {
                print("Error getting documents: \(err)")
            } else {
                for document in querySnapshot!.documents{
                    let dd = document.data()
                    let id = document.documentID
                    if self.selectedRide.passagerId.contains(id){
                        let nom = dd["nom"] as? String ?? ""
                        let prenom = dd["prenom"] as? String ?? ""
                        let mail = dd["mail"] as? String ?? ""
                        let tel = dd["tel"] as? String ?? ""
                        let photo = dd["photo"] as? String ?? ""
                        let adresse = dd["adresse"] as? String ?? ""
                        let passenger = Employe(id: id, nom: nom, prenom: prenom, mail: mail, tel: tel, photo: photo, adresse: adresse)
                        self.passengers.append(passenger)
                    }
                    self.tableView.reloadData()
                }
                
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = false
    }
    

    public class func newInstance(selectedRide: Course) -> DetailRideDriverViewController{
        let cvc = DetailRideDriverViewController()
        cvc.selectedRide = selectedRide
        return cvc
    }

    @IBAction func callPassenger(_ sender: UIButton) {
    }
}
extension DetailRideDriverViewController: UITableViewDelegate,UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.passengers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell") ?? UITableViewCell(style: .default, reuseIdentifier: "Cell")
            
        let fullnameDeliver = "\(self.passengers[indexPath.row].nom) \(self.passengers[indexPath.row].prenom)"
        cell.textLabel?.text = fullnameDeliver
            return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let passenger = self.passengers[indexPath.row]
        self.nomLabel.text = "\(passenger.nom) \(passenger.prenom)"
        self.phoneButton.setTitle(passenger.tel, for: .normal)
        self.imageView.kf.setImage(with: URL(string: passenger.photo))
        self.addressPassengerLabel.text = passenger.adresse
        createItineraire(adressArrive: passenger.adresse)
    }
    
}
extension DetailRideDriverViewController: MKMapViewDelegate{
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        let renderer = MKPolylineRenderer(overlay: overlay)
        renderer.strokeColor = .systemPink
        renderer.lineWidth = 3.0
        return renderer
    }
}
