package protein;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.List;

import scala.Tuple2;




public class PPINetworks {

	public static void main(String[] args) {

		Logger.getLogger("org").setLevel(Level.ERROR);
		Logger.getLogger("akka").setLevel(Level.ERROR);
		SparkConf sc = new SparkConf();
		sc.setAppName("PPINetworks");
		sc.setMaster("local[*]");
		JavaSparkContext jsc = new JavaSparkContext(sc);

		
		
	    
	    
		// Importazione del dataset
		JavaRDD<String> proteine = jsc.textFile("data/DatasetGrande.txt");

		
		
		// Oggetti che devono essere dichiarati fuori ciclo
		List<String> Risultati = new ArrayList<String>();
	   	Long numeroclusterprecedente;
	    	Long numerocluster;
	    	JavaPairRDD<String,String> copiaproteine = proteine.mapToPair(x->new Tuple2<String,String>(x.split(" ")[0],x.split(" ")[1]));
		float den = copiaproteine.count();
		
		
		
		
		// INIZIO CICLO
		do{
			
		// Preparazione Adjacency List
		JavaRDD<String> nodi = proteine.flatMap(new EstrapolaNodi()).distinct();
		
		JavaPairRDD<String, String> nodiroot = nodi.cartesian(nodi);
		
		JavaPairRDD<String, String> vicini = proteine.flatMapToPair(new CalcolaVicini()).distinct().reduceByKey((x, y) -> (x + "," + y));

		JavaPairRDD<String, Tuple2<String, String>> nodirootvicini = nodiroot.join(vicini);
		
		JavaPairRDD<String,String> finale = nodirootvicini.mapToPair(x-> {
			if(x._1.equalsIgnoreCase(x._2._1))
				return new Tuple2<String, String>(x._1 +" "+ x._2._1, x._2._2 +" "+ 0 +" "+ "GRAY" +" "+ "null");						
			else return new Tuple2<String,String> (x._1 +" "+x._2._1 , x._2._2 +" "+ Integer.MAX_VALUE +" "+ "WHITE" +" "+ "null");		
		});
		
		
				
		
		
		
		// FORWARD MR //
		do {

		// Map
		JavaPairRDD<String, String> map = finale.flatMapToPair(new MapForward());
		
		// Reduce
		finale = map.reduceByKey((x,y)->x +"_"+ y).mapToPair(new ReduceForward());
		
		}while(finale.filter(x->x._2.split(" ")[2].equalsIgnoreCase("GRAY")).isEmpty() == false);
		

		
		
		
		// BACKWARD MR
		
		// Map
		JavaPairRDD<String,Integer> archetti = finale.flatMapToPair(new AccoppiaPath());

		// Reduce
		JavaPairRDD<String,Integer> archettiridotto = archetti.reduceByKey((x,y)-> x+y);


				
				
		// BC(e)				
		Long denominatore = finale.filter(x-> !x._2.split("")[2].equalsIgnoreCase("WHITE")&& x._1.split(" ")[0]!=x._1.split(" ")[1]).count();
		
		JavaPairRDD<Float,String> BCeOrdinato = archettiridotto.mapToPair(x->new Tuple2<Float,String>((float) x._2/totarchetti,x._1.split(",")[0] + " " + x._1.split(",")[1])).sortByKey(false);
				

		


		// STEP 3
		
		// Eliminazione arco con maggiore BC(e)
		List<String> primo = new ArrayList<String>();

		primo.add(BCeOrdinato.values().first());
		primo.add(BCeOrdinato.values().first().split(" ")[1] +" "+ BCeOrdinato.values().first().split(" ")[0]);

		JavaRDD<String> primoinrdd = jsc.parallelize(primo);
		
		proteine = proteine.subtract(primoinrdd);

		
		
		// Assegnazione cluster ai nodi
		JavaPairRDD<String,String> passo1 = finale.filter(x->x._2.split(" ")[2].equalsIgnoreCase("WHITE")).sortByKey().mapToPair(x-> new Tuple2<String,String> (x._1.split(" ")[0],x._1.split(" ")[1])).reduceByKey((x,y)->x+" "+y);

		JavaPairRDD<String,String> passo2 = passo1.mapToPair(x->new Tuple2<String, String>(x._2,x._1)).reduceByKey((x,y)->x+" "+y);

		JavaPairRDD<String, Long> passo3 = passo2.zipWithIndex().flatMapToPair(new DisaccoppiaNodi());
				

		JavaPairRDD<String, Tuple2<String,Long>> assegnoClusterA = copiaproteine.join(passo3).mapToPair(x-> new Tuple2<String,Tuple2<String,Long>> (x._2._1, new Tuple2<String,Long>( x._1 , x._2._2)));
		JavaPairRDD<String, String> assegnoClusterB = assegnoClusterA.join(passo3).mapToPair(x-> new Tuple2 <String,String> (x._1 +" "+ x._2._1._1 ,  x._2._2 +" "+ x._2._1._2  ));


				
		// Indice modularità Q
		JavaPairRDD<String,Float> conta = assegnoClusterB.flatMapToPair(new ComputaCluster()).reduceByKey((x,y)-> x+y).sortByKey().mapToPair(x->{
			if(x._1.contains(" "))
				
				return new Tuple2<String,Float> (x._1.split(" ")[0],x._2/den);  
			
			else return new Tuple2<String,Float> (x._1, (float) Math.pow(x._2/den,2));	
		
		}).reduceByKey((x,y)-> y-x);

	


		// Stampa di Q
		Float Q = conta.values().reduce((x,y)->x+y);
				
		Long numerodeicluster = conta.count();				

		Risultati.add(numerodeicluster +" "+ Q);
				
		System.out.println("Q = "+ Risultati);
			    

			    
				
		// Condizione per il ciclo	   
		numerocluster = Long.valueOf(Risultati.get(Risultati.size()-1).split(" ")[0]).longValue();
			    
		if(Risultati.size()>1)
		   	numeroclusterprecedente = Long.valueOf(Risultati.get(Risultati.size()-2).split(" ")[0]).longValue();
		else numeroclusterprecedente = (long) 1;
			    
		}while(numeroclusterprecedente <= numerocluster); 
	
	}

}
