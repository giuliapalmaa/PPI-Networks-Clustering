package protein;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class AccoppiaPath implements PairFlatMapFunction<Tuple2<String, String>, String, Integer> {

	public Iterator<Tuple2<String, Integer>> call(Tuple2<String, String> line) {

		String node = line._1.split(" ")[0];
		String[] path = line._2.split(" ")[3].split(",");
		String pathintero = line._2.split(" ")[3];
		Integer distanza = Integer.parseInt(line._2.split(" ")[1]);


		List<Tuple2<String, Integer>> output = new ArrayList<Tuple2<String, Integer>>();

		if (!pathintero.equalsIgnoreCase("null")) {
			
			if (distanza == 1)
				output.add(new Tuple2<String, Integer>(path[0] + "," + node, 1));

			else {
				output.add(new Tuple2<String, Integer>(node + "," + path[0], 1));
				for (int i = 0; i < path.length - 2; i++) {

					String p1 = path[i];
					int j = i + 1;
					String p2 = path[j];

					output.add(new Tuple2<String, Integer>(p1 + "," + p2, 1));
				}
			}
		}
		return output.iterator();

	}
}