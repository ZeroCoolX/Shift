package lucky8s.shift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Christian on 10/20/2015.
 */
public class PromoCodes {

    public static ArrayList<String> UNLOCK_LEVEL = new ArrayList<>();
    public static ArrayList<String> UNLOCK_NO_ADS = new ArrayList<>();
    public static ArrayList<String> UNLOCK_ALL = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_500 = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_1000 = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_2000 = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_5000 = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_10000 = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_20000 = new ArrayList<>();
    public static ArrayList<String> FREE_SILVER_50000 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_10 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_25 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_50 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_100 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_200 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_500 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_1000 = new ArrayList<>();
    public static ArrayList<String> FREE_GOLD_3000 = new ArrayList<>();
    public static ArrayList<String> SPEED_1 = new ArrayList<>();
    public static ArrayList<String> SPEED_2 = new ArrayList<>();
    public static ArrayList<String> SPEED_3 = new ArrayList<>();
    public static ArrayList<String> SPEED_4 = new ArrayList<>();
    public static ArrayList<String> SPEED_5 = new ArrayList<>();
    public static ArrayList<String> SPEED_6 = new ArrayList<>();
    public static ArrayList<String> SPEED_7 = new ArrayList<>();
    public static ArrayList<String> SPEED_8 = new ArrayList<>();
    public static ArrayList<String> SPEED_9 = new ArrayList<>();
    public static ArrayList<String> SPEED_10 = new ArrayList<>();
    public static ArrayList<String> CLEAR_USED = new ArrayList<>();
    public static HashMap<String, HashMap<Integer, ArrayList<String>>> categories = new HashMap<>();
    public static HashMap<Integer, ArrayList<String>> levels = new HashMap<>();
    public static HashMap<Integer, ArrayList<String>> coins = new HashMap<>();
    public static HashMap<Integer, ArrayList<String>> boosts = new HashMap<>();
    public static HashMap<Integer, ArrayList<String>> admin = new HashMap<>();

    public static void initialize() {
        categories.put("levels", levels);
        levels.put(1, UNLOCK_LEVEL);
        levels.put(2, UNLOCK_NO_ADS);
        levels.put(3, UNLOCK_ALL);


        categories.put("coins", coins);
        coins.put(1, FREE_SILVER_500);
        coins.put(2, FREE_SILVER_1000);
        coins.put(3, FREE_SILVER_2000);
        coins.put(4, FREE_SILVER_5000);
        coins.put(5, FREE_SILVER_10000);
        coins.put(6, FREE_SILVER_20000);
        coins.put(7, FREE_SILVER_50000);
        coins.put(8, FREE_GOLD_10);
        coins.put(9, FREE_GOLD_25);
        coins.put(10, FREE_GOLD_50);
        coins.put(11, FREE_GOLD_100);
        coins.put(12, FREE_GOLD_200);
        coins.put(13, FREE_GOLD_500);
        coins.put(14, FREE_GOLD_1000);
        coins.put(15, FREE_GOLD_3000);

        categories.put("boosts", boosts);
        boosts.put(1, SPEED_1);
        boosts.put(2, SPEED_2);
        boosts.put(3, SPEED_3);
        boosts.put(4, SPEED_4);
        boosts.put(5, SPEED_5);
        boosts.put(6, SPEED_6);
        boosts.put(7, SPEED_7);
        boosts.put(8, SPEED_8);
        boosts.put(9, SPEED_9);
        boosts.put(10, SPEED_10);

        categories.put("admin", admin);
        admin.put(1, CLEAR_USED);

    }
    public static String getUnusedCode(Set<String> usedCodes, String category, String item){
        HashMap<Integer, ArrayList<String>> catTemp = categories.get(category);
        ArrayList<String> codes = catTemp.get(getKey(category,item));
        for(int i = 0; i < codes.size(); i++){
            if(usedCodes == null || !usedCodes.contains(codes.get(i))){
                return codes.get(i);
            }
        }
        return "";
    }
    public static int getKey(String category, String item){
        switch(category) {
            case "levels":
                switch (item) {
                    case "level":
                        return 1;
                    case "level_no_ads":
                        return 2;
                    case "all_levels":
                        return 3;
                }
                break;
            case "coins":
                switch (item) {
                    case "silver_500":
                        return 1;
                    case "silver_1000":
                        return 2;
                    case "silver_2000":
                        return 3;
                    case "silver_5000":
                        return 4;
                    case "silver_10000":
                        return 5;
                    case "silver_20000":
                        return 6;
                    case "silver_50000":
                        return 7;
                    case "gold_10":
                        return 8;
                    case "gold_25":
                        return 9;
                    case "gold_50":
                        return 10;
                    case "gold_100":
                        return 11;
                    case "gold_200":
                        return 12;
                    case "gold_500":
                        return 13;
                    case "gold_1000":
                        return 14;
                    case "gold_3000":
                        return 15;

                }
                break;
            case "boosts":
                switch (item){
                    case "speed1":
                        return 1;
                    case "speed2":
                        return 2;
                    case "speed3":
                        return 3;
                    case "speed4":
                        return 4;
                    case "speed5":
                        return 5;
                    case "speed6":
                        return 6;
                    case "speed7":
                        return 7;
                    case "speed8":
                        return 8;
                    case "speed9":
                        return 9;
                    case "speed10":
                        return 10;
                }
                break;
            case "admin":
                switch (item){
                    case "clear":
                        return 1;
                }
                break;
        }
        return 0;
    }
}
