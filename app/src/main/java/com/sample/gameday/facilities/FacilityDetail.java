package com.sample.gameday.facilities;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sample.gameday.R;
import com.sample.gameday.utility.GPSTracker;
import com.sample.gameday.utility.HelpMe;
import com.sample.gameday.volley.AppController;
import com.sample.gameday.volley.CustomJsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhi on 18/04/16.
 */
public class FacilityDetail extends AppCompatActivity implements GPSTracker.OnLocationChangedListener {

    private static final String TAG = "FacilityDetail";
    private TextView facilityNameTxt;
    private TextView facilityCityTxt;
    private TextView facilityGPSTxt;
    private ProgressDialog mDialog;
    private LinearLayout facilityDetailLL;
    private ArrayList<String> checkedAmenitiesList;
    private String facilityId;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_detail);

        setUpToolBar();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("please wait...");
        mDialog.setCanceledOnTouchOutside(false);

        facilityId = getIntent().getStringExtra("facilityId");
        String facilityName = getIntent().getStringExtra("facilityName");
        String facilityCity = getIntent().getStringExtra("facilityCity");
        checkedAmenitiesList = new ArrayList<>();
        latitude = null;
        longitude = null;


        facilityNameTxt = (TextView) findViewById(R.id.facility_name);
        facilityCityTxt = (TextView) findViewById(R.id.facility_city);
        facilityGPSTxt = (TextView) findViewById(R.id.facility_gps);
        facilityDetailLL = (LinearLayout) findViewById(R.id.facility_detail_ll_amenities);

        facilityNameTxt.setText("Facility Name : " + facilityName);
        facilityCityTxt.setText("City : " + facilityCity);

        getGPSLocation();
        getAmenitiesList();


    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Facility Detail");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Makes a call to backend server and fetches all unverified list of facilities
    public void getAmenitiesList() {

        // Check internet connection
        if (!HelpMe.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        mDialog.show();

        String url = "http://ec2-52-34-17-117.us-west-2.compute.amazonaws.com/facilities/amenities";
        Log.d(TAG, "url is" + url);

        // Request a string response from the provided URL.
        CustomJsonArrayRequest signUpReg = new CustomJsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        renderAmenities(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Unable to sign up volley error -> " + error);
                // EventBus.getDefault().post(new JsonResponseEvent(null, SyncStateContract.Constants.JE_SSO_FB_RESULT, false));
            }
        });

        // Add the request to the RequestQueue.
        AppController.getInstance().getRequestQueue().add(signUpReg);
    }

    private void renderAmenities(JSONArray response) {

        int len = response.length();
        for (int i = 0; i < len; i++) {

            try {
                String id = response.getJSONObject(i).getString("id");
                String name = response.getJSONObject(i).getString("name");

                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setId(Integer.parseInt(id));
                cb.setTextColor(getResources().getColor(R.color.black));
                cb.setText(name);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Log.d(TAG, "id = " + String.valueOf(buttonView.getId()));
                            checkedAmenitiesList.add(String.valueOf(buttonView.getId()));
                        } else {
                            checkedAmenitiesList.remove(String.valueOf(buttonView.getId()));
                        }
                    }
                });

                facilityDetailLL.addView(cb);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        mDialog.dismiss();

    }

    public void updateFacilityDetail(View v) {
        Log.d(TAG, "update button clicked");

        // Check internet connection
        if (!HelpMe.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        mDialog.show();

        String url = "http://ec2-52-34-17-117.us-west-2.compute.amazonaws.com/facilities/" + facilityId;
        Log.d(TAG, "url is" + url);

        JSONObject jsonBody = null;

        try {

            // JSON address attribites
            JSONObject addressAttributes = new JSONObject();
            addressAttributes.put("id", facilityId);
            addressAttributes.put("lat_lng", "(" + latitude + "," + longitude + ")");

            // facility JSON values
            JSONObject facility = new JSONObject();
            facility.put("verified", true);
            facility.put("address_attributes", addressAttributes);

            JSONArray amenitiesArray = new JSONArray();
            for (int i = 0; i < checkedAmenitiesList.size(); i++) {
                amenitiesArray.put(checkedAmenitiesList.get(i));
            }

            Log.d(TAG, "checkedList = " + amenitiesArray.toString());
            facility.put("amenity_ids", amenitiesArray);

            // Final JOSN body
            jsonBody = new JSONObject();
            jsonBody.put("facility", facility);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "JSON body sent is =" + jsonBody.toString());

        JsonObjectRequest updateFacility = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(TAG, "successful  with response =  " + jsonObject.toString());
                        mDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "Unable to sign up volley error -> " + volleyError);
                mDialog.dismiss();
            }
        });

        // Add the request to the RequestQueue.
        AppController.getInstance().getRequestQueue().add(updateFacility);

    }

    private void getGPSLocation() {

        GPSTracker gps = new GPSTracker(this);
        gps.setLocationChangedListener(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude(); // returns latitude
            longitude = gps.getLongitude(); // returns longitude
        } else {
            Log.d(TAG, "Network issues. cannot get latitude & longitude");
        }
    }


    @Override
    public void onReceiveLocation(Location receiveLocation, int resultCode) {
        latitude = receiveLocation.getLatitude();
        longitude = receiveLocation.getLongitude();
        Log.d(TAG, "on recieve loation called with lat & lon = " + receiveLocation.getLatitude() + receiveLocation.getLongitude());
        facilityGPSTxt.setText("Current GPS = " + latitude + "," + longitude);
        // EventBus.getDefault().post(new PlaceFetchEvent(getAddressFromLocation(receiveLocation.getLatitude(), receiveLocation.getLongitude()), mCallerCode, true));
    }

}
