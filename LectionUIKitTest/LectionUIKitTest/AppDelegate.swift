//
//  AppDelegate.swift
//  LectionUIKitTest
//
//  Created by Konstantin Polin on 31/10/2019.
//  Copyright © 2019 Konstantin Polin. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

	var window: UIWindow?

	func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
		// Override point for customization after application launch.
		
		print("didFinishLaunchingWithOptions")
		return true
	}

	func applicationWillEnterForeground(_ application: UIApplication) {
		print("applicationWillEnterForeground")
	}
	
	func applicationWillResignActive(_ application: UIApplication) {
		print("applicationWillResignActive")

	}
	
	func applicationDidBecomeActive(_ application: UIApplication) {
		print("applicationDidBecomeActive")
	}
	
	func applicationDidEnterBackground(_ application: UIApplication) {
		print("applicationDidEnterBackground")
	}
}

