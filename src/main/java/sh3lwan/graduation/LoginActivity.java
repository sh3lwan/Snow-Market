package sh3lwan.graduation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SessionManager session;
    private EditText emailEt;
    private EditText passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        emailEt = ((EditText) findViewById(R.id.login_email_et));
        passwordEt = ((EditText) findViewById(R.id.login_password_et));
        Button loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
    }

    private boolean checkInput(String... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].trim().length() == 0) {
                return false;
            }
        }
        return true;
    }

    private void login(String email, String password) {
        new Connection(this).login(email, password, new Connection.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                boolean status = result.optBoolean("status");
//                Log.d("token", status + "");
                if (status) {
                    Toast.makeText(LoginActivity.this, "" + result.optString("message"), Toast.LENGTH_SHORT).show();
                    //token & type
                    String token = result.optString("token");
                    Log.d("Logout", token);
                    String type = result.optString("customer_type");
                    JSONObject data = result.optJSONObject("data");
                    // data
                    String name = data.optString("name");
                    String email = data.optString("email");
                    String image = data.optString("image");

                    session.createLoginSession(token, type, email, name, image);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    String message = result.optString("message");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        if (checkInput(email, password)) {
            Toast.makeText(this, "Trying to Login", Toast.LENGTH_SHORT).show();
            login(email, password);
        } else {
            Toast.makeText(this, "Please enter fields correctly!", Toast.LENGTH_SHORT).show();
        }

    }
}
