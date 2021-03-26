package in.co.eko.fundu.utils;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.widget.EditText;
import android.widget.TextView;

/*
 * Custom Typeface Manager
 *
 * Created by Bhuvnesh
 */

public class TypefaceManager {

    public static final String OPEN_SANS_BOLD = "OpenSans-Bold.ttf";
    public static final String OPEN_SANS_REGULAR = "OpenSans-Regular.ttf";
    public static final String OPEN_SANS_SEMI_BOLD = "OpenSans-Semibold.ttf";
    public static final String OPEN_SANS_LIGHT = "OpenSans-Light.ttf";
    public static final String MULI_LIGHT = "Muli-Light.ttf";
    public static final String MULI_REGULAR = "Muli-Regular.ttf";
    public static final String MULI_SEMIBOLD = "Muli-SemiBold.ttf";
    private static LruCache<String, Typeface> mCache;
    private static AssetManager mAssetManager;
    private static TypefaceManager mInstance;

    public static synchronized TypefaceManager getInstance(Context context) {
        if (TypefaceManager.mInstance == null) {
            TypefaceManager.mInstance = new TypefaceManager();
        }
        mAssetManager = context.getAssets();
        mCache = new LruCache<>(5);
        return TypefaceManager.mInstance;
    }

    private static Typeface getTypeface(final String filename) {
        Typeface typeface = mCache.get(filename);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mAssetManager, "fonts/" + filename);
            mCache.put(filename, typeface);
        }
        return typeface;
    }

    public Typeface getOpenSansBold() {
        return getTypeface(OPEN_SANS_BOLD);
    }

    public Typeface getOpenSansRegular() {
        return getTypeface(OPEN_SANS_REGULAR);
    }

    public Typeface getOpenSansSemiBold() {
        return getTypeface(OPEN_SANS_SEMI_BOLD);
    }

    public Typeface getOpenSansLight() {
        return getTypeface(OPEN_SANS_LIGHT);
    }


    public Typeface getMuliSemibold() {
        return getTypeface(MULI_SEMIBOLD);
    }

    public Typeface getMuliLight() {
        return getTypeface(MULI_LIGHT);
    }

    public Typeface getMuliRegular() {
        return getTypeface(MULI_REGULAR);
    }

    public void setTypeface(TextView tv, String typefaceName){
        tv.setTypeface(getTypeface(typefaceName));
    }
    public void setTypeface(EditText tv, String typefaceName){
        tv.setTypeface(getTypeface(typefaceName));
    }
}
