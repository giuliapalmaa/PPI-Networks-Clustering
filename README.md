# Protein-Protein Interaction Clustering



Le associazioni fisiche tra proteine sono oggetto di interesse medico principalmente per due aspetti: un interesse sulla *fisiologia* del corpo e un aspetto *patologico*. Il primo approccio cerca di individuare i pattern che si creano tra proteine interagenti e i conseguenti processi biologici che avvengono all'interno di cellule; il secondo cerca le interazioni con le proteine che sono "viziate" (proteine intatte ma la cui genesi non si è svolta correttamente): queste interagiscono con altre proteine viziate, tendono a legarsi e a creare patologie all'interno dell'organismo. 

L'obiettivo è scoprire le strutture di clustering nella rete PPI, cioè determinare una struttura di cluster di proteine in cui ogni proteina è più vicina alle altre all'interno dello stesso insieme rispetto alle proteine al di fuori dell'insieme.

Non solo per l'importanza di questi studi ma anche per la loro complessità il cercare le interazioni tra proteine (Protein-Protein Interactions, *PPI*) è un compito molto stimolante e impegnativo.  Molti infatti sono gli algoritmi scritti, di cui uno dei più popolari è quello per la edge-betweeness di Girvan-Newmans. Questo progetto propone una nuova implementazione in *Java-Spark* utilizzando il paradigma Map-Reduce, e successivamente analizza la struttura in cluster tramite la modularità (indice Q). Ci siamo basati sull'articolo "A MapReduce-Based Parallel Clustering Algorithm for Large Protein-Protein Interaction Networks", di Li Liu e altri. Inoltre alcuni risultati di questo algoritmo verranno visualizzati con un database NoSQL *Neo4J*.

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

Dataset | Contenuto
------------ | -------------
DatasetGrande | Mus Musculus
DatasetPiccolo | Drosophila Melanogaster



## Preparazione file di input




## Creazione Adjacency List

L'algoritmo Clustering-MR lavora su una tabella composta da nxn righe (dove n è il numero di nodi distinti che compongono il grafo). 
Questa tabella viene definita Adjacency List ed ogni record è registrato mediante una coppia Chiave,Valore. 
La chiave è composta dall'ID del nodo e l'ID di un altro nodo definito come root, in totale le righe rappresentano tutte le possibili combinazioni tra i nodi (cartesian).
I valori invece conservano, per ogni coppia nodeID-root, l'informazione degli ID dei nodi vicini alla proteina, la distanza tra il nodo e la root, il colore del nodo e il percorso che collega il nodo alla root (path).
Di default la distanza è impostata pari a MAX, il colore WHITE e il path NULL. Per quanto riguarda i record che presentano l'ID del nodo uguale all'ID della root la distanza è pari a 0 e il colore GRAY, sarà proprio da questi nodi che il 










