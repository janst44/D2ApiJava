To Run from the directory containing the pom file run the following:
1. mvn compile
2. mvn exec:java -Dexec.mainClass="HttpGetData"

TODO:
add entire record to a database for future ML (synergies and counters detailed like even items)
add grid visualization (red/green) with adjustable drag scrolls as thresholds for min and max win rate threshold exclusion to filter results (frontend)
filter by ranked, all pick
of the top 5 counters, who appears the most? Which hero's have the least significant counters and high win rate? Create a dream team pool of few hero's, some labeled first pick, others situational counter.