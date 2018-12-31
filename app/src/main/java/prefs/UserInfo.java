package prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {
    private static final String TAG = UserSession.class.getSimpleName();
    private static final String PREF_NAME = "userinfo";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ANTEMAIL = "antemail";
    private static final String KEY_ANTUSERNAME = "antusername";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public UserInfo(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setUsername(String username){
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void setEmail(String email){
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void setAntEmail(String antemail){
        editor.putString(KEY_ANTEMAIL, antemail);
        editor.apply();
    }

    public void setAntUsername(String antusername){
        editor.putString(KEY_ANTUSERNAME, antusername);
        editor.apply();
    }

    public void clearUserInfo(){
        editor.clear();
        editor.commit();
    }

    public String getKeyUsername(){return prefs.getString(KEY_USERNAME, "");}

    public String getKeyEmail(){return prefs.getString(KEY_EMAIL, "");}

    public String getKeyAntEmail(){return prefs.getString(KEY_ANTEMAIL, "");}

    public String getKeyAntUsername(){return prefs.getString(KEY_ANTUSERNAME, "");}
}
