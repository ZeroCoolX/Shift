package lucky8s.shift;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;

import java.lang.ref.Reference;

/**
 * Created by Christian on 12/6/2015.
 */
public class Portal extends View {

    Portal brother;
    int brotherInt;
    Context context;

    public Portal(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public Portal(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(attrs);
    }

    public Portal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Portal);
            a.recycle();
        }
        setBackgroundResource(R.drawable.play_portal);
    }
    public Portal getBrother(){
        return brother;
    }
    public void setBrotherInt(int brotherInt){
        this.brotherInt = brotherInt;
    }
    public int getBrotherInt(){
        return brotherInt;
    }
    public void setBrother(Portal brother){
        this.brother = brother;
    }
}
