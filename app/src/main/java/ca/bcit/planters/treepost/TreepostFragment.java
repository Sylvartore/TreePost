package ca.bcit.planters.treepost;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TreepostFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final int REQUEST_CODE = 10;
    MapView map = null;
    Drawable bwTree = null;

    public TreepostFragment() {
        // Required empty public constructor
    }

    class PopulateIcon extends AsyncTask<OverlayItem, Void, Void> {
        @Override
        public Void doInBackground(OverlayItem... items){
            final OverlayItem overlayItem = items[0];
            overlayItem.setMarker(bwTree);
            return null;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_treepost, container, false);

//        AnimationBottomNavigationView.addAnimation(
//                rootView,
//                getActivity().findViewById(R.id.navigation_treepost),
//                ContextCompat.getColor(getActivity(), R.color.animation_start),
//                ContextCompat.getColor(getActivity(), R.color.animation_end)
//        );

        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        map = rootView.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        getLocation();

        MapController mMapController = (MapController) map.getController();
        mMapController.setZoom(5);
        GeoPoint gPt = new GeoPoint(49.2057, -122.911); // for New West: 49.2057, -122.9110
        mMapController.setCenter(gPt);

        List<IGeoPoint> points = new ArrayList<>(); List<OverlayItem> items = new ArrayList<>();

        List<String[]> list = new ArrayList<>();
        try {
            InputStream in = getResources().openRawResource(R.raw.tree_west);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            reader.readLine(); // skip the first line
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                list.add(row);
            }
            in.close();
            reader.close();

            in = getResources().openRawResource(R.raw.tree_east);
            reader = new BufferedReader(new InputStreamReader(in));

            reader.readLine(); // skip the first line

            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                list.add(row);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading csv files");
        }

        Bitmap b = ((BitmapDrawable)getActivity().getDrawable(R.drawable.col)).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
        bwTree = new BitmapDrawable(getResources(), bitmapResized);

        for (String[] row : list) {
            if (!row[3].equals("")) {
                double latitude = Double.parseDouble(row[7]);
                double longitude = Double.parseDouble(row[6]);
                IGeoPoint point = new LabelledGeoPoint(latitude, longitude, row[3]);
                points.add(point);


                final OverlayItem overlayItem = new OverlayItem("", "", point);

                overlayItem.setMarker(new ColorDrawable(Color.TRANSPARENT));
                new TreepostFragment.PopulateIcon().execute(overlayItem);
                items.add(overlayItem);

            }
        }


        Overlay overlay = new ItemizedOverlayWithFocus<>(this.getActivity().getApplicationContext(), items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        final String[] types = {"Public Message", "Private Message", "Cancel"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Select message type to view");
                        builder.setItems(types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which != 2) {
                                    IGeoPoint clicked = item.getPoint();
                                    String treeId = clicked.getLatitude() + "_" + clicked.getLongitude();
                                    Intent intent = new Intent(getActivity(), TreeActivity.class);
                                    intent.putExtra("type", types[which]);
                                    intent.putExtra("id", treeId.replace('.', '*'));
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        map.getOverlays().add(overlay);
        map.invalidate();

        return rootView;

    }

    // Rename method, update argument and hook method into UI event
    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            }
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(criteria, true);

        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(getContext(), "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                MapController mMapController = (MapController) map.getController();
                mMapController.setZoom(15);
                GeoPoint gPt = new GeoPoint(latitude, longitude); // for New West: 49.2057, -122.9110
                mMapController.setCenter(gPt);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(provider, 10000, 10, locationListener);
    }

}
