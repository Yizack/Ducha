package com.esmifrase.duchita;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.esmifrase.duchita.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import prefs.UserInfo;
import prefs.UserSession;

public class Perfil extends AppCompatActivity {
    private TextView tvUsername, tvEmail;
    private UserInfo userInfo;
    private UserSession userSession;
    private EditText tvEditUsername, tvEditEmail;
    private boolean isEdit = true;
    private String TAG = Perfil.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        userInfo        = new UserInfo(this);
        userSession     = new UserSession(this);
        tvUsername      = (TextView)findViewById(R.id.key_username);
        tvEmail         = (TextView)findViewById(R.id.key_email);
        tvEditUsername = (EditText)findViewById(R.id.key_editusername);
        tvEditEmail = (EditText)findViewById(R.id.key_editemail);

        if(!userSession.isUserLoggedin()){
            startActivity(new Intent(this, Login.class));
            finish();
        }

        String username = userInfo.getKeyUsername();
        String email    = userInfo.getKeyEmail();
        
        if(!email.equals(userInfo.getKeyAntEmail()) || !username.equals(userInfo.getKeyAntUsername())){
            userInfo.setAntEmail(email);
            userInfo.setAntUsername(username);
            Snackbar.make(findViewById(android.R.id.content), "Los cambios han sido guardados.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        tvEmail.setText(email);
        tvUsername.setText(username);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userInfo.getKeyUsername();
                String email    = userInfo.getKeyEmail();
                String antemail = email;
                String antusername = username;

                if(isEdit == true){
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setImageResource(R.drawable.save);
                    tvUsername.setVisibility(4);
                    tvEmail.setVisibility(4);

                    tvEditUsername.setVisibility(1);
                    tvEditEmail.setVisibility(1);

                    tvEditUsername.setText(username);
                    tvEditEmail.setText(email);
                    Snackbar.make(view, "Realiza los cambios deseados", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    isEdit = false;
                }
                else{
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setImageResource(R.drawable.pencil);
                    isEdit = true;
                    tvUsername.setVisibility(1);
                    tvEmail.setVisibility(1);

                    tvEditUsername.setVisibility(4);
                    tvEditEmail.setVisibility(4);

                    username = tvEditUsername.getText().toString();
                    email = tvEditEmail.getText().toString();

                    boolean cancel = false;
                    View focusView = null;

                    if(!isUsernameValid(username)){
                        tvEditUsername.setError(getString(R.string.error_invalid_username));
                        focusView = tvEditUsername;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(email)) {
                        tvEditEmail.setError(getString(R.string.error_field_required));
                        focusView = tvEditEmail;
                        cancel = true;
                    } else if (!isEmailValid(email)) {
                        tvEditEmail.setError(getString(R.string.error_invalid_email));
                        focusView = tvEditEmail;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(username)) {
                        tvEditUsername.setError(getString(R.string.error_field_required));
                        focusView = tvEditUsername;
                        cancel = true;
                    }

                    if (cancel) {
                        // Si hay errores los marca y no guarda los cambios
                        focusView.requestFocus();
                        hideKeyboard();
                        Snackbar.make(view, "Error: No se guardaron los cambios.\nVuelve a editar para verificar los errores.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        // Guardar cambios
                        if(email.equals(userInfo.getKeyAntEmail()) || username.equals(userInfo.getKeyAntUsername()) ){
                            if(email.equals(userInfo.getKeyAntEmail()) && username.equals(userInfo.getKeyAntUsername()) ){

                            }
                            else if(username.equals(userInfo.getKeyAntUsername())){
                                update(username, email, antemail, antusername);
                            }
                            else if(email.equals(userInfo.getKeyAntEmail())){
                                update(username, email, antemail, antusername);
                            }
                        }
                        else{
                            update(username, email, antemail, antusername);
                        }
                    }
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUsernameValid(String username) {
        return username.length() <= 50 && username.length() > 0;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void update(final String username, final String email, final String antemail, final String antusername){
        String tag_string_req = "req_signup";
        overridePendingTransition(0, 0);


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Utils.UPDATE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        toast(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvEmail.setText(email);
                    tvUsername.setText(username);
                    userInfo.setEmail(email);
                    userInfo.setUsername(username);
                    userInfo.setAntEmail(antemail);
                    userInfo.setAntUsername(antusername);
                    Intent intent = new Intent(Perfil.this, Duchita.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 0);
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(Perfil.this, Perfil.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                toast("Unknown Error occurred");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                System.out.println(username+" "+email+" "+antemail+" "+antusername);
                params.put("username", username);
                params.put("email", email);
                params.put("antemail", antemail);
                params.put("antusername", antusername);
                return params;
            }

        };

        ControladorDeInicio.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void toast(String x){
        Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
