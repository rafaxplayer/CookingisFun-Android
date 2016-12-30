package rafaxplayer.cookingisfun.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rafax on 29/12/2016.
 */

public class tag extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String created_at;
    private String updated_at;

    public tag() {
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
}
