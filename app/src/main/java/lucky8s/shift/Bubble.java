package lucky8s.shift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;

/**
 * Created by Christian on 12/6/2015.
 */
public class Bubble extends View {

    public Bubble(Context context) {
        super(context);
        init();
    }

    public Bubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Bubble(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public void init(){
        setBackgroundResource(R.drawable.play_bubble);
    }

}
