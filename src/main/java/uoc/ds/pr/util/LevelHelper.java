package uoc.ds.pr.util;

import uoc.ds.pr.SportEvents4Club;

public class LevelHelper {
    public static SportEvents4Club.Level getLevel(int numRatings){
        if (numRatings < 2){
            return SportEvents4Club.Level.ROOKIE;
        }
        if (numRatings >= 2 && numRatings < 5){
            return SportEvents4Club.Level.PRO;
        }
        if (numRatings >= 5 && numRatings < 10){
            return SportEvents4Club.Level.EXPERT;
        }
        if (numRatings >= 10 && numRatings < 15){
            return SportEvents4Club.Level.MASTER;
        }
        if (numRatings >= 15){
            return SportEvents4Club.Level.LEGEND;
        }

        return null;
    }

}
