package rafaxplayer.cookingisfun;


import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;
import rafaxplayer.cookingisfun.models.favorite;


/**
 * Created by rafax on 11/12/2016.
 */

public class CookingIsFun extends Application {

    public static AtomicInteger FavoriteID = new AtomicInteger();

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
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        GlobalUtttilities.updateRecipes(getApplicationContext() ,realm);
        GlobalUtttilities.updateCategories(getApplicationContext() ,realm);

        FavoriteID = setAtomicId(realm, favorite.class);
    }

    private <T extends RealmObject> AtomicInteger setAtomicId(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
     }
}
