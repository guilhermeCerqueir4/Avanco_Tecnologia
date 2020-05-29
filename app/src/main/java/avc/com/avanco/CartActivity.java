package avc.com.avanco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import avc.com.avanco.ViewHolder.CartViewHolder;
import avc.com.avanco.model.Cart;
import avc.com.avanco.prevalent.Prevalent;

public class CartActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;
    private Button limparCarrinho;
    private Integer oneTypeProductType = 0;


    private String productID = "";

    private Integer overTotalPrice = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle("Meu Carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productID = getIntent().getStringExtra("pid");

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = findViewById(R.id.next_process_button);
        txtTotalAmount = findViewById(R.id.total_price);
        limparCarrinho = findViewById(R.id.limpar_carrinho_button);

        txtMsg1 = findViewById(R.id.msg1);


        limparCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overTotalPrice = 0;
                txtTotalAmount.setText("TOTAL: R$ "+ String.valueOf(overTotalPrice));


                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CartActivity.this, "O carrinho foi limpo.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("Deletar", "Erro. Tente novamente mais tarde.");
                                }
                            }
                        });

                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List").child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CartActivity.this, "O carrinho foi limpo.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("Deletar", "Erro. Tente novamente mais tarde.");
                                }
                            }
                        });

            }
        });





        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //txtTotalAmount.setText("TOTAL: R$ " + String.valueOf(overTotalPrice));

                if (overTotalPrice != 0) {
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Preço Total", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();

                }
                else {
                    Toast.makeText(CartActivity.this, "O carrinho está vazio.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

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

                oneTypeProductType = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductType;
                txtTotalAmount.setText("TOTAL: R$ "+ String.valueOf(overTotalPrice));


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Remover item",
                                "Editar item"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Opções do Carrinho:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==1) {

                                    overTotalPrice = overTotalPrice - oneTypeProductType;
                                    txtTotalAmount.setText("TOTAL: R$ "+ String.valueOf(overTotalPrice));

                                    //Toast.makeText(CartActivity.this, Prevalent.currentOnlineUser.getPhone() +" e " + model.getPid(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (which==0)   {

                                    overTotalPrice = overTotalPrice - oneTypeProductType;
                                    txtTotalAmount.setText("TOTAL: R$ "+ String.valueOf(overTotalPrice));

                                   FirebaseDatabase.getInstance().getReference()
                                    .child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(model.getPid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "O item foi retirado do carrinho.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(CartActivity.this, "Erro. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Cart List").child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products").child(model.getPid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "O item foi retirado do carrinho.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(CartActivity.this, "Erro. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                 }


                            }
                        });
                        builder.show();
                    }
                });

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

    private void deleteData(String phone, String pid) {


         DatabaseReference cart1 = FirebaseDatabase.getInstance().getReference().child("User View")
                .child(phone).child("Products").child(pid);
         DatabaseReference cart2 = FirebaseDatabase.getInstance().getReference().child("Admin View")
                .child(phone).child("Products").child(pid);

            cart1.removeValue();
            cart2.removeValue();

        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();

    }


    private void CheckOrderState(){

        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String statusCompra = dataSnapshot.child("status").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (statusCompra.equals("Enviado")){
                        txtTotalAmount.setText("Olá "+ userName + "\n Pedido enviado com sucesso.");
                        recyclerView.setVisibility(View.GONE);
                        limparCarrinho.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Parabéns, seu pedido de compra foi realizado com sucesso.");
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Você pode comprar mais produtos assim que finalizar seu pedido.",Toast.LENGTH_SHORT).show();
                    }
                    else if (statusCompra.equals("Aguardando Pagamentoe")){
                        txtTotalAmount.setText("Status de Compra: Não Enviado" );
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);
                        limparCarrinho.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Você pode comprar mais produtos assim que finalizar seu pedido.",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
