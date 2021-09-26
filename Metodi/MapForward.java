package protein;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class MapForward implements PairFlatMapFunction<Tuple2<String, String>, String, String> {
	public java.util.Iterator<Tuple2<String,String>>	call(Tuple2 <String, String> line){
		
		
		String nodeMadre = line._1.split(" ")[0];
		String root = line._1.split(" ")[1];
		
		String[] vicini = line._2.split(" ")[0].split(",");
		String color = line._2.split(" ")[2];
		Integer distance = Integer.parseInt(line._2.split(" ")[1]);
		String path = line._2.split(" ")[3];
		
		
		List<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
		

		if (color.equalsIgnoreCase("GRAY")) {
			
		
			for(int i=0; i<vicini.length; i++) {
				
				
				if(!path.startsWith(vicini[i]))
					
				
				result.add(new Tuple2<String, String> (vicini[i] +" "+ root, "null" +" "+ (distance+1) +" "+ "GRAY" +" "+  nodeMadre +","+ path));	
			
				
			}
			
			
			result.add(new Tuple2<String, String> (nodeMadre +" "+ root, line._2.split(" ")[0] +" "+ distance +" "+ "BLACK" +" "+ path));
			
			
		}else result.add(line);  
					
		
		return result.iterator();

	}
}