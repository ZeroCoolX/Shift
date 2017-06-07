package lucky8s.shift;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Christian on 11/2/2015.
 */
public class    EtchView extends TextView {

    Context context;
    float skewedX;
    float skewedY;
    float skewedZ;
    Camera mCamera;
    Matrix mMatrix;
    String pivotPoint;

    public EtchView(Context context) {
        super(context);
        mCamera = new Camera();
        mMatrix = new Matrix();
        this.context = context;
    }

    public EtchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCamera = new Camera();
        mMatrix = new Matrix();
        init(attrs);
        this.context = context;
    }

    public EtchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCamera = new Camera();
        mMatrix = new Matrix();
        init(attrs);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas = applyMatrix(canvas);
        super.onDraw(canvas);
        canvas.restore();

    }
    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EtchView);
            String skewX = a.getString(R.styleable.EtchView_skewX);
            String skewY = a.getString(R.styleable.EtchView_skewY);
            String skewZ = a.getString(R.styleable.EtchView_skewZ);
            pivotPoint = a.getString(R.styleable.EtchView_pivotPoint);
            if (!skewX.equals("")) {
                skewedX = (float) Integer.valueOf(skewX);
            }
            if(!skewY.equals("")){
                skewedY = (float) Integer.valueOf(skewY);
            }
            if(!skewZ.equals("")){
                skewedZ = (float) Integer.valueOf(skewZ);
            }
            a.recycle();
        }
    }
    public Canvas applyMatrix(Canvas canvas) {
        mCamera.save();
        mCamera.rotateX(skewedX);
        mCamera.rotateY(skewedY);
        mCamera.rotateZ(skewedZ);
        mCamera.getMatrix(mMatrix);

        mMatrix.preTranslate((pivotPoint.equals("start") ? -getMeasuredWidth() : -0), -getMeasuredHeight()/2); //This is the key to getting the correct viewing perspective
        mMatrix.postTranslate((pivotPoint.equals("start") ? getMeasuredWidth() : 0), getMeasuredHeight()/2);

        canvas.concat(mMatrix);
        mCamera.restore();

        return canvas;
    }
}