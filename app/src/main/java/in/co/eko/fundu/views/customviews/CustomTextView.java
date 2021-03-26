package in.co.eko.fundu.views.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import in.co.eko.fundu.R;

/*Created by ankit on 30-08-2017.
*/


public class CustomTextView extends TextView {

    private static final String FOINT_NAMESPACE = "font";
    private static final String FOINT_ATTRIBUTE = "fontname";


    private Typeface typeface;

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
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
