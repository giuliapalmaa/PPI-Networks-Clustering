package protein;

import java.util.*;

import org.apache.spark.api.java.function.FlatMapFunction;

public class EstrapolaNodi implements FlatMapFunction<String, String> {

	public java.util.Iterator<String> call(String line) {
		
		String[] nodi = line.split(" ");
		
		
		List<String> result = new ArrayList<String>();
		
		
		for (String nodo : nodi) {
			result.add(nodo);
		}
		
		return result.iterator();
	
	}
}
