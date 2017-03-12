package markov_model;

import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextGenerator {

	static final Path dataFile = Paths.get("data/korwin.txt");


	public static void main(String[] args) throws IOException {
		Markov modelTrump = new Markov(Paths.get("data/trump.txt"));		
		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("data/trump_results.txt"), Charset.forName("Cp1252"))) {
			for(int i=0; i<100; i++){
    			writer.write(generateLine(modelTrump));
    			writer.newLine();
    		}   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
		
		
		Markov modelObama = new Markov(Paths.get("data/obama.txt"));		
		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("data/obama_results.txt"), Charset.forName("Cp1252"))) {
			for(int i=0; i<100; i++){
    			writer.write(generateLine(modelObama));
    			writer.newLine();
    		}   
		
		} catch (IOException e) {
			throw new IOException("Can't load the data file.", e);
		}
		
		Markov modelKorwin = new Markov(Paths.get("data/korwin.txt"));		
		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("data/korwin_results.txt"), Charset.forName("Cp1252"))) {
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
