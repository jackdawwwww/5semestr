//
//  ImageListViewController.swift
//  LectionUIKitTest
//
//  Created by Gala on 12/11/2019.

import UIKit

private struct CellData {
    let text: String
    let image: UIImage?
}

private struct SectionData {
    let headerText: String
    let footerText: String
    let cells: [CellData]
}

class ImageListViewController: UIViewController {
    
    @IBOutlet var tableView: UITableView!
    
    private let icon = UIImage(named: "im")
    
    private lazy var sections: [SectionData] = [
        SectionData(headerText: "It's first section", footerText: "It's footer", cells: [
            CellData(text: "It's first cell", image: icon),
            CellData(text: "It's second cell", image: icon),
        ]),
        SectionData(headerText: "It's first section", footerText: "It's footer", cells: [
            CellData(text: "Good", image: icon),
            CellData(text: "Buy", image: icon),
            CellData(text: "America", image: icon),
        ]),
        SectionData(headerText: "It's first section", footerText: "It's footer", cells: [
            CellData(text: "Hello", image: icon),
            CellData(text: "Table", image: icon),
            CellData(text: "View", image: icon),
            CellData(text: "And", image: icon),
            CellData(text: "World", image: icon)
        ])
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.delegate = self
        tableView.dataSource = self

        view.backgroundColor = .lightGray
        tableView.backgroundColor = .white
        tableView.tableFooterView = UIView(frame: .zero)
    }
}

extension ImageListViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sections[section].headerText
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let label = UILabel(frame: .zero)
        
        label.text = sections[section].footerText
        label.backgroundColor = .gray
        
        return label
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sections[section].cells.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return TableCell()
    }
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        let data = sections[indexPath.section].cells[indexPath.row]
        if let tableCell = cell as? TableCell {
            tableCell.configure(data: data)
        }
    }
}


private class TableCell: UITableViewCell {
    private let previewImageView: UIImageView = UIImageView()
    private let imageNameLabel: UILabel = UILabel(frame: .zero)

    init() {
        super.init(style: .default, reuseIdentifier: nil)
        configureViews()
        confihureConstraints()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        configureViews()
        confihureConstraints()
    }

    

    private func configureViews() {
        imageNameLabel.translatesAutoresizingMaskIntoConstraints = false
        previewImageView.translatesAutoresizingMaskIntoConstraints = false
        
        contentView.addSubview(previewImageView)
        contentView.addSubview(imageNameLabel)
        }
    
    private func confihureConstraints() {
        NSLayoutConstraint.activate([
            previewImageView.rightAnchor.constraint(equalTo: imageNameLabel.leftAnchor),
            previewImageView.widthAnchor.constraint(equalToConstant: 70),
            previewImageView.heightAnchor.constraint(equalToConstant: 70),
            previewImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            imageNameLabel.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            
            contentView.leftAnchor.constraint(equalTo: previewImageView.leftAnchor),
            contentView.topAnchor.constraint(equalTo: imageNameLabel.topAnchor),
            contentView.rightAnchor.constraint(equalTo: imageNameLabel.rightAnchor),
         //   contentView.bottomAnchor.constraint(equalTo: imageNameLabel.bottomAnchor),
            contentView.heightAnchor.constraint(equalToConstant: 80.0)
        ])
    }
    
    func configure(data: CellData) {
        imageNameLabel.text = data.text
        previewImageView.image = data.image
    }
}
