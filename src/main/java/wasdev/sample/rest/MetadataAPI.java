package wasdev.sample.rest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.jmatio.io.*;
import com.jmatio.types.*;
public class MetadataAPI {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		MatFileReader matfilereader = new MatFileReader("src/main/metadata/cars_meta.mat");
		Map<String, MLArray> mp = matfilereader.getContent();
		Iterator<?> it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		//MLArray mlArrayRetrieved = matfilereader.getMLArray("cell");
		MLArray mlArray =  matfilereader.getMLArray("class_names");
		System.out.println(mlArray.getSize());
	    System.out.println(mlArray.contentToString());
	    //System.out.println(mlArrayRetrieved.contentToString());
		//System.out.println(it.next());
		//System.out.println(st.getDescription());
		//System.out.println(al.indexOf(0));
	    //MLArray mlArrayRetrieved = matfilereader.getMLArray("data");
	    //System.out.println(mlArrayRetrieved);
	    //System.out.println(mlArrayRetrieved.contentToString());
		//String folderName = MetadataAPI.class.getName();
		//System.out.println(folderName);
	}

}
