package naive_bayes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataSplit {
		
	public static List<Map<List<String>, ClassType>> splitCVStratifiedRandom(Map<List<String>, ClassType> dataSet, int seed) {
		List<Map<List<String>, ClassType>> sets =  new ArrayList<Map<List<String>, ClassType>>();
		int size = dataSet.size();
		
		//calculate class sizes
		Map<ClassType, Integer> classSizes = new HashMap<ClassType, Integer>();
		
		for (List<String> tweet : dataSet.keySet()){
			ClassType person = dataSet.get(tweet);
			Integer temp = classSizes.getOrDefault(person,0);
			temp++;
			classSizes.put(person, temp);
		}
				
		Set<ClassType> possibleLabels = new HashSet<ClassType>(dataSet.values());
		Map<ClassType, List<List<String>>> labelListPaths = new HashMap<ClassType, List<List<String>>>();
		for (ClassType label: possibleLabels) {
			// Select all data point with the label.
			List<List<String>> paths = dataSet.keySet().stream()
					.filter(p -> dataSet.get(p).equals(label))
					.collect(Collectors.toList());
			// Randomize the order.
			Collections.shuffle(paths, new Random(seed));
			labelListPaths.put(label, paths);
		}
		for (int i=0; i<10; i++){
			Map<List<String>, ClassType> newSet = new HashMap<List<String>, ClassType> ();
			
			for (ClassType label: labelListPaths.keySet()) {
				List<List<String>> paths = labelListPaths.get(label);
				newSet.putAll(paths
					.subList(i*classSizes.get(label)/20, (i+1)*classSizes.get(label)/20).stream()
					.collect(Collectors.toMap(Function.identity(), p -> label)));		
			}
			
			sets.add(newSet);
		}
		
		return sets;
	}
}
