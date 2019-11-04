//
//  car.swift
//  cars
//
//  Created by Gala on 30/10/2019.
//  Copyright © 2019 Gala. All rights reserved.
//

import Foundation

protocol PropertyReflectable { }

extension PropertyReflectable {
    subscript(key: String) -> Any? {
        let m = Mirror(reflecting: self)

        for child in m.children {
            if (child.label == key) {
                return child.value
            }
        }

        return nil
    }
}

struct Car: CustomStringConvertible, Equatable, Codable {
    private let id: UUID = UUID()
    
    static let allowedParameters = ["name", "year", "model"]
    
    let name: String
    let year: String
    let model: String
    
    var description: String {
        return """
        Name \(name)
        Year \(year)
        Model \(model)
        """
    }
    
    static func == (lhs: Car, rhs: Car) -> Bool {
        return lhs.id == rhs.id
    }
}

extension Car : PropertyReflectable {}
