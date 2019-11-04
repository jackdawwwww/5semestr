//
//  main.swift
//  cars
//
//  Created by Gala on 30/10/2019.
//  Copyright © 2019 Gala. All rights reserved.
//


import Foundation

let storage = Storage()

storage.load()

let console = Console(storage: storage)

console.run() 
storage.save()
