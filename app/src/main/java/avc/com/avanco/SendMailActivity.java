package avc.com.avanco;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class SendMailActivity extends AppCompatActivity {

    String fromEmail, fromPassword, emailSubject, emailBody, toEmails;
    private ProgressDialog loadingBar;

    private Button send;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        loadingBar = new ProgressDialog(this);

        getSupportActionBar().setTitle("Faça seu contato");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.suporte);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.products:

                        startActivity(new Intent(getApplicationContext(), HomeeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.settings:

                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        send = (Button) this.findViewById(R.id.button1);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                ConfirmSuporte();

            }
        });
    }


    private void ConfirmSuporte() {

        Log.i("SendMailActivity", "Send Button Clicked.");

        fromEmail = ((TextView) findViewById(R.id.editText1))
                .getText().toString();
        fromPassword = ((TextView) findViewById(R.id.editText2))
                .getText().toString();
        toEmails = "avancotech.app@gmail.com" ;
        final List<String> toEmailList = Arrays.asList(toEmails
                .split("\\s*,\\s*"));
        Log.i("SendMailActivity", "To List: " + toEmailList);
        emailSubject = ((TextView) findViewById(R.id.editText4))
                .getText().toString();
        emailBody = ((TextView) findViewById(R.id.editText5))
                .getText().toString();


        if (TextUtils.isEmpty(fromEmail)){
            Toast.makeText(this, "Por favor, digitar seu e-mail Google.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fromPassword)){
            Toast.makeText(this, "Por favor, digitar sua senha do e-mail.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emailSubject)){
            Toast.makeText(this, "Por favor, digitar o assunto do e-mail.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emailBody)){
            Toast.makeText(this, "Por favor, digitar o corpo do email.", Toast.LENGTH_SHORT).show();
        }

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            new SendMailTask(SendMailActivity.this).execute(fromEmail,
                                    fromPassword, toEmailList, emailSubject, emailBody);

                            Toast.makeText(SendMailActivity.this, "Obrigado! Responderemos em breve.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeeActivity.class));
                            overridePendingTransition(0,0);
                            finish();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(SendMailActivity.this);
            builder.setMessage("Confirmar envio de suporte?").setPositiveButton("SIM", dialogClickListener)
                    .setNegativeButton("NÃO", dialogClickListener).show();
        }


    }


    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_cart) {

            startActivity(new Intent(getApplicationContext(), CartActivity.class));
            overridePendingTransition(0,0);
        }

        if(id == R.id.action_logout) {


            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            overridePendingTransition(0,0);

                            loadingBar.setTitle("Fazendo Logoff...");
                            loadingBar.setMessage("Por favor aguarde");
                            loadingBar.setCanceledOnTouchOutside(false);
                            loadingBar.show();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(SendMailActivity.this);
            builder.setMessage("Deseja sair da conta?").setPositiveButton("SIM", dialogClickListener)
                    .setNegativeButton("NÃO", dialogClickListener).show();

        }




        return true;

    }


}