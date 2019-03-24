package ca.bcit.planters.treepost;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 10;
    MapView map = null;
    com.google.firebase.database.FirebaseDatabase database = com.google.firebase.database.FirebaseDatabase.getInstance();
    com.google.firebase.database.DatabaseReference myRef = database.getReference();
    Drawable bwTree = null;

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
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        getLocation();

        MapController mMapController = (MapController) map.getController();
        mMapController.setZoom(15);
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

        Bitmap b = ((BitmapDrawable)getDrawable(R.drawable.col)).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
        bwTree = new BitmapDrawable(getResources(), bitmapResized);

        for (String[] row : list) {
            if (!row[3].equals("")) {
                double latitude = Double.parseDouble(row[7]);
                double longitude = Double.parseDouble(row[6]);
                IGeoPoint point = new LabelledGeoPoint(latitude, longitude, row[3]);
                points.add(point);


                final OverlayItem overlayItem = new OverlayItem("", "", point);
                //Bitmap b = ((BitmapDrawable)getDrawable(R.drawable.col)).getBitmap();
                //Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
                //overlayItem.setMarker(new BitmapDrawable(getResources(), bitmapResized));

                overlayItem.setMarker(new ColorDrawable(Color.TRANSPARENT));
                new PopulateIcon().execute(overlayItem);
                items.add(overlayItem);


                /*
                String id = latitude+"_"+longitude;
                id = id.replace('.', '*');
                com.google.firebase.database.DatabaseReference ref = myRef.child(id);
                com.google.firebase.database.DatabaseReference pubMsgRef = ref.child("publicMsg");
                com.google.firebase.database.DatabaseReference privMsgRef = ref.child("privateMsg");
                pubMsgRef.addValueEventListener(new ValueEventListener() {
                    private OverlayItem it = overlayItem;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Message msg = ds.getValue(Message.class);
                            if(msg.owner.equals(getIntent().getStringExtra("email"))) {
                                Bitmap b = ((BitmapDrawable)getDrawable(R.drawable.col)).getBitmap();
                                Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
                                it.setMarker(new BitmapDrawable(getResources(), bitmapResized));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                pubMsgRef.addValueEventListener(new ValueEventListener() {
                    private OverlayItem it = overlayItem;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Message msg = ds.getValue(Message.class);
                            if(msg.owner.equals(getIntent().getStringExtra("email"))) {
                                Bitmap b = ((BitmapDrawable)getDrawable(R.drawable.col)).getBitmap();
                                Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 100, 100, false);
                                it.setMarker(new BitmapDrawable(getResources(), bitmapResized));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                */
            }
        }


        /*
        // wrap them in a theme
        SimplePointTheme pt = new SimplePointTheme(points, false);

        // create label style
        Paint textStyle = new Paint();
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);

        // set some visual options for the overlay
        // we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
        SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(15).setIsClickable(true).setCellSize(30).setTextStyle(textStyle);

        // create the overlay with the theme
        final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);

        // onClick callback
        sfpo.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(final SimpleFastPointOverlay.PointAdapter points, final Integer point) {

                final String[] types = {"Public Message", "Private Message", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select message type to view");
                builder.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != 2) {
                            String treeId = points.get(point).getLatitude() + "_" + points.get(point).getLongitude();
                            Intent intent = new Intent(MainActivity.this, TreeActivity.class);
                            intent.putExtra("type", types[which]);
                            intent.putExtra("id", treeId.replace('.', '*'));
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });

        // add overlay
        map.getOverlays().add(sfpo);
        */


        Overlay overlay = new ItemizedOverlayWithFocus<>(this.getApplicationContext(), items,
            new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                    final String[] types = {"Public Message", "Private Message", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Select message type to view");
                    builder.setItems(types, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which != 2) {
                                IGeoPoint clicked = item.getPoint();
                                String treeId = clicked.getLatitude() + "_" + clicked.getLongitude();
                                Intent intent = new Intent(MainActivity.this, TreeActivity.class);
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            }
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(criteria, true);

        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(this, "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();
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