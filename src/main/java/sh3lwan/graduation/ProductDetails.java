package sh3lwan.graduation;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetails extends AppCompatActivity {
    private int productID;
    private RatingBar ratingBar;
    SessionManager session;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.coordinator_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Product product = (Product) getIntent().getSerializableExtra(Constants.PRODUCT);
        productID = product.getID();
        getSupportActionBar().setTitle("Product Details");
        fillFields(product);
        session = new SessionManager(this);
        type = session.getType();

//        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.background_grey), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestItems(productID);

    }

    public void requestItems(int id) {
        String token = session.getToken();
        new Connection(this).requestRelated(token, id, new Connection.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                if (result.optBoolean("status", false)) {
                    Log.d("token", "Here!!!");
                    JSONArray jsonArray = result.optJSONArray("data");
                    fill(jsonArray);
                } else {
                    Toast.makeText(ProductDetails.this, "" + result.optString("message", "Please try again later!"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fillFields(Product product) {
        TextView nameET = (TextView) findViewById(R.id.details_product_name);
        TextView descET = (TextView) findViewById(R.id.product_details_description);
        TextView priceET = (TextView) findViewById(R.id.product_details_price);
        TextView secPriceET = (TextView) findViewById(R.id.product_details_sec_price);
        ratingBar = (RatingBar) findViewById(R.id.details_ratingbad);
        ImageView coverImage = (ImageView) findViewById(R.id.details_cover);
        TextView reviewsTV = (TextView) findViewById(R.id.details_reviews);
        descET.setMovementMethod(new ScrollingMovementMethod());
        nameET.setText(product.getName());
        descET.setText(product.getDescription());
        priceET.setText(product.getPrice());
        secPriceET.setText(product.getSecondPrice());
        ratingBar.setRating(product.getRate());
        reviewsTV.setText("(" + product.getReviews() + ") reviews");
//        Log.d("Picasso", product.getImageURL());
        String imageURL = product.getImageURL();
//        imageURL = "http://www.sqebd.com/vb/Photo/2015_1418827408_881.jpg";
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.d("Picasso", exception + "");
            }
        });
        builder.build().load(imageURL).fit().error(R.drawable.details_cover).into(coverImage);
    }

    public int fill(JSONArray jsonArray) {
        List<Product> list = new ArrayList<>();
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.optInt("id");
                    String description = jsonObject.optString("description", "");
                    String name = jsonObject.optString("name", "product " + i);
                    String imagePath = getResources().getString(R.string.API_PRODUCT_IMAGE);
                    String image = imagePath + jsonObject.optString("image");
                    float price = Float.parseFloat(jsonObject.optString("price", "0.0"));
                    JSONObject rateCount = jsonObject.optJSONObject("rating_count");
                    float rate = 0f;
                    float oldPrice = 0f;
                    int reviews = 0;
                    if (rateCount != null) {
                        rate = Float.parseFloat(rateCount.optString("totalRating", "0.0"));
                        reviews = rateCount.optInt("totalCount", 0);
                    }
                    JSONObject offer = jsonObject.optJSONObject("offer");
                    if (offer != null) {
                        float percentage = (offer.optInt("percentage") / 100.0f);
                        oldPrice = price;
                        price = price - (price * percentage);
                    }
                    String secondPrice = "$" + oldPrice;
                    if (oldPrice <= 0f) {
                        secondPrice = "";
                    }
                    Product product = new Product(id, reviews, description, name, rate, "$" + price, secondPrice, image);
                    list.add(product);
                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.related_recycler_view);
//                recyclerView.setfo
                ProductsAdapter adapter = new ProductsAdapter(this, list, R.layout.main_product_item);
                recyclerView.setAdapter(adapter);
//                recyclerView.setPadding(0, 0, 0, 0);
                LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);
            } catch (JSONException ex) {
            }
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.favorite_product);
        if (!type.matches("B")) {
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.favorite_product:
                new Connection(this).addWish(productID, new SessionManager(this).getToken(), new Connection.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        boolean status = result.optBoolean("status");
                        if (status) {
                            Toast.makeText(getApplicationContext(), "Product added to wishlist!", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(ProductDetails.this, "Sorry, can't add to wishlist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
