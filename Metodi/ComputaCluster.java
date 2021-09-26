package protein;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class ComputaCluster implements PairFlatMapFunction<Tuple2<String, String>, String, Float> {
	public java.util.Iterator<Tuple2<String,Float>>	call(Tuple2 <String, String> line){
		
		Integer clusterA = Integer.parseInt(line._2.split(" ")[0]);
		Integer clusterB = Integer.parseInt(line._2.split(" ")[1]);
		
	
		List<Tuple2<String, Float>> result = new ArrayList<Tuple2<String, Float>>();
		
		
		if(clusterA == clusterB) {
			result.add(new Tuple2<String,Float>(line._2.split(" ")[0] +" "+ line._2.split(" ")[0], (float) 1));
			result.add(new Tuple2<String,Float>(line._2.split(" ")[0],(float)1));
			}
		else {
			result.add(new Tuple2<String,Float>(line._2.split(" ")[0],(float)1));
		}
			
		return result.iterator();
		
	}
}