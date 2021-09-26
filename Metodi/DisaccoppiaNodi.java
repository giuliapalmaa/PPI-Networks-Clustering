package protein;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class DisaccoppiaNodi implements PairFlatMapFunction<Tuple2<Tuple2<String, String>, Long>, String, Long > {

	public java.util.Iterator<Tuple2<String, Long>> call(Tuple2<Tuple2<String, String>,Long> line) throws Exception {
		
		String[] nodo = line._1._2.split(" ");
		Long cluster = line._2;
		
		
		List<Tuple2<String,Long>> result = new ArrayList<Tuple2<String, Long>>();
		
		
		for(int i=0; i<nodo.length;i++)
			result.add(new Tuple2<String,Long>(nodo[i], cluster));
		
		return result.iterator();
		
	}
}