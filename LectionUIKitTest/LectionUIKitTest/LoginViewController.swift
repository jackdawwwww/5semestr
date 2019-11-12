//
//  LoginViewController.swift
//  LectionUIKitTest
//
//  Created by Konstantin Polin on 31/10/2019.
//  Copyright © 2019 Konstantin Polin. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController, UITextFieldDelegate {

	@IBOutlet var nameText: UITextField!
	@IBOutlet var surnameText: UITextField!
	@IBOutlet var loginButton: UIButton!
    @IBOutlet weak var loginFormView: UIView!
    @IBOutlet weak var loginFormViewYConstraint: NSLayoutConstraint!
    
    var initialLoginFormViewY: CGFloat = 0

    override func viewDidLoad() {
		super.viewDidLoad()
        
        addTapGestureToHideKeyboard()
        addKeyboardObservers()

		nameText.delegate = self
		surnameText.delegate = self
	}
    
    override func viewDidAppear(_ animated: Bool) {
        initialLoginFormViewY = loginFormViewYConstraint.constant
    }
    
    func addTapGestureToHideKeyboard(){
        let tapGesture = UITapGestureRecognizer(target: view, action: #selector(view.endEditing))
        
        view.addGestureRecognizer(tapGesture)
    }
    
    func addKeyboardObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }

	@IBAction func onLoginTapped(_ sender: Any) {
		guard let name = nameText.text, let surname = surnameText.text else {
			 return
		}
        
        if name == "Galina" && surname == "Khlimankova" {
            performSegue(withIdentifier: "ShowImage", sender: self)
        } else {
            let alertController = UIAlertController(
                title: "ERROR!!!",
                message: "Your write incorrect name. Please write correct name",
                preferredStyle: .alert)
            
            alertController.addAction(UIAlertAction(
                title: "Close",
                style: .default,
                handler: { _ in
                alertController.dismiss(animated: true, completion: nil)
            }))
            
            self.present(alertController, animated: true, completion: nil)
            
            DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(5)) {
                alertController.dismiss(animated: true, completion: nil)
            }
        }
	}
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
        
        if let imageVC = segue.destination as? ImageViewController {
            
            let name = nameText.text ?? ""
            let surname = surnameText.text ?? ""
            
            imageVC.name = name
            imageVC.surname = surname
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        
        return true
    }
    
	@objc func keyboardWillShow(notification: NSNotification) {
		let value = notification.userInfo![UIResponder.keyboardFrameBeginUserInfoKey] as! NSValue
		let keyboardFrame = value.cgRectValue

        if loginFormViewYConstraint.constant == initialLoginFormViewY {
            loginFormViewYConstraint.constant -= keyboardFrame.size.height / 2
            
            UIView.animate(withDuration: 0.3) {
                self.view.layoutIfNeeded()
            }
        }
	}
    
	@objc func keyboardWillHide(notification: NSNotification) {
        if loginFormViewYConstraint.constant != initialLoginFormViewY {
			loginFormViewYConstraint.constant = initialLoginFormViewY
            
            UIView.animate(withDuration: 0.3) {
                self.view.layoutIfNeeded()
            }
        }
	}
}


//        nameText.translatesAutoresizingMaskIntoConstraints = false
//        surnameText.translatesAutoresizingMaskIntoConstraints = false
//        loginButton.translatesAutoresizingMaskIntoConstraints = false
//          loginFormView.translatesAutoresizingMaskIntoConstraints = false
//        let safeAreaGuide = view.safeAreaLayoutGuide
//
//        NSLayoutConstraint.activate([surnameText.centerXAnchor.constraint(equalTo: safeAreaGuide.centerXAnchor),
//                           surnameText.centerYAnchor.constraint(equalTo: safeAreaGuide.centerYAnchor),
//                           loginButton.topAnchor.constraint(equalTo: surnameText.bottomAnchor, constant: 20),
//                           loginButton.centerXAnchor.constraint(equalTo: safeAreaGuide.centerXAnchor),
//                           surnameText.topAnchor.constraint(equalTo: nameText.bottomAnchor, constant: 20),
//                           nameText.centerXAnchor.constraint(equalTo: safeAreaGuide.centerXAnchor)])
//
