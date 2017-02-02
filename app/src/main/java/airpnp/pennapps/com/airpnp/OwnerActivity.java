package airpnp.pennapps.com.airpnp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwnerActivity extends AppCompatActivity {
    DatabaseReference firebase;
    Intent intent;
    TextView ownersText;
    TextView confirmText;
    ToggleButton toggleButton;
    ListView listview;
    String userEmail;
    Boolean hostConfirm;
    Boolean clientConfirm;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_owner);
        listview = (ListView) findViewById(R.id.reviews);
        context=this;
        intent=getIntent();
        userEmail = MyApplication.userEmail;
        ownersText = (TextView) findViewById(R.id.owner_text);
        confirmText = (TextView) findViewById(R.id.confirm_text);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firebase.child("owners").child(userEmail).child("hostConfirm").setValue(true);
                } else {
                    firebase.child("owners").child(userEmail).child("hostConfirm").setValue(false);
                }
            }
        });
        intent = getIntent();
        final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ffs.ttf");
        ownersText.setTypeface(tf);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hostConfirm = dataSnapshot.child(userEmail).child("hostConfirm").getValue(Boolean.class);
                clientConfirm = dataSnapshot.child(userEmail).child("clientConfirm").getValue(Boolean.class);
                if (hostConfirm && clientConfirm) {
                    confirmText.setText("You and the driver have confirmed this parking location. You are good to go!");
                    confirmText.setTextColor(Color.GREEN);
                    toggleButton.setChecked(true);
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Transaction Complete");
                    alertDialog.setMessage("Thank you for using Airpnp!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    if (! ((Activity) context).isFinishing()) {
                        alertDialog.show();
                    }
                }
                else if(hostConfirm){
                    confirmText.setText("Warning: Driver has not confirmed this parking spot");
                    confirmText.setTextColor(Color.RED);
                }
                else{
                    confirmText.setText("You have not confirmed this parking spot.");
                    confirmText.setTextColor(Color.RED);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("!!!", "loadPost:onCancelled", databaseError.toException());
            }
        };
        firebase.child("owners").addValueEventListener(postListener);


        ArrayList<String> reviews = new ArrayList();
        reviews.add("AdviceTagz - Review: 5 Stars");
        reviews.add("DatasTech - Review: 4 Stars");
        reviews.add("InstantRoys - Review: 1 Stars");
        reviews.add("LessGenius - Review: 3 Stars");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, reviews);

        // Bind to our new adapter.
        listview.setAdapter(adapter);
    }
}
