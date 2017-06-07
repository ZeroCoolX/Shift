package lucky8s.shift;

/**
 * Created by Christian on 5/30/2015.
 */
public class Leader  {

    private String username = "";
    private int score;
    private int position;

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setScore(int score){
        this.score = score;
    }
    public int getScore(){
        return this.score;
    }
    public int getPosition(){
        return this.position;
    }
    public void setPosition(int position){
        this.position = position;
    }
}
