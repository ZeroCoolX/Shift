package lucky8s.shift;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Christian on 12/29/2015.
 */
public class Generator {
    private String pack = "";
    private int level = 0;
    private Context context;
    private Activity activity;
    private TableLayout table;
    private RelativeLayout levelLayout;
    private View rink;
    private ArrayList<View> obstaclesTable = new ArrayList<>();
    private ArrayList<View> obstaclesRel = new ArrayList<>();
    private ArrayList<Integer> moves = new ArrayList<>();
    private View dude;
    private View finish;
    private int numColumns;

    public Generator(String pack, int level, Context context, Activity activity) {
        this.pack = pack;
        this.level = level;
        this.context = context;
        this.activity = activity;
    }
    public ArrayList<View> getObstacles() {
        return this.obstaclesRel;
    }

    public ArrayList<Integer> getMoves() {
        return this.moves;
    }

    public void generate(Context context, Activity activity) {
        this.table = (TableLayout) activity.findViewById(R.id.table);
        this.levelLayout = (RelativeLayout) activity.findViewById(R.id.level_layout);
        this.rink = activity.findViewById(R.id.rink);

        String levelString = getLevelString(pack, level);
        parse(levelString);
        brotherPortals(obstaclesTable);
    }
    public int getMovesCount(){
        String levelString = getLevelString(pack, level);
        char tempChar = ' ';
        int movesCount = 0;
        int i = levelString.length()-1;
        while(tempChar != '|'){
            tempChar = levelString.charAt(i);
            i--;
            if(tempChar == '1' || tempChar == '2' || tempChar == '3' || tempChar == '4'){
                movesCount++;
            }
        }
        return movesCount;
    }
    public void brotherPortals(ArrayList<View> obstacles){
        for(int i = 0; i < obstacles.size(); i++){
            View tempView = obstacles.get(i);
            if(tempView instanceof Portal){
                int brother1 = ((Portal) tempView).getBrotherInt();
                for(int j = 0; j < obstacles.size(); j++){
                    View poss = obstacles.get(j);
                    if(poss != tempView && poss instanceof Portal){
                        int possBrotherInt = ((Portal) poss).getBrotherInt();
                        if(((Portal) poss).getBrotherInt() == brother1){
                            ((Portal) obstacles.get(j)).setBrother((Portal)tempView);
                            ((Portal) obstacles.get(i)).setBrother((Portal)poss);
                        }
                    }
                }
            }
        }
    }
    public boolean isDrawn(){
        for(int i = 0; i < obstaclesTable.size(); i++){
            if(!(obstaclesTable.get(i).getWidth() > 0)){
                return false;
            }
        }
        if(dude != null && dude.getWidth() > 0 && finish != null && finish.getWidth() > 0){
            return true;
        }else{
            return false;
        }

    }
    public String getLevelString(String pack, int level) {
        String levelString = "";
        AssetManager am = context.getAssets();
        String filename = pack.toLowerCase().replace(" ", "_");
        try {
            InputStream is = am.open(filename + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int line = 0;
            String temp = "";
            while (line < level) {
                temp = br.readLine();
                while (!(temp.endsWith("1") || temp.endsWith("2") || temp.endsWith("3") || temp.endsWith("4"))) {
                    temp += br.readLine();
                }
                line++;
            }
            levelString = temp;
        } catch (IOException io) {
        }
        return levelString;
    }

    public void parse(String levelString) {
        //sample string would look like such:   |-----------|-----------|...|-----------|-----------|123412123
        TableRow newTableRow = null;
        for (int i = 0; i < levelString.length(); i++) {
            char tempChar = levelString.charAt(i);
            View tempView = null;
            if (tempChar == '|') {
                if (newTableRow != null) {
                    table.addView(newTableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1));
                    numColumns = newTableRow.getChildCount();
                }
                if (!(levelString.charAt(i + 1) == '1' || levelString.charAt(i + 1) == '2' || levelString.charAt(i + 1) == '3' || levelString.charAt(i + 1) == '4')) {
                    newTableRow = new TableRow(context);
                }
            } else if (tempChar != '|') {
                tempView = getView(tempChar);
                if(tempView instanceof Portal){
                    int brotherInt = Character.getNumericValue(levelString.charAt(i+1));
                    ((Portal) tempView).setBrotherInt(brotherInt);
                    i++;
                }
            }
            if (tempView != null && newTableRow != null) {
                newTableRow.addView(tempView, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT));
                if (!(tempView instanceof Space || tempView instanceof Dude || tempView instanceof Finish)) {
                    obstaclesTable.add(tempView);
                }else if(tempView instanceof Dude){
                    this.dude = tempView;
                }else if(tempView instanceof Finish){
                    this.finish = tempView;
                }
            }
        }
    }
    public void replicate(){
        //add each view from the TableLayout to the relativeLayout, remove each view from obstaclesTable then add to obstaclesRel.
        int zeroX = getLeft(rink);
        int zeroY = getTop(rink);
        int lowestWidth = 0;
        int lowestHeight = 0;

        //margin from x axis of grid will be positionX-zeroX
        for(int i = 0; i < obstaclesTable.size(); i++){
            View thisObst = obstaclesTable.get(i);
            View tempObstacle;
            if(thisObst instanceof Standard){
                tempObstacle = new Standard(context);
            }else if(thisObst instanceof Breakable){
                tempObstacle = new Breakable(context);
            }else if(thisObst instanceof Molten){
                tempObstacle = new Molten(context);
            }else if(thisObst instanceof Portal){
                tempObstacle = new Portal(context);
                ((Portal) tempObstacle).setBrotherInt(((Portal)thisObst).getBrotherInt());
            }else if(thisObst instanceof Frozen){
                tempObstacle = new Frozen(context);
            }else if(thisObst instanceof Bubble){
                tempObstacle = new Bubble(context);
            }else {
                continue;
            }
            if(tempObstacle != null) {
                if(lowestWidth == 0 || lowestHeight == 0){
                    lowestWidth=thisObst.getMeasuredWidth();
                    lowestHeight=thisObst.getMeasuredHeight();
                }else{
                    if(thisObst.getMeasuredWidth() < lowestWidth){
                        lowestWidth = thisObst.getMeasuredWidth();
                    }
                    if(thisObst.getMeasuredHeight() < lowestHeight){
                        lowestHeight = thisObst.getMeasuredHeight();
                    }
                }
                tempObstacle.setX(getLeft(thisObst) - zeroX);
                tempObstacle.setY(getTop(thisObst) - zeroY);
                levelLayout.addView(tempObstacle, new RelativeLayout.LayoutParams(thisObst.getMeasuredWidth(), thisObst.getMeasuredHeight()));
                obstaclesRel.add(tempObstacle);
                table.removeView(thisObst);
            }
        }
        Finish tempFinish = new Finish(context);
        tempFinish.setX(getLeft(finish)-zeroX);
        tempFinish.setY(getTop(finish)-zeroY);
        int width = finish.getMeasuredWidth();
        int height = finish.getMeasuredHeight();
        levelLayout.addView(tempFinish, new RelativeLayout.LayoutParams(lowestWidth, lowestHeight));
        table.removeView(finish);
        finish = tempFinish;

        Dude tempDude = new Dude(context);
        tempDude.setX(getLeft(dude)-zeroX);
        tempDude.setY(getTop(dude)-zeroY);
        levelLayout.addView(tempDude, new RelativeLayout.LayoutParams(lowestWidth, lowestHeight));
        table.removeView(dude);
        dude = tempDude;

        table.removeAllViews();

        setLevelDims(width, height);

        brotherPortals(obstaclesRel);
    }
    public void setLevelDims(int width, int height){
        rink.getLayoutParams().width = width*numColumns;
    }
    public View getDude() {
        return this.dude;
    }

    public View getFinish() {
        return this.finish;
    }

    public View getView(char tempChar){
        View tempView = null;
        switch (tempChar){
            case '-':
                tempView = new Space(context);
                break;
            case 'S':
                tempView = new Standard(context);
                break;
            case 'M':
                tempView = new Molten(context);
                break;
            case 'P':
                tempView = new Portal(context);
                break;
            case 'b':
                tempView = new Bubble(context);
                break;
            case 'B':
                tempView = new Breakable(context);
                break;
            case 'f':
                tempView = new Frozen(context);
                break;
            case 'F':
                tempView = new Finish(context);
                break;
            case 'D':
                tempView = new Dude(context);
                break;
            case '1'://move up
                moves.add(1);
                break;
            case '2'://move right
                moves.add(2);
                break;
            case '3'://move down
                moves.add(3);
                break;
            case '4'://move left
                moves.add(4);
                break;
        }
        return tempView;
    }
    public int getLeft(View view){
        View temp = view;
        int [] viewCoords = new int[2];
        temp.getLocationOnScreen(viewCoords);

        return viewCoords[0];
    }
    public int getTableLeft(TableLayout view){
        TableLayout temp = view;
        int [] viewCoords = new int[2];
        temp.getLocationOnScreen(viewCoords);

        return viewCoords[0];
    }
    public int getTop(View view){
        View temp = view;
        int [] viewCoords = new int[2];
        temp.getLocationOnScreen(viewCoords);
        return viewCoords[1];
    }
    public int getRight(View view){
        return getLeft(view)+view.getMeasuredWidth();
    }
    public int getTableRight(TableLayout view){
        return getLeft(view)+view.getMeasuredWidth();
    }
    public int getBottom(View view){
        return getTop(view)+view.getMeasuredHeight();
    }
}
