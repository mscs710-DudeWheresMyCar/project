package application;



import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import application.rest.CsvDataAPI;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
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


@RestController
public class Model {

    static Instances data=null;
    static Classifier NaiveBayesClassifier=null;
    static Classifier RandomForestClassifier=null;
    static Classifier J48Classifier=null;
    static Classifier DecisionTableClassifier=null;
    static Classifier MultilayerPerceptronClassifier=null;
    
    Gson gson = new Gson();
    


    @RequestMapping("/model")
    public String photoCarApi(){
        String result = "";
        buildClassifiers("cars.csv");
        result = ""+checkAccuracy(NaiveBayesClassifier,10);
        boolean boolResult =  isPhotoACar(NaiveBayesClassifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10);
        result += boolResult;

        return result;
    }
    public static void main(String args[])
    {

        buildClassifiers("cars.csv");


        // let's check their accuracies
        System.out.print("NaiveBayes - ");
        System.out.println(checkAccuracy(new NaiveBayes(),10)+"%");

        System.out.print("RandomForest - ");
        System.out.println(checkAccuracy(new RandomForest(),10)+"%");

        /*

        System.out.print("J48 - ");
        System.out.println(checkAccuracy(new J48(),10));

        System.out.print("DecisionTable - ");
        System.out.println(checkAccuracy(new DecisionTable(),10)+"%");

        System.out.print("MultilayerPerceptron - ");
        System.out.println(checkAccuracy(new MultilayerPerceptron(),10)+"%");

        */

        System.out.println("Is https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg a car? ");
        if(isPhotoACar(NaiveBayesClassifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10))
                System.out.println("Naivebayes says yes");
        else
            System.out.println("Naivebayes says no");

        if(isPhotoACar(RandomForestClassifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10))
            System.out.println("RandomForest says yes");
        else
             System.out.println("RandomForest says no");

/*
        if(isPhotoACar(J48Classifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10))
            System.out.println("J48 says yes");
        else
            System.out.println("J48 says no");
        if(isPhotoACar(DecisionTableClassifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10))
            System.out.println("DecisionTable says yes");
        else
            System.out.println("DecisionTable says no");

        if(isPhotoACar(MultilayerPerceptronClassifier,"https://dal.objectstorage.open.softlayer.com/v1/AUTH_d80c340568a44039847b6e7887bbdd93/DefaultProjectthomasginader1maristedu/00010.jpg",10))
            System.out.println("MultilayerPerceptron says yes");
        else
            System.out.println("MultilayerPerceptron says no");
            */

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
        System.out.println(result);
        return result==1;

    }
    public static double checkAccuracy(Classifier classifier,int sections)
    {
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


    public static void buildClassifiers(String sourceCSV)
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
            System.out.println("Building NaiveBayes");
            NaiveBayesClassifier=new NaiveBayes();
            NaiveBayesClassifier.buildClassifier(data);
            System.out.println("Building RandomForest");
            RandomForestClassifier=new RandomForest();
            RandomForestClassifier.buildClassifier(data);
            /*
            System.out.println("Building J48");
            J48Classifier=new J48();
            J48Classifier.buildClassifier(data);
            System.out.println("Building DecisionTable");
            DecisionTableClassifier=new DecisionTable();
            DecisionTableClassifier.buildClassifier(data);
            System.out.println("Building MultilayerPerceptron");
            MultilayerPerceptronClassifier=new MultilayerPerceptron();
            MultilayerPerceptronClassifier.buildClassifier(data);
            */


        }
        catch(Exception e)
        {
            System.out.println("error building classifier");
            e.printStackTrace();
        }

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