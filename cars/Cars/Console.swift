//
//  Console.swift
//  cars
//
//  Created by Gala on 30/10/2019.
//  Copyright © 2019 Gala. All rights reserved.
//
import Foundation
class Console{
    private let storage: Storage
    
    init(storage: Storage){
        self.storage = storage
    }
    
    func run() {
        var isWorking: Bool = true
    
        while (isWorking) {
            print("Enter command: [\(allCommandsOfStr())]")
            
            guard let commandStr = readLine() else {
                fatalError("error")
            }
            
            guard let command = Commands(rawValue: commandStr) else {
                    print("Please enter another command: [\(allCommandsOfStr())]")
            
                    continue
            }
            
            switch command {
                case .exit:
                    isWorking = false
                
                case .add:
                    pushCar()
                
                case .remove:
                    removeCar()
                
                case .print:
                    printCarList()
            }
        }
    }
    
    func allCommandsOfStr() -> String {
        var result: String = ""
        
        for command in Commands.commands {
            result += "'\(command.rawValue)'"
        }
    
        return result
    }


    private func addNewCar(_ newCar:Car) {
        while (true) {
            print("Push: \n 1. To index \n 2. To back")
            
            guard let pushBy = readLine() else {
                return
            }
            var index = storage.cars.count
            
            if (pushBy == "1") {
                print("Enter index")
                
                guard let ind = readLine() else {
                    return
                }
                let intIndex: Int = Int(ind) ?? -1
                
                if (intIndex < 0 || intIndex > storage.cars.count) {
                    print("No car with that index")
                    
                    continue
                }
                index = intIndex
            }
            
            if (pushBy != "2") {
                continue
            }
            
            storage.addCar(newCar, index)
            
            return
        }
    }
    
    private func pushCar(){
        let carName = readParameter(parameterName: "car name")
        let carModel = readParameter(parameterName: "car model")
        let carYear = readParameter(parameterName: "car year", checkInt: true)
        
        self.addNewCar(Car(name:carName, year: carYear, model: carModel))
    }

    private func readParameter(parameterName: String, checkInt: Bool = false) -> String {
        print("Please enter \(parameterName): ")

        while (true) {
            let value = readLine() ?? ""
                
            if (value.isEmpty) {
                print("Please enter correct \(parameterName)!")

                continue
            }

            if (checkInt) {
                if (Int(value) == nil) {
                    print("Please enter correct number for \(parameterName)!")
                    
                    continue
                }
            }

            return value
        }
    }

    private func removeByParameter() {
        let parameter = requestParameter()
        
        while (true) {
            print("'exit' for exit \nEnter \(parameter):")

            let searchValue = readLine() ?? ""
            
            if (searchValue == "exit"){
                return
            }

            var isRemovedAny = false
            var removedCount = 0
            
            for car in storage.cars {
                if ((car[parameter] as? String) != nil) {
                    let carParameterValue = car[parameter] as! String

                    if (carParameterValue.contains(searchValue)) {
                        storage.removeCar(car)

                        removedCount += 1
                        isRemovedAny = true
                    }
                }
            }

            if (isRemovedAny == false) {
                print("No car with \(parameter): \(searchValue)")
                
                continue
            } else {
                print("Removed \(removedCount) cars")
                
                return
            }
        }
    }
    
    private func requestParameter() -> String {
        print("Which parameter? \n Allowed:")
        
        for parameter in Car.allowedParameters {
            print(" - \(parameter)")
        }

        while (true) {
            let type = readLine() ?? ""
            
            if (type.isEmpty || !Car.allowedParameters.contains(type.lowercased())) {
                print("Please enter correct parameter")
                
                continue
            }
            
            return type
        }
    }
    
    private func removeByIndex() {
        print("Enter car numb")
        
        guard let carCharacteristic = readLine() else {
            return
        }
        let CarIndex: Int = Int(carCharacteristic) ?? -1
        
        if (CarIndex != -1 && CarIndex <= storage.cars.count) {
            let tmpCar: Car = storage.cars[CarIndex - 1]
            
            storage.removeCar(tmpCar)
        }
        
        return
    }
    
    private func removeCar() {
        if (storage.cars.isEmpty) {
            print("List is empty")
            
            return
        }
        
        while (true) {
            print("Remove by: \n 1. Index \n 2. Parameter")
            
            guard let task = readLine() else {
                return
            }
            
            if (task == "1") {
                removeByIndex()
                
                return
            } else {
                if (task == "2") {
                    removeByParameter()
                    
                    return
                } else {
                    continue
                }
            }
        }
    }
    
    func getCar() -> [Car] {
        return storage.cars
    }
    
    private func printCarList() {
        if (storage.cars.isEmpty) {
            print("List is empty")
            
            return
        }
        
        for (i, car) in storage.cars.enumerated() {
            print("№ ", i + 1)
            print(car)
        }
    }
}
