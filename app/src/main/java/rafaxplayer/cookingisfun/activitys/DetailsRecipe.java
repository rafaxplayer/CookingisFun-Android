package rafaxplayer.cookingisfun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rafaxplayer.cookingisfun.R;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;
import rafaxplayer.cookingisfun.models.recipe;

import static rafaxplayer.cookingisfun.R.id.user;

public class DetailsRecipe extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapser)
    CollapsingToolbarLayout collapser;
    @BindView(R.id.details_profile_image)
    ImageView image_profile;
    @BindView(user)
    TextView textUser;
    @BindView(R.id.time)
    TextView textTime;
    @BindView(R.id.textIngredients)
    TextView textIngredients;
    @BindView(R.id.textElaboration)
    TextView textElaboration;
    @BindView(R.id.img)
    ImageView recipeImg;
    @BindView(R.id.text_favorite)
    TextView textFavorite;
    @BindView(R.id.favorite_container)
    LinearLayout fav_container;
    @BindView(R.id.favorite_start)
    MaterialFavoriteButton favoriteButton;

    private Realm realm;
    private recipe rec;

    @OnClick(R.id.fab)
    public void sharerecipe() {
        share();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_recipe);
        ButterKnife.bind(this);
        //Realm.init(this);
        realm = Realm.getDefaultInstance();
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean isFavorite) {
                       // Log.e("rafax","Is Favorite :change");
                        int recipe_id = getIntent().getExtras().getInt("recipeid");
                        int user_id = GlobalUtttilities.getPrefs(getApplicationContext()).getInt("id",0);

                        if(isFavorite){

                            GlobalUtttilities.favoriteCreate(realm,user_id,recipe_id);

                        }else{
                            GlobalUtttilities.favoriteDelete(realm,user_id,recipe_id);

                        }

                        favoriteText(isFavorite);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra("recipeid")){
            Bundle bun = getIntent().getExtras();
            int id = bun.getInt("recipeid");
            rec = realm.where(recipe.class).equalTo("id",id).findFirst();
            if(rec != null){
                Picasso.with(getApplicationContext()).load(rec.getImg()).into(recipeImg);
                Picasso.with(getApplicationContext()).load(rec.getUser().first().getAvatar()).placeholder(R.drawable.user_placeholder).into(image_profile);
                collapser.setTitle(rec.getName().toUpperCase());
                textUser.setText(rec.getUser().first().getName());
                textTime.setText(rec.getElaboration_time());
                textIngredients.setText(rec.getIngredients());
                textElaboration.setText(rec.getElaboration());
            }

            if(GlobalUtttilities.getPrefs(getApplicationContext()).getBoolean("login",false)){

                int recipe_id = getIntent().getExtras().getInt("recipeid");
                int user_id = GlobalUtttilities.getPrefs(getApplicationContext()).getInt("id",0);

                favoriteButton.setFavorite(GlobalUtttilities.favoriteExists(realm,user_id,recipe_id),true);
            }else{
                fav_container.setVisibility(View.GONE);

            }

        }

    }
    private void favoriteText(Boolean isFavorite){
        if(isFavorite){
            textFavorite.setText("Eliminar de favoritos");
        }else{
            textFavorite.setText("Añadir a favoritos");
        }

    }
    private void share(){

        if(rec!=null){

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, GlobalUtttilities.urlsharerecipes + rec.getId());
            startActivity(Intent.createChooser(shareIntent, "Share recipe using"));
        }

    }
    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }



}
