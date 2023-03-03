//
//  HomeViewController.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 02/03/2023.
//

import UIKit
import Firebase
import FirebaseFirestore
import FirebaseAuth
import MapKit

class HomeViewController: UIViewController {
    
    private var db = Firestore.firestore()
    var userId: String = ""
    let refreshControl = UIRefreshControl()
    
    @IBOutlet weak var tableView: UITableView!
    
    var courses : [Course] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupVC()
    }
    
    func setupVC(){
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib(nibName: "courseTableViewCell", bundle: nil), forCellReuseIdentifier: "courseCell")
        guard let userID = Auth.auth().currentUser?.uid else { return }
        self.userId = userID
        refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        self.tableView.addSubview(refreshControl)
        
    }
    
    @objc func refresh(_ sender: AnyObject) {
        self.courses = []
        fetchData()
        refreshControl.endRefreshing()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = true
        self.courses = []
        fetchData()
    }

    @IBAction func profilTapped(_ sender: Any) {
    }
    @IBAction func addCarpool(_ sender: UIButton) {
        self.navigationController?.pushViewController(CreateCourseViewController(), animated: false)
    }
    
    @IBAction func passengerTapped(_ sender: UIButton) {
        self.navigationController?.pushViewController(PassengerViewController(), animated: false)
    }
    
    @IBAction func driverTapped(_ sender: UIButton) {
        self.navigationController?.pushViewController(DriverViewController(), animated: false)
    }
    func fetchData(){
        self.db.collection("Course").getDocuments { querySnapshot, err in
            if let err = err {
                print("Error getting documents: \(err)")
            } else {
                for document in querySnapshot!.documents{
                    let dd = document.data()
                    let id = document.documentID
                    let conducteurId = dd["conducteurId"] as? String ?? ""
                    if let dateHeure = dd["dateHeure"] as? Timestamp{
                        if dateHeure.dateValue() > Date(){
                            if conducteurId != self.userId{
                                let passagerId = dd["passagerId"] as? [String] ?? []
                                let nbPlace = dd["nbPlace"] as? Int ?? 0
                                let plaque = dd["plaque"] as? String ?? ""
                                let lieuDepart = dd["lieuDepart"] as? String ?? ""
                                let lieuArrive = dd["lieuArrive"] as? String ?? ""
                                let course = Course(id: id, idConducteur: conducteurId, passagerId: passagerId, nbPlace: nbPlace, plaque: plaque, lieuDepart: lieuDepart, lieuArrive: lieuArrive, dateHeure: dateHeure)
                            
                                self.courses.append(course)
                        }
                        }
                    }
                }
            }
            self.tableView.reloadData()
        }
    }

    

}
extension HomeViewController: UITableViewDataSource,UITableViewDelegate{
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        200
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        courses.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "courseCell", for: indexPath) as? courseTableViewCell{
            let course = courses[indexPath.row]
            let date = course.dateHeure.dateValue()
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "EEEE dd MMMM, HH:mm"
            let heureString = dateFormatter.string(from: date)
            cell.heureDepartLabel.text = heureString
            cell.adresseDepartLabel.text = course.lieuDepart
            cell.adresseArriveLabel.text = course.lieuArrive
            
            cell.jyvaisLabel.isHidden = !course.passagerId.contains(userId)
            cell.tempsTrajetLabel.text = "0 mn"
            cell.nbPlaceLabel.text = String(course.nbPlace-course.passagerId.count)
            cell.passeeLabel.isHidden = true
            return cell
        }
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if self.courses[indexPath.row].nbPlace > self.courses[indexPath.row].passagerId.count || self.courses[indexPath.row].passagerId.contains(self.userId) {
            let vc = DetailRideViewController.newInstance(selectedRide: self.courses[indexPath.row])
            self.navigationController?.pushViewController(vc, animated: true)
        }else{
            AlertUtilities.displayAlert("Plus de place", "Il n'y a plus de place pour ce covoiturage", VC: self)
        }
        
    }
    
    
}
