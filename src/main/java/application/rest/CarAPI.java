/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/ 
package application.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;


import application.rest.BMConnObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class CarAPI extends Application {
    //Initiate image processing object
    BMConnObject cap = new BMConnObject();
    /**
     * getCar
     * Note: This function does not work for this version of the software
     * @TODO: Call this API with an String arg representing file name for the image to be returned
     */
    @RequestMapping(value = "/api/getCar", method = RequestMethod.GET)
    public String getCar() throws ClassNotFoundException, IOException {
    	//System.out.println("Cars");
        List<String> names = new ArrayList<String>();
        names.add("Car1");
        names.add("Car2");
        names.add("Car3");
        return new Gson().toJson(names);
    }
    
    /**
     * postCar
     *
     * This is the REST API function for POST data
     * We save the image to the data Object 
     * 
     * @param: Car Object, the new car to create.
     * 
     * @return: String, The hash of the image stored in the data Object after it has been stored or empty.
     */
    @RequestMapping(value = "/api/postCar", method = RequestMethod.POST)
    public String postCar(@RequestBody Car car) {
    	String answer = "error";
    	//Get only data part of base64 string
    	String [] data = car.getImgData().split(",");
    	//Create stream 
        byte[] imageDataBytes = Base64.getMimeDecoder().decode(data[1]);
        //Get file name
        String fileName = car.getFileName();
        //Start processing 
        answer = cap.processImage(imageDataBytes,fileName);
        //@TODO: return useful information. This is just for testing
        return answer;
    }
}
