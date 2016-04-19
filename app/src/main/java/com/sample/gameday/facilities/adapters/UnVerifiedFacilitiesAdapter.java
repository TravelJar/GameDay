package com.sample.gameday.facilities.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.gameday.R;
import com.sample.gameday.facilities.FacilityDetail;
import com.sample.gameday.models.Facility;

import java.util.List;

/**
 * Created by abhi on 18/04/16.
 */

public class UnVerifiedFacilitiesAdapter extends RecyclerView.Adapter<UnVerifiedFacilitiesAdapter.ViewHolder> {
    private static final String TAG = "<UnVerifiedFacilitiesAdapter>";
    private List<Facility> mDataset;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public UnVerifiedFacilitiesAdapter(List<Facility> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    public void add(int position, Facility item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Facility item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unverified_facilities_list_item,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Facility facilityItem = mDataset.get(position);
        final String name = facilityItem.getName();
        final String place = facilityItem.getCity();

        holder.facilityName.setText(name);
        holder.facilityPlace.setText(place);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView facilityName;
        public TextView facilityPlace;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            facilityName = (TextView) v.findViewById(R.id.active_journey_list_name);
            facilityPlace = (TextView) v.findViewById(R.id.active_journey_list_buddy_place);
        }

        // In Recycler views OnItemCLick is handled here
        @Override
        public void onClick(View v) {
            Facility facility = mDataset.get(getLayoutPosition());

            Intent intent = new Intent(mContext, FacilityDetail.class);
            intent.putExtra("facilityId", facility.getId());
            intent.putExtra("facilityName", facility.getName());
            intent.putExtra("facilityCity", facility.getCity());
            mContext.startActivity(intent);
        }
    }
}
