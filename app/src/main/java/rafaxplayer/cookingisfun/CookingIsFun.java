package rafaxplayer.cookingisfun;


import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

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
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        GlobalUtttilities.updateRecipes(getApplicationContext() ,realm);
        GlobalUtttilities.updateCategories(getApplicationContext() ,realm);

    }

}
