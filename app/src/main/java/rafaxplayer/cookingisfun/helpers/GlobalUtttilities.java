package rafaxplayer.cookingisfun.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.IOException;
import rafaxplayer.cookingisfun.activitys.MainActivity;
import rafaxplayer.cookingisfun.models.favorite;
import rafaxplayer.cookingisfun.models.recipe;
import rafaxplayer.cookingisfun.models.tag;

/**
 * Created by rafax on 11/12/2016.
 */

public class GlobalUtttilities {

    public static String urlrecipes = "http://www.rafaxplayer.com/cooking-is-fun/api/recipes";
    public static String urlcategories = "http://www.rafaxplayer.com/cooking-is-fun/api/categories";
    public static String urlsharerecipes = "http://www.rafaxplayer.com/cooking-is-fun/recipes/";
    public static String urluser = "http://www.rafaxplayer.com/cooking-is-fun/api/user/";
    public static String urllogin = "http://www.rafaxplayer.com/cooking-is-fun/api/authenticate?";

    public static SharedPreferences.Editor editSharePrefs(Context con) {

        SharedPreferences.Editor editor = getPrefs(con).edit();

        return editor;
    }

    public static SharedPreferences getPrefs(Context con) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(con);

        return settings;
    }

    public static void updateRecipes(Context con, final Realm realm) {

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                GlobalUtttilities.urlrecipes,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final JSONArray jsonArray = response.getJSONArray("data");
                            Log.e("rafax",jsonArray.toString());
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        if (jsonArray.length() > 0) {

                                            realm.where(recipe.class).findAll().deleteAllFromRealm();
                                        }
                                        realm.createAllFromJson(recipe.class, jsonArray.toString());

                                        realm.close();

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                        } catch (JSONException ex) {
                            Log.e("Error : ", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error en JSON: ", error.getMessage());

                    }
                }
        );

        MySocialMediaSingleton.getInstance(con).addToRequestQueue(jsArrayRequest);
    }

    public static void updateCategories(Context con, final Realm realm) {

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                GlobalUtttilities.urlcategories,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final JSONArray jsonArray = response.getJSONArray("data");

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        if (jsonArray.length() > 0) {

                                            realm.where(tag.class).findAll().deleteAllFromRealm();
                                        }
                                        realm.createAllFromJson(tag.class, jsonArray.toString());

                                        realm.close();

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                        } catch (JSONException ex) {
                            Log.e("Error : ", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error en JSON: ", error.getMessage());

                    }
                }
        );

        MySocialMediaSingleton.getInstance(con).addToRequestQueue(jsArrayRequest);
    }

    public static void Login(final Context con, final Realm realm, final String email, final String pass){

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                GlobalUtttilities.urllogin + "email=" + email + "&password=" + pass,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //Log.e("rafa","Response : "+response.getString("status"));
                            if(response.getString("status").contains("ok")){
                                JSONObject userObject = response.getJSONObject("data");
                                SharedPreferences.Editor ed = GlobalUtttilities.editSharePrefs(con)
                                        .putBoolean("login",true)
                                        .putInt("id",userObject.getInt("id"))
                                        .putString("email",userObject.getString("email"))
                                        .putString("password",pass)
                                        .putString("name",userObject.getString("name"))
                                        .putString("avatar",userObject.getString("avatar"));
                                ed.commit();
                                ed.apply();
                                Toast.makeText(con,"Ok login correct",Toast.LENGTH_LONG).show();
                                goHome(con);

                            }else{

                                unLogin(con);
                                Toast.makeText(con,"Error : login incorrect",Toast.LENGTH_LONG).show();
                                //goHome(con);
                            }

                        } catch (JSONException ex) {
                            Log.e("rafa", "Error : "+ ex.getMessage());
                            Toast.makeText(con,ex.getMessage(),Toast.LENGTH_LONG).show();
                            goHome(con);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(error.getMessage());
                        Toast.makeText(con,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
        );

        MySocialMediaSingleton.getInstance(con).addToRequestQueue(jsArrayRequest);
    }

    public static RealmResults<recipe> getAllRecipes(final Realm realm) {

        return realm.where(recipe.class).findAll();
    }

    public static RealmResults<tag> getAllCategories(final Realm realm) {

        return realm.where(tag.class).findAll();
    }

    public static void goHome(Context con){
        Intent intent=new Intent(con, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        con.startActivity(intent);
    }

    public static RealmResults<recipe> searchRecipeWithName(final Realm realm, String name) {

        return realm.where(recipe.class).contains("name", name, Case.INSENSITIVE).findAll();
    }

    public static Boolean TestConnection(Context con) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static Boolean isLogin(Context con){

        return getPrefs(con).getBoolean("login",false);

    }

    public static void unLogin(Context con){
        SharedPreferences.Editor ed = GlobalUtttilities.editSharePrefs(con)
                .putBoolean("login",false)
                .putInt("id",0)
                .putString("email","")
                .putString("password","")
                .putString("name","")
                .putString("avatar","");
        ed.commit();
        ed.apply();

    }

    public static Boolean favoriteExists(Realm realm,int user_id,int recipe_id){
        favorite fav = realm.where(favorite.class)
                .equalTo("recipe_id", recipe_id)
                .equalTo("user_id",user_id)
                .findFirst();
        if(fav!=null){
            return true;
        }
        return false;
    }

    public static void  favoriteCreate(Realm realm ,int user_id,int recipe_id){

        if(!favoriteExists(realm,user_id,recipe_id)) {
            realm.beginTransaction();
            favorite fav = new favorite(user_id, recipe_id);
            realm.copyToRealm(fav);
            recipe rec = realm.where(recipe.class).equalTo("id",recipe_id).findFirst();
            rec.getFavs().add(fav);
            realm.commitTransaction();
        }
    }

    public static void favoriteDelete(Realm realm,int user_id,int recipe_id){

        if(favoriteExists(realm,user_id,recipe_id)) {
            realm.beginTransaction();
            RealmResults<favorite> favs = realm.where(favorite.class)
                    .equalTo("recipe_id", recipe_id)
                    .equalTo("user_id",user_id)
                    .findAll();
            if(favs.size()>0){
                favs.deleteAllFromRealm();
            }
            realm.commitTransaction();
        }
    }

    public static void hidekeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
