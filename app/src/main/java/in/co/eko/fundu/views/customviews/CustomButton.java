package in.co.eko.fundu.views.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;

import in.co.eko.fundu.R;

/**
 * Created by pallavi on 8/10/17.
 */

public class CustomButton extends Button {



    private Typeface typeface;

    public CustomButton(Context context, @Nullable AttributeSet attrs) {
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
