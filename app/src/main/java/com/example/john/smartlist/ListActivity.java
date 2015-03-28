package com.example.john.smartlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class ListActivity extends ActionBarActivity {
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private BeaconManager beaconManager;
    private static final Region ALL_ESTIMOTE_BEACONS1 = new Region("wine", ESTIMOTE_PROXIMITY_UUID, 25431, 7731);
    private static final Region ALL_ESTIMOTE_BEACONS2 = new Region("grocery", ESTIMOTE_PROXIMITY_UUID, 41072, 44931);
    private static final Region ALL_ESTIMOTE_BEACONS3 = new Region("lifestyle", ESTIMOTE_PROXIMITY_UUID, 15212, 31506);

    private List<ParseObject> allData;
    private RegionItem currentRegionItem;

    private ArrayList<RegionItem> currentTrackedRegions;
    private HashMap<String, RegionItem> currentRegionsMap;
    private HashSet<String> removedObjects;

    private static final String TAG = "demo";
    RecyclerView mRecyclerView;
    ListAdapterHolder mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        removedObjects = new HashSet<>();

        allData = new ArrayList<ParseObject>();

        currentRegionsMap = new HashMap<String, RegionItem>();
        currentTrackedRegions = new ArrayList<RegionItem>();
        getSupportActionBar().setTitle("My Smart List");

        beaconManager = new BeaconManager(this);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                RegionItem r;
                if (beacons.size() > 0) {
                    Beacon beacon = beacons.get(0);
                    if (currentRegionsMap.containsKey(region.getIdentifier())) {
                        r = currentRegionsMap.get(region.getIdentifier());
                        r.updateDistance(Utils.computeAccuracy(beacon));
                        //decrement the other regions.
                        Log.d("1. demo", "Region already present");
                    } else {
                        r = new RegionItem(region, beacon);
                        currentRegionsMap.put(region.getIdentifier(), r);
                        currentTrackedRegions.add(r);
                        Log.d("1. demo", "Creating new on region");
                    }

                    if (currentRegionItem == null) { //initial case
                        currentRegionItem = r;
                        Log.d("demo", "2. initial case where tracking null");
                    } else {
                        Log.d("demo", "3. Sorting");
                        Collections.sort(currentTrackedRegions, new Comparator<RegionItem>() {
                            @Override
                            public int compare(RegionItem lhs, RegionItem rhs) {
                                double diff = lhs.distance - rhs.distance;
                                if (diff > 0) {
                                    return 1;
                                } else if (diff < 0) {
                                    return -1;
                                }
                                return 0;
                            }
                        });

                        if (currentRegionItem.equals(currentTrackedRegions.get(0))) {
                            currentRegionItem.resetCount();
                            Log.d("demo", "3. Current is still current " + currentRegionItem.toString());
                        } else {
                            Log.d("demo", "3. decrement current ");
                            currentRegionItem.decrementCount();
                            if (currentRegionItem.count <= 0) {
                                currentRegionItem = currentTrackedRegions.get(0);
                                currentRegionItem.resetCount();
                                Log.d("demo", "3. changing current to " + currentRegionItem.toString());

                                refreshList(getFilteredList(currentRegionItem.region.getIdentifier()));
                            }
                        }
                    }
                }
            }
        });
        setupItems();

    }

    private void setupItems() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("History");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByAscending("region");
        query.addAscendingOrder("recommendation");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    if (parseObjects == null || parseObjects.size() == 0) {
                        //do something !!

                    } else {
                        allData = parseObjects;
                        if (currentRegionItem == null) {
                            List<ParseObject> list = new ArrayList<>();
                            for (ParseObject object : allData) {
                                list.add(object);
                            }
                            refreshList(list);
                        } else {
                            refreshList(getFilteredList(currentRegionItem.region.getIdentifier()));
                        }
                    }
                } else {
                    Toast.makeText(ListActivity.this, "Error Loading List", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<ParseObject> getFilteredList(String regionId) {
        List<ParseObject> list = new ArrayList<>();
        for (ParseObject object : allData) {
            if (object.getString("region").equals(regionId) && !removedObjects.contains(object.getObjectId())) {
                list.add(object);
            }
        }
        return list;
    }


    private void refreshList(List<ParseObject> list) {
        mLayoutManager = new LinearLayoutManager(ListActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true); //no paging supported

        mAdapter = new ListAdapterHolder(ListActivity.this, list);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setOnItemClickListener(new ListAdapterHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //item clicked need to remove the item
                Log.d("demo", "Clicked " + position + "");
                removedObjects.add(mAdapter.mData.get(position).getObjectId());
                mAdapter.mData.remove(position);
                mAdapter.notifyDataSetChanged();
                //mRecyclerView.refreshDrawableState();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOutInBackground();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    //beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS1);
                    //beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS2);
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS1);
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS2);
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS3);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS1);
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS2);
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS3);
            //beaconManager.stopMonitoring(ALL_ESTIMOTE_BEACONS1);
            //beaconManager.stopMonitoring(ALL_ESTIMOTE_BEACONS2);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }


}
