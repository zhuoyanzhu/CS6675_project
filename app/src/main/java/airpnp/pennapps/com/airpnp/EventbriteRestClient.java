package airpnp.pennapps.com.airpnp;

import android.net.Uri;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Andy on 10/29/2016.
 */

public class EventbriteRestClient {
    public static Uri.Builder builder = new Uri.Builder()
            .scheme("https")
            .authority("www.eventbriteapi.com")
            .appendPath("v3");

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(), params, responseHandler);

    }

    public static void post(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(), params, responseHandler);

    }

    private static String getAbsoluteUrl() {
        String url=builder.build().toString();
        builder = new Uri.Builder()
                .scheme("https")
                .authority("www.eventbriteapi.com")
                .appendPath("v3");
        client = new AsyncHttpClient();
        return url;
    }
}
