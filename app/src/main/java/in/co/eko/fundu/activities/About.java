package in.co.eko.fundu.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.utils.Utils;

public class About extends AppCompatActivity {


    ImageView imageviewClose;
    TextView textviewVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        imageviewClose = (ImageView)findViewById(R.id.imageview_close);
        textviewVersion = (TextView) findViewById(R.id.textview_version);
        textviewVersion.setText(getString(R.string.version)+": "+Utils.getVersionCode());
        imageviewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
