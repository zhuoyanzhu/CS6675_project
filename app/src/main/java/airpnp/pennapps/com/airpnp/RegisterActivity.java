package airpnp.pennapps.com.airpnp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String streetAddress;
    private String city;
    private String state;
    private String zip;
    private double lat;
    private double lng;
    private double rate;

    private Switch ownerSwitch;
    private EditText registerFirst;
    private EditText registerLast;
    private EditText registerEmail;
    private EditText registerMobile;
    private EditText registerPassword;
    private EditText registerPassword2;
    private EditText registerStreet;
    private EditText registerCity;
    private EditText registerState;
    private EditText registerZip;
    private TextInputLayout textWrapperComments;
    private EditText ownerRemarks;
    private TextInputLayout textWrapperRates;
    private EditText hourlyRate;
    private Button registerButton;

    private void findViews() {
        ownerSwitch = (Switch) findViewById(R.id.ownerSwitch);
        registerFirst = (EditText) findViewById(R.id.registerFirst);
        registerLast = (EditText) findViewById(R.id.registerLast);
        registerEmail = (EditText) findViewById(R.id.registerEmail);
        registerMobile = (EditText) findViewById(R.id.registerMobile);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        registerPassword2 = (EditText) findViewById(R.id.registerPassword2);
        registerStreet = (EditText) findViewById(R.id.registerStreet);
        registerCity = (EditText) findViewById(R.id.registerCity);
        registerState = (EditText) findViewById(R.id.registerState);
        registerZip = (EditText) findViewById(R.id.registerZip);
        textWrapperComments = (TextInputLayout) findViewById(R.id.text_wrapper_comments);
        ownerRemarks = (EditText) findViewById(R.id.owner_remarks);
        textWrapperRates = (TextInputLayout) findViewById(R.id.text_wrapper_rates);
        hourlyRate = (EditText) findViewById(R.id.hourly_rate);
        registerButton=(Button)findViewById(R.id.registerButton);
    }

    private JSONObject tempJSONObject;
    private JSONArray tempJSONArray;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        findViews();

        final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ffs.ttf");
        final TextView tv = (TextView) findViewById(R.id.register_text);
        tv.setTypeface(tf);

        textWrapperComments.setVisibility(View.VISIBLE);
        textWrapperRates.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        
        ownerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    textWrapperComments.setVisibility(View.VISIBLE);
                    textWrapperRates.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                } else {
                    textWrapperComments.setVisibility(View.GONE);
                    textWrapperRates.setVisibility(View.GONE);
                    registerButton.setVisibility(View.GONE);
                }
            }
        });
    }

    public void register(View v) {
        boolean notDouble=true;
        try
        {
            rate=Double.parseDouble(hourlyRate.getText().toString());
            notDouble=false;
        }
        catch(NumberFormatException e)
        {
            Log.e("!!!",e.getMessage());
        }
        if (registerFirst.getText().equals(""))
            Toast.makeText(context, "Please enter first name", Toast.LENGTH_LONG).show();
        else if (registerLast.getText().equals(""))
            Toast.makeText(context, "Please enter last name", Toast.LENGTH_LONG).show();
        else if (registerEmail.getText().equals(""))
            Toast.makeText(context, "Please enter email", Toast.LENGTH_LONG).show();
        else if (registerMobile.getText().equals(""))
            Toast.makeText(context, "Please enter mobile", Toast.LENGTH_LONG).show();
        else if (registerStreet.getText().equals(""))
            Toast.makeText(context, "Please enter street", Toast.LENGTH_LONG).show();
        else if (registerState.getText().equals(""))
            Toast.makeText(context, "Please enter state", Toast.LENGTH_LONG).show();
        else if (ownerRemarks.getText().equals(""))
            Toast.makeText(context, "Please enter remarks", Toast.LENGTH_LONG).show();
        else if(notDouble){
            Toast.makeText(context, "Please enter a valid rate", Toast.LENGTH_LONG).show();
        }
        else {
            streetAddress=registerStreet.getText().toString();
            state=registerState.getText().toString();
            String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + streetAddress + " " + state + "&sensor=true_or_false";
            Log.d("!!!", url);
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                    // Handle resulting parsed JSON response here
                    Log.d("!!!", response.toString());
                    try {
                        tempJSONArray = response.getJSONArray("results");
                        tempJSONObject = tempJSONArray.getJSONObject(0);
                        String s_lat = tempJSONObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String s_long = tempJSONObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                        double lat=Double.parseDouble(s_lat);
                        double lng=Double.parseDouble(s_long);
                        firstName=registerFirst.getText().toString();
                        firebase.child("owners").child(firstName).child("city").setValue(registerCity.getText().toString());
                        firebase.child("owners").child(firstName).child("clientConfirm").setValue(false);
                        firebase.child("owners").child(firstName).child("email").setValue(registerEmail.getText().toString());
                        firebase.child("owners").child(firstName).child("firstname").setValue(firstName);
                        firebase.child("owners").child(firstName).child("hostConfirm").setValue(false);
                        firebase.child("owners").child(firstName).child("lastname").setValue(registerLast.getText().toString());
                        firebase.child("owners").child(firstName).child("latitude").setValue(lat);
                        firebase.child("owners").child(firstName).child("longitude").setValue(lng);
                        firebase.child("owners").child(firstName).child("phone").setValue(registerMobile.getText().toString());
                        firebase.child("owners").child(firstName).child("rate").setValue(rate);
                        firebase.child("owners").child(firstName).child("state").setValue(registerState.getText().toString());
                        firebase.child("owners").child(firstName).child("street").setValue(registerStreet.getText().toString());
                        MyApplication.userEmail=firstName;
                        Intent intent=new Intent(RegisterActivity.this,OwnerActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Toast toast = Toast.makeText(context, res, Toast.LENGTH_LONG);
                    toast.show();
                    Log.d("!!!", res);
                }
            });
        }
    }
}
