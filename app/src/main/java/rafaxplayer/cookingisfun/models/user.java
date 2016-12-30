package rafaxplayer.cookingisfun.models;

import io.realm.RealmObject;

/**
 * Created by rafax on 23/12/2016.
 */

public class user  extends RealmObject {

    int id;
    String name;
    String email;
    String avatar;

    public user() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
