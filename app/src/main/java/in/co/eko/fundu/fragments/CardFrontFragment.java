package in.co.eko.fundu.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import in.co.eko.fundu.R;

import static in.co.eko.fundu.utils.CreditCardUtils.AMEX;
import static in.co.eko.fundu.utils.CreditCardUtils.DISCOVER;
import static in.co.eko.fundu.utils.CreditCardUtils.MASTERCARD;
import static in.co.eko.fundu.utils.CreditCardUtils.NONE;
import static in.co.eko.fundu.utils.CreditCardUtils.VISA;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFrontFragment extends Fragment {


    TextView tvNumber;
   TextView tvName;
    TextView tvValidity;
    ImageView ivType;

   // FontTypeChange fontTypeChange;

    public CardFrontFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_card_front, container, false);

        tvNumber = (TextView)view.findViewById(R.id.tv_card_number);
        tvName = (TextView)view.findViewById(R.id.tv_member_name);
        tvValidity = (TextView)view.findViewById(R.id.tv_validity);
        ivType = (ImageView) view.findViewById(R.id.ivType);
       /* fontTypeChange=new FontTypeChange(getActivity());
        tvNumber.setTypeface(fontTypeChange.get_fontface(3));
        tvName.setTypeface(fontTypeChange.get_fontface(3));*/

        return view;
    }

    public TextView getNumber()
    {
        return tvNumber;
    }
    public TextView getName()
    {
        return tvName;
    }
    public TextView getValidity()
    {
        return tvValidity;
    }

    public ImageView getCardType()
    {
        return ivType;
    }


    public void setCardType(int type)
    {
        switch(type)
        {
            case VISA:
                ivType.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visa));
                break;
            case MASTERCARD:
                ivType.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.mastercard));
                break;
            case AMEX:
                ivType.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.amex));
                break;
            case DISCOVER:
                ivType.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.discover_card));
                break;
            case NONE:
                ivType.setImageResource(android.R.color.transparent);
            break;

        }


    }


}
