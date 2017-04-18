package sh3lwan.graduation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Product> productList = new ArrayList();
    private ProductsAdapter adapter;
    private static final Map<String, Integer> categories;
    private SessionManager session;

    static {
        categories = new HashMap<>();
        categories.put(Constants.CLOTH_CATEGORY, Constants.CLOTHING_ID);
        categories.put(Constants.ElECTRONICS_CATEGORY, Constants.ELECTRONICS_ID);
        categories.put(Constants.SHOES_CATEGORY, Constants.SHOES_ID);
        categories.put(Constants.SPORT_CATEGORY, Constants.SPORT_ID);
        categories.put(Constants.BOOKS_CATEGORY, Constants.BOOKS_ID);
        categories.put(Constants.WATCHES_CATEGORY, Constants.WATCHES_ID);
    }

    int catNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        session = new SessionManager(this);
        SessionManager sessionManager = new SessionManager(this);

        String catName = getIntent().getStringExtra(Constants.CATEGORY);
        int wishlist = getIntent().getIntExtra(Constants.WISHLIST, -1);
        if (wishlist == 1) {
            requestShit(Connection.WISHLIST, null);
            initToolbar("Wish list");
        } else {
            if (catName != null) {
                sessionManager.saveCategory(catName);
            } else {
                catName = sessionManager.getCategory();
            }

            catNumber = categories.get(catName);
            initToolbar(catName);
        }

//        initSpinner();
        Button btn = (Button) findViewById(R.id.load_more_button);
        btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestShit(Connection.DEFAULT, null);
    }
//
//    private void initSpinner() {
//        Spinner spinner = (Spinner) findViewById(R.id.filter_spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.product_filter));
//        spinner.setAdapter(adapter);
//
//    }

    private void initToolbar(String catName) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.products_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(catName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Changes status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.blue_dark));
    }

//    public void initRecyclerView() {
//        class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
//            private Drawable mDivider;
//
//            public SimpleDividerItemDecoration(Context context) {
//                mDivider = context.getResources().getDrawable(R.drawable.line_divider);
//            }
//
//            @Override
//            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                int left = parent.getPaddingLeft();
//                int right = parent.getWidth() - parent.getPaddingRight();
//
//                int childCount = parent.getChildCount();
//                for (int i = 0; i < childCount; i++) {
//                    View child = parent.getChildAt(i);
//                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//                    int top = child.getBottom() + params.bottomMargin;
//                    int bottom = top + mDivider.getIntrinsicHeight();
//                    mDivider.setBounds(left, top, right, bottom);
//                    mDivider.draw(c);
//                }
//            }
//        }
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.products_recyclerView);
//        adapter = new ProductsAdapter(this, productList, R.layout.product_item);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        //Divider Line
//        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
//    }

    public void requestShit(int choice, String URL) {
        String token = session.getToken();
        new Connection(this).requestCategory(choice, URL, token, catNumber, new Connection.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                if (result.optBoolean("status")) {
                    String nextURL = result.optString("next_page", "");
                    session.saveURL(nextURL);
                    JSONArray jsonArray = result.optJSONArray("data");
                    fill(jsonArray);
                } else {
//                    session.logoutUser();
//                    Toast.makeText(ProductsActivity.this, "You must login first!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });
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
                        reviews = rateCount.optInt("totalCount");
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
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.products_recyclerView);
                ProductsAdapter adapter = new ProductsAdapter(this, list, R.layout.product_item);
//                Log.d("Token", "Item count" + adapter.getItemCount());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                //Divider Line
                class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
                    private Drawable mDivider;

                    public SimpleDividerItemDecoration(Context context) {
                        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
                    }

                    @Override
                    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                        int left = parent.getPaddingLeft();
                        int right = parent.getWidth() - parent.getPaddingRight();

                        int childCount = parent.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View child = parent.getChildAt(i);
                            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                            int top = child.getBottom() + params.bottomMargin;
                            int bottom = top + mDivider.getIntrinsicHeight();
                            mDivider.setBounds(left, top, right, bottom);
                            mDivider.draw(c);
                        }
                    }
                }
                recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

            } catch (JSONException ex) {
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        String url = session.getNextURL();
        if (url == null) {
            requestShit(Connection.DEFAULT, "");
        } else {
            requestShit(Connection.NEXTPAGE, url);
        }
    }
}