package rafaxplayer.cookingisfun.activitys;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rafaxplayer.cookingisfun.R;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;
import rafaxplayer.cookingisfun.models.tag;

import static rafaxplayer.cookingisfun.R.id.action_gallery;

public class New_Recipe_Activity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.action_camera)
    FloatingActionButton fab_camera;
    @BindView(action_gallery)
    FloatingActionButton fab_gallery;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recipe_name)
    EditText textName;
    @BindView(R.id.buttonTime)
    Button buttontime;
    @BindView(R.id.buttoncats)
    Button buttoncats;
    @BindView(R.id.recipe_time)
    EditText texttime;
    @BindView(R.id.tag_group)
    TagView tagGroup;
    private Realm realm;

    private ArrayList<Integer> categorias_check;
    private boolean[] checkeditems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        ButterKnife.bind(this);
        realm= Realm.getDefaultInstance();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        fab_camera.setOnClickListener(this);
        fab_gallery.setOnClickListener(this);
        buttontime.setOnClickListener(this);
        buttoncats.setOnClickListener(this);
        categorias_check= new ArrayList<>();
        GlobalUtttilities.hidekeyboard(this);
        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(final Tag tag, final int position) {


            }
        });

        //set delete listener
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(New_Recipe_Activity.this);
                builder.setTitle("Eliminar categoria");
                builder.setMessage("Seguro quieres eliminar la categoria "+tag.text+"?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tagGroup.remove(position);
                        int idx = categorias_check.indexOf(tag.id);
                        categorias_check.remove(idx);
                        Toast.makeText(New_Recipe_Activity.this, "Eliminada categoria "+tag.text, Toast.LENGTH_SHORT).show();


                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();

            }
        });

        //set long click listener
        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, int position) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.action_camera:
                Toast.makeText(getApplicationContext(),"Camera",Toast.LENGTH_LONG).show();
                break;
            case action_gallery:
                Toast.makeText(getApplicationContext(),"Gallery",Toast.LENGTH_LONG).show();
                break;
            case R.id.buttonTime:
                TimePickerDialog timePickerDialog = new TimePickerDialog(New_Recipe_Activity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                texttime.setText(hourOfDay + ":" + minute);
                            }
                        }, 0, 0, false);
                timePickerDialog.show();
                break;
            case R.id.buttoncats:
                showDialogCategories();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private String[] getCategories(){
        RealmResults<tag> tags = GlobalUtttilities.getAllCategories(realm);
        String[] categories = new String[tags.size()];
        checkeditems = new boolean[categories.length];
        for(int i=0;i<tags.size();i++){
            categories[i]=tags.get(i).getName();
            checkeditems[i]=false;
        }
        return categories;
    }



    private void showDialogCategories(){

        AlertDialog.Builder builder = new AlertDialog.Builder(New_Recipe_Activity.this);
        final String[] categories = getCategories();

        for(int i=0;i<categorias_check.size();i++){
                int idx= categorias_check.get(i);
                checkeditems[idx]=true;
        }

        builder.setMultiChoiceItems(categories,checkeditems,new DialogInterface.OnMultiChoiceClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    if(!categorias_check.contains(i))
                        checkeditems[i] = true;
                        categorias_check.add(i);
                }else if(categorias_check.contains(i)){

                    int index = categorias_check.indexOf(i);
                    categorias_check.remove(index);
                    checkeditems[i] = false;

                }
            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("Your preferred colors?");

        // Set the positive/yes button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("rafax",categorias_check.size()+"");

                if(categorias_check.size()>0)
                    tagGroup.removeAll();
                    for(int index : categorias_check){
                        Tag tg= new Tag(categories[index]);
                        tg.layoutColor=getResources().getColor(R.color.tag_color);
                        tg.isDeletable=true;
                        tg.id=index;
                        tagGroup.addTag(tg);

                    }


            }
        });


        // Set the neutral/cancel button click listener
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the neutral button
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

    }

}
