package avc.com.avanco;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import avc.com.avanco.model.AdminOrders;
import avc.com.avanco.prevalent.Prevalent;

public class UsersNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_new_orders);

        getSupportActionBar().setTitle("Meus Pedidos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef,AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int i, @NonNull final AdminOrders model) {

                        holder.userName.setText("Nome:          " + model.getName());
                        holder.userPhoneNumber.setText("Telefone:     " + model.getPhone());
                        holder.status.setText("Status:     " + model.getStatus());
                        holder.userTotalPedido.setText("TOTAL:  R$ " + model.getTotalPedido());
                        holder.userDataTime.setText("Data/hora:   " + model.getDate() +"  "+model.getTime());
                        holder.userShippingAddress.setText("Endereço:    " + model.getAddress() + ", " + model.getCep());

                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(i).getKey();

                                Intent intent = new Intent(UsersNewOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };


        ordersList.setAdapter(adapter);
        adapter.startListening();

    }
    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, status, userTotalPedido, userDataTime, userShippingAddress;
        public Button ShowOrdersBtn;


        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            status = itemView.findViewById(R.id.order_status);
            userTotalPedido = itemView.findViewById(R.id.order_total_price);
            userDataTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address);
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
