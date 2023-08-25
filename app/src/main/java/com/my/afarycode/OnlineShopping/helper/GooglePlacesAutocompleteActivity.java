package com.my.afarycode.OnlineShopping.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.my.afarycode.OnlineShopping.servercommunication.GPSTracker;
import com.my.afarycode.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.my.afarycode.OnlineShopping.CheckOutDelivery.address_select;

public class GooglePlacesAutocompleteActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final Integer THRESHOLD = 2;
    public String order_landmarkadd;
    int PERMISSION_ID = 44;
    private AutoCompleteTextView gettypedlocation;
    private int count = 0;
    private double longitude;
    private double latitude;
    private String order_landmarkadd1;
    private TextView cancle_text;
    private GooglePlacesAutocompleteActivity mContext;
    private GPSTracker gps;
    private String address;
    private GoogleMap mMap;
    private LatLng locationLatLong;
    private Animation myAnim;
    private ImageView image_pin;
    private TextView done_text;
    private Object MyListener;
    private GPSTracker gpsTracker;
    private String address1;
    private double lat;
    private String get_address;
    private EditText edittext_location;
    private String edittext_location_get;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private FusedLocationProviderClient fusedLocationClient;
    private double lattitutte_last = 31.205753;
    private double logigiute_last = 29.924526;
    private double latiute;
    private double logitiute;
    int increment = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(GooglePlacesAutocompleteActivity.this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lattitutte_last = location.getLatitude();
                            logigiute_last = location.getLongitude();
                            Log.e(">MyLastLocation>>", "MyLastLocation coordinat :" + lattitutte_last);
                        }
                    }
                });


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                get_address = null;
            } else {
                get_address = extras.getString("get_address");
            }

        } else {
            get_address = (String) savedInstanceState.getSerializable("get_address");
        }

        if (get_address.equals("map")) {

            GetLocation();

        } else {

        }

        image_pin = findViewById(R.id.image_pin);
        gettypedlocation = findViewById(R.id.gettypedlocation);
        edittext_location = findViewById(R.id.edittext_location);
        myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        done_text = findViewById(R.id.done_text);

        done_text.setOnClickListener(v -> {
            // AppointmentFragment.address.setText(edittext_location.getText().toString().trim());
            finish();
        });

        // edittext_location.setVisibility(View.VISIBLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        autocompleteView();
    }

    private void GetLocation() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(GooglePlacesAutocompleteActivity.this).
                    addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                }

                @Override
                public void onConnectionSuspended(int i) {
                    googleApiClient.connect();
                }
            }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                }
            }).build();

            googleApiClient.connect();

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10 * 1000);
            mLocationRequest.setFastestInterval(1 * 1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder1 = new
                    LocationSettingsRequest.Builder();
            builder1.addLocationRequest(mLocationRequest);
            builder1.setAlwaysShow(true);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            try {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.e("longitude_l>>", "" + longitude);
                Log.e("latitude_l>>", "" + latitude);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder1.build());
            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(GooglePlacesAutocompleteActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                break;
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setCurrentLoc();

                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    private void autocompleteView() {
        gettypedlocation = findViewById(R.id.gettypedlocation);
        edittext_location_get = edittext_location.getText().toString().trim();
        gettypedlocation.requestFocus();
        gettypedlocation.setThreshold(THRESHOLD);

        gettypedlocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {

                    loadData(gettypedlocation.getText().toString());

                } else {

                }
            }
        });
    }

    private void loadData(String s) {

        try {

            if (count == 0) {

                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    Log.e("lattttt>>>", "" + latitude);
                    Log.e("longitude>>>", "" + longitude);

                    l1.add(s);
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(GooglePlacesAutocompleteActivity.this, l1, "" + latitude, "" + longitude);
                    gettypedlocation.setAdapter(ga);
                    ga.notifyDataSetChanged();
                }
            }
            count++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // gpsTracker = new GPSTracker(getApplicationContext());
        mMap = googleMap;

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setCurrentLoc();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCurrentLoc();
            }
        }
    }

    private void setCurrentLoc() {
        gpsTracker = new GPSTracker(GooglePlacesAutocompleteActivity.this);
        double lat = gpsTracker.getLatitude();
        double lon = gpsTracker.getLatitude();

        Log.e("latitutee", "" + lat);
        Log.e("long", "" + lon);

        Log.e("latitutee_last", "" + lattitutte_last);
        Log.e("long", "" + logigiute_last);

        if (lat == 0.0 && lon == 0.0) {
            latiute = lattitutte_last;
            logitiute = logigiute_last;

        } else {
            latiute = gpsTracker.getLatitude();
            logitiute = gpsTracker.getLongitude();
        }

        try {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(latiute, logitiute))));
            Log.e("latiutessssa>", "" + latiute);
            Log.e("logitiutessssa>", "" + logitiute);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latiute, logitiute, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double lat = mMap.getCameraPosition().target.latitude;
                double lng = mMap.getCameraPosition().target.longitude;
                locationLatLong = new LatLng(lat, lng);

                Geocoder geocoder;
                List<Address> addresses = null;

                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    address1 = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                } catch (Exception e) {
                    e.printStackTrace();
                }
                image_pin.startAnimation(myAnim);
                gettypedlocation.setText("" + address1);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    public class GeoAutoCompleteAdapter extends BaseAdapter implements Filterable {

        private final LayoutInflater layoutInflater;
        private final WebOperations wo;
        private final String lat;
        private final String lon;
        private final Context context;
        private List<String> l2 = new ArrayList<>();

        public GeoAutoCompleteAdapter(Activity context, List<String> l2, String lat, String lon) {
            this.context = context;
            this.l2 = l2;
            this.lat = lat;
            this.lon = lon;
            layoutInflater = LayoutInflater.from(context);
            wo = new WebOperations(context);
        }

        @Override
        public int getCount() {

            return l2 == null ? 0 : l2.size();
        }

        @Override
        public Object getItem(int i) {
            return l2.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.geo_search_result, viewGroup, false);
            TextView geo_search_result_text = view.findViewById(R.id.geo_search_result_text);

            try {

                geo_search_result_text.setText(l2.get(i));

                done_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        if (l2 == null || l2.isEmpty()) {
                            address_select.setText(edittext_location_get);
                            finish();

                        } else {

                            gettypedlocation.setText("" + l2.get(i));
                            gettypedlocation.dismissDropDown();
                            order_landmarkadd = gettypedlocation.getText().toString();


                            address_select.setText(order_landmarkadd);
                            finish();
                            //PreferenceConnector.writeString(com.homescanupdate.helper.GooglePlacesAutocompleteActivity.this, PreferenceConnector.Address_Save1, order_landmarkadd);
                        }
                    }
                });

                geo_search_result_text.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        InputMethodManager inputManager = (InputMethodManager)
                                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        if (l2 == null || l2.isEmpty()) {

                        } else {

                            Log.e("city>>>", l2.get(i));

                            gettypedlocation.setText("" + l2.get(i));
                            gettypedlocation.dismissDropDown();
                            order_landmarkadd = gettypedlocation.getText().toString();

                            address_select.setText(order_landmarkadd);


                            finish();
                            //   PreferenceConnector.writeString(com.homescanupdate.helper.GooglePlacesAutocompleteActivity.this, PreferenceConnector.Address_Save1, order_landmarkadd);
                        }
                    }
                });

            } catch (Exception e) {

            }

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyDQhXBxYiOPm-aGspwuKueT3CfBOIY3SJs&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=geocode&sensor=true");
                        // wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyCy4LieSCpx8z5OITTv50vgF4Nj7TrwMX0&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=geocode&sensor=true");
                        String result = null;

                        Log.e("latitute>>up", lat + "latitute>>up" + lon);

                        try {
                            result = new MyTask(wo, 3).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        parseJson(result);

                        filterResults.values = l2;
                        filterResults.count = l2.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count != 0) {
                        l2 = (List) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        private void parseJson(String result) {
            try {
                l2 = new ArrayList<>();
                JSONObject jk = new JSONObject(result);

                JSONArray predictions = jk.getJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject js = predictions.getJSONObject(i);
                    l2.add(js.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}