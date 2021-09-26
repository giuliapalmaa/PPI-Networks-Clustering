# Protein-Protein Interaction Clustering



Le associazioni fisiche tra proteine sono oggetto di interesse medico principalmente per due aspetti: un interesse sulla *fisiologia* del corpo e un aspetto *patologico*. Il primo approccio cerca di individuare i pattern che si creano tra proteine interagenti e i conseguenti processi biologici che avvengono all'interno di cellule; il secondo cerca le interazioni con le proteine che sono "viziate" (proteine intatte ma la cui genesi non si è svolta correttamente): queste interagiscono con altre proteine viziate, tendono a legarsi e a creare patologie all'interno dell'organismo.  
L'obiettivo è scoprire le strutture di clustering nella rete PPI, cioè determinare una struttura di cluster di proteine in cui ogni proteina è più vicina alle altre all'interno dello stesso insieme rispetto alle proteine al di fuori dell'insieme.  
Non solo per l'importanza di questi studi ma anche per la loro complessità il cercare le interazioni tra proteine (Protein-Protein Interactions, *PPI*) è un compito molto stimolante e impegnativo.  Molti infatti sono gli algoritmi scritti, di cui uno dei più popolari è quello per la edge-betweeness di Girvan-Newmans, che ci dà una misura di importanza per ogni arco. Questo progetto propone una nuova implementazione di quest'ultimo, e successivamente analizza la struttura in cluster tramite la modularità (indice Q). Ci siamo basati sull'articolo "A MapReduce-Based Parallel Clustering Algorithm for Large Protein-Protein Interaction Networks", di Li Liu e altri. 
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
[archiBest](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/archiBest.csv) | file con lista degli archi del dataset con miglire modularità
[archiFirst](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/archiFirst.csv) | file con lista degli archi del dataset iniziale
[archiLast](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/archiLast.csv) | file con lista degli archi del dataset all'ultima iterazione dell'algoritmo
[nodiBesteFirst](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/nodiBesteFirst.csv) | file con lista dei nodi del dataset iniziale e con migliore modularità
[nodiLast](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Dataset%20Neo4j/nodiLast.csv) | file con lista dei nodi del dataset all'ultima iterazione dell'algoritmo

## Importazione
Per poter importare i dataset sia in *Java* sia in *Neo4J* sono state apportate delle modifiche al file di partenza:  
Su *Excel* abbiamo eliminato tutte le colonne diverse dai due nodi di interazione. Il risultato è una lista di archi con i nodo di interazione separati da uno spazio.  
Per l'importazione in *Neo4J* i file sono stati trasformati in *csv*. Attraverso l'interfaccia [EstrapolaNodi](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/EstrapolaNodi.java) abbiamo ottenuto la lista dei nodi che è richiesta, insieme alla lista degli archi, per la creazione del grafo in *Neo4J*

## Preparazione file di input


### Creazione Adjacency List

L'algoritmo Clustering-MR lavora su una tabella composta da *nxn* righe (dove n è il numero di nodi distinti che compongono il grafo). Questa tabella viene definita **Adjacency List** ed ogni record è registrato mediante una coppia Chiave,Valore.   
La chiave è composta dall'ID del nodo e l'ID di un altro nodo definito *root*: le *nxn* righe rappresentano tutte le possibili combinazioni tra i nodi (funzione cartesian).  
I valori invece conservano, per ogni coppia nodeID-root, gli ID dei nodi vicini alla proteina il cui ID è in chiave, la distanza tra il nodo e la root, il colore del nodo (status) e il percorso che collega il nodo alla root (path).  
Il colore è WHITE se il record non è stato ancora considerato, GRAY se è considerato ma non ha concluso la fase di aggiornamento delle caratteristiche, BLACK se  è  stato elaborato e non verrà più preso in considerazione.  
Di default la distanza è impostata pari a MAX, il colore WHITE e il path NULL. I record il cui ID del nodo è uguale all'ID della root la distanza è pari a 0 e il colore GRAY; questi saranno i nodi da cui l'algoritmo Forward-MR comincerà ad aggiornare i valori.  
Se un record presenta un Node ID e una root che non sono collegati da nessun arco (si trovano in cluster diversi), l'Adjacency List conserverà i valori di default (dist = MAX, col = WHITE).  
 
I vicini sono stati trovati con il metodo [CalcolaVicini](https://github.com/giuliapalmaa/Modularity-Optimization-for-PPI-Networks/blob/main/Metodi/CalcolaVicini.java), la cui logica è quella di duplicare ogni record degli archi che compongono il grafo invertendo NodeID e root, considerarli distinti e ridurli con ReduceByKey.
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

Il Backward-MR è un algoritmo che prende in input l'output del Forward-MR, il suo scopo è quello di determinare quanti shortest path passano per ogni arco del nostro grafo.  
E' composto da una fase MAP in cui, per ogni riga, applicando il metodo AccoppiaPath() otteniamo in output il path del record frammentato in tutti gli archi che lo compongono, accompagnati dal valore 1.

Nella fase Reduce l'algoritmo va a sommare per chiave i valori e restituisce come output ogni arco con il numero di volte in cui è presente nei path dell'adjacency list.

java
// Map
JavaPairRDD<String,Integer> archetti = finale.flatMapToPair(new AccoppiaPath());

// Reduce
JavaPairRDD<String,Integer> archettiridotto = archetti.reduceByKey((x,y)-> x+y);
