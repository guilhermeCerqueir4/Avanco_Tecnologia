package avc.com.avanco;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FinishPedidoActivity extends AppCompatActivity {


    private Button voltarInicioBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_pedido);

        voltarInicioBtn = findViewById(R.id.volta_inicio_btn);

        getSupportActionBar().setTitle("Parabéns!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        voltarInicioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FinishPedidoActivity.this, HomeeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

    }



}