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
    
    private func removeByIndex(){
        print("Print car numb")
        guard let carCharacteristic = readLine() else{
            return
        }
        let CarIndex: Int = Int(carCharacteristic) ?? -1
        if CarIndex != -1 && CarIndex < storage.cars.count{
            let tmpCar: Car=storage.cars[CarIndex-1]
            storage.removeCar(tmpCar)
        }
        return
    }
    
    private func removeByName(){
        print("Print car name")
        guard let carCharacteristic = readLine() else{ return }
        for i in storage.cars{
            if i.name == carCharacteristic{
                storage.removeCar(i)
                return
            }
        }
        print("No car with that name")
        return
    }
    
    private func removeByYear(){
        print("Print car year")
        guard let carCharacteristic = readLine() else{ return }
        let tmpYear: Int = Int(carCharacteristic) ?? -1
        for i in storage.cars{
            if i.year == tmpYear{
                storage.removeCar(i)
                return
            }
        }
        print("No car with that year")
        return
    }
    
    private func removeByModel(){
        print("Print car model")
        guard let carCharacteristic = readLine() else{ return }
        for i in storage.cars{
            if i.model == carCharacteristic{
                storage.removeCar(i)
                return
            }
        }
        print("No car with that model")
        return
    }
    
    private func removeByChar(){
        let isWorked = true
        while isWorked{
            print("Do you eant chose car by 'name', 'year' or by 'model'?")
            guard let type = readLine() else { return }
        switch type {
            case "name":
                removeByName()
                return
            case "year":
                removeByYear()
                return
            case "model":
                removeByModel()
                return
            default:continue
            }
        }
    }

    private func removeCar(){
        if(storage.cars.isEmpty){
          print("List empty")
            return
        }
        let isWorked = true
        while isWorked{
            print("Please write how you want to chose car,if you wont to find by number write 'num' or 'ch' if you want to find by characteristic")
            guard let task = readLine() else{ return }
            if task == "num" {
                removeByIndex()
                return
            }
            else{
                if task=="ch"{
                    removeByChar()
                    return
                }
                else { continue }
            }
        }
    }
    
}
