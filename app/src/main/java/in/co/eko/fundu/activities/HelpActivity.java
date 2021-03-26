package in.co.eko.fundu.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.HelpListAdapter;

public class HelpActivity extends AppCompatActivity {



    RecyclerView helpList;
    String helpListArray[] ;
    ImageView back;
    HelpListAdapter helpListAdapter;
    private static LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        helpList = (RecyclerView)findViewById(R.id.helpList);
        back = (ImageView) findViewById(R.id.imageview_back);
        helpListArray = getResources().getStringArray(R.array.helpList);
        helpListAdapter = new HelpListAdapter(helpListArray,this);
        mLayoutManager = new LinearLayoutManager(this);
        helpList.setLayoutManager(mLayoutManager);
        helpList.setAdapter(helpListAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
