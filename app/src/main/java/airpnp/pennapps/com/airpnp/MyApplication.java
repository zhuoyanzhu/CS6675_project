package airpnp.pennapps.com.airpnp;

import android.app.Application;
import android.content.res.Resources;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

public class MyApplication extends Application {
    public static HashMap<String, Marker> markerHashMap = new HashMap<>();
    public static String userEmail;
    public static Resources resources;
    public static ArrayList<Event> listitems=new ArrayList<>();

    @Override
    public void onCreate(){
        resources = getResources();
    }
}



