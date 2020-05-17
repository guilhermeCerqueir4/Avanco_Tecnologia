package avc.com.avanco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import avc.com.avanco.prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText cpfEditText, nameEditText, phoneEditText, addressEditText, cepEditText;
    private Button confirmOrderBtn;
    private String totalPedido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


        totalPedido = getIntent().getStringExtra("Total Price");

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        cpfEditText = findViewById(R.id.shipment_cpf);
        nameEditText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        addressEditText = findViewById(R.id.shipment_address);
        cepEditText = findViewById(R.id.shipment_cep);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });

    }

    private void Check() {

        if (TextUtils.isEmpty(cpfEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu CPF.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu nome completo.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu celular.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu endereço.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(cepEditText.getText().toString())){
            Toast.makeText(this, "Por favor, digitar seu CEP.", Toast.LENGTH_SHORT).show();
        }
        else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Calendar calforDate =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat( "MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calforDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat( "HH:mm:ss a");
        saveCurrentTime = currentTime.format(calforDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalPedido", totalPedido );
        ordersMap.put("cpf", cpfEditText.getText().toString());
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("cep", cepEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("status", "Aguardando Pagamento");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Seu pedido foi processado com sucesso.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                }
            }
        });

    }


}
