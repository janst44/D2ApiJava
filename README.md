To Run from the directory containing the pom file run the following:
1. mvn compile
2. mvn exec:java -Dexec.mainClass="HttpGetData"

TODO:
add entire record to a database for future ML (synergies and counters detailed like even items)
add grid visualization (red/green) with adjustable drag scrolls as thresholds for min and max win rate threshold exclusion to filter results (frontend)
filter by ranked, all pick
of the top 5 counters, who appears the most? Which hero's have the least significant counters and high win rate? Create a dream team pool of few hero's, some labeled first pick, others situational counter.

pick 2 from the first pick pool. (Which is a list of hero's that have the least counters, taking into consideration popularity of counters and win both win rate statistics leaning more on the by unit win rate)
Pick next two from the generally good counters pool-based off opponents 2 first picks. (Maximize Counter, here we look at top counters for first 2 and )
Make final pick from entire pool now taking into consideration opponents 4 picks. (Maximize likely hood of not being countered(popularity of other counters) and boosting if countering previous 4)
