# Protein-Protein Interaction Clustering



Le associazioni fisiche tra proteine sono oggetto di interesse medico principalmente per due aspetti: un interesse sulla *fisiologia* del corpo e un aspetto *patologico*. Il primo approccio cerca di individuare i pattern che si creano tra proteine interagenti e i conseguenti processi biologici che avvengono all'interno di cellule; il secondo cerca le interazioni con le proteine che sono "viziate" (proteine intatte ma la cui genesi non si è svolta correttamente): queste interagiscono con altre proteine viziate, tendono a legarsi e a creare patologie all'interno dell'organismo. 
L'obiettivo è scoprire le strutture di clustering nella rete PPI, cioè determinare una struttura di cluster di proteine in cui ogni proteina è più vicina alle altre all'interno dello stesso insieme rispetto alle proteine al di fuori dell'insieme.
Non solo per l'importanza di questi studi ma anche per la loro complessità il cercare le interazioni tra proteine (Protein-Protein Interactions, *PPI*) è un compito molto stimolante e impegnativo.  Molti infatti sono gli algoritmi scritti, di cui uno dei più popolari è quello per la edge-betweeness di Girvan-Newmans. Questo progetto propone una nuova implementazione in *Java-Spark* utilizzando il paradigma Map-Reduce, basato sull'articolo "A MapReduce-Based Parallel Clustering Algorithm for Large Protein-Protein Interaction Networks", di Li Liu e altri. Inoltre alcuni risultati di questo algoritmo verranno visualizzati con un database NoSQL *Neo4J*.


## Presentazione dei file


## Il Dataset
Il dataset utilizzato è disponibile al link https://dip.doe-mbi.ucla.edu/dip/Download.cgi?SM=3, con data 05/18/2012. 
Il dataset è formato da 16 variabili, di cui abbiamo preso solo le prime due, rappresentanti il nodo di interazione A e il nodo di interazione B. 
In totale ci sono +++++ nodi, che, separati tra loro da uno spazio, formano +++++ archi.


## Preparazione file di input










