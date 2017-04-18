package sh3lwan.graduation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

public class CategoriesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catergories);
        //Sets categories images to GridView
        initGrid();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.categories_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Changes status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.blue_dark));
    }

    private void initGrid() {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new CategoriesAdapter(this));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {
        Intent i;
        i = new Intent(v.getContext(), ProductsActivity.class);
//        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        switch (position) {
            case 0:
                //Clothing
                i.putExtra(Constants.CATEGORY, Constants.CLOTH_CATEGORY);
                break;
            case 1:
                //Electronics
                i.putExtra(Constants.CATEGORY, Constants.ElECTRONICS_CATEGORY);
                break;
            case 2:
                //Shoes
                i.putExtra(Constants.CATEGORY, Constants.SHOES_CATEGORY);
                break;
            case 3:
                //Kids
                i.putExtra(Constants.CATEGORY, Constants.SPORT_CATEGORY);
                break;
            case 4:
                //Books
                i.putExtra(Constants.CATEGORY, Constants.BOOKS_CATEGORY);
                break;
            case 5:
                //Watches
                i.putExtra(Constants.CATEGORY, Constants.WATCHES_CATEGORY);
                break;
            default:
                break;
        }
        startActivity(i);
//        finish();
    }
}



