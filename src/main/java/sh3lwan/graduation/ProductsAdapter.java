package sh3lwan.graduation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shabon on 10/14/2016.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private List<Product> products;
    private Context context;
    private int layout;
//    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public ProductsAdapter(Context context, List<Product> products, int layout) {
        this.layout = layout;
        this.context = context;
        this.products = products;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, priceTv, secondPriceTv;
        ImageView imageIv;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            switch (layout) {
                case R.layout.product_item:
                    nameTv = (TextView) view.findViewById(R.id.product_item_name);
                    imageIv = (ImageView) view.findViewById(R.id.product_item_image);
                    priceTv = (TextView) view.findViewById(R.id.product_item_price);
                    secondPriceTv = (TextView) view.findViewById(R.id.product_item_sec_price);
                    ratingBar = (RatingBar) view.findViewById(R.id.product_item_ratingBar);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Product product = products.get(getAdapterPosition());
                            Intent i = new Intent(context, ProductDetails.class);
                            i.putExtra(Constants.PRODUCT, product);
                            context.startActivity(i);
                        }
                    });
                    break;
                case R.layout.main_product_item:
                    nameTv = (TextView) view.findViewById(R.id.main_product_name);
                    priceTv = (TextView) view.findViewById(R.id.main_product_price);
                    secondPriceTv = (TextView) view.findViewById(R.id.main_product_sec_price);
                    imageIv = (ImageView) view.findViewById(R.id.main_product_image);
                    ratingBar = (RatingBar) view.findViewById(R.id.main_product_ratingbar);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Product product = products.get(getAdapterPosition());
                            Intent i = new Intent(context, ProductDetails.class);
                            i.putExtra(Constants.PRODUCT, product);
                            context.startActivity(i);
                        }
                    });
                    imageIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Product product = products.get(getAdapterPosition());
                            Intent i = new Intent(context, ProductDetails.class);
                            i.putExtra(Constants.PRODUCT, product);
                            context.startActivity(i);
                        }
                    });
                    break;
            }

        }
    }

    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Product product = products.get(i);
        viewHolder.nameTv.setText(product.getName());
        viewHolder.secondPriceTv.setText(product.getSecondPrice() + "");
        viewHolder.secondPriceTv.setPaintFlags(viewHolder.secondPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.priceTv.setText(product.getPrice() + "");
        viewHolder.ratingBar.setRating(product.getRate());
        Picasso.Builder builder = new Picasso.Builder(context);
        // To check error messages
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.d("Exception Tag", exception + "");
            }
        });
        if (layout == R.layout.product_item) {
            builder.build().load(products.get(i).getImageURL()).centerCrop().resize(120, 125).into(viewHolder.imageIv);

        } else {
            builder.build().load(products.get(i).getImageURL()).centerCrop().resize(212, 212).into(viewHolder.imageIv);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
