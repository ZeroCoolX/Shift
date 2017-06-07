package lucky8s.shift;

/**
 * Created by Christian on 9/27/2015.
 */
public class Level {
    int level;
    int lowestMoves;
    int resId;
    String pack;

    public Level(String pack, int level, int lowestMoves, int resId){
        this.level = level;
        this.lowestMoves = lowestMoves;
        this.resId = resId;
        this.pack = pack;
    }
}
