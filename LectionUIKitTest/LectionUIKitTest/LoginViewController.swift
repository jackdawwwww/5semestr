//
//  LoginViewController.swift
//  LectionUIKitTest
//
//  Created by Konstantin Polin on 31/10/2019.
//  Copyright © 2019 Konstantin Polin. All rights reserved.
//

import UIKit

private enum Consts {
    static let loginFirstName: String = ""
    static let loginSurName: String = ""
}

class LoginViewController: UIViewController, UITextFieldDelegate {

	@IBOutlet private var nameText: UITextField!
	@IBOutlet private var surnameText: UITextField!
	@IBOutlet private var loginButton: UIButton!
    @IBOutlet weak var loginFormView: UIView!
    @IBOutlet weak var loginFormViewYConstraint: NSLayoutConstraint!
    
    var initialLoginFormViewY: CGFloat = 0
 
    override func viewDidLoad() {
		super.viewDidLoad()
        
        addTapGestureToHideKeyboard()
        addKeyboardObservers()
        configureViews()
	}
    
    private func configureViews() {
        nameText.delegate = self
        surnameText.delegate = self

        nameText.translatesAutoresizingMaskIntoConstraints = false
        surnameText.translatesAutoresizingMaskIntoConstraints = false
        loginButton.translatesAutoresizingMaskIntoConstraints = false
        loginFormView.translatesAutoresizingMaskIntoConstraints = false
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

        if name == Consts.loginFirstName, surname == Consts.loginSurName {
            performSegue(withIdentifier: "ShowImage", sender: self)
        } else {
            showIncorrectNameError()
        }
    }
    private func showIncorrectNameError() {
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

        present(alertController, animated: true, completion: nil)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(5)) {
           alertController.dismiss(animated: true, completion: nil)
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
