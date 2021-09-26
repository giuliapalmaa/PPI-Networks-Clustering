package protein;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class ReduceForward implements PairFunction<Tuple2<String, String>, String, String> {

	public Tuple2<String, String> call(Tuple2<String, String> line)throws Exception{
		
		String node = line._1.split(" ")[0];
		String root = line._1.split(" ")[1];
		
		String[] righe = line._2.split("_");
		
		String vicini = "";
		Integer distanza = Integer.MAX_VALUE;
		String path = "";
		String color = "";
		
		
		if(righe.length == 1)
			return line;
		
			else for(int i=0; i<righe.length ; i++) {
						
					if(!righe[i].split(" ")[0].equalsIgnoreCase("null")) 
						vicini = righe[i].split(" ")[0];
					
						if(Integer.parseInt(righe[i].split(" ")[1]) <= distanza) {
							distanza = Integer.parseInt(righe[i].split(" ")[1]);
							path = righe[i].split(" ")[3];}
						
							if(!righe[i].split(" ")[2].equalsIgnoreCase("WHITE"))
								color = righe[i].split(" ")[2];

			}
		
		return new Tuple2<String, String> (node +" "+ root, vicini +" "+ distanza +" "+ color +" "+ path);
				
	}
}