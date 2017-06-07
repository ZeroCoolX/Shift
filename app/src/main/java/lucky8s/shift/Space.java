package lucky8s.shift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;

/**
 * Created by Christian on 12/30/2015.
 */
public class Space extends View {

    public Space(Context context) {
        super(context);
        init();
    }

    public Space(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Space(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public void init(){
        this.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT));
    }

}

