package in.co.eko.fundu.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.models.FunduUser;
import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.AppPreferences;
import in.co.eko.fundu.utils.Fog;


public class CompleteYourProfileActivity extends AppCompatActivity implements View.OnClickListener {



    private ImageView imgProfilePic, imgEditProfileImage;
    private Button btnConfirm;
    private EditText dispalyName ;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String imagePath;

    private static final int CAMERA_REQUEST = 111;
    private static final int RESULT_LOAD_IMAGE = 112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_your_profile);
        init();
        setOnClickListeners();


    }

    private boolean checkCameraPermission() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        int camera  = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read    = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE
            );
        }


        if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
            return true;
    }
    private void setOnClickListeners() {

        imgEditProfileImage.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    private void init() {
        AppPreferences appPreferences = FunduUser.getAppPreferences();
        imgProfilePic = (ImageView)findViewById(R.id.img_profile_pic);
        dispalyName   = (EditText) findViewById(R.id.edittext_enter_name);
        imgEditProfileImage = (ImageView)findViewById(R.id.img_edit_profile_image);
        btnConfirm = (Button)findViewById(R.id.btn_confirm);
        imgEditProfileImage.setVisibility(View.GONE);
        String name = FunduUser.getFullName();
        //String imagepath = AppPreferences.getInstance(this).getString(Constants.PROFILEIMAGEPATH);
        String imagepath = appPreferences.getString(Constants.PROFILE_PIC_URL);
        if(imagepath.equalsIgnoreCase("")||imagepath.isEmpty()){

        }
        else{
            Picasso.with(this).load(imagepath).resize(300,300).into(imgProfilePic);

        }


        dispalyName.setText(FunduUser.getFullName());
        dispalyName.setSelection(dispalyName.length());
        /*if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(imagepath)){


            dispalyName.setSelection(dispalyName.length());
            File file = new File(imagepath);
            //imgProfilePic.setImageURI(Uri.fromFile(file));
            Picasso.with(this).load(file).into(imgProfilePic);
            Fog.d("imagepath","imagepath"+imagepath);
            Fog.d("imagepath","file"+file);

        }
        else {*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //checkCameraPermission();
            }

        //}
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.img_edit_profile_image:

                showImagePickerDialog();
                break;

            case R.id.btn_confirm:

                if(!TextUtils.isEmpty(dispalyName.getText().toString())){

                    AppPreferences.getInstance(this).putString(Constants.NAME,dispalyName.getText().toString());
                    Toast.makeText(this, "Profile Data has been saved sucessfully.", Toast.LENGTH_SHORT).show();
                    callUpdateProfileService();
                    finish();

                }
                else if(TextUtils.isEmpty(dispalyName.getText().toString())) {
                    Toast.makeText(this, "Please enter all the information required to proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void callUpdateProfileService() {



    }

    private void showImagePickerDialog() {


        final CharSequence[] items = { "Take Photo", "Choose from Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    //userChoosenTask="Take Photo";

                        cameraIntent();

                    dialog.dismiss();
                } else if (items[item].equals("Choose from Library")) {
                    //userChoosenTask="Choose from Library";

                        galleryIntent();

                                     dialog.cancel();

                }
            }
        });


        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });



        builder.show();

    }

    private void cameraIntent()
    {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);*/
        Intent intent = new Intent(CompleteYourProfileActivity.this, TakeImage.class);
        intent.putExtra("from", "camera");
        startActivityForResult(intent, CAMERA_REQUEST);

    }

    private void galleryIntent()
    {
        /*Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);*/

        Intent intent = new Intent(CompleteYourProfileActivity.this, TakeImage.class);
        intent.putExtra("from", "gallery");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == CAMERA_REQUEST || requestCode == RESULT_LOAD_IMAGE)) {

            File imgFile = new File(data.getStringExtra("filePath"));
            if (imgFile.exists()) {

                try {
                    Picasso.with(this).load(Uri.fromFile(imgFile)).into(imgProfilePic);
                   // imgProfilePic.setImageURI(Uri.fromFile(imgFile));
                   // imagePath = FileUtils.readFileToString(imgFile);
                    Fog.d("str", "str" + imagePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Image path not available, please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   imgEditProfileImage.setEnabled(true);

                } else {

                    imgEditProfileImage.setEnabled(false);
                }
                return;
        }
    }













}
