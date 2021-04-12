//
//  MainScreenDataModel.java
//  SpeechImprov
//
//  Created by Simone Karani on 4/10/2021.
//  Copyright © 2021 SpeechImprov. All rights reserved.
//
package com.simonekarani.speechimprov.model;

public class MainScreenDataModel {
    String name;
    int id_;
    int image;

    public MainScreenDataModel(String name, int id_, int image) {
        this.name = name;
        this.id_ = id_;
        this.image=image;
    }


    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getId() {
        return id_;
    }
}
