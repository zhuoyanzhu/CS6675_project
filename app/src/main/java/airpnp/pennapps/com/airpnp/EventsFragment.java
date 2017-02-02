package airpnp.pennapps.com.airpnp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static airpnp.pennapps.com.airpnp.MyApplication.listitems;
import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EventsFragment extends Fragment {
    RecyclerView MyRecyclerView;


    private OnFragmentInteractionListener mListener;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Local Events");
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MyRecyclerView.setLayoutManager(MyLayoutManager);
        if(!listitems.isEmpty()) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
            ((EventsActivity) getActivity()).getPleaseWait().dismiss();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getEventList(){
        if(listitems.size()==0) {
            EventbriteRestClient.builder
                    .appendPath("events")
                    .appendPath("search");
            RequestParams params = new RequestParams();
            params.put("token", getString(R.string.eventbrite_token));
            LatLng latLng = ((EventsActivity) getActivity()).getLatLng();
            params.put("location.latitude", Double.toString(latLng.latitude));
            params.put("location.longitude", Double.toString(latLng.longitude));
            EventbriteRestClient.get(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray arr = response.getJSONArray("events");
                        for (int i = 0; i < arr.length(); i++) {
                            Event event = new Event();
                            JSONObject jsonobject = arr.getJSONObject(i);
                            JSONObject name = jsonobject.getJSONObject("name");
                            event.name = name.getString("text");
                            event.description = jsonobject.getString("description");
                            event.venudID = jsonobject.getString("venue_id");
                            if (jsonobject.has("logo") && !jsonobject.isNull("logo")) {
                                JSONObject logo = jsonobject.getJSONObject("logo");
                                event.imageURL = logo.getString("url");
                            }
                            listitems.add(event);
                        }
                        if (listitems.size() > 0 & MyRecyclerView != null) {
                            MyRecyclerView.setAdapter(new MyAdapter(listitems));
                        }
                        ((EventsActivity) getActivity()).getPleaseWait().dismiss();
                    } catch (Exception e) {
                        Log.e("!!!", e.toString());
                        ((EventsActivity) getActivity()).getPleaseWait().dismiss();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.e("!!!", errorResponse.toString());
                        Toast toast = makeText(getContext(), errorResponse.toString(), Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Toast toast = makeText(getContext(), "null error response", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    ((EventsActivity) getActivity()).getPleaseWait().dismiss();

                }

            });
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<Event> list;
        public MyAdapter(ArrayList<Event> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.titleTextView.setText(list.get(position).name);
            Picasso.with(getContext()).load(list.get(position).imageURL).into(holder.coverImageView);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public ImageView coverImageView;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((EventsActivity)getActivity()).getPleaseWait().show();
                    Log.d("!!!", "onClick " + getLayoutPosition());
                    final Event event = listitems.get(getLayoutPosition());
                    if (event.lat != null && event.lng != null){
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        intent.putExtra("latitude", event.lat);
                        intent.putExtra("longitude", event.lng);
                        startActivity(intent);
                        ((EventsActivity) getActivity()).getPleaseWait().dismiss();
                    }
                    else {
                        String venueID = event.venudID;
                        EventbriteRestClient.builder
                                .appendPath("venues")
                                .appendPath(venueID);
                        RequestParams params = new RequestParams();
                        params.put("token", getString(R.string.eventbrite_token));
                        EventbriteRestClient.get(params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    event.lat = response.getDouble("latitude");
                                    event.lng = response.getDouble("longitude");
                                    Intent intent = new Intent(getContext(), MapsActivity.class);
                                    intent.putExtra("latitude", event.lat);
                                    intent.putExtra("longitude", event.lng);
                                    startActivity(intent);
                                    ((EventsActivity) getActivity()).getPleaseWait().dismiss();
                                } catch (Exception e) {
                                    Log.e("!!!", e.toString());
                                    ((EventsActivity) getActivity()).getPleaseWait().dismiss();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                if (errorResponse != null) Log.e("!!!", errorResponse.toString());
                                else {
                                    Toast toast = Toast.makeText(getContext(), "error connecting to server", Toast.LENGTH_LONG);
                                    toast.show();

                                }

                                ((EventsActivity) getActivity()).getPleaseWait().dismiss();
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
