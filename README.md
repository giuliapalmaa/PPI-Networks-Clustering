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

Metodi | Contenuto
------------ | -------------
AccoppiaPath | Interfaccia che individua i singoli archi dei path
CalcolaVicini | Interfaccia che trova i vicini dei nodi
ComputaCluster | Interfaccia che associa a ogni nodo il cluster di appartenenza
DisaccoppiaNodi | Interfaccia che da ogni arco ricava i nodi dagli archi e ci associa il cluster
EstrapolaNodi | Interfaccia che ricava i nodi distinti dagli archi
MapForward | Interfaccia che implementa il MapForward
ReduceForward | Interfaccia che implementa il ReduceForward

Dataset Java| Contenuto
------------ | -------------
DatasetGrande | Mus Musculus
DatasetPiccolo | Drosophila Melanogaster

Dataset Neo4J| Contenuto
------------ | -------------
archiBest | file con lista degli archi del dataset con miglire modularità
archiFirst | file con lista degli archi del dataset iniziale
archiLast | file con lista degli archi del dataset all'ultima iterazione dell'algoritmo
nodiBesteFirst | file con lista dei nodi del dataset iniziale e con migliore modularità
nodiLast | file con lista dei nodi del dataset all'ultima iterazione dell'algoritmo




## Preparazione file di input
L'algoritmo Clustering-MR lavora su una tabella composta da nxn righe (dove n è il numero di nodi distinti che compongono il grafo). Questa tabella viene definita Adjacency List ed ogni record è registrato mediante una coppia Chiave,Valore. La chiave è composta dall'ID del nodo e l'ID di un altro nodo definito come root, in totale le righe rappresentano tutte le possibili combinazioni tra i nodi (cartesian). I valori invece conservano, per ogni coppia nodeID-root, l'informazione degli ID dei nodi vicini alla proteina, la distanza tra il nodo e la root, il colore del nodo e il percorso che collega il nodo alla root (path). Di default la distanza è impostata pari a MAX, il colore WHITE e il path NULL. Per quanto riguarda i record che presentano l'ID del nodo uguale all'ID della root la distanza è pari a 0 e il colore GRAY, sarà proprio da questi nodi che il



## Creazione Adjacency List

L'algoritmo Clustering-MR lavora su una tabella composta da nxn righe (dove n è il numero di nodi distinti che compongono il grafo). 
Questa tabella viene definita Adjacency List ed ogni record è registrato mediante una coppia Chiave,Valore. 
La chiave è composta dall'ID del nodo e l'ID di un altro nodo definito come root, in totale le righe rappresentano tutte le possibili combinazioni tra i nodi (cartesian).
I valori invece conservano, per ogni coppia nodeID-root, l'informazione degli ID dei nodi vicini alla proteina, la distanza tra il nodo e la root, il colore del nodo e il percorso che collega il nodo alla root (path).
Di default la distanza è impostata pari a MAX, il colore WHITE e il path NULL. Per quanto riguarda i record che presentano l'ID del nodo uguale all'ID della root la distanza è pari a 0 e il colore GRAY, sarà proprio da questi nodi che l'algoritmo Forward-MR comincerà ad aggiornare i valori.
Il colore è WHITE se il record non è stato ancora considerato, GRAY se è considerato ma non ha concluso la fase di aggiornamento delle caratteristiche, BLACK se  è  stato elaborato e non verrà più preso in considerazione.
Se un record presenta un Node ID e una root che non sono collegati da nessun arco (si trovano in cluster diversi), l'Adjacency List conserverà i valori di default (dist = MAX, col = WHITE).
I NodeID sono stati registrati mediante un metodo EstrapolaNodi, che va a registrare ogni nodo che compare nel file di input, sia che si trovi nella prima che nella seconda colonna.
I vicini sono stati calcolati con un metodo CalcolaVicini(), la logica è di duplicare ogni record degli archi che compongono il grafo invertendo NodeID e root, considerarli distinti e ridurli con ReduceByKey.
Per tutti gli altri valori abbiamo eseguito un mapToPair.

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



