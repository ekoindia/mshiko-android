package in.co.eko.fundu.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import in.co.eko.fundu.R;
import in.co.eko.fundu.models.Contact;


public class IdentificationFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Contact mParam1;
    private String mParam2;

    public static IdentificationFragment newInstance(Contact param1, String param2) {
        IdentificationFragment fragment = new IdentificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public IdentificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_identification, container, false);
        Button nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton :
                Bundle bundle = new Bundle();
                bundle.putString(FRAGMENT_NAME,"EnterPhoneNumberFragment");
                bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
                onButtonPressed(bundle);
                break;

        }
    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
