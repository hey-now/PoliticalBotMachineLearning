package markov_model;

import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextGenerator {

	static final Path dataFile = Paths.get("data/korwin.txt");


	public static void main(String[] args) throws IOException {
		
		//Donald Trump
		Markov modelTrump = new Markov(Paths.get("data/trump.txt"));		
		
		try {			
			BufferedWriter writer;
			Path results = Paths.get("data/trump_results.txt");
			if (!Files.exists(results))
				writer = Files.newBufferedWriter(results);
			else
				writer = Files.newBufferedWriter(results, StandardOpenOption.APPEND);
			
			for(int i=0; i<100; i++){
    			writer.write(generateLine(modelTrump));
    			writer.newLine();
    		}   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
		
		
		//Barack Obama
		Markov modelObama = new Markov(Paths.get("data/obama.txt"));
		
		try {			
			BufferedWriter writer;
			Path results = Paths.get("data/obama_results.txt");
			if (!Files.exists(results))
				writer = Files.newBufferedWriter(results);
			else
				writer = Files.newBufferedWriter(results, StandardOpenOption.APPEND);
			
			for(int i=0; i<100; i++){
    			writer.write(generateLine(modelObama));
    			writer.newLine();
    		}   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
		
		
		//Janusz Korwin Mikke
		Markov modelKorwin = new Markov(Paths.get("data/korwin.txt"));		
		
		try {
			BufferedWriter writer;
			Path results = Paths.get("data/korwin_results.txt");
			if (!Files.exists(results))
				writer = Files.newBufferedWriter(results, Charset.forName("Cp1252"));
			else
				writer = Files.newBufferedWriter(results, Charset.forName("Cp1252"), StandardOpenOption.APPEND);
			
			for(int i=0; i<100; i++){
    			writer.write(generateLine(modelKorwin));
    			writer.newLine();
    		}   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
	}
	
	public static String generateLine(Markov model){
		String line = "";
		
		Double random = Math.random();
		
		String state = null;
		
		//getting initial state
		double cumulative = 0.0;
		double wordProb;
		
		for (String word : model.initialProbs.keySet()){
			wordProb = model.initialProbs.get(word);
			if (random >= cumulative && random < cumulative+wordProb){
				line += word;
				state = word;
				break;
			}
			cumulative+=wordProb;
		}
		
		while (state != "FinalState"){
			random = Math.random();
			cumulative = 0.0;
			
			for (String word : model.transitions.get(state).keySet()){
				wordProb = model.transitions.get(state).get(word);
				if (random >= cumulative && random < cumulative+wordProb){
					if(word!="FinalState") line += (" " + word);
					state = word;
					break;
				}
				cumulative+=wordProb;
			}
			
		}
	
		return line;	
	}
}
