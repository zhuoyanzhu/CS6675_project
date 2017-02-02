package airpnp.pennapps.com.airpnp;

import android.net.Uri;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Andy on 11/12/2016.
 */

public class NexmoRestClient {
    public static Uri.Builder builder = new Uri.Builder()
            .scheme("https")
            .authority("rest.nexmo.com")
            .appendPath("sms")
            .appendPath("json")
            .appendQueryParameter("api_key", MyApplication.resources.getString(R.string.nexmo_id))
            .appendQueryParameter("api_secret", MyApplication.resources.getString(R.string.nexmo_secret))
            .appendQueryParameter("from", "12675097486");

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
                .authority("rest.nexmo.com")
                .appendPath("sms")
                .appendPath("json")
                .appendQueryParameter("api_key", MyApplication.resources.getString(R.string.nexmo_id))
                .appendQueryParameter("api_secret", MyApplication.resources.getString(R.string.nexmo_secret))
                .appendQueryParameter("from", "12675097486");
        return url;
    }
}
