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

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rafaxplayer.cookingisfun.R;
import rafaxplayer.cookingisfun.activitys.DetailsRecipe;
import rafaxplayer.cookingisfun.activitys.MainActivity;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;
import rafaxplayer.cookingisfun.models.recipe;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private RealmResults<recipe> mDataset;
    private Context con;
    private Realm realm;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipesAdapter(Context con,RealmResults<recipe> myDataset) {
        this.con = con;
        this.mDataset = myDataset;
        realm = Realm.getDefaultInstance();
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
        holder.name.setText(mDataset.get(position).getName().toUpperCase());
        holder.user.setText(mDataset.get(position).getUser().first().getName());
        holder.time.setText(mDataset.get(position).getElaboration_time());
        Picasso.with(con).load(mDataset.get(position).getImg()).placeholder(R.drawable.recipe_placeholder)
                .error(R.drawable.recipe_placeholder).into(holder.img);

        if(GlobalUtttilities.getPrefs(con).getBoolean("login",false)){

            int recipe_id = mDataset.get(position).getId();
            int user_id = GlobalUtttilities.getPrefs(con).getInt("id",0);
            holder.favButton.setFavorite(GlobalUtttilities.favoriteExists(realm,user_id,recipe_id),false);

        }else{
            holder.favButton.setVisibility(View.GONE);

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataset.size();
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
            intent.putExtra("recipeid",mDataset.get(ViewHolder.this.getLayoutPosition()).getId());

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
