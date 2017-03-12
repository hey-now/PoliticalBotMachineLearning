package naive_bayes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class NaiveBayes {
	
	public Map<ClassType, Double> calculateClassProbabilities(Map<List<String>, ClassType>  trainingSet) throws IOException {

		Map<ClassType, Integer> classCounter = new HashMap<ClassType, Integer>();
		
		for (List<String> tweet : trainingSet.keySet()){
			ClassType label = trainingSet.get(tweet);
			
			if (classCounter.containsKey(label)) {
				Integer temp = classCounter.get(label);
				temp++;
				classCounter.put(label, temp);
			}
			else classCounter.put(label, 1);	
		}
		
		Map<ClassType, Double> classProbabilities = new HashMap<ClassType, Double>();
		
		for (ClassType label : classCounter.keySet()){
			classProbabilities.put(label, (double)(classCounter.get(label) / (double)trainingSet.size()));
		}
		
		return classProbabilities;
	}
	
	
	public Map<String, Map<ClassType, Double>> calculateSmoothedLogProbs(Map<List<String>, ClassType> trainingSet)
			throws IOException {
		
	    Map<String, Map<ClassType, Integer>> wordCounter = new HashMap<String, Map<ClassType, Integer>>();
		Map<ClassType,Integer> classCounter = new HashMap<ClassType,Integer>();
		    
			
		for (ClassType ClassType : trainingSet.values()){
			classCounter.put(ClassType,0);
		}

		for (List<String> tweet : trainingSet.keySet()){
				
		//	List<List<String>> data = Markov.loadFile(reviewPath);
			ClassType label = trainingSet.get(tweet);
			
		//	for (List<String> words : data){
				for (String word : tweet) {
					if (wordCounter.containsKey(word)) {
						if (wordCounter.get(word).containsKey(label)){
							Integer temp = wordCounter.get(word).get(label);					
							temp++;
							wordCounter.get(word).put(label, temp);
						}
						else wordCounter.get(word).put(label, 1);	

					}
					else {
						Map<ClassType, Integer> labelMap = new HashMap<ClassType, Integer>();
						
						labelMap.put(label,1);
						wordCounter.put(word, labelMap);				
					}					
						Integer temp = classCounter.get(label);
						temp++;
						classCounter.put(label, temp);
				}		
			
		}

		Map<String, Map<ClassType, Double>> SmoothedLogProbs = new HashMap<String, Map<ClassType, Double>>();

		
		for (String word : wordCounter.keySet()){
				
			Map<ClassType, Double> probabilities = new HashMap<ClassType, Double>();
				
			for (ClassType ClassType : trainingSet.values()){
				if (wordCounter.get(word).containsKey(ClassType))
					probabilities.put(ClassType, Math.log((double)(wordCounter.get(word).get(ClassType)+1)/ ((double)classCounter.get(ClassType)+ wordCounter.size())));
				else probabilities.put(ClassType, Math.log(1/ ((double)classCounter.get(ClassType)+ wordCounter.size())));
			}	
				
			SmoothedLogProbs.put(word, probabilities);
		}
			
	
		return SmoothedLogProbs;
		
	}


	public Map<List<String>, ClassType> naiveBayes(Set<List<String>> testSet, Map<String, Map<ClassType, Double>> tokenLogProbs,
			Map<ClassType, Double> classProbabilities) throws IOException {
		
		Map<List<String>, ClassType> results = new HashMap<List<String>, ClassType>();
		
		for (List<String> words : testSet){
			
			Map<ClassType, Double> NB = new HashMap<ClassType, Double>();
			
			for (ClassType label : classProbabilities.keySet() ){
				Double wordProb = 0.0;
				for (String word : words) {
					if (tokenLogProbs.containsKey(word) && tokenLogProbs.get(word).containsKey(label))
						wordProb += tokenLogProbs.get(word).get(label);
				}
				
				NB.put(label, Math.log10(classProbabilities.get(label)) + wordProb);
			}
			
			ClassType prediction = (NB.get(ClassType.TRUMP) > NB.get(ClassType.OBAMA))?ClassType.TRUMP:ClassType.OBAMA;
			
			
			results.put(words, prediction);
			
			//System.out.println(words.toString() + " " + prediction);
			
		}
				
		return results;
	}
}
