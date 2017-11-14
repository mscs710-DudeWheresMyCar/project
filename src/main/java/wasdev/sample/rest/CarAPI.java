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
package wasdev.sample.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import org.apache.cxf.attachment.Base64DecoderStream;

import wasdev.sample.Car;
import wasdev.sample.Visitor;
import wasdev.sample.store.VisitorStore;
import wasdev.sample.store.VisitorStoreFactory;
import application.Testing;

import com.google.gson.Gson;

@ApplicationPath("api")
@Path("/cars")
public class CarAPI extends Application {
	
	//Our database store
	//For testing db connection here
	//VisitorStore store = VisitorStoreFactory.getInstance();
	//CarApi obect
	//@TODO: Rename this file
	BMConnObject cap = new BMConnObject();
	//Db2 db = new Db2();
	//Testing tst = new Testing();
  /**
   * Gets all Visitors.
   * REST API example:
   * <code>
   * GET http://localhost:9080/GetStartedJava/api/visitors
   * </code>
   * 
   * Response:
   * <code>
   * [ "Bob", "Jane" ]
   * </code>
   * @return A collection of all the Visitors
 * @throws ClassNotFoundException 
 * @throws IOException 
   */
    @GET
    @Path("/")
    @Produces({"application/json"})
    public String getCars() throws ClassNotFoundException, IOException {
    	System.out.println("Cars");
		
		List<String> names = new ArrayList<String>();
		/*
		for (Visitor doc : store.getAll()) {
			String name = doc.getName();
			if (name != null){
				names.add(name);
			}
		}
		*/
		//names.add(cap.testObjectCon());
		//db.getAll();
		//tst.testApp();
		//cap.testObjectCon();
		names.add("Genti");
		names.add("Genti2");
		return new Gson().toJson(names);
    }
    
    /**
     * Creates a new Visitor.
     * 
     * REST API example:
     * <code>
     * POST http://localhost:9080/GetStartedJava/api/visitors
     * <code>
     * POST Body:
     * <code>
     * {
     *   "name":"Bob"
     * }
     * </code>
     * Response:
     * <code>
     * {
     *   "id":"123",
     *   "name":"Bob"
     * }
     * </code>
     * @param visitor The new Visitor to create.
     * @return The Visitor after it has been stored.  This will include a unique ID for the Visitor.
     */
    @POST
    @Produces("application/text")
    @Consumes("application/json")
    public String newToDo(Car car) {
    	String smth = "empty";// gg testing
    	//Get only data part of base64 string
    	String [] data = car.getImgData().split(",");
    	//Create stream 
        byte[] imageDataBytes = Base64.getMimeDecoder().decode(data[1]);
        //Get file name
        String fileName = car.getFileName();
        //Start processing 
        smth = cap.processImage(imageDataBytes,fileName);
        //@TODO: return useful information. This is just for testing
        return smth;
    }

}