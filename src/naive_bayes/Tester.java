package naive_bayes;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Tester {
	
	static Map<List<String>, ClassType> dataSet = new HashMap<List<String>, ClassType>();

	public static void main(String[] args) throws IOException {

		loadData(Paths.get("data/trump.txt"),ClassType.TRUMP);
		//System.out.println(dataSet.size());
		loadData(Paths.get("data/obama.txt"),ClassType.OBAMA);
		//System.out.println(dataSet.size());

		
		List<Map<List<String>, ClassType>> folds = DataSplit.splitCVStratifiedRandom(dataSet, 0);

		Map<List<String>, ClassType> trainingSet = new HashMap<List<String>, ClassType>();
		Map<List<String>, ClassType> validationSet  = new HashMap<List<String>, ClassType>();
				
		NaiveBayes implementationNB = new NaiveBayes();
		
		//put 2 first folds into validation set and the rest to training set (will give 8:2 proportion)
		for (int j=0; j<10; j++){
			if (j==0 || j==1) validationSet.putAll(folds.get(j));
			else  trainingSet.putAll(folds.get(j));
		}
				
		Map<ClassType, Double> classProbabilities = implementationNB.calculateClassProbabilities(trainingSet);

		Map<String, Map<ClassType, Double>> smoothedLogProbs = implementationNB
				.calculateSmoothedLogProbs(trainingSet);
		 //System.out.println("Log probabilities of smoothed classifier:");
		 //System.out.println(smoothedLogProbs);
		 //System.out.println();

		Map<List<String>, ClassType> smoothedNBPredictions = implementationNB.naiveBayes(validationSet.keySet(),
				smoothedLogProbs, classProbabilities);

		
		double smoothedNBAccuracy = calculateAccuracy(validationSet, smoothedNBPredictions);
		System.out.println("Naive Bayes classifier accuracy with smoothing:");
		System.out.println(smoothedNBAccuracy);
		System.out.println();
		
		//Cross-validation
		System.out.println("---------CROSS-VALIDATION-----------");

		double[] results = crossValidate(folds);
		
		System.out.println("Prediction accuracy score average: " + cvAccuracy(results));
		System.out.println("Prediction accuracy score variance: " + cvVariance(results));

	}
	
	public static void loadData(Path sequenceFile, ClassType person) throws IOException{
		List<List<String>> words = new LinkedList<List<String>>();
		
		System.out.println("Loading data from: " + sequenceFile);
		
		
		try (BufferedReader reader = Files.newBufferedReader(sequenceFile, Charset.forName("Cp1252"))) {
			String line = null;
			 
	        while ((line = reader.readLine()) != null) {
	        	//System.out.println(line);
	        	String[] splittedLine = line.split(" ");
	        	List<String> listLine = new LinkedList<String>();
	        	
	        	for (String w : splittedLine){
	        		if (!w.startsWith("http")) listLine.add(w);
	        	}
	        	
	        	if(listLine.size()>0 && listLine.get(0)!="RT")
	        		dataSet.put(listLine,person);
	        }   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
		
	}
	
	public static double calculateAccuracy(Map<List<String>, ClassType> trueClasses, Map<List<String>, ClassType> predictedClasses) {
		double c = 0; //correct counter
		double i = 0; //incorrect counter
		
		for (List<String> tweet : trueClasses.keySet())
		{
			if (predictedClasses.get(tweet) != null) {
				if (trueClasses.get(tweet) == predictedClasses.get(tweet))
					c++;
				else {
					i++;					
				}
			}		
		}
		return (double)(c /(c + i));
	}
	

	public static double[] crossValidate(List<Map<List<String>, ClassType>> folds) throws IOException {
		double[] results = new double[10];
		NaiveBayes implementation = new NaiveBayes();

		for (int i=0; i<10; i++){
			Map<List<String>, ClassType> trainingSet = new HashMap<List<String>, ClassType>();
			Map<List<String>, ClassType> validationSet  = new HashMap<List<String>, ClassType>();
					
			NaiveBayes implementationNB = new NaiveBayes();
			
			for (int j=0; j<10; j++){
				if (j==i) validationSet.putAll(folds.get(j));
				else  trainingSet.putAll(folds.get(j));
			}

			Map<ClassType, Double> classProbabilities = implementationNB.calculateClassProbabilities(trainingSet);

			Map<String, Map<ClassType, Double>> smoothedLogProbs = implementationNB.calculateSmoothedLogProbs(trainingSet);

			Map<List<String>, ClassType> smoothedNBPredictions = implementationNB.naiveBayes(validationSet.keySet(),
					smoothedLogProbs, classProbabilities);

			double smoothedNBAccuracy = calculateAccuracy(validationSet, smoothedNBPredictions);
			System.out.println("Naive Bayes classifier accuracy with fold "+ i + " used for testing: "+smoothedNBAccuracy);

			results[i]=smoothedNBAccuracy ;
		}
		
		return results;
	}
	
	public static double cvAccuracy(double[] scores) {
		double sum = 0;
		for (double s : scores) sum+=s;
		
		return sum/(scores.length);
	}

	public static double cvVariance(double[] scores) {
		double avg = cvAccuracy(scores);
		double sum = 0;
		int n = scores.length;
		
		
		for (double s : scores) {
			sum+= (s-avg)*(s-avg);
		}
		
		return sum/n;
	}
}
