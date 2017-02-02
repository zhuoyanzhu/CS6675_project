package airpnp.pennapps.com.airpnp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class ParkingDetailsActivity extends AppCompatActivity {

    ToggleButton toggle;
    TextView confirmText;

    Button arrivalStartDateBtn;
    Button arrivalStartTimeBtn;
    Button arrivalEndDateBtn;
    Button arrivalEndTimeBtn;

    Boolean hostConfirm;
    Boolean clientConfirm;

    private String phone,street,city,state;

    private double hourlyRate, latitude, longitude;

    public Calendar startDate;
    public Calendar endDate;

    private long hours;

    private DatabaseReference firebase;

    private String ownerEmail;
    private static String TAG = "DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);
        ownerEmail = getIntent().getStringExtra("owner_email");

        firebase = FirebaseDatabase.getInstance().getReference();

        // Setting initial calendar values
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        SimpleDateFormat sdfDateFormatter = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat sdfTimeFormatter = new SimpleDateFormat("h:mm a");

        confirmText = (TextView) findViewById(R.id.parking_confirm_text);

        toggle = (ToggleButton) findViewById(R.id.toggleButton_parking);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firebase.child("owners").child(ownerEmail).child("clientConfirm").setValue(true);
                    bookParking();
                } else {
                    firebase.child("owners").child(ownerEmail).child("clientConfirm").setValue(false);
                }
            }
        });


        arrivalStartDateBtn = (Button) findViewById(R.id.btn_start_date);
        arrivalStartDateBtn.setText(sdfDateFormatter.format(startDate.getTime()));
        arrivalStartDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(true);
            }
        });
        arrivalStartTimeBtn = (Button) findViewById(R.id.btn_start_time);
        arrivalStartTimeBtn.setText(sdfTimeFormatter.format(startDate.getTime()));
        arrivalStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(true);
            }
        });
        arrivalEndDateBtn = (Button) findViewById(R.id.btn_end_date);
        arrivalEndDateBtn.setText(sdfDateFormatter.format(endDate.getTime()));
        arrivalEndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(false);
            }
        });
        arrivalEndTimeBtn = (Button) findViewById(R.id.btn_end_time);
        arrivalEndTimeBtn.setText(sdfTimeFormatter.format(endDate.getTime()));
        arrivalEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(false);
            }
        });

        ValueEventListener mapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                String ownerFirstName = dataSnapshot.child("firstname").getValue(String.class);
                String ownerLastName = dataSnapshot.child("lastname").getValue(String.class);
                street = dataSnapshot.child("street").getValue(String.class);
                city = dataSnapshot.child("city").getValue(String.class);
                state = dataSnapshot.child("state").getValue(String.class);
                latitude = dataSnapshot.child("latitude").getValue(Double.class);
                longitude = dataSnapshot.child("longitude").getValue(Double.class);
                hourlyRate = dataSnapshot.child("rate").getValue(Double.class);
                phone = dataSnapshot.child("phone").getValue(String.class);
                TextView textView1 = (TextView) findViewById(R.id.tv_owner_name);
                TextView textView2 = (TextView) findViewById(R.id.tv_phone);
                TextView textView3 = (TextView) findViewById(R.id.tv_rate);
                TextView textView4 = (TextView) findViewById(R.id.tv_rules);
                TextView textView5 = (TextView) findViewById(R.id.tv_house_name);
                TextView textView6 = (TextView) findViewById(R.id.tv_house_addr);
                textView1.setText("Name: " + ownerFirstName + " " + ownerLastName);
                textView2.setText("Contact: " + phone);
                textView3.setText("Rate: $" + hourlyRate + " / hr");
                textView4.setText("Remarks: No Minivans please");
                textView5.setText(street);
                textView6.setText(city);

                hostConfirm = dataSnapshot.child("hostConfirm").getValue(Boolean.class);
                clientConfirm = dataSnapshot.child("clientConfirm").getValue(Boolean.class);
                if (hostConfirm && clientConfirm) {
                    confirmText.setText("You and the lot owner have confirmed this parking location. You are good to go!");
                    confirmText.setTextColor(Color.parseColor("#117A65"));
                    toggle.setChecked(false);
                    firebase.child("owners").child(ownerEmail).child("clientConfirm").setValue(false);
                    firebase.child("owners").child(ownerEmail).child("hostConfirm").setValue(false);
                    startPayment();
                } else if (clientConfirm) {
                    confirmText.setText("Warning: Owner has not confirmed this parking spot, do not leave your car");
                    confirmText.setTextColor(Color.RED);
                } else {
                    confirmText.setText("You have not confirmed this parking spot.");
                    confirmText.setTextColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        firebase.child("owners/" + ownerEmail).addValueEventListener(mapListener);


    }


    public void setDate(final boolean isArrival) {
        Calendar currCalendar;
        if (isArrival) {
            currCalendar = startDate;
        } else {
            currCalendar = endDate;
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                        // Set the new calendar dates
                        if (isArrival) {
                            startDate.set(Calendar.YEAR, year);
                            startDate.set(Calendar.MONTH, monthOfYear);
                            startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            arrivalStartDateBtn.setText(sdf.format(startDate.getTime()));
                        } else {
                            endDate.set(Calendar.YEAR, year);
                            endDate.set(Calendar.MONTH, monthOfYear);
                            endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            arrivalEndDateBtn.setText(sdf.format(endDate.getTime()));
                        }

                    }
                },
                currCalendar.get(Calendar.YEAR),
                currCalendar.get(Calendar.MONTH),
                currCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void setTime(final boolean isArrival) {
        Calendar currCalendar;
        if (isArrival) {
            currCalendar = startDate;
        } else {
            currCalendar = endDate;
        }
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        // Set the new calendar times
                        if (isArrival) {
                            startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            startDate.set(Calendar.MINUTE, minute);
                            startDate.set(Calendar.SECOND, second);

                            arrivalStartTimeBtn.setText(sdf.format(startDate.getTime()));
                        } else {
                            endDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            endDate.set(Calendar.MINUTE, minute);
                            endDate.set(Calendar.SECOND, second);

                            arrivalEndTimeBtn.setText(sdf.format(endDate.getTime()));
                        }
                    }
                },
                currCalendar.get(Calendar.HOUR_OF_DAY),
                currCalendar.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void getDirections(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
        startActivity(intent);
    }

    public void bookParking() {
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.nexmo_id));
        params.put("api_secret", getString(R.string.nexmo_secret));
        params.put("from", "12675097486");
        params.put("to", phone);
        params.put("text", "Hi! This is AirPnP notifying you that " + MyApplication.userEmail + " has booked your parking spot!");
        NexmoRestClient.post(params,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("!!!",response.toString());
            }
        });
    }
    public void startPayment(){
        String startTime = arrivalStartTimeBtn.getText().toString();
        String endTime = arrivalEndTimeBtn.getText().toString();
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

        int startYear = startDate.get(Calendar.YEAR);
        int startMonth = startDate.get(Calendar.MONTH);
        int startDay = startDate.get(Calendar.DAY_OF_MONTH);

        int endYear = endDate.get(Calendar.YEAR);
        int endMonth = endDate.get(Calendar.MONTH);
        int endDay = endDate.get(Calendar.DAY_OF_MONTH);

        try {
            Date date = parseFormat.parse(startTime);
            startTime = displayFormat.format(date);
            date = parseFormat.parse(endTime);
            endTime = displayFormat.format(date);
            int startHour = Integer.parseInt(startTime.split(":")[0]);
            int startMinute = Integer.parseInt(startTime.split(":")[1]);
            int endHour = Integer.parseInt(endTime.split(":")[0]);
            int endMinute = Integer.parseInt(endTime.split(":")[1]);
            DateTime dateTime1 = new DateTime(startYear, startMonth, startDay, startHour, startMinute, 0);
            DateTime dateTime2 = new DateTime(endYear, endMonth, endDay, endHour, endMinute, 0);
            Interval interval = new Interval(dateTime1, dateTime2);
            Duration duration = interval.toDuration();
            hours = duration.getStandardHours();
            double cost=hours*hourlyRate;
            Intent intent=new Intent(this,PaymentActivity.class);
            intent.putExtra("street",street);
            intent.putExtra("state",state);
            intent.putExtra("city",city);
            intent.putExtra("hours",hours);
            intent.putExtra("cost",cost);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("!!!",e.getMessage());
        }
    }


}