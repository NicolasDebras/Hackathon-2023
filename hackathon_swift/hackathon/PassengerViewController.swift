//
//  PassengerViewController.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 03/03/2023.
//

import UIKit
import Firebase
import FirebaseFirestore
import FirebaseAuth

class PassengerViewController: UIViewController {
    
    private var db = Firestore.firestore()
    var userId: String = ""
    let refreshControl = UIRefreshControl()
    
    var courses : [Course] = []

    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()
        setupVC()
        // Do any additional setup after loading the view.
    }
    
    func setupVC(){
        self.tableView.dataSource = self
        self.tableView.delegate = self
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
                            if conducteurId != self.userId{
                                let passagerId = dd["passagerId"] as? [String] ?? []
                                if passagerId.contains(self.userId){
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

    @IBAction func homeTapped(_ sender: UIButton) {
        self.navigationController?.pushViewController(HomeViewController(), animated: false)
    }
    
    @IBAction func driverTapped(_ sender: UIButton) {
        self.navigationController?.pushViewController(DriverViewController(), animated: false)
    }
    @IBAction func addCourse(_ sender: UIButton) {
        self.navigationController?.pushViewController(CreateCourseViewController(), animated: true)
    }
    @IBAction func profilTapped(_ sender: UIButton) {
    }
    
}

extension PassengerViewController: UITableViewDelegate,UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.courses.count
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
            if course.dateHeure.dateValue() > Date(){
                cell.passeeLabel.isHidden = true
                cell.jyvaisLabel.isHidden = false
            }else{
                cell.passeeLabel.isHidden = false
                cell.jyvaisLabel.isHidden = true
            }
            
            return cell
        }
        return UITableViewCell()
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let course = self.courses[indexPath.row]
        if course.passagerId.contains(userId){
            let vc = DetailRideViewController.newInstance(selectedRide: course)
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        200
    }
    
}
