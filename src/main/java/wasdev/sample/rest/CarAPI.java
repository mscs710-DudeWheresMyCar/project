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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.storage.object.SwiftAccount;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.openstack.OSFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import wasdev.sample.Visitor;
import wasdev.sample.store.VisitorStore;
import wasdev.sample.store.VisitorStoreFactory;

import org.openstack4j.api.OSClient.OSClientV3;


@ApplicationPath("api")
@Path("/cars")
public class CarAPI extends Application {
	
	//Our database store
	VisitorStore store = VisitorStoreFactory.getInstance();

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
   */
    @GET
    @Path("/")
    @Produces({"application/json"})
    public String getVisitors() {		
		if (store == null) {
			return "[]";
		}
		
		List<String> names = new ArrayList<String>();
		for (Visitor doc : store.getAll()) {
			String name = doc.getName();
			if (name != null){
				names.add(name);
			}
		}
		this.testObjectCon();
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
    public String newToDo(Visitor visitor) {
      if(store == null) {
    	  return String.format("Hello %s!", visitor.getName());
      }
      store.persist(visitor);
      return String.format("Hello %s! I've added you to the database.", visitor.getName());
    }

  //Testing object data
    public <T> void testObjectCon()
    {
    	String arr =   "{\"Object-Storage\": [{\"credentials\": {\"auth_url\": \"https://identity.open.softlayer.com\",";
    			arr += "\"project\": \"object_storage_850aaf2d_73ea_4b8d_ba87_f3629bcb7c68\",";
    		    arr += "\"projectId\": \"d80c340568a44039847b6e7887bbdd93\",";
    		    arr += "\"region\": \"dallas\",";
    		    arr += " \"userId\": \"231a8503e7354c3280fbdd9617262b39\",";
    		    arr +=  "\"username\": \"admin_0555aeb40e0677c6f61b1d2161fd962a4cf8d1e3\",";
    		    arr +=  "\"password\": \"BMh#ale-Y7_0S_Tk\",";
    		    arr +=  "\"domainId\": \"9c76df66cd264a70b8a2a295c60ad7e7\",";
    		    arr +=  "\"domainName\": \"1433205\",";
    		    arr +=  "\"role\": \"admin\"";
    		    arr +=  "},";
    		    arr +=  "\"syslog_drain_url\": null,";
    		    arr +=  "\"volume_mounts\": [],";
    		    arr +=  "\"label\": \"Object-Storage\",";
    		    arr +=  "\"provider\": null,";
    		    arr +=  "\"plan\": \"Free\",";
    		    arr +=  "\"name\": \"DSX-ObjectStorage\",";
    		    arr +=  "\"tags\": [";
    		    arr +=  "\"storage\",";
    		    arr +=  "\"ibm_release\",";
    		    arr +=  "\"ibm_created\",";
    		    arr +=  "\"lite\"";
    		    arr +=  "]";
    		    arr +=	"}";
				arr +=	"]";
				arr += 	"}";
    	
    	
    	//String envApp = System.getenv("VCAP_APPLICATION");
    	//String envServices = System.getenv("VCAP_SERVICES");
    	JsonParser parser = new JsonParser();
    	Object obj = parser.parse(arr);
		JsonObject jsonObject = (JsonObject) obj;
		JsonArray vcapArray = (JsonArray) jsonObject.get("Object-Storage");
		JsonObject vcap = (JsonObject) vcapArray.get(0);
		JsonObject credentials = (JsonObject) vcap.get("credentials");
		String userId = credentials.get("userId").toString().replaceAll("\"", "");
		String password = credentials.get("password").toString().replaceAll("\"", "");
		String auth_url = credentials.get("auth_url").toString().replaceAll("\"", "") + "/v3";
		String domain = credentials.get("domainName").toString().replaceAll("\"", "");
		String project = credentials.get("project").toString().replaceAll("\"", "");
		Identifier domainIdent = Identifier.byName(domain);
		Identifier projectIdent = Identifier.byName(project);
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

    			SwiftAccount account = os.objectStorage().account().get();
    			//System.out.println(account.getTemporaryUrlKey());
    			List<? extends SwiftContainer> containers = os.objectStorage().containers().list();
    			System.out.println("Container Name: "+containers.get(0).getName());
    			System.out.println("Objects in container: "+containers.get(0).getObjectCount());
    			Iterator<? extends SwiftContainer> it = containers.iterator();
    			String containerName = it.next().getName();
    			Map<String, String> md = os.objectStorage().containers().getMetadata(containerName);
    			List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName);
    			System.out.println(objs.get(1).getName());
    }
    public static void main(String [] args) {
		CarAPI api = new CarAPI();
		api.testObjectCon();
	}

}
