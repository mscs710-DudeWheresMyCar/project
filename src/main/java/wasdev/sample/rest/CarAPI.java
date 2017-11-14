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

<<<<<<< HEAD
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
=======
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ObjectLocation;
import org.openstack4j.openstack.OSFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.openstack4j.api.OSClient.OSClientV3;

public class CarAPI {
	/************Fields*************/
	final String USERID = "userId"; 
	final String PASSWORD = "password";
	final String AUTHURL = "auth_url";
	final String DOMAINNAME = "domainName";
	final String PROJECT = "project";
	/************Constructors*******/
	public CarAPI(){}
	/************Methods************/
	
	/**
	 * getAuthString
	 * 
	 * Get the authentication string in JSON format
	 * 
	 * @return: String authString
	 */
	public String getAuthString()
	{
		String	authStr =  "{\"Object-Storage\": [{\"credentials\": {\"auth_url\": \"https://identity.open.softlayer.com\",";
		authStr += "\"project\": \"object_storage_850aaf2d_73ea_4b8d_ba87_f3629bcb7c68\",";
		authStr += "\"projectId\": \"d80c340568a44039847b6e7887bbdd93\"," + "\"region\": \"dallas\",";
		authStr += " \"userId\": \"231a8503e7354c3280fbdd9617262b39\",";
		authStr += "\"username\": \"admin_0555aeb40e0677c6f61b1d2161fd962a4cf8d1e3\",";
		authStr += "\"password\": \"BMh#ale-Y7_0S_Tk\"," + "\"domainId\": \"9c76df66cd264a70b8a2a295c60ad7e7\",";
		authStr += "\"domainName\": \"1433205\"," + "\"role\": \"admin\"" + "},";
		authStr += "\"syslog_drain_url\": null," + "\"volume_mounts\": []," + "\"label\": \"Object-Storage\",";
		authStr += "\"provider\": null," + "\"plan\": \"Free\"," + "\"name\": \"DSX-ObjectStorage\",";
		authStr += "\"tags\": [" + "\"storage\"," + "\"ibm_release\"," + "\"ibm_created\"," + "\"lite\"]}]}";
		return authStr;
	}
	
	/**
	 * getCredentials
	 * 
	 * Get the credentials object from JSON string
	 * 
	 * @param: String JSON String authStr
	 * 
	 * @return: JsonObject credentials
	 * 
	 */
	public Map<String,String> getCredentials(String authStr)
	{
		Map<String, String> map = new HashMap<String, String>();
		JsonParser parser = new JsonParser();
    	Object obj = parser.parse(authStr);
		JsonObject jsonObject = (JsonObject) obj;
		JsonArray vcapArray = (JsonArray) jsonObject.get("Object-Storage");//get this key from JSON
		JsonObject vcap = (JsonObject) vcapArray.get(0);// using vcap var name as in IBM Bluemix Data object
		JsonObject credentials = (JsonObject) vcap.get("credentials");
		Iterator<Entry<String, JsonElement>> it = credentials.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, JsonElement> entry = it.next();
			map.put(entry.getKey(), entry.getValue().getAsString());
		}
		return map;
	}
	
	public OSClientV3 connectionObject()
	{
		//String project = credentials.get("project").toString().replaceAll("\"", ""); // make sure we have string without quotes (original)
    	String	authStr = this.getAuthString();
		Map<String, String> credentials = this.getCredentials(authStr);
		
		/*NOTE: Use replaceAll("\"", "") if quotes are added to the string */
		String userId = credentials.get(USERID);
		String password = credentials.get(PASSWORD);
		String auth_url = credentials.get(AUTHURL) + "/v3";
		String domain = credentials.get(DOMAINNAME);
		String project = credentials.get(PROJECT);
		
		Identifier domainIdent = Identifier.byName(domain);
		Identifier projectIdent = Identifier.byName(project);
		//Build version 3 Auth
    	OSClientV3 os = OSFactory.builderV3()
    			.endpoint(auth_url)
    			.credentials(userId, password)
    			.scopeToProject(projectIdent,domainIdent)
    			.authenticate();
    			/*OSClientV3 os2 = OSFactory.builderV3()
                .endpoint(auth_url)
                .credentials(userId, password,Identifier.byName(domain))
                .scopeToDomain(Identifier.byName(domain))
                .authenticate();*/
		return os;
	}
  //Testing object data
	public String testObjectCon() throws IOException
    {
				OSClientV3 os = this.connectionObject();
				//Get Containers (just one in our case)
    			List<? extends SwiftContainer> containers = os.objectStorage().containers().list();
    			//Get iterator just in case we have more containers
    			Iterator<? extends SwiftContainer> it = containers.iterator();
    			String containerName = it.next().getName();
    			
    			//Map<String, String> md = os.objectStorage().containers().getMetadata(containerName);
    			List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName);
    			
    			System.out.println("Container Name: "+containerName);
    			System.out.println("Objects in container: "+containers.get(0).getObjectCount());
    			System.out.println("Car object: " + objs.get(1).getName());
    			System.out.println(ObjectLocation.create(objs.get(1).getContainerName(), objs.get(1).getName()).getURI());
    			
    			//Download picture
    			DLPayload pl = objs.get(1).download();
    			File f = new File("car_1.bmp");
    	        OutputStream os1 = new FileOutputStream(f);
    			byte[] buf = new byte[1024];
    		    int len;
    		    while ((len = pl.getInputStream().read(buf)) > 0) 
    		    {
    		    	os1.write(buf, 0, len);
    		    }
    		    BufferedImage image = ImageIO.read(f);
    		    JLabel label = new JLabel(new ImageIcon(image));
    	        JFrame jF = new JFrame();
    	        jF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	        jF.getContentPane().add(label);
    	        jF.pack();
    	        jF.setLocation(200,200);
    	        jF.setVisible(true);
    		    
    			os1.close();
    			return ObjectLocation.create(objs.get(1).getContainerName(), objs.get(1).getName()).getURI();
    }
    public static void main(String [] args) throws IOException {
		CarAPI api = new CarAPI();
		api.testObjectCon();
	}

}
>>>>>>> d848415ae645189faa7b96ae7389beec53250b75
