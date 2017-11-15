package application;



import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import application.rest.CsvDataAPI;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.Loader;


@RestController
public class Model {

    static Instances data=null;
    Gson gson = new Gson();


    @RequestMapping("/model")
    public String photoCarApi(@RequestParam(value="url") String photoUrl){
        String result = "";
        Classifier classifier = buildClassifier("cars.csv");
        result = testAccuracy(classifier,10);
        boolean boolResult =  isPhotoACar(classifier,photoUrl,10);
        result += boolResult;

        return result;
    }
    public static void main(String args[])
    {

        Classifier classifier = buildClassifier("cars.csv");
        testAccuracy(classifier,10);
        boolean result =  isPhotoACar(classifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10);




        System.out.println(result);
    }
   /* public static void main(String args[])
    {

        Classifier classifier= buildClassifier("C:\\cars.csv");
        testAccuracy(classifier,10);
        // 	isPhotoACar(classifier,"C:\\images\\r1.jpg",10);
        checkDirectory(classifier,"C:\\\\images\\test\\",10);



        System.out.println("done");
    }*/
    public static void checkDirectory(Classifier classifier,String foldername,int sections)
    {
        final File testDir = new File(foldername);
        for(final File testImage : testDir.listFiles())
        {
            boolean result = isPhotoACar(classifier,testImage.getAbsolutePath(),10);
            System.out.print("we predict that "+testImage+" does");
            if(result)
                System.out.print(" not");
            System.out.println(" contain a car");
        }
    }
    public static boolean isPhotoACar(Classifier classifier,String filename,int sections)
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
            System.exit(0);
        }

        return result==1;

    }
    public static String testAccuracy(Classifier classifier,int sections)
    {
        String result = "";
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
            result = "True Positive: "+truPos + "\n" + "True Negative: "+truNeg + "\n" + "False Positive: "+falPos +
                    "\n" + "False Negative: "+falNeg + "\n" + correct+" correct" + "\n" + incorrect+" incorrect" + "\n"
                    + 100*correct/(correct+incorrect)+"%";

        }
        catch(Exception e)
        {
            System.out.println("Unable to classify");
            e.printStackTrace();
        }

        return result;

    }


    public static Classifier buildClassifier(String sourceCSV)
    {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("csv/cars.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        //	String filepath="C:\\csv\\cars.csv";
        try {
            //DataSource source = new DataSource(br);
            CSVLoader loader = new CSVLoader();
            loader.setSource(is);
            data = loader.getDataSet();
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
        //String values=CsvDataAPI.imageToRow(filename,sections);
        String values= CsvDataAPI.imageToRowFromLinks(filename,sections);
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