package airpnp.pennapps.com.airpnp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    AutoCompleteTextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        textView = (AutoCompleteTextView) findViewById(R.id.email);


        // Init components

        final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ffs.ttf");
        final TextView tv = (TextView) findViewById(R.id.welcome_text);
        tv.setTypeface(tf);

        final int color1 = 160;
        final int color2 = 189;
        final int color3 = 255;
        final int color4 = 224;
        final TextView linkSignup = (TextView) findViewById(R.id.signup_link);
        //Change the text color when the user touches it
        linkSignup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        linkSignup.setTextColor(Color.argb(color1, color2, color2, color2));
                        break;
                    case MotionEvent.ACTION_UP:
                        linkSignup.setTextColor(Color.argb(color3, color4, color4, color4));
                        break;
                }
                return false;
            }
        });
        linkSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }

        });

    }

    public void loginAction(View view) {
        EditText editText1 = (EditText) findViewById(R.id.email);
        EditText editText2 = (EditText) findViewById(R.id.password);
        final String email = editText1.getText().toString();
        String password = editText2.getText().toString();

        if (password.equals("owner")) {
            firebase.child("owners").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(email)) {
                        Intent intent = new Intent(LoginActivity.this, OwnerActivity.class);
                        MyApplication.userEmail=email;
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Username not found", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("!!!", "loadPost:onCancelled", databaseError.toException());
                }
            });
        } else {
            Intent intent = new Intent(LoginActivity.this, EventsActivity.class);
            MyApplication.userEmail=email;
            startActivity(intent);
        }

    }
}
