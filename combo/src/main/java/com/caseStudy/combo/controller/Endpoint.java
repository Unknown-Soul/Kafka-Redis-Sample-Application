package com.caseStudy.combo.controller;

import com.caseStudy.combo.dto.User;
import com.caseStudy.combo.service.SendDataToProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class Endpoint {


    @Autowired
    SendDataToProducer sendDataToProducer;

    @PostMapping("/update")
    public void updateUser(@RequestBody User user){
        sendDataToProducer.publishData(user);

    }

}
