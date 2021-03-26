package in.co.eko.fundu.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import in.co.eko.fundu.R;
import in.co.eko.fundu.adapters.FaqListAdapter;

public class Faq extends AppCompatActivity {


    RecyclerView faqList;
    ImageView back;
    FaqListAdapter faqListAdapter;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        faqList = (RecyclerView)findViewById(R.id.recyclerview);
        back = (ImageView)findViewById(R.id.imageview_back);
    }
}
