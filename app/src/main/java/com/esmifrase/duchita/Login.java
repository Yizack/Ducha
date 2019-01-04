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

public class Login extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = Login.class.getSimpleName();
    private EditText email, password;
    private Button login;
    private TextView signup;
    private ProgressDialog progressDialog;

    private UserSession session;
    private UserInfo userInfo;

    private Hash256 hash = new Hash256();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        email        = (EditText)findViewById(R.id.email);
        password        = (EditText)findViewById(R.id.password);
        login           = (Button)findViewById(R.id.login);
        signup          = (TextView)findViewById(R.id.open_signup);
        progressDialog  = new ProgressDialog(this);
        session         = new UserSession(this);
        userInfo        = new UserInfo(this);

        if(session.isUserLoggedin()){
            startActivity(new Intent(this, Duchita.class));
            finish();
        }

        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    private void attemptLogin() throws NoSuchAlgorithmException {
        TextView mEmailView = email;
        TextView mPasswordView = password;
        mEmailView.setError(null);
        mPasswordView.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Verificar por una contraseña válida
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

        // Verifica por un correo válido
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // Si hay errores, cancela el inicio de sesión y marca los errores
            focusView.requestFocus();
        } else {
            // Inicio de sesión
            password = hash.sha256(password); // Encriptacion de la contraseña con SHA-256
            login(email, password);
        }
    }

    // Método que verificación de correo válido
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains("."); // Retorna verdadero si contiene "@" o "."
    }

    // Método de verificación de contraseña válida
    private boolean isPasswordValid(String password) {
        return password.length() > 4;  // Retorna verdadero si la contraseña contiene más de 4 caracteres
    }

    // Método de inicio de sesión
    private void login(final String email, final String password){
        String tag_string_req = "req_login";
        progressDialog.setMessage("Iniciando...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Utils.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login" + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Verifica un error de JSON
                    if (!error) {
                        // Ahora guarda los datos en SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        String uName = user.getString("username");
                        String email = user.getString("email");

                        // Inserta una fila en la tabla de usuarios
                        userInfo.setEmail(email);
                        userInfo.setUsername(uName);
                        session.setLoggedin(true);

                        startActivity(new Intent(Login.this, Duchita.class));
                        finish();
                    } else {
                        /// Si hay un error muestra un mensaje
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
                toast("Error de conexión");
                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Pasa los parametros para el link de inicio de sesión
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Solicitud en cola
        ControladorDeInicio.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.login:
                try {
                    attemptLogin();
                }catch(NoSuchAlgorithmException e){}
                break;

            case R.id.open_signup:
                startActivity(new Intent(this, Registrarse.class));
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }
}
