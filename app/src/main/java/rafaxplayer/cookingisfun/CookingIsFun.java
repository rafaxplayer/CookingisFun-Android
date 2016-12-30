package rafaxplayer.cookingisfun;


import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;


/**
 * Created by rafax on 11/12/2016.
 */

public class CookingIsFun extends Application {

    private Realm realm;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration conf = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(conf);
        realm = Realm.getDefaultInstance();
        GlobalUtttilities.updateRecipes(getApplicationContext() ,realm);
        GlobalUtttilities.updateCategories(getApplicationContext() ,realm);

    }

}
