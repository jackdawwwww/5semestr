//
//  Console.swift
//  Cars
//
//  Created by User on 24/10/2019.
//  Copyright © 2019 User. All rights reserved.
//

class Console{
    private let storage: Storage
    
    init(storage: Storage){
        self.storage = storage
    }
    func run(){
        var isWorked:Bool = true
        while isWorked{
            print("Write command:")
            guard let commandStr = readLine() else{
                fatalError("blablabla")
            }
            guard let command = Commands(rawValue: commandStr)
                else{
                    print("Please write another command: [\(allCommandsOfStr())]")
                    continue
            }
            
            switch command {
            case .exit:
                isWorked = false
                break
            case .add:
                addCar()
            case .remove:
                removeCar()
            case .print:
                printCarList()
            }
            
        }

    }
    
    private func allCommandsOfStr() -> String {
        var result: String = ""
        for command in Commands.commands{
            result += "'\(command.rawValue)'"
        }
        return result
    }
    
    private func printCarList(){
        if(storage.cars.isEmpty){
                print("List is empty")
            return
        }
        
        for (i,car) in storage.cars.enumerated(){
            print("№ ",i+1)
            print(car)
        }
    }
    private func addCar(){
        print("Please write car name: ",separator: "",terminator: "")
        guard let carName = readLine() else {
               return
        }
        print("Please write car year: ",separator: "",terminator: "")
        var carYear: Int = 0
        while true{
            guard let carYearOfStr = readLine(), let newCarYear = Int(carYearOfStr)else{
                print("Please write correct year")
                continue
            }
            carYear = newCarYear
            break
        }
        print("Please write car model: ",separator: "",terminator: "")
        guard let carModel = readLine() else {
               return
        }
        storage.addCar(Car(name:carName, year: carYear,model: carModel))
    }

    private func removeCar(){
        if(storage.cars.isEmpty){
          print("List empty")
            return
        }
        print("Please write car index or charactiristic ")
        guard let carCharacteristic = readLine() else{
            return
        }
        let CarIndex: Int = Int(carCharacteristic) ?? -1
        if CarIndex != -1 && CarIndex < storage.cars.count{
            let tmpCar: Car=storage.cars[CarIndex-1]
            storage.removeCar(tmpCar)
        }
        else{
            let tmpYear: Int = Int(carCharacteristic) ?? -1
            for i in storage.cars{
                if (i.model == carCharacteristic || i.name == carCharacteristic || i.year == tmpYear){
                    storage.removeCar(i)
                    return
                }
            }
            print("No car with that characteristic")
            return
        }
    }
}
