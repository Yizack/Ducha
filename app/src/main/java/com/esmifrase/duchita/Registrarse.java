package com.esmifrase.duchita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import prefs.UserInfo;
import prefs.UserSession;

public class Registrarse extends AppCompatActivity {
    private String TAG = Registrarse.class.getSimpleName();
    private EditText username, email, password;
    private Button signup;
    private ProgressDialog progressDialog;
    private UserSession session;
    private UserInfo userInfo;
    private TextView login;
    private Hash256 hash = new Hash256();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        username        = (EditText)findViewById(R.id.username);
        email           = (EditText)findViewById(R.id.email);
        password        = (EditText)findViewById(R.id.password);
        signup          = (Button)findViewById(R.id.signup);
        progressDialog  = new ProgressDialog(this);
        session         = new UserSession(this);
        userInfo        = new UserInfo(this);
        login           = (TextView)findViewById(R.id.link_login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    attemptSignUp();
                }catch(NoSuchAlgorithmException e){
                    //
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });

    }

    private void attemptSignUp() throws NoSuchAlgorithmException{
        TextView mEmailView = email;
        TextView mPasswordView = password;
        TextView mUsernameView = username;
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUsernameView.setError(null);
        String username = mUsernameView.getText().toString().trim();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Verifica usuario
        if(!isUsernameValid(username)){
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Verifica por contraseña valida
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        else if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Verifica por correo valido
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // Si hay un error no intenta registrarse y marca los errores
            focusView.requestFocus();
        } else {
            // Se registra un nuevo usuario en la base de datos
            password = hash.sha256(password);
            signup(username, email, password);
        }
    }


    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".") && email.length() <= 200;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUsernameValid(String username) {
        return username.length() <= 50 && username.length() > 0;
    }

    private void signup(final String username, final String email, final String password){
        String tag_string_req = "req_signup";
        progressDialog.setMessage("Iniciando Sesión...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Utils.REGISTER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                        String uName = user.getString("username");
                        String email = user.getString("email");

                        // Inserta una fila
                        userInfo.setEmail(email);
                        userInfo.setUsername(uName);
                        session.setLoggedin(true);

                        startActivity(new Intent(Registrarse.this, Duchita.class));
                        startActivity(new Intent(Registrarse.this, Intro.class));
                    } else {
                        // Errorr en inicio muestra un mensaje
                        String errorMsg = jObj.getString("error_msg");
                        toast(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    toast("Json error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                toast("Unknown Error occurred");
                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Pasa los parametros al link
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Agregar solicitud en cola
        ControladorDeInicio.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }
}
