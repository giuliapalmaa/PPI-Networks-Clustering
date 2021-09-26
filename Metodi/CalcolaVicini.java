package protein;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class CalcolaVicini implements PairFlatMapFunction<String, String, String> {
	public java.util.Iterator<Tuple2<String,String>>	call(String line){
		
		
		List<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
		
		
		result.add(new Tuple2<String, String>( line.split(" ")[1],line.split(" ")[0]));
		result.add(new Tuple2<String, String>(line.split(" ")[0],line.split(" ")[1]));
		
		return result.iterator();
		
	}
}