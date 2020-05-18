package avc.com.avanco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CategoryActivity extends AppCompatActivity {
    GridView gridView ;
    String[] numberWord = {"Placas-Mãe", "Placas de Vídeo", "Processadores","Discos (HD)"};
    int[] numberImage = {R.drawable.categorymotherboard, R.drawable.categoryvideocard, R.drawable.categoryprocessor, R.drawable.categoryhd};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);



        gridView = findViewById(R.id.grid_view);
         CategoryAdapter adapter = new CategoryAdapter(CategoryActivity.this, numberWord, numberImage);
         gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (numberWord[+position].equals("Placas-Mãe")) {
                    Intent intent = new Intent(CategoryActivity.this, PlacaMaeActivity.class);
                    startActivity(intent);
                }
                if (numberWord[+position].equals("Placas de Vídeo")) {
                    Intent intent = new Intent(CategoryActivity.this, PlacaVideoActivity.class);
                    startActivity(intent);
                }
                if (numberWord[+position].equals("Processadores")) {
                    Intent intent = new Intent(CategoryActivity.this, ProcessadorActivity.class);
                    startActivity(intent);
                }
                if (numberWord[+position].equals("Discos (HD)")) {
                    Intent intent = new Intent(CategoryActivity.this, DiscoRigidoActivity.class);
                    startActivity(intent);
                }

            }


        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){


                    case R.id.products:
                        finish();
                        startActivity(new Intent(getApplicationContext(), HomeeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.settings:
                        finish();
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
/*
                    case R.id.logout:
                        loadingBar.setTitle("Sair");
                        loadingBar.setMessage("Fazendo logoff...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        overridePendingTransition(0,0);
                        return true;*/



                }
                return false;
            }
        });
    }
}
