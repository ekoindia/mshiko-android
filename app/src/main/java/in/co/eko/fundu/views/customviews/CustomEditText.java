package in.co.eko.fundu.views.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import in.co.eko.fundu.R;

/**
 * Created by ankit on 31-08-2017.
 */

public class CustomEditText extends EditText {


    private static final String FOINT_NAMESPACE = "font";
    private static final String FOINT_ATTRIBUTE = "fontname";
    private Typeface typeface;


    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.font);
        String fontName = a.getString(R.styleable.font_fontname);
        a.recycle();
        if (fontName != null)
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);

        if (typeface != null)
            try {
                setTypeface(typeface);
            } catch (Exception e) {
                e.printStackTrace();
            }


    }
}
