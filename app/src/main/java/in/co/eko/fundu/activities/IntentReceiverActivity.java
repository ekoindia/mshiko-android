package in.co.eko.fundu.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;


import in.co.eko.fundu.R;
import in.co.eko.fundu.constants.Constants;

/**
 * Created by zartha on 4/3/18.
 */

public class IntentReceiverActivity extends Activity {

    private static final int REQUEST_INTENT_TRANSACTION = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mid = Constants.mid;
        String merchantKey = Constants.merchantKey;

        Bundle bundle = new Bundle();
        bundle.putString("mid", mid);
        bundle.putString("merchantKey", merchantKey);
        bundle.putString("intentData",getIntent().getData().toString());

        Toast.makeText(this, R.string.receive_add, Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(IntentReceiverActivity .this,IntentHandlerActivity.class);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, REQUEST_INTENT_TRANSACTION);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INTENT_TRANSACTION && resultCode == Activity.RESULT_OK
                && data != null) {
            Intent responseIntent = new Intent();
            responseIntent.putExtra("response", data.getStringExtra("response"));
            setResult(Activity.RESULT_OK, responseIntent);
            finish();
        }
    }
}
