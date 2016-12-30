package rafaxplayer.cookingisfun.activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rafaxplayer.cookingisfun.R;
import rafaxplayer.cookingisfun.adapters.RecipesAdapter;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;
import rafaxplayer.cookingisfun.models.recipe;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.list_recipes)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navview)
    NavigationView navView;

    private CircleImageView profileImage;
    private TextView username;
    private TextView useremail;
    private LinearLayout header_back;
    private Realm realm;
    private RecipesAdapter mAdapter;
    private RealmResults<recipe> recipeslist;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        View header = navView.getHeaderView(0);
        username = (TextView) header.findViewById(R.id.username);
        useremail = (TextView) header.findViewById(R.id.useremail);
        profileImage = (CircleImageView) header.findViewById(R.id.profile_image);
        header_back = (LinearLayout) header.findViewById(R.id.header_back);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        realm = Realm.getDefaultInstance();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        recipeslist = GlobalUtttilities.getAllRecipes(realm);
        recipeslist.addChangeListener(new RealmChangeListener<RealmResults<recipe>>() {
            @Override
            public void onChange(RealmResults<recipe> element) {
                mAdapter.notifyDataSetChanged();
            }
        });


        showListRecipes(recipeslist);
        header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),GlobalUtttilities.isLogin(getApplicationContext())+"",Toast.LENGTH_LONG).show();
                Boolean isLogin = GlobalUtttilities.getPrefs(getApplicationContext()).getBoolean("login", false);
                if (isLogin) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("¿Deseas desconetarte de la aplicacion?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    GlobalUtttilities.unLogin(getApplicationContext());
                                    updateprofile(username, profileImage);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                } else {

                    showLogin(MainActivity.this);

                }
            }
        });
        Menu men = navView.getMenu();
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.menu_recipes:
                                onResume();
                                break;
                            case R.id.menu_new_recipe:
                                if(GlobalUtttilities.isLogin(getApplicationContext())){
                                    startActivity(new Intent(getApplicationContext(),New_Recipe_Activity.class));
                                }
                                break;
                            case R.id.menu_favorites:

                                if (GlobalUtttilities.isLogin(getApplicationContext())) {
                                    int id = GlobalUtttilities.getPrefs(getApplicationContext()).getInt("id", 0);
                                    recipeslist = realm.where(recipe.class).equalTo("favs.user_id", id).findAll();
                                    if (recipeslist.size() > 0) {
                                        showListRecipes(recipeslist);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No tienes recetas en favoritos", Toast.LENGTH_LONG).show();
                                    }

                                }

                                break;
                            case R.id.menu_contact:
                                if(GlobalUtttilities.isLogin(getApplicationContext())){
                                    startActivity(new Intent(MainActivity.this,Contact_Activity.class));
                                }else{
                                    Toast.makeText(MainActivity.this, "No estas Autentificado", Toast.LENGTH_SHORT).show();
                                }

                                break;
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean islogin = GlobalUtttilities.isLogin(getApplicationContext());
        showHideUserItemsMenu(islogin);
        if (islogin) {

            updateprofile(username, profileImage);

        }
    }

    @Override
    protected void onDestroy() {
        if (!realm.isClosed())
            realm.removeAllChangeListeners();
        realm.close();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getText(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                //se oculta el EditText
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                showListRecipes(searchRecipe(realm, newText.trim()));
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showListRecipes(RealmResults<recipe> recipeslist) {
        mAdapter = new RecipesAdapter(MainActivity.this, recipeslist);
        mRecyclerView.setAdapter(mAdapter);

    }

    private RealmResults<recipe> searchRecipe(Realm realm, String name) {

        if (!TextUtils.isEmpty(name)) {
            return GlobalUtttilities.searchRecipeWithName(realm, name);
        }
        return GlobalUtttilities.getAllRecipes(realm);

    }

    private void updateprofile(TextView username, CircleImageView img) {

        SharedPreferences prefs = GlobalUtttilities.getPrefs(getApplicationContext());
        if (prefs.getBoolean("login", false)) {
            //Log.e("rafa", "name 2 : " + prefs.getString("name", "No login"));
            username.setText(prefs.getString("name", "No login"));
            useremail.setText(prefs.getString("email", ""));
            Picasso.with(getApplicationContext()).load(prefs.getString("avatar", "")).placeholder(R.drawable.user_placeholder).into(img);
        }

    }

    private void showLogin(Context con) {
        Intent intent = new Intent(con, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showHideUserItemsMenu(boolean isLogeg) {

        navView.getMenu().findItem(R.id.menu_new_recipe).setVisible(isLogeg);
        navView.getMenu().findItem(R.id.menu_favorites).setVisible(isLogeg);

    }

}