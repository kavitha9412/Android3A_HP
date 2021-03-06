package com.example.android3a_hp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android3a_hp.R.id.recycler_view;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://raw.githubusercontent.com/kavitha9412/Android3A_HP/master/";

    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("application_esiea",Context.MODE_PRIVATE);

        gson = new GsonBuilder()
                .setLenient()
                .create();

        List<HP_personnage> hp_personnageList = getDataFromCache();
        if(hp_personnageList !=null) {
            showList(hp_personnageList);
        } else {
            makeApiCall();
        }

        
    }

    private List<HP_personnage> getDataFromCache() {
        String jsonHP_personnage = sharedPreferences.getString(Constants.KEY_HP_Personnage_LIST, null);

        if (jsonHP_personnage == null) {
            return null;
        } else {
        Type listType = new TypeToken<List<HP_personnage>>(){}.getType();
        return gson.fromJson(jsonHP_personnage, listType);
        }
    }

    private void showList(List<HP_personnage> hp_personnageList) {
        //Affichage liste
        recyclerView = (RecyclerView) findViewById(recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



       // define an adapter
        mAdapter = new ListAdapter(hp_personnageList);
        recyclerView.setAdapter(mAdapter);
    }


    private void makeApiCall() {



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        HpApi HpApi = retrofit.create(HpApi.class);


        Call<List<HP_personnage>> call = HpApi.getHP_personnage();
        call.enqueue(new Callback<List<HP_personnage>>() {
            @Override
            public void onResponse(Call<List<HP_personnage>> call, Response<List<HP_personnage>> response) {

                if(response.isSuccessful() && response.body() != null){
                        List<HP_personnage> hp_personnageList = response.body();
                        saveList(hp_personnageList);
                        showList(hp_personnageList);
                } else {
                    showError();
                }

            }

            @Override
            public void onFailure(Call<List<HP_personnage>> call, Throwable t) {
                showError();

            }
        });

    }

    private void saveList(List<HP_personnage> hp_personnageList) {
        String jsonString = gson.toJson(hp_personnageList);
        sharedPreferences
                .edit()
                .putString(Constants.KEY_HP_Personnage_LIST,jsonString)
                .apply();
        Toast.makeText(getApplicationContext(), "List Saved", Toast.LENGTH_SHORT).show();
    }


    private void showError() {
        Toast.makeText(getApplicationContext(), "API Error", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
