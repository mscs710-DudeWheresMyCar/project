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
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.J48;
import weka.classifiers.rules.DecisionTable;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.Loader;
import weka.classifiers.evaluation.NominalPrediction;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class Model {


    static Instances data=null;
    static Classifier NaiveBayesClassifier=null;
    static Classifier RandomForestClassifier=null;
    static Classifier J48Classifier=null;
    static Classifier DecisionTableClassifier=null;
    static Classifier LogisticClassifier=null;

    
    Gson gson = new Gson();

    @RequestMapping("/classify")
    public String photoClassifyApi(@RequestParam(value="url") String photoUrl)
    {
        return isPhotoACar(photoUrl);
    }

    @RequestMapping("/classifytest")
    public String photoClassifyApiTest() {
        String output="";
        String url;
        for(int x=10;x<100;x++)
        {
            url="https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/000"+x+".jpg";
            output+="<br><img src=\""+url+"\"><br>";
            output+=isPhotoACar(url).replaceAll("\n","<br>");
        }

        return output;

    }



    @RequestMapping("/model")
    public String photoCarApi(@RequestParam(value="url") String photoUrl){
        String result = "";

        buildClassifiers("cars.csv");
        result = ""+checkAccuracy(NaiveBayesClassifier,10);
        double boolResult =  isPhotoACar(NaiveBayesClassifier,photoUrl,10);
        result += boolResult==1;


        return result;
    }
    public static void main(String args[])
    {
       Model m = new Model();
       m.buildClassifiers("cars.csv");
       System.out.println(m.isPhotoACar("https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00059.jpg"));



    }
    public String isPhotoACar(String filename)
    {
        if (filename==null)
            return "Error couldn't read input image";
        String output="";
        int stackTrue=0;
        int stackFalse=0;
        double nbRes=isPhotoACar(NaiveBayesClassifier,filename,10);
        if(nbRes==-1)
            output+="NaiveBayes,Unavailable,";
        else if(nbRes==0) {
            output += "NaiveBayes,True,";
            stackTrue++;
        }
        else if(nbRes==1) {
            output += "NaiveBayes,False,";
            stackFalse++;
        }
        else
            output+="NaiveBayes,Error,";



        double rfRes=isPhotoACar(RandomForestClassifier,filename,10);
        if(rfRes==-1)
            output+="RandomForest,Unavailable,";
        else if(rfRes==0) {
            output += "RandomForest,True,";
            stackTrue++;
        }
        else if(rfRes==1) {
            output += "RandomForest,False,";
            stackFalse++;
        }
        else
            output+="RandomForest,Error,";



        double j48Res=isPhotoACar(J48Classifier,filename,10);
        if(j48Res==-1)
            output+="J48,Unavailable,";
        else if(j48Res==0) {
            output += "J48,True,";
            stackTrue++;
        }
        else if(j48Res==1) {
            output += "J48,False,";
            stackFalse++;
        }
        else
            output+="J48,Error,";



        double dtRes=isPhotoACar(DecisionTableClassifier,filename,10);
        if(dtRes==-1)
            output+="DecisionTable,Unavailable,";
        else if(dtRes==0) {
            output += "DecisionTable,True,";
            stackTrue++;
        }
        else if(dtRes==1) {
            output += "DecisionTable,False,";
            stackFalse++;
        }
        else
            output+="DecisionTable,Error,";



        double loRes=isPhotoACar(LogisticClassifier,filename,10);
        if(loRes==-1)
            output+="Logistic,Unavailable,";
        else if(loRes==0) {
            output += "Logistic,True,";
            stackTrue++;
        }
        else if(loRes==1) {
            output += "Logistic,False,";
            stackFalse++;
        }
        else
            output+="Logistic,Error,";


        if(stackTrue+stackFalse==0)
            output+="Stacking,None";
        else if(stackTrue==stackFalse)
            output+="Stacking,True(tie)";
        else if(stackTrue>stackFalse)
            output+="Stacking,True";
        else
            output+="Stacking,False";
        return output;



    }

    public double isPhotoACar(Classifier classifier,String filename,int sections)
    {
        if(classifier==null)
        {
            return -1;
        }

        Instance ins = instanceFromImage(filename,sections);
        double result=-1;
        try {
            if (ins==null) {
                System.out.println("!!!!no instance for image compare, serious error!!!!");
                return -3;
            }
            result=classifier.classifyInstance(ins);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            e.getCause();
            System.exit(0);
        }
       // System.out.println(result);
        return result;

    }

    @RequestMapping("/accuracy")
    public String checkAccuracy()
    {
        try {


            String output = "";
            output += "NaiveBayes - ";
            if (NaiveBayesClassifier == null)
                output += "model not yet initialized. \n ";
            else
                output += checkAccuracy(NaiveBayes.makeCopy(NaiveBayesClassifier), 10) + "%. \n ";
            output += "RandomForest - ";
            if (RandomForestClassifier == null)
                output += "model not yet initialized. \n ";
            else
                output += checkAccuracy(RandomForest.makeCopy(RandomForestClassifier), 10) + "%. \n ";
            output += "J48 - ";
            if (J48Classifier == null)
                output += "model not yet initialized. \n ";
            else
                output += checkAccuracy(J48.makeCopy(J48Classifier), 10) + "%. \n ";
            output += "DecisionTable - ";
            if (DecisionTableClassifier == null)
                output += "model not yet initialized. \n ";
            else
                output += checkAccuracy(DecisionTable.makeCopy(DecisionTableClassifier), 10) + "%. \n ";
            output += "Logistic - ";
            if (LogisticClassifier == null)
                output += "model not yet initialized. \n ";
            else
                output += checkAccuracy(Logistic.makeCopy(LogisticClassifier), 10) + "%. \n ";


            return output.replaceAll("\n", "<br>");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return "Error unable to check accuracy at this time";
        }

    }

    public double checkAccuracy(Classifier classifier,int sections)
    {
        if (classifier==null)
            return -1;

        try {
            Instances[] trainingSplits = new Instances[10];
            Instances[] testingSplits = new Instances[10];
            //relies on static Instances Data which was set previously and should remain constant
            for (int i = 0; i < 10; i++) {
                trainingSplits[i] = data.trainCV(10, i);
                testingSplits[i] = data.testCV(10, i);

            }
            FastVector predictions = new FastVector();
            for (int i = 0; i < trainingSplits.length; i++) {
                // Evaluation validation = classify(classifier, trainingSplits[i], testingSplits[i]);

                Evaluation evaluation = new Evaluation(trainingSplits[i]);
                classifier.buildClassifier(trainingSplits[i]);
                evaluation.evaluateModel(classifier, testingSplits[i]);
                predictions.appendElements(evaluation.predictions());
            }


            // double accuracy = calculateAccuracy(predictions);
            int correct = 0;
            for (int i = 0; i < predictions.size(); i++) {
                NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
                //	System.out.println("for "+i+" you guessed "+np.predicted()+" and it was "+np.actual());
                if (np.predicted() == np.actual())
                    correct++;
             //   System.out.println(correct + " correct of");
            }


            return correct*100/data.size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Couldn't check accuracy");
            return -1;
        }
    }

    @RequestMapping("/rebuild")
    public static String buildClassifiers(String sourceCSV)
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
            //NaiveBayes nb = new NaiveBayes();
           // RandomForest nb = new RandomForest();


            //build the classifiers against data
            //use temp variable to not overwrite the usable static classifier until available
            System.out.println("Building NaiveBayes");
            NaiveBayes NaiveBayesClassifierTemp=new NaiveBayes();
            NaiveBayesClassifierTemp.buildClassifier(data);
            NaiveBayesClassifier=NaiveBayesClassifierTemp;

            System.out.println("Building RandomForest");
            RandomForest RandomForestClassifierTemp=new RandomForest();
            RandomForestClassifierTemp.buildClassifier(data);
            RandomForestClassifier=RandomForestClassifierTemp;

            System.out.println("Building J48");
            J48 J48ClassifierTemp=new J48();
            J48ClassifierTemp.buildClassifier(data);
            J48Classifier=J48ClassifierTemp;

            System.out.println("Building DecisionTable");
            DecisionTable DecisionTableClassifierTemp=new DecisionTable();
            DecisionTableClassifierTemp.buildClassifier(data);
            DecisionTableClassifier=DecisionTableClassifierTemp;

            System.out.println("Building Logistic");
            Logistic LogisticClassifierTemp=new Logistic();
            LogisticClassifierTemp.buildClassifier(data);
            LogisticClassifier=LogisticClassifierTemp;
            System.out.println("All classifers built");
            return "All models built";



        }
        catch(Exception e)
        {
            System.out.println("error building classifier");
            e.printStackTrace();
            System.out.println("Error building models");
            return "Error building models";
        }

    }


    public Instance instanceFromImage(String filename,int sections)
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

            ins.setValue(i,Integer.parseInt(val[i]));
        }



        return ins;
    }


}
