package application;



import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import wasdev.sample.rest.CsvDataAPI;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Model {
	
	static Instances data=null;


      public static void main(String args[])
    {

    	Classifier classifier= buildClassifier("C:\\csv\\cars.csv");
    	testAccuracy(classifier,10);
    	//isFileAPhoto(classifier,"C:\\images\\r1.jpg",10);
    	isFileAPhoto(classifier,"C:\\images\\noncars\\cat.31.jpg",10);
    	isFileAPhoto(classifier,"C:\\images\\noncars\\cat.35.jpg",10);
    	isFileAPhoto(classifier,"C:\\images\\noncars\\cat.32.jpg",10);
    	isFileAPhoto(classifier,"C:\\images\\noncars\\cat.39.jpg",10);
    	

    	System.out.println("done");
    }
    public static boolean isFileAPhoto(Classifier classifier,String filename,int sections)
    {
    	Instance ins = instanceFromImage(filename,sections);
    	double result=-1;
    	try {
    		
    	result=classifier.classifyInstance(ins);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		e.getCause();
    		System.exit(0);;
    	}
    	System.out.print(filename+" does");
    	if(result==1)
    	System.out.print(" not");
    	System.out.println(" contain a car");
    	return result==1;	

    }
    public static void testAccuracy(Classifier classifier,int sections)
    {
    	
    	double actual[] = data.attributeToDoubleArray(sections*sections*3);
    	try {
    		int truPos=0;
    		int falPos=0;
    		int truNeg=0;
    		int falNeg=0;
    	
    		for(int photoNum=0;photoNum<actual.length;photoNum++)
    		
    		{
    			double prediction= classifier.classifyInstance(data.get(photoNum));
    	// System.out.println(photoNum+" predicted: "+prediction+" actual: "+actual[photoNum]);
    			if(prediction==1&&actual[photoNum]==1)
    			{
    				truPos++;
    			}
    			if(prediction==1&&actual[photoNum]==0)
    			{
    				falPos++;
    			}
    			if(prediction==0&&actual[photoNum]==1)
    			{
    				falNeg++;
    			}
    			if(prediction==0&&actual[photoNum]==0)
    			{
    				truNeg++;
    			}
    		}
    			
    		System.out.println("True Positive: "+truPos);	
    		System.out.println("True Negative: "+truNeg);	
    		System.out.println("False Positive: "+falPos);	
    		System.out.println("False Negative: "+falNeg);
    		int correct = truPos+truNeg;
    		int incorrect = falNeg+falPos;
    		System.out.println(correct+" correct");
    		System.out.println(incorrect+" incorrect");
    		System.out.println(100*correct/(correct+incorrect)+"%");
    	 
    	}
    	catch(Exception e)
    	{
    		System.out.println("Unable to classify");
    		e.printStackTrace();
    	}
     
       
    }
    

    public static Classifier buildClassifier(String sourceCSV) 
    {
	//	String filepath="C:\\csv\\cars.csv";
    	try {
		 DataSource source = new DataSource(sourceCSV);
		data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
    	NaiveBayes nb = new NaiveBayes();

    	nb.buildClassifier(data);
		Evaluation evaluation = new Evaluation(data);
		 
		evaluation.evaluateModel(nb,data);

    	return nb;
    	}
    	catch(Exception e)
    	{
    		System.out.println("error building classifier");
    		e.printStackTrace();
    	}
    	return null;
    }


   public static Instance instanceFromImage(String filename,int sections)
    {
    	Instance ins = new DenseInstance((sections*sections*3)+1);
    	ins.setDataset(data);
    	String headers="";
    	String values=CsvDataAPI.imageToRow(filename,sections);
    	//build headers based on number of sections
    	
      for(int row=0;row<sections;row++)
        {
            for(int col=0;col<sections;col++)
            {
               headers+="red row "+row+" col "+col+",";
               headers+="green row "+row+" col "+col+",";
               headers+="blue row "+row+" col "+col+",";


            }
        }
        String head[]=headers.split(",");
        
        String val[]=values.split(",");
  
  //      System.out.println("this many attributes"+val.length);
    //    System.out.println(ins.equalHeaders(data.firstInstance()));
        
        
        for(int i=0;i<val.length;i++)
        {
       // 	System.out.println(head[i]+": "+val[i]);
       // System.out.println(Integer.parseInt(val[i]));
       // 	System.out.println(new Attribute(head[i]));
        	
        	//ins.setValue(new Attribute(head[i]),Integer.parseInt(val[i]));
        	ins.setValue(i,Integer.parseInt(val[i]));
        }
        

            
    	return ins;
    }

    
}
