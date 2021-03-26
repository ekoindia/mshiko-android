package in.co.eko.fundu.parser;
/*
 * Created by Bhuvnesh
 */

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import in.co.eko.fundu.models.Neighbour;
import in.co.eko.fundu.utils.Fog;

public class GetNeighborsParserTask extends AsyncTask<String, String, ArrayList<Neighbour>> {

    @Override
    protected ArrayList<Neighbour> doInBackground(String[] params) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return new ArrayList<>(Arrays.asList(gson.fromJson(params[0], Neighbour[].class)));
    }

    @Override
    protected void onPostExecute(ArrayList<Neighbour> arrayList) {
        super.onPostExecute(arrayList);
        Fog.d("Parser-->", "Size-> "+arrayList.size());

    }
}
