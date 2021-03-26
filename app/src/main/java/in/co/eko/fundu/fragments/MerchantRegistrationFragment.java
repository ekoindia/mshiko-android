package in.co.eko.fundu.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.HomeActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.Contact;
import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.requests.VerifyMerchantAgent;
import in.co.eko.fundu.utils.Fog;
import in.co.eko.fundu.utils.GPSTracker;
import in.co.eko.fundu.utils.Utils;

/**
 * Created by Rahul on 3/21/17.
 */

public class MerchantRegistrationFragment extends BaseFragment implements VerifyMerchantAgent.OnVerifyMerchantAgentResults {

    private EditText contact_name, contact_number, bussiness_name, incorporate_number, address, shopLocation;
    //    private Switch allow_withdraw;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Contact mParam1;
    private ProgressDialog dialog;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    private Spinner spinnerverticalMarket, spinnerbussinessType;
    private RelativeLayout relativeMap;
    private Button verifyButton;
    private String switch_value = "No";
    private GPSTracker gpsTracker = null;
    View view1;
    private int PICK_IMAGE_REQUEST = 1;
    TextView texterror;
    private VerifyMerchantAgent verifyMerchantAgentRequest;
    private Location location;
    private ImageView merchant_pic;
    boolean becomemercahnt = false;
    String base64string = null, phoneNo = null;

    public static MerchantRegistrationFragment newInstance(Contact param1, String param2) {
        MerchantRegistrationFragment fragment = new MerchantRegistrationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MerchantRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity parentActivity = getActivity ();
        if(parentActivity != null && parentActivity instanceof HomeActivity){
            ((HomeActivity)parentActivity).hideHamburgerIcon ();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Contact) getArguments().getSerializable(ARG_PARAM1);
            if (mParam1 != null)
                phoneNo = mParam1.getContactId();
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
//            HomeActivity.toolbar.setVisibility(View.GONE);
            becomemercahnt = bundle.getBoolean("becomemerchant");
        }
        if (becomemercahnt)
            phoneNo = FunduUser.getContactId();
        verifyMerchantAgentRequest = new VerifyMerchantAgent(getActivity());
        verifyMerchantAgentRequest.setParserCallback(this);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.isGPSEnabled())
            location = gpsTracker.getLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_merchant_registration, container, false);
        spinnerverticalMarket = (Spinner) view.findViewById(R.id.verticalMarket);
        spinnerbussinessType = (Spinner) view.findViewById(R.id.bussinessType);
        contact_name = (EditText) view.findViewById(R.id.personName);
        contact_number = (EditText) view.findViewById(R.id.personNumber);
        bussiness_name = (EditText) view.findViewById(R.id.bussinessName);
        incorporate_number = (EditText) view.findViewById(R.id.incorporateNumber);
        address = (EditText) view.findViewById(R.id.address);
        merchant_pic = (ImageView) view.findViewById(R.id.merchant_pic);
        merchant_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= 23){
//                    // Here, thisActivity is the current activity
//                    if (ContextCompat.checkSelfPermission(getActivity(),
//                            Manifest.permission.READ_EXTERNAL_STORAGE)
//                            != PackageManager.PERMISSION_GRANTED) {
//
//                        // Should we show an explanation?
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                            // Show an expanation to the user *asynchronously* -- don't block
//                            // this thread waiting for the user's response! After the user
//                            // sees the explanation, try again to request the permission.
//
//                        } else {
//
//                            // No explanation needed, we can request the permission.
//
//                            ActivityCompat.requestPermissions(getActivity(),
//                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//
//                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
//                            // app-defined int constant. The callback method gets the
//                            // result of the request.
//                        }
//                    }else{
//                        ActivityCompat.requestPermissions(getActivity(),
//                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                    }
//                }else {
//                    showFileChooser();
//                }

            }
        });
//        shopLocation = (EditText) view.findViewById(R.id.shoplocation);
//        relativeMap = (RelativeLayout) view.findViewById(R.id.relative_gotomap);
//        shopLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getContext(), MapShopAddressFragment.class);
//                startActivity(in);
//            }
//        });
//        relativeMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getContext(), MapShopAddressFragment.class);
//                startActivity(in);
//            }
//        });

        contact_name.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
                        Fog.e("DD", src + "start" + start + "end" + end + "dstart" + dstart + "dend" + dend);

                        if (dend > 20) {
                            return "";
                        } else if (src.equals("")) {
                            return src;
                        } else if (src.toString().matches("[a-zA-Z0-9 ]+")) {
                            return src;
                        }

                        return "";
                    }

                }
        });

//        bussiness_name.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (src.equals("")) {
//                            return src;
//                        }
//                        if (src.toString().matches("[a-zA-Z1-9 ]+")) {
//                            return src;
//                        }
//                        return "";
//                    }
//                }
//        });
//        allow_withdraw = (Switch) view.findViewById(R.id.allow_withdraw);
        verifyButton = (Button) view.findViewById(R.id.verifyButton);
//        allow_withdraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch_value = isChecked ? "Yes" : "No";
//            }
//        });

        List<String> bussinesstypelist = new ArrayList<String>();
        bussinesstypelist.add("Company");
        bussinesstypelist.add("Partnership");
        bussinesstypelist.add("Sole Proprietor");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, bussinesstypelist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbussinessType.setAdapter(dataAdapter);
        List<String> verticalMarketlist = new ArrayList<String>();
//        String vertical_market[] = {"Chemist", "Shop", "Supermarket", "Fuel Station", "Salon", "Barber Shop",
//                "Hardware Shop", "Studio", "Office", "Restaurant", "Hotel", "Kiosk" };
        verticalMarketlist.add("Chemist");
        verticalMarketlist.add("Shop");
        verticalMarketlist.add("Supermarket");
        verticalMarketlist.add("Fuel Station");
        verticalMarketlist.add("Salon");
        verticalMarketlist.add("Barber Shop");
        verticalMarketlist.add("Hardware Shop");
        verticalMarketlist.add("Studio");
        verticalMarketlist.add("Office");
        verticalMarketlist.add("Restaurant");
        verticalMarketlist.add("Hotel");
        verticalMarketlist.add("Kiosk");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, verticalMarketlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerverticalMarket.setAdapter(adapter);

        String addresss = null;
        if (location != null) {
            addresss = Utils.getAddress(getContext(), location.getLatitude(), location.getLongitude()); /*707050858*/
        }
        address.setText(addresss);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newaddress = address.getText().toString().trim();
                incorporate_number.setText(incorporate_number.getText().toString().trim());
                contact_name.setText(contact_name.getText().toString().trim());
                if (bussiness_name.length() < 1) {
                    Toast.makeText(getContext(), "Enter business name", Toast.LENGTH_SHORT).show();
                } else if (incorporate_number.length() < 1) {
                    Toast.makeText(getContext(), "Enter incorporation number", Toast.LENGTH_SHORT).show();
                } else if (contact_name.length() < 1) {
                    Toast.makeText(getContext(), "Enter contact person name", Toast.LENGTH_SHORT).show();
                } else if (contact_number.length() < 1) {
                    Toast.makeText(getContext(), "Enter person's contact number", Toast.LENGTH_SHORT).show();
                } else if (address.length() < 1 || newaddress.equalsIgnoreCase(" ") || newaddress.isEmpty()) {
                    Toast.makeText(getContext(), "Enter shop address", Toast.LENGTH_SHORT).show();
                } else {
                    gotoVerify();
                }

            }
        });
        return view;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onButtonPressed(Bundle bundle) {
        super.onButtonPressed(bundle);
//        HomeActivity.toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    showFileChooser();
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            base64string = null;
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                merchant_pic.setImageBitmap(bitmap);
//                merchant_pic.setScaleType(ImageView.ScaleType.FIT_XY);
//                Fog.i("Base64", getStringImage(bitmap));
                base64string = getStringImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    // Decodes image and scales it to reduce memory consumption
//    private Bitmap decodeFile(File f) {
//        try {
//            // Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//
//            // The new size we want to scale to
//            final int REQUIRED_SIZE=70;
//
//            // Find the correct scale value. It should be the power of 2.
//            int scale = 1;
//            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
//
//            // Decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
//            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
//        } catch (FileNotFoundException e) {}
//        return null;
//    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    void gotoVerify() {
        if (Utils.isNetworkAvailable(getContext())) {
            Utils.hideSoftKeyboard(getActivity());
            dialog.show();
            verifyMerchantAgentRequest.setData(FunduUser.getCountryShortName(), phoneNo, bussiness_name.getText().toString(),
                    incorporate_number.getText().toString(), spinnerbussinessType.getSelectedItem().toString(),
                    spinnerverticalMarket.getSelectedItem().toString(), contact_name.getText().toString(), contact_number.getText().toString(),
                    null/*switch_value*/, "07:00", "20:00", "SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY", address.getText().toString(), base64string, becomemercahnt);
            verifyMerchantAgentRequest.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onVerifyMerchantAgentResponse(String object) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(object);
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");

            if (status.equalsIgnoreCase("ERROR")) {
                Utils.showLongToast(getContext(), message);
            } else {
                pref.putString(Constants.DAYS, "SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY");
                pref.putString(Constants.OPENING_TIME, "07:00");
                pref.putString(Constants.CLOSING_TIME, "20:00");
                if (becomemercahnt) {
                    JSONObject contactdata = new JSONObject(jsonObject.optString("data"));
                    Utils.showLongToast(getContext(), "Merchant Verified");
                    pref.putString(Constants.CONTACT_TYPE_PA, "AGENT");
                    pref.putString(Constants.NAME, contactdata.optString("business_name"));
                    pref.putString(Constants.ALLOW_WITHDRAW, contactdata.optString(Constants.ALLOW_WITHDRAW));

                    Intent intent = new Intent(Constants.HOME_ACTIVITY_ACTION);
                    intent.putExtra(Constants.IS_USER_LOGGED_IN, true);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                    Fragment fragment = new SettingsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();

                } else {
                    Utils.hideSoftKeyboard(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putString(FRAGMENT_NAME, "CreateFunduPinFragment");
                    bundle.putSerializable(Contact.class.getSimpleName(), mParam1);
                    onButtonPressed(bundle);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onVerifyMerchantAgentError(VolleyError error) {
        dialog.dismiss();
        Utils.showLongToast(getContext(), error.getMessage());
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
