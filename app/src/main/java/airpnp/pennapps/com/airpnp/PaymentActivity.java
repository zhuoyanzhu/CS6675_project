package airpnp.pennapps.com.airpnp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class PaymentActivity extends AppCompatActivity {

    private String userEmail;
    private String street;
    private String city;
    private String state;
    private String hours;

    private TextView textView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_payment);
        Intent intent=getIntent();
        street=intent.getStringExtra("street");
        city=intent.getStringExtra("city");
        state=intent.getStringExtra("state");
        userEmail=MyApplication.userEmail;
        hours=String.valueOf(intent.getLongExtra("hours", 0));
        double cost=intent.getDoubleExtra("cost",0);

        textView = (TextView)findViewById(R.id.textView1);

        textView.setText("Good day to you " + userEmail + "!\nYou have requested a space at " + street + " " + city + " " + state + " for " + hours + " hrs.\n\nHere is your total for this booking:");
        TextView textView1 = (TextView)findViewById(R.id.money_int);
        TextView textView2 = (TextView)findViewById(R.id.money_decimal);
        String intString=String.valueOf((int)(Math.floor(cost)));
        int decimal=(int)((cost-Math.floor(cost))*100);
        String decString=String.valueOf(decimal);
        textView1.setText(intString);
        textView2.setText(decString);
    }



    public void payParking(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Transaction Complete");
        alertDialog.setMessage("Thank you for using Airpnp!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, EventsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        alertDialog.show();
    }
}
