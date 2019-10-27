//
//  Command.swift
//  Cars
//
//  Created by User on 24/10/2019.
//  Copyright © 2019 User. All rights reserved.
//
enum Commands: String{
    case print = "print"
    case add = "add"
    case remove = "rm"
    case exit = "exit"
    static let commands: [Commands] = [.print,.add,.remove,.exit]
}
