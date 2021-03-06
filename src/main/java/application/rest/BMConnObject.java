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

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.identity.v3.Endpoint;
import org.openstack4j.model.identity.v3.Service;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ObjectLocation;
import org.openstack4j.openstack.OSFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.types.Facing;

public class BMConnObject
{
    /************Fields*************/
    final String USERID = "userId";
    final String PASSWORD = "password";
    final String AUTHURL = "auth_url";
    final String DOMAINNAME = "domainName";
    final String PROJECT = "project";
    final String OBJECTURI = "https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/";    

    /*
     * @TODO: use logger4j
     *
     * static
     *{
     *   Logger rootLogger = Logger.getRootLogger();
     *   rootLogger.setLevel(Level.INFO);
     *   rootLogger.addAppender((Appender) new ConsoleAppender(
     *              new PatternLayout("%-6r [%p] %c - %m%n")));
     *}
    */
    /************Constructors*******/
    public BMConnObject(){}
    /************Methods************/

    /**
     * getAuthString
     *
     * Get the authentication string in JSON format
     *
     * @return: String authStr
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
     * @return: Map Object map
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

    /**
     * connectionObject
     *
     * Creates the connection object
     *
     * @return: OSClientV3 os connection object*/
    public OSClientV3 connectionObject()
    {
        //We can fix quotes in the string like this
        //String project = credentials.get("project").toString().replaceAll("\"", "");

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
        OSClientV3 os = null;
        try
        {
            os = OSFactory.builderV3()
                    .endpoint(auth_url)
                    .credentials(userId, password)
                    .scopeToProject(projectIdent,domainIdent)
                    .authenticate();
        }
        catch(Exception e)
        {
            System.out.println("Connection Error");
        }
        /*OSClientV3 os2 = OSFactory.builderV3()
          .endpoint(auth_url)
          .credentials(userId, password,Identifier.byName(domain))
          .scopeToDomain(Identifier.byName(domain))
          .authenticate();*/
        return os;
    }

    /**
     * processImage
     *
     * This function saves the image to the data object
     *
     * @param: imageByteArr
     * @param: fileName
     *
     * @return: String
     */
    public String processImage(byte [] imageByteArr, String fileName){
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByteArr);
        //Check for null
        OSClientV3 os = null;
        try{
            os = this.connectionObject();
        }catch(NullPointerException ex){
            System.out.println("Null");
        }
        List<? extends SwiftContainer> containers = os.objectStorage().containers().list();
        Iterator<? extends SwiftContainer> it = containers.iterator();
        //If Mike is reading this comment this is a clear case of "Technical Debt" :) gg
        //I know that we only have one container that is why I am getting next from Iterator (Hey it works!)
        String containerName = it.next().getName();
        String etag = os.objectStorage().objects().put(containerName, fileName, Payloads.create(bis));
        //@TODO: Check MD5 in the header if object is created successfully
    	return OBJECTURI + fileName;    
    }

    /**
     * testObjectCon
     *
     * Since the connection to data Object can be tricky we need this function for testing connection only.
     *
     */
    public String testObjectCon() throws IOException
    {
        //Check for null
        OSClientV3 os = null;
        try{
            os = this.connectionObject();
        }catch(NullPointerException ex){
            System.out.println("Null");
        }

        //Get Containers (just one in our case)
        List<? extends SwiftContainer> containers = os.objectStorage().containers().list();
        //Get iterator just in case we have more containers
        Iterator<? extends SwiftContainer> it = containers.iterator();
        String containerName = it.next().getName();
        String objectUrl="";

        //Map<String, String> md = os.objectStorage().containers().getMetadata(containerName);
        List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName);

        for (Service s : os.getToken().getCatalog()) {
            if (s.getName().equals("swift")) {
                for (Endpoint e : s.getEndpoints()) {
                    if (e.getRegion().equals("dallas") && e.getIface().equals(Facing.PUBLIC)) {
                        objectUrl = e.getUrl().toString();
                        System.out.println(objectUrl);
                    }
                }
            }
        }

        System.out.println("Container Name: "+containerName);
        System.out.println("Objects in container: "+containers.get(0).getObjectCount());
        System.out.println("Car object: " + objs.get(1).getName());
        System.out.println(ObjectLocation.create(objs.get(1).getContainerName(), objs.get(1).getName()).getURI());

        File fileImg = null;
        this.createImageObject(os, containerName, fileImg);// gg uncomment to test
        /*
        //We can download picture too instead of using references to the data Object. (Not used for this version of the software)
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
        */
        return objectUrl + "/" + containerName + "/" + objs.get(1).getName();
    }

    /**
     * createImageObject
     *
     * This function creates a local file first than saves it to the data object
     * This function is uset together with testObjectCon
     *
     * @param: OSClientV3 os
     * @param: String containerName 
     * @param: File fileImg 
     *
     */
    public void createImageObject(OSClientV3 os,String containerName, File fileImg)
    {
        fileImg = new File("C:\\Users\\genti\\Downloads\\cars\\cars\\08143.jpg");
        String objectName = fileImg.getName();
        //System.out.println(fileImg.getName());
        //String etag = os.objectStorage().objects().put(containerName, objectName, Payloads.create(fileImg));
        //System.out.println(etag);
    }
}
