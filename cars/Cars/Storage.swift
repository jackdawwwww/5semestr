//
//  Storage.swift
//  cars
//
//  Created by Gala on 30/10/2019.
//  Copyright © 2019 Gala. All rights reserved.
//


import Foundation

private let fileURL = URL(fileURLWithPath: "/Users/user/Desktop/cars/cars/car.txt")

class Storage {
    internal private(set) var cars: [Car] = []
    
    func addCar(_ car: Car, _ ind: Int) {
        cars.insert(car, at: ind)
        save()
    }
    
    func removeCar(_ carForRemove: Car) {
        cars.removeAll { car in
            return car == carForRemove
        }
        
        save()
    }
    
    func removeByIndex(_ index: Int) {
        cars.remove(at: index)
    }
    
    func save() {
        guard let data = try? JSONEncoder().encode(cars) else {
           fatalError("Can't encode data")
        }
        
        try? data.write(to: fileURL)
    }
    
    func load() {
        guard let data = try? Data(contentsOf: fileURL) else {
            return
        }
        guard let loadedCars = try? JSONDecoder().decode([Car].self, from: data) else {
            return
        }
        
        cars = loadedCars
    }
}


