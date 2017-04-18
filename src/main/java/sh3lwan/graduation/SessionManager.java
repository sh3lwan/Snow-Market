package sh3lwan.graduation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AndroidHivePref";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String TOKEN = "token";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String TYPE = "type";
    public static final String EMAIL = "email";
    public static final String CATEGORY = "category";
    public static final String URL = "url";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getNextURL() {
        return pref.getString(URL, null);
    }

    public void createLoginSession(String token, String type, String email, String name, String image) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(TOKEN, token);
        editor.putString(TYPE, type);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(IMAGE, image);
        editor.commit();

    }

    public String getType() {
        return pref.getString(TYPE, "A");
    }

    public void saveCategory(String category) {
        editor.putString(CATEGORY, category);
        editor.commit();
    }

    public String getCategory() {
        return pref.getString(CATEGORY, "");
    }

    public String getToken() {
        return pref.getString(TOKEN, null);
    }

    public HashMap<String, String> getUserToken() {
        HashMap<String, String> result = new HashMap();
        result.put(TOKEN, pref.getString(TOKEN, null));
        result.put(TYPE, pref.getString(TYPE, null));
        result.put(NAME, pref.getString(NAME, null));
        result.put(EMAIL, pref.getString(EMAIL, null));
        result.put(IMAGE, pref.getString(IMAGE, null));
        return result;
    }

    public void checkLogin() {
        Intent intent;
        if (isLoggedIn()) {
            intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else {
            intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        String token = pref.getString(TOKEN, "");
//        Log.d("Logout", token);
        new Connection(context).logout(token);
        editor.clear();
        editor.commit();
    }

    public void saveURL(String url) {
        editor.putString(URL, url);
        editor.commit();
    }
}