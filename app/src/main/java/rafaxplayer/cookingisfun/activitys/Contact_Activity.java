package rafaxplayer.cookingisfun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cookingisfun.R;
import rafaxplayer.cookingisfun.helpers.GlobalUtttilities;

public class Contact_Activity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.email_subject)
    EditText textSubject;
    @BindView(R.id.email_message)
    EditText textMessage;
    @BindView(R.id.action_send_email)
    FloatingActionButton fab_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        GlobalUtttilities.hidekeyboard(this);
        fab_email.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.action_send_email:
                sendEmail();
                break;
        }

    }
    private void sendEmail(){

        String email = GlobalUtttilities.getPrefs(this).getString("email","");
        if(!isValidSubject()){
            Toast.makeText(this, "El asunto es muy corto", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidMessage()){
            Toast.makeText(this, "El mensage es muy corto", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Tu email de usuario no es valido", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"amsapzs@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, textSubject.getText());
        i.putExtra(Intent.EXTRA_TEXT   , textMessage.getText());

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.",Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isValidSubject(){
        if(textSubject.getText().length()<5){
            return false;
        }

        return true;

    }
    private boolean isValidMessage(){
        if(textMessage.getText().length()<15){
            return false;
        }
        return true;
    }

}
