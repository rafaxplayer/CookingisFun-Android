package rafaxplayer.cookingisfun.models;

import io.realm.RealmObject;

/**
 * Created by rafax on 24/12/2016.
 */

public class favorite extends RealmObject {

    private int user_id;
    private int recipe_id;

    public favorite() {
    }

    public favorite(int user_id, int recipe_id) {
        this.user_id = user_id;
        this.recipe_id = recipe_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }
}
