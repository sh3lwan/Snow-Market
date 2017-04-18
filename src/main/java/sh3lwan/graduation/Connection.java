package sh3lwan.graduation;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by shabon on 11/27/2016.
 */

public class Connection {
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";
    private Context context;
    private RequestQueue queue;
    public static final int WISHLIST = 1;
    public static final int DEFAULT = 2;
    public static final int NEXTPAGE = 3;

    public Connection(Context context) {
        this.context = context;
        queue = VolleyFactory.getInstance().
                getRequestQueue();

    }


    public void logout(String token) {
        String outURL = context.getResources().getString(R.string.API_Logout) + token;
        StringRequest request = new StringRequest(outURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Logout", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public void requestRelated(String token, int id, final VolleyCallback callback) {
        token = "?api_token=" + token;
        String productsURL = context.getResources().getString(R.string.API_RELATED) + id + token;
//        Log.d("token",productsURL);
        StringRequest request = new StringRequest(productsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public void login(final String email, final String password, final VolleyCallback callback) {
//        Toast.makeText(context, ""+email + password, Toast.LENGTH_SHORT).show();
        String loginURL = context.getResources().getString(R.string.API_Login);
        StringRequest request = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Connection Timeout", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(EMAIL_PARAM, email);
                params.put(PASSWORD_PARAM, password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(request);
    }

    public void requestProducts(String token, final VolleyCallback callback) {
        String productsURL = context.getResources().getString(R.string.API_Products) + token;
        Log.d("Token", "URL:" + productsURL);
        StringRequest request = new StringRequest(productsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Token", "Here3" + response);
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public void addWish(int id, String token, final VolleyCallback callback) {
        token = "?api_token=" + token;
        String productsURL = context.getResources().getString(R.string.API_WISH_ADD) + id + token;
        StringRequest request = new StringRequest(productsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    Log.d("Token", "Here3" + response);
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    Toast.makeText(context, "Sorry, Can't add to wishlist", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public void requestCategory(int choice, String URL, String token, int id, final VolleyCallback callback) {
        String productsURL = "";
        if (choice == Connection.DEFAULT) {
            token = "?api_token=" + token;
            productsURL = context.getResources().getString(R.string.API_CATEGORY) + id + token;
            Log.d("next_page", token + "  a, " + URL);
        } else if (choice == Connection.NEXTPAGE) {
            token = "&api_token=" + token;
            Log.d("next_page", token + "  b, " + URL);
            productsURL = URL + token;
        } else if (choice == Connection.WISHLIST) {
            token = "?api_token=" + token;
            Log.d("next_page", token + "  c, " + URL);
            productsURL = context.getResources().getString(R.string.API_WISHLIST) + token;
        }

        StringRequest request = new StringRequest(productsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Token", response);
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public void requestRate() {
        String token = new SessionManager(context).getToken();
        String rateURL = context.getResources().getString(R.string.API_RATE);
    }

    public void requestWishlist(String token, final VolleyCallback callback) {
        token = "?token=" + token;
        String url = context.getResources().getString(R.string.API_WISHLIST) + token;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public interface VolleyCallback {
        void onSuccess(JSONObject result);
    }

}




