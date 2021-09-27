# Protein-Protein Interaction Clustering



Le associazioni fisiche tra proteine sono oggetto di interesse medico principalmente per due aspetti: un interesse sulla *fisiologia* del corpo e un aspetto *patologico*. Il primo approccio cerca di individuare i pattern che si creano tra proteine interagenti e i conseguenti processi biologici che avvengono all'interno di cellule; il secondo cerca le interazioni con le proteine che sono "viziate" (proteine intatte ma la cui genesi non si è svolta correttamente): queste interagiscono con altre proteine viziate, tendono a legarsi e a creare patologie all'interno dell'organismo.  
L'obiettivo è scoprire le strutture di clustering nella rete PPI, cioè determinare una struttura di cluster di proteine in cui ogni proteina è più vicina alle altre all'interno dello stesso insieme rispetto alle proteine al di fuori dell'insieme.  
Non solo per l'importanza di questi studi ma anche per la loro complessità, il cercare le interazioni tra proteine (Protein-Protein Interactions, *PPI*) è un compito molto stimolante e impegnativo.  Molti infatti sono gli algoritmi scritti, di cui uno dei più popolari è quello per l'edge-betweeness di Girvan-Newmans.  
Questo progetto propone una nuova implementazione di quest'ultimo, e successivamente analizza la struttura in cluster tramite la modularità (indice Q). Ci siamo basati sull'articolo "A MapReduce-Based Parallel Clustering Algorithm for Large Protein-Protein Interaction Networks", di Li Liu e altri. 
L'algoritmo è implementato in *java-Spark*, con il paradigma Map-Reduce. Inoltre alcuni risultati di questo algoritmo verranno visualizzati con un database NoSQL *Neo4J*.

## I Dataset
I dataset utilizzati sono disponibile al link https://dip.doe-mbi.ucla.edu/dip/Download.cgi?SM=3, con data 18/05/2012.  
Sono formati da 16 variabili, di cui abbiamo preso solo le prime due, rappresentanti il nodo di interazione A e il nodo di interazione B. Le relazioni descritte nel dataset sono indirette.  
Il dataset Mus Musculus è composto da 728 nodi e 634 archi.  
Il dataset Drosophila Melanogaster è composto da 284 nodi e 297 archi.


## Presentazione dei file

[Metodi](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi) | Contenuto
------------ | -------------
[AccoppiaPath](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/AccoppiaPath.java) | Interfaccia che individua i singoli archi dei path
[CalcolaVicini](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/CalcolaVicini.java) | Interfaccia che trova i vicini dei nodi
[ComputaCluster](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/ComputaCluster.java) | Interfaccia che associa a ogni nodo il cluster di appartenenza
[DisaccoppiaNodi](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/DisaccoppiaNodi.java) | Interfaccia che da ogni arco ricava i nodi dagli archi e ci associa il cluster
[EstrapolaNodi](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/EstrapolaNodi.java) | Interfaccia che ricava i nodi distinti dagli archi
[MapForward](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/MapForward.java) | Interfaccia che implementa il MapForward
[ReduceForward](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/ReduceForward.java) | Interfaccia che implementa il ReduceForward

[Dataset Java](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Java)| Contenuto
------------ | -------------
[DatasetGrande](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Java/DatasetGrande.txt) | Mus Musculus
[DatasetPiccolo](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Java/DatasetPiccolo.txt) | Drosophila Melanogaster

[Dataset Neo4J](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j)| Contenuto
------------ | -------------
[archiBest](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/archiBest.csv) | file con lista degli archi del dataset con migliore modularità
[archiFirst](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/archiFirst.csv) | file con lista degli archi del dataset iniziale
[archiLast](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/archiLast.csv) | file con lista degli archi del dataset all'ultima iterazione dell'algoritmo
[nodiBesteFirst](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/nodiBesteFirst.csv) | file con lista dei nodi del dataset iniziale e con migliore modularità
[nodiLast](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/nodiLast.csv) | file con lista dei nodi del dataset all'ultima iterazione dell'algoritmo

## Importazione dei file
Per poter importare i dataset sia in *Java* sia in *Neo4J* sono state apportate delle modifiche ai file di partenza:  
Su *Excel* abbiamo eliminato tutte le colonne diverse dai due nodi di interazione. Il risultato è una lista di archi con i nodi di interazione separati da uno spazio.  





## Creazione dei file di input: Adjacency List

L'algoritmo Clustering-MR lavora su una tabella composta da *nxn* righe (dove n è il numero di nodi distinti che compongono il grafo). Questa tabella viene definita **Adjacency List** ed ogni record è registrato mediante una coppia Chiave,Valore.   
La chiave è composta dal nome del nodo e dal nome di un altro nodo definito *root*: le *nxn* righe rappresentano tutte le possibili combinazioni tra i nodi (funzione cartesian).  I valori invece conservano, per ogni coppia nodo-root, i nodi vicini alla proteina in chiave, la distanza tra il nodo e la root, il colore del nodo (status) e il percorso che collega il nodo alla root (path).  
Il colore è WHITE se il record non è stato considerato, GRAY se è considerato ma non ha concluso la fase di aggiornamento delle caratteristiche, BLACK se è stato completamente aggiornato.  
Di default la distanza è impostata pari a MAX, il colore WHITE e il path NULL. Nei record il cui nodo è uguale alla root la distanza è pari a 0 e il colore GRAY; questi saranno i nodi da cui l'algoritmo Forward-MR comincerà ad aggiornare i valori.  
Se un record presenta un nodo e una root che non sono collegati da nessun arco (si trovano in cluster diversi), l'Adjacency List conserverà i valori di default (dist = MAX, col = WHITE).  
 
I vicini sono stati trovati con il metodo [CalcolaVicini](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/CalcolaVicini.java), la cui logica è quella di duplicare ogni record invertendo NodeID e root, eliminare i record uguali e ridurli con ReduceByKey.
Per tutti gli altri valori abbiamo eseguito un mapToPair:

```java
JavaRDD<String> nodi = proteine.flatMap(new EstrapolaNodi()).distinct();
		
JavaPairRDD<String, String> nodiroot = nodi.cartesian(nodi);
		
JavaPairRDD<String, String> vicini = proteine.flatMapToPair(new CalcolaVicini()).distinct().reduceByKey((x, y) -> (x + "," + y));

JavaPairRDD<String, Tuple2<String, String>> nodirootvicini = nodiroot.join(vicini);
		
JavaPairRDD<String,String> finale = nodirootvicini.mapToPair(x-> {
	if(x._1.equalsIgnoreCase(x._2._1))
		return new Tuple2<String, String>(x._1 +" "+ x._2._1, x._2._2 +" "+ 0 +" "+ "GRAY" +" "+ "null");						
	else return new Tuple2<String,String> (x._1 +" "+x._2._1 , x._2._2 +" "+ Integer.MAX_VALUE +" "+ "WHITE" +" "+ "null");		
});
```

## Forward-MR

Il Forward-MR è un algoritmo che partendo dalle root percorre ed aggiorna i valori (distanza, colore e path) in parallelo di ogni vicino del nodo in chiave.  
I primi record toccati sono quelli dove il NodeID è uguale al root. L'algoritmo genera una coppia (K,V) per ogni vicino del nodo, in cui la distanza è la distanza del nodo madre + 1, il colore è GRAY e il path è composto dal path del nodo madre + il NodeID del nodo madre. Il colore della riga del nodo madre viene aggiornato in BLACK affinchè non venga più considerata nelle iterazioni successive.

La fase di reduce seleziona tra le diverse coppie (K,V) dei vicini che sono state generate quella dove la distanza è minima.

Successivamente l'algoritmo ricomincia dagli archi grigi, finché questi non sono più presenti, ovvero fino a quando sono stati processati tutti i vicini dei vicini possibili per i root in parallelo. L'output finale è una tabella composta da *nxn* righe, come l' input iniziale, dove però è registrata la distanza minima e il path tra il NodeID e la Root.

```java
do {
	// Map
	JavaPairRDD<String, String> map = finale.flatMapToPair(new MapForward());
		
	// Reduce
	finale = map.reduceByKey((x,y)->x +"_"+ y).mapToPair(new ReduceForward());
		
}while(finale.filter(x->x._2.split(" ")[2].equalsIgnoreCase("GRAY")).isEmpty() == false);
```

## Backward-MR

Il Backward-MR è un algoritmo che prende in input l'output del Forward-MR, e determina quanti shortest path passano per ogni arco del nostro grafo.  
E' composto da una fase Map in cui si applica il metodo [AccoppiaPath](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/AccoppiaPath.java) per ottenere il path del record frammentato in tutti gli archi che lo compongono, accompagnati dalla frequenza 1.

Nella fase Reduce l'algoritmo va a sommare le frequenze dei record con la stessa chiave e restituisce ogni arco con il numero di volte in cui è presente nei path dell'adjacency list.


```java
// Map
JavaPairRDD<String,Integer> archetti = finale.flatMapToPair(new AccoppiaPath());

// Reduce
JavaPairRDD<String,Integer> archettiridotto = archetti.reduceByKey((x,y)-> x+y);
```

## Edge-Betweenness e Modularità
Successivamente l'algoritmo calcola per ogni arco il valore di **betweenness centrality**, che ci dà una misura di centralità di ogni arco all'intero di un grafo:

```java
Long totarchetti = archettiridotto.count();
		 
JavaPairRDD<Float,String> BCeOrdinato = archettiridotto.mapToPair(x->new Tuple2<Float,String>((float) x._2/totarchetti,x._1.split(",")[0] + " " + x._1.split(",")[1])).sortByKey(false);
```

Si elimina l'arco con il valore di BC(e) maggiore, si aggiorna il dataset, si calcola l'indice Q e si ripete l'algoritmo. 

L'indice Q va a misurare la modularità del grafo, ovvero la qualità della suddivisione delle proteine in cluster a seconda delle loro interazioni. Un valore di Q prossimo all'1 indica una suddivisione buona, dove le proteine più connesse si trovano all'interno dello stesso cluster, ma sono debolmente connesse con i nodi delle altre comunità della rete.

```java
JavaPairRDD<String,Float> conta = assegnoClusterB.flatMapToPair(new ComputaCluster()).reduceByKey((x,y)-> x+y).sortByKey().mapToPair(x->{
	if(x._1.contains(" "))	
		return new Tuple2<String,Float> (x._1.split(" ")[0],x._2/den);  
			
	else return new Tuple2<String,Float> (x._1, (float) Math.pow(x._2/den,2));	
		
}).reduceByKey((x,y)-> y-x);

Float Q = conta.values().reduce((x,y)->x+y);
```

Per il calcolo di Q bisogna conoscere il cluster di appartenenza di ogni nodo di un arco, così da discernere archi i cui nodo fanno parte dello stesso cluster e archi i cui nodi connettono due comunità distinte. Per fare ciòabbiamo applicato agli archi il cui colore è rimasto WHITE una serie di trasformazioni (i nodi di questi archi si trovano in cluster diversi per definizione):

```java
JavaPairRDD<String,String> passo1 = finale.filter(x->x._2.split(" ")[2].equalsIgnoreCase("WHITE")).sortByKey().mapToPair(x-> new Tuple2<String,String> (x._1.split(" ")[0],x._1.split(" ")[1])).reduceByKey((x,y)->x+" "+y);

JavaPairRDD<String,String> passo2 = passo1.mapToPair(x->new Tuple2<String, String>(x._2,x._1)).reduceByKey((x,y)->x+" "+y);

JavaPairRDD<String, Long> passo3 = passo2.zipWithIndex().flatMapToPair(new DisaccoppiaNodi());
				

JavaPairRDD<String, Tuple2<String,Long>> assegnoClusterA = copiaproteine.join(passo3).mapToPair(x-> new Tuple2<String,Tuple2<String,Long>> (x._2._1, new Tuple2<String,Long>( x._1 , x._2._2)));
JavaPairRDD<String, String> assegnoClusterB = assegnoClusterA.join(passo3).mapToPair(x-> new Tuple2 <String,String> (x._1 +" "+ x._2._1._1 ,  x._2._2 +" "+ x._2._1._2  ));
```

L'algoritmo si interrompe quando le sue azioni diventano superflue, ovvero quando l'eliminazione dell'arco più attraversato restituisce un numero di cluster minore rispetto a quello del passaggio precedente. In una situazione di questo tipo ci troviamo di fronte ad un grafo strutturato in cluster composti da uno o due proteine l'uno.



## Validazione risultati in Neo4J

### Importazione dei file

Per l'importazione in *Neo4J* i file *txt* sono stati trasformati in *csv*. Attraverso l'interfaccia [EstrapolaNodi](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/EstrapolaNodi.java) abbiamo ottenuto la lista dei nodi che è richiesta, insieme alla lista degli archi, per la creazione del grafo in *Neo4J*.  
Sono stati caricati i file dei grafi sia del datset di partenza, sia del dataset che presenta il valore della migliore modularità, sia del dataset all'ultima iterazione.  

### Distanza e Shortest Path
L'output del Forward MR è una lista di tutte le combinazioni dei nodi con annesse distanze e shortest path. Per verificare la correttezza di questi risultati abbiamo creato un "named graph" su cui abbiamo applicato un algoritmo presente nella libreria *Graph Data Science*: 


```sql
CALL gds.alpha.allShortestPaths.stream
({nodeProjection: 'Protein', relationshipProjection: 
{INTERACTION: {type: 'INTERACTION' }}})
YIELD sourceNodeId, targetNodeId, distance
WITH sourceNodeId, targetNodeId, distance
WHERE gds.util.isFinite(distance) = true
MATCH (source:Protein) WHERE id(source) = sourceNodeId
MATCH (target:Protein) WHERE id(target) = targetNodeId
RETURN source.nodi AS source, target.nodi AS target, distance
```
Con questa interrogazione otteniamo uno tra i percorsi più brevi e la distanza minima tra tutte le coppie di nodi.  
Per controllare la distanza e il path più breve tra due nodi dati abbiamo utilizzato la query: 

```sql
MATCH (p:Protein {nodi: 'DIP-27777N'}),  (q:Protein {nodi: 'DIP-29672N'}),
 ShortestPath=shortestPath((p)-[*]-(q))
RETURN ShortestPath, length(ShortestPath)
```










