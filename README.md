To Run from the directory containing the pom.xml file run the following:
1. mvn spring-boot:run

Requires java version 8 or later
Requires local install of maven

When started, by default we only grab matches starting from the last match played. (most recent)
This allows you to delete (or move) the 'saveDotaStats.txt' file and re-start gathering new stats at the time of restart. (this is convenient when the meta changes)

TODO:
Add entire record to a database for future ML (synergies and counters detailed like even items)
Add grid visualization (red/green) with adjustable drag scrolls as thresholds for min and max win rate threshold exclusion to filter results (frontend)
Of the top 5 counters, who appears the most? Which hero's have the least significant counters and high win rate? Create a dream team pool of few hero's, some labeled first pick, others situational counter.
Make final pick smarter(Maximize likelyhood of not being countered(popularity of other counters) and boosting if countering previous 4)

