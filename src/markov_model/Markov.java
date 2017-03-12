package markov_model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Markov {
	protected Map<String, Map<String, Double>> transitions;
	protected Map<String, Double> initialProbs;
	
	public Markov(Path sequenceFile) throws IOException{
		List<List<String>> data = loadFile(sequenceFile);
		
		loadInitialProb(data);
		loadTransitionProb(data);
		
		System.out.println(initialProbs);
	}
	
	
	/**
	 * Loads a file with a text
	 */
	public static List<List<String>> loadFile(Path sequenceFile) throws IOException {
		List<List<String>> words = new LinkedList<List<String>>();
		
		System.out.println(sequenceFile);
		
		
		try (BufferedReader reader = Files.newBufferedReader(sequenceFile, Charset.forName("Cp1252"))) {
        	System.out.println("abc");

			String line = null;
			 
	        while ((line = reader.readLine()) != null) {
	        	//System.out.println(line);
	        	String[] splittedLine = line.split(" ");
	        	List<String> listLine = new LinkedList();
	        	
	        	for (String w : splittedLine){
	        		if (!w.startsWith("http")) listLine.add(w);
	        	}
	        	
	        	if(listLine.size()>0 && listLine.get(0)!="RT")
	        		words.add(listLine);
	        }   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
		return words;
	}

	
	
	
	protected void loadInitialProb(List<List<String>> seq){
		initialProbs = new HashMap<String, Double>();
		Map<String, Integer> stateBeginCounter = new HashMap<String,  Integer>();

		for(List<String> dataSet : seq){
			String beginState = dataSet.get(0);
			
			if(stateBeginCounter.containsKey(beginState)){				
				Integer temp = stateBeginCounter.get(beginState);					
				temp++;
				stateBeginCounter.put(beginState, temp);				
			}
			else{				
				stateBeginCounter.put(beginState, 1);
			}

		}
		
		for (String type :  stateBeginCounter.keySet()){
			if (stateBeginCounter.containsKey(type))
				initialProbs.put(type, stateBeginCounter.get(type)/ (double)seq.size());
		}
		
	}
	
	
	protected void loadTransitionProb(List<List<String>> seq){

		transitions = new HashMap<String, Map<String, Double>>(); 
		Map<String, Map<String, Integer>> transitionCounter = new HashMap<String, Map<String, Integer>>();
		int allTransitions = 0;
		Map<String, Integer> stateCounter = new HashMap<String,  Integer>();
		
		for(List<String> dataSet : seq){
			String previous = null;
			String beginState = dataSet.get(0);

			
			for(String current : dataSet){
				if (previous != null){
					if(transitionCounter.containsKey(previous)){
						if(transitionCounter.get(previous).containsKey(current)){
							Integer temp = transitionCounter.get(previous).get(current);					
							temp++;
							transitionCounter.get(previous).put(current, temp);
						}
						else{
							transitionCounter.get(previous).put(current,1);
						}
						
					
					}
					else{
						Map<String, Integer> transitionsTo = new HashMap<String, Integer>();
						transitionsTo.put(current, 1);
						transitionCounter.put(previous, transitionsTo);
						
					}
					
					Integer temp = stateCounter.getOrDefault(previous, 0);				
					temp++;
					stateCounter.put(previous, temp);

					allTransitions++;
				}
				previous = current;
			}
			
			
			//adding the transition to the final state
			//final state represented as "finalState"

			if(transitionCounter.containsKey(previous)){
				Integer temp = transitionCounter.get(previous).getOrDefault(null, 0);				
				temp++;
				transitionCounter.get(previous).put("FinalState", temp);	
			}
			else{
				Map<String, Integer> transitionsTo = new HashMap<String, Integer>();
				transitionsTo.put("FinalState", 1);
				transitionCounter.put(previous, transitionsTo);
				
			}
			
			Integer temp = stateCounter.getOrDefault(previous, 0);				
			temp++;
			stateCounter.put(previous, temp);

			allTransitions++;
		}

		
				
		for (String previous :  transitionCounter.keySet()){	
			Map<String, Double> transitionsTo = new HashMap<String, Double>();

			for (String current :  transitionCounter.get(previous).keySet()){
				if(transitionCounter.containsKey(previous) && transitionCounter.get(previous).containsKey(current))
					transitionsTo.put(current, (double)transitionCounter.get(previous).get(current) / (double)stateCounter.get(previous));	
			}	
			transitions.put(previous, transitionsTo);
		}		
		
	}
	
	
	
}

	
