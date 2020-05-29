package avc.com.avanco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import avc.com.avanco.ViewHolder.CartViewHolder;
import avc.com.avanco.model.Cart;
import avc.com.avanco.prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText cpfEditText, nameEditText, phoneEditText, addressEditText, cepEditText;
    private Button confirmOrderBtn;
    private String totalPedido = "";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        getSupportActionBar().setTitle("Dados de Compra");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.shipment_cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        totalPedido = getIntent().getStringExtra("Preço Total");
        Toast.makeText(this, "Total Pedido: " + totalPedido, Toast.LENGTH_SHORT).show();

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
        else if (TextUtils.isEmpty(cepEditText.getText().toString())){
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
                ;

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("cpf", cpfEditText.getText().toString());
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("cep", cepEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("status", "Aguardando Pagamento");
        ordersMap.put("totalPedido", totalPedido);

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




                                       // Toast.makeText(ConfirmFinalOrderActivity.this, "Seu pedido foi processado com sucesso.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, UsersNewOrdersActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class)
                        .build();

        final FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.txtProductQuantity.setText("Quantidade: " + model.getQuantity());
                //int subtotal = model.getQuantity() * model.getPrice();
                holder.txtProductPrice.setText(" R$ " + model.getPrice());
                holder.txtProductName.setText(model.getPname().toUpperCase());

                //oneTypeProductType = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                //overTotalPrice = overTotalPrice + oneTypeProductType;
                //txtTotalAmount.setText("TOTAL: R$ "+ String.valueOf(overTotalPrice));


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


}
