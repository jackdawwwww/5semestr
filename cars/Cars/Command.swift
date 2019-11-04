//
//  Command.swift
//  cars
//
//  Created by Gala on 30/10/2019.
//  Copyright © 2019 Gala. All rights reserved.
//

import Foundation

enum Commands: String{
    case print = "print"
    case add = "add"
    case remove = "rm"
    case exit = "exit"
    static let commands: [Commands] = [.print,.add,.remove,.exit]
}
