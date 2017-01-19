package rafaxplayer.cookingisfun.adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rafaxplayer.cookingisfun.R;
import rafaxplayer.cookingisfun.activitys.DetailsRecipe;
import rafaxplayer.cookingisfun.activitys.MainActivity;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;
import rafaxplayer.cookingisfun.models.recipe;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private RealmResults<recipe> mDataset;
    private List<recipe> dDataset;
    private Context con;
    private Realm realm;
    public static int MAX_ITEMS = 16;
    public int currentItems = 0;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipesAdapter(Context con,RealmResults<recipe> myDataset) {
        this.con = con;
        this.mDataset = myDataset;
        this.dDataset= new RealmList<>();
        realm = Realm.getDefaultInstance();
        this.LoadMore();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(dDataset.get(position).getName().toUpperCase());
        holder.user.setText(dDataset.get(position).getUser().first().getName());
        holder.time.setText(dDataset.get(position).getElaboration_time());
        Picasso.with(con).load(dDataset.get(position).getImg()).fit().placeholder(R.drawable.recipe_placeholder)
                .error(R.drawable.recipe_placeholder).into(holder.img);

        if(GlobalUtttilities.getPrefs(con).getBoolean("login",false)){

            int recipe_id = dDataset.get(position).getId();
            int user_id = GlobalUtttilities.getPrefs(con).getInt("id",0);
            holder.favButton.setFavorite(GlobalUtttilities.favoriteExists(realm,user_id,recipe_id),false);

        }else{
            holder.favButton.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {

        return dDataset.size();
    }

    public boolean getAllItemsLoaded(){

        return currentItems >= mDataset.size();
    }

    public void LoadMore(){

        if(MAX_ITEMS > mDataset.size()-1){
            dDataset=mDataset;
            currentItems = mDataset.size();
            notifyDataSetChanged();
            return;

        }

        if(getAllItemsLoaded()){
            Toast.makeText(con, "Limit max items", Toast.LENGTH_SHORT).show();
        }else{
            currentItems += MAX_ITEMS;
            if(getAllItemsLoaded()){

                currentItems = currentItems-MAX_ITEMS;

                currentItems += (mDataset.size()-currentItems) ;

            }
            dDataset = mDataset.subList(0, currentItems);
            notifyDataSetChanged();

        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.imageRecipe)
        ImageView img;
        @BindView(R.id.textName)
        TextView name;
        @BindView(R.id.textTime)
        TextView time;
        @BindView(R.id.textUser)
        TextView user;
        @BindView(R.id.favorite_item_start)
        MaterialFavoriteButton favButton;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
            this.favButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean isFavorite) {
                            int user_id= GlobalUtttilities.getPrefs(con).getInt("id",0);
                            recipe rec = mDataset.get(ViewHolder.this.getLayoutPosition());
                            if(isFavorite){

                                GlobalUtttilities.favoriteCreate(realm,user_id,rec.getId());

                            }else{
                                GlobalUtttilities.favoriteDelete(realm,user_id,rec.getId());

                            }

                        }
                    });
        }

        @Override
        public void onClick(View view) {
            Intent intent= new Intent(con, DetailsRecipe.class);
            intent.putExtra("recipeid",dDataset.get(ViewHolder.this.getLayoutPosition()).getId());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation((MainActivity)con, view, img.getTransitionName());
                con.startActivity(intent, options.toBundle());
            } else {
                con.startActivity(intent);
            }


        }
    }
}
