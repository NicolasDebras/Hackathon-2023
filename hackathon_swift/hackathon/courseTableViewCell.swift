//
//  courseTableViewCell.swift
//  hackathon
//
//  Created by Zinedine Megnouche on 02/03/2023.
//

import UIKit

class courseTableViewCell: UITableViewCell {
    @IBOutlet weak var adresseDepartLabel: UILabel!
    
    @IBOutlet weak var tempsLabel: UILabel!
    @IBOutlet weak var adresseArriveLabel: UILabel!
    @IBOutlet weak var tempsTrajetLabel: UILabel!
    @IBOutlet weak var heureDepartLabel: UILabel!
    @IBOutlet weak var jyvaisLabel: UILabel!
    @IBOutlet weak var nbPlaceLabel: UILabel!
    @IBOutlet weak var passeeLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        tempsLabel.isHidden = true
        tempsTrajetLabel.isHidden = true
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
