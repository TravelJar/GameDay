package com.sample.gameday.facilities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sample.gameday.R;
import com.sample.gameday.utility.HelpMe;
import com.sample.gameday.facilities.adapters.UnVerifiedFacilitiesAdapter;
import com.sample.gameday.models.Facility;
import com.sample.gameday.volley.AppController;
import com.sample.gameday.volley.CustomJsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class UnVerifiedFacilities extends AppCompatActivity {

    private static final String TAG = "UnVerifiedFacilities";
    private ProgressDialog mDialog;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Facility> facilityArrayList;
    private ArrayList<Facility> unVerifiedFacilityArraylist;
    private UnVerifiedFacilitiesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unverified_facilities_list);

        setUpToolBar();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("please wait...");
        mDialog.setCanceledOnTouchOutside(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.active_journey_recycler_view);
        mRecyclerView.setVisibility(View.VISIBLE);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Add pull to refresh functionality
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.active_journey_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchAllUnverifiedFacilities();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("UnVerfied Facilities");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    // Makes a call to backend server and fetches all unverified list of facilities
    public void fetchAllUnverifiedFacilities() {

        // Check internet connection
        if (!HelpMe.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            return;
        }

        mDialog.show();

        String url = "http://ec2-52-34-17-117.us-west-2.compute.amazonaws.com/facilities";
        Log.d(TAG, "SSO fb url is" + url);

        // Request a string response from the provided URL.
        CustomJsonArrayRequest signUpReg = new CustomJsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        parseJSON(response);
                        // EventBus.getDefault().post(new JsonResponseEvent(response, SyncStateContract.Constants.JE_SSO_FB_RESULT, true));
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


    // Parse the response from server and create a list of custom objects
    private void parseJSON(JSONArray response) {

        int len = response.length();
        facilityArrayList = new ArrayList<>();

        try {

            for (int i = 0; i < len; i++) {
                String name = response.getJSONObject(i).getString("name");
                String id = response.getJSONObject(i).getString("id");
                boolean isVerified = response.getJSONObject(i).getBoolean("verified");
                String city = response.getJSONObject(i).getJSONObject("address").getString("city");
                Log.d(TAG, "name is  = " + name);

                if (!isVerified) {
                    Facility f = new Facility(id, name, city, isVerified, null, null);
                    facilityArrayList.add(f);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        renderList(facilityArrayList);
    }

    private void renderList(ArrayList<Facility> facilityArrayList) {
        if (facilityArrayList.size() > 0) {
            findViewById(R.id.active_journey_zero_journeys_layout).setVisibility(View.GONE);
            findViewById(R.id.active_journey_some_journeys_layout).setVisibility(View.VISIBLE);
            if (mAdapter == null) {
                Log.d(TAG, "adapter was null");
                unVerifiedFacilityArraylist = facilityArrayList;
                mAdapter = new UnVerifiedFacilitiesAdapter(unVerifiedFacilityArraylist, this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Log.d(TAG, "adapter was NOT null");
                unVerifiedFacilityArraylist.clear();
                unVerifiedFacilityArraylist.addAll(facilityArrayList);
                mAdapter.notifyDataSetChanged();
            }
        } else {

            findViewById(R.id.active_journey_zero_journeys_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.active_journey_some_journeys_layout).setVisibility(View.GONE);
        }

        mDialog.dismiss();
    }

    @Override
    public void onResume() {
        fetchAllUnverifiedFacilities();
        super.onResume();
    }

}

