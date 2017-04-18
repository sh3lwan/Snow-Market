package sh3lwan.graduation;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private SessionManager session;
    private Context context = this;
    private boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visible = true;
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(this);
//        initRecycler();
        initDrawer();
        changeUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        visible = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        visible = true;
    }

    private void initDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                drawerLayout.closeDrawers();
                switch (id) {
                    case R.id.nav_home:
                        if (!visible) startActivity(new Intent(context, MainActivity.class));

                        break;
                    case R.id.nav_categories:
                        startActivity(new Intent(context, CategoriesActivity.class));
//                        finish();

                        break;
                    case R.id.nav_fav:
                        if (session.getType().matches("B"))
                            startActivity(new Intent(context, ProductsActivity.class).putExtra(Constants.WISHLIST, 1));
                        else
                            Toast.makeText(context, "You don't have a wishlist!", Toast.LENGTH_SHORT).show();
//                        finish();

                        break;
                    case R.id.nav_logout:
                        session.logoutUser();
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                        break;
                }
                return false;
            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void changeUser() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView imageView = (CircleImageView) headerView.findViewById(R.id.profile_image);
        TextView name = (TextView) headerView.findViewById(R.id.username);
        TextView email = (TextView) headerView.findViewById(R.id.email);

        //Get data
        HashMap<String, String> user = session.getUserToken();
        String imageURL = user.get(SessionManager.IMAGE);
        String path = getResources().getString(R.string.API_CUSTOMER_IMAGE);
        imageURL = path + imageURL;

        //Changings
        name.setText(user.get(SessionManager.NAME));
        email.setText(user.get(SessionManager.EMAIL));
        Picasso.with(getApplicationContext()).load(imageURL)
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
                .into(imageView);
    }


//    private void initRecycler() {
//        newAdapter = new ProductsAdapter(this, productList, R.layout.main_product_item);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_new_recycler_view);
//        recyclerView.setAdapter(newAdapter);
//        recyclerView.setPadding(16, 16, 16, 16);
//        LinearLayoutManager gridLayoutManager1 = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager1);
//
//        shoesAdapter = new ProductsAdapter(this, shoes, R.layout.main_product_item);
//        recyclerView = (RecyclerView) findViewById(R.id.shoes_recycler_view);
//        recyclerView.setAdapter(newAdapter);
//        recyclerView.setPadding(16, 16, 16, 16);
//        LinearLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager2);
//
//        clothingAdapter = new ProductsAdapter(this, clothing, R.layout.main_product_item);
//        recyclerView = (RecyclerView) findViewById(R.id.clothing_recycler_view);
//        recyclerView.setAdapter(clothingAdapter);
//        recyclerView.setPadding(16, 16, 16, 16);
//        LinearLayoutManager gridLayoutManager3 = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager3);
//
//        watchesAdapter = new ProductsAdapter(this, watches, R.layout.main_product_item);
//        recyclerView = (RecyclerView) findViewById(R.id.clothing_recycler_view);
//        recyclerView.setAdapter(watchesAdapter);
//        recyclerView.setPadding(16, 16, 16, 16);
//        LinearLayoutManager gridLayoutManager4 = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager4);
//
//        electonicsAdapter = new ProductsAdapter(this, electronics, R.layout.main_product_item);
//        recyclerView = (RecyclerView) findViewById(R.id.clothing_recycler_view);
//        recyclerView.setAdapter(electonicsAdapter);
//        recyclerView.setPadding(16, 16, 16, 16);
//        LinearLayoutManager gridLayoutManager5 = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(gridLayoutManager4);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;
        String token = session.getToken();
        new Connection(this).requestProducts(token, new Connection.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                if (result.optBoolean("status", false) == true) {

                    JSONArray clothingArray = result.optJSONArray("clothes");
                    fill(clothingArray, R.id.clothing_recycler_view);

                    JSONArray shoesArray = result.optJSONArray("shoes");
                    fill(shoesArray, R.id.shoes_recycler_view);

                    JSONArray electronics = result.optJSONArray("electronics");
                    fill(electronics, R.id.electronics_recycler_view);

                    JSONArray watches = result.optJSONArray("watches");
                    fill(watches, R.id.watches_recycler_view);

                    JSONArray newProducts = result.optJSONArray("newProducts");
                    fill(newProducts, R.id.main_new_recycler_view);
                }
            }
        });
    }

    public int fill(JSONArray jsonArray, int productLayout) {
        List<Product> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.optInt("id");
                String description = jsonObject.optString("description");
                String productName = jsonObject.optString("name", "product " + i);
                float rate = 0f;
                int reviews = 0;
                JSONObject ratingCount = jsonObject.optJSONObject("rating_count");
                if (ratingCount != null) {
                    rate = Float.parseFloat(ratingCount.optString("totalRating"));
                    reviews = ratingCount.optInt("totalCount");
                }
                float oldPrice = 0f;
                JSONObject offer = jsonObject.optJSONObject("offer");
                float price = Float.parseFloat(jsonObject.optString("price", "0.0"));
                if (offer != null) {
                    float percentage = (offer.optInt("percentage") / 100.0f);
                    oldPrice = price;
                    price = price - (price * percentage);
                }
                String secondPrice = oldPrice == 0f ? "" : "$" + oldPrice;
                String imagePath = getResources().getString(R.string.API_PRODUCT_IMAGE);
                String image = imagePath + jsonObject.optString("image");
                Product product = new Product(id, reviews, description, productName, rate, "$" + price, secondPrice, image);
                list.add(product);
            }
            ProductsAdapter adapter = new ProductsAdapter(this, list, R.layout.main_product_item);
            RecyclerView recyclerView = (RecyclerView) findViewById(productLayout);
            recyclerView.setAdapter(adapter);
            recyclerView.setPadding(16, 16, 16, 16);
            LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);
            return adapter.getItemCount();
        } catch (JSONException ex) {
        }
        return -1;
    }
}

