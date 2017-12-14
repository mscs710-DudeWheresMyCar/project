package application;



import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import application.rest.BMConnObject;
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
    /**
     * REST takes an incoming URL and calls isPhotoACar passing that url

     * @param photoUrl    The URL of a photo to be classified
     * @return            A string of classifier values to be parsed by front-end
     */
    @RequestMapping("/classify")
    public String photoClassifyApi(@RequestParam(value="url") String photoUrl)
    {
        return isPhotoACar(photoUrl);
    }


    /**
     * Used for testing purposes only, classifies one hardcoded image
e
     */
    public static void main(String args[])
    {
       Model m = new Model();
       m.buildClassifiers("cars.csv");
       System.out.println(m.isPhotoACar("https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00059.jpg"));



    }
    /**
     *isPhotoACar
     *
     * This method assumes use of 10 sections and the 5 static classifiers
     * It builds an Instance of the input image
     * It then calls specific isPhotoACar methods for each of these classifiers
     *
     * @param filename the url of the photo to be classified
     * @return String, The location of the output jpeg, followed by comma seperates classifier,returnvalue
     */
    public String isPhotoACar(String filename)
    {
        if (filename==null)
            return "Error couldn't read input image";
        Instance ins=instanceFromImage(filename,10);
        String output="";
        int stackTrue=0;
        int stackFalse=0;
        double nbRes=isPhotoACar(NaiveBayesClassifier,ins);
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



        double rfRes=isPhotoACar(RandomForestClassifier,ins);
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



        double j48Res=isPhotoACar(J48Classifier,ins);
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




        double dtRes=isPhotoACar(DecisionTableClassifier,ins);
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



        double loRes=isPhotoACar(LogisticClassifier,ins);
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

        String outputfilename=CsvDataAPI.addPixelizedToFileName(filename);
        return outputfilename+","+output;



    }
    /**
     *isPhotoACar
     *
     * This method to be called by more generic isPhotoACar for one classifier and instance

     *
     * @param classifier  the classifier for which to evaluate the instance
     * @param ins the instance representing one row of data (the value from one specific car)
     *
     * @return double, -1= unavailable, -3= null instance,0=true, 1=false
     */
    private double isPhotoACar(Classifier classifier,Instance ins)
    {
        if(classifier==null)
        {
            return -1;
        }


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
    /**
     *checkAccuracy
     *
     * This method calls checkAccuracy for each of the 5 static classifiers


     * @return String, text indicating the accuracy of each classifier
     */
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
    /**
     * Uses cross validation to determine the accuracy of a particular classifier
     * against the static instances data

     * @param classifier  The classifier to be validated
     * @param sections    The dimension of square grid to interpret photo
     * @return            accuracy value between 0-100
     */
    private double checkAccuracy(Classifier classifier,int sections)
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

    /**
     * Rebuilds classifiers based on an inputCSV
     * @param sourceCSV   a CSV file used to retrain the classifiers
     * @return            A string indicating whether or not models were successfully rebuilt
     */
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


    /**
     *instanceFromImage
     *
     * This method creates an Instance of the input image appending it to the data Instances

     *
     * @param filename the filename or url pf the photo for which to classify
     * @param sections the number of sections to divide each dimension
     *
     * @return double, -1= unavailable, -3= null instance,0=true, 1=false
     */
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
