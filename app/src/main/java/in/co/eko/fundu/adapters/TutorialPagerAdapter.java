package in.co.eko.fundu.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.eko.fundu.R;
import in.co.eko.fundu.interfaces.SocialListener;


/**
 * Created by zartha on 7/17/17.
 */

public class TutorialPagerAdapter extends PagerAdapter{

    private Context context;

    SocialListener listener;

    public TutorialPagerAdapter(Context context,SocialListener listener) {
        super();
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int resId = 0;
        switch (position) {
            case 0:
                resId = R.layout.fragment_get_cash;
                break;
            case 1:
                resId = R.layout.fragment_help_others_tutorial;
                break;
            case 2:
                resId = R.layout.completely_safe_secure;
                break;
            case 3:
                resId = R.layout.fragment_locate_atm_tutorial;
                break;
        }
        View view = inflater.inflate(resId, null);
        if(position == 0){
//            ImageView iCurrencyLogo = (ImageView) view.findViewById(R.id.img_getcash);
//            if(Utils.getCountryID().equalsIgnoreCase("IN"))
//            {
//                iCurrencyLogo.setImageResource(R.drawable.ic_getcash);
//            }
        }
        container.addView(view, 0);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }




}