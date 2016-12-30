package rafaxplayer.cookingisfun.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class recipe extends RealmObject {

    @PrimaryKey
    private int id;

    private int user_id;

    private String name;

    private String ingredients;

    private String elaboration;

    private String elaboration_time;

    private String created_at;

    private String updated_at;

    private String img_url;

    private RealmList<user> user;

    private RealmList<favorite> favs;

    private RealmList<tag> tags;

    public recipe() {
    }

    public int getId() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int id() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getElaboration() {
        return elaboration;
    }

    public void setElaboration(String elaboration) {
        this.elaboration = elaboration;
    }

    public String getElaboration_time() {
        return elaboration_time;
    }

    public void setElaboration_time(String elaboration_time) {
        this.elaboration_time = elaboration_time;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreate_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public RealmList<tag> getTags() {
        return tags;
    }

    public void setTags(RealmList<tag> tags) {
        this.tags = tags;
    }

    public String getImg() {
        return img_url;
    }

    public void setImg(String img) {
        this.img_url = img;
    }

    public RealmList<user> getUser() {
        return user;
    }

    public void setUser(RealmList<user> user) {
        this.user = user;
    }

    public RealmList<favorite> getFavs() {
        return favs;
    }

    public void setFavs(RealmList<favorite> favs) {
        this.favs = favs;
    }
}
