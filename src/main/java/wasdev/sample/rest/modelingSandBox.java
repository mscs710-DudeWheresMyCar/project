
package wasdev.sample.rest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.FastVector;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
 
public class modelingSandBox {

	public static Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);
 
		model.buildClassifier(trainingSet);
		evaluation.evaluateModel(model, testingSet);
 
		return evaluation;
	}
 
	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
		//	System.out.println("for "+i+" you guessed "+np.predicted()+" and it was "+np.actual());
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
 
		return 100 * correct / predictions.size();
	}
 
	public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];
 
		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
	
		}
		
 
		return split;
	}
 
	public static void main(String[] args) throws Exception {
		String filepath="C:\\csv\\cars.csv";

		 DataSource source = new DataSource(filepath);
		 Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		data.randomize(new Random());
	
 
		// Do 10-split cross validation
		Instances[][] split = crossValidationSplit(data, 10);
 
		// Separate split into training and testing arrays
		Instances[] trainingSplits = split[0];
		Instances[] testingSplits = split[1];
 
		NaiveBayes nb = new NaiveBayes();
		
		Classifier models =(Classifier)nb;
		
 
		// Run for each model
		
 
			// Collect every group of predictions for current model in a FastVector
			FastVector predictions = new FastVector();
 
			// For each training-testing split pair, train and test the classifier
			for (int i = 0; i < trainingSplits.length; i++) {
				Evaluation validation = classify(models, trainingSplits[i], testingSplits[i]);
				
				//System.out.println(validation);
				System.out.println("validating "+(i+1)+" of "+trainingSplits.length);
 
				predictions.appendElements(validation.predictions());
 
				// Uncomment to see the summary for each training-testing pair.
			//	System.out.println(models.toString());
			}
 
			// Calculate overall accuracy of current classifier on all splits
			double accuracy = calculateAccuracy(predictions);

			System.out.println("Accuracy of NaiveBayes model: "+accuracy);
			
		
 
	}
}