package avc.com.avanco.ViewHolder;


import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import avc.com.avanco.Interface.ItemClickListener;
import avc.com.avanco.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{



    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listner;

    Typeface type;


    public ProductViewHolder(View itemView)
    {

        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);

        type = Typeface.createFromAsset(txtProductName.getContext().getAssets(),"fonts/amazon.ttf");
        txtProductName.setTypeface(type);
        type = Typeface.createFromAsset(txtProductPrice.getContext().getAssets(),"fonts/amazon.ttf");
        txtProductPrice.setTypeface(type);
        type = Typeface.createFromAsset(txtProductDescription.getContext().getAssets(),"fonts/amazon.ttf");
        txtProductDescription.setTypeface(type);
    }

    public void setItemClickListner(ItemClickListener listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}