package com.example.progetto;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.progetto.ViewModel.AddViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.progetto.Utilities.REQUEST_IMAGE_CAPTURE;

public class AddFragment extends Fragment {

    private TextInputEditText usernameTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private TextInputEditText firstnameTextInputEditText;
    private TextInputEditText lastnameTextInputEditText;
    private TextInputEditText descriptionTextInputEditText;
    private TextInputEditText birthdayTextInputEditText;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private boolean requestingLocationUpdates = false;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private RequestQueue requestQueue;
    private final static String OSM_REQUEST_TAG = "OSM_REQUEST";

    //callback that keep monitored the internet connection
    private ConnectivityManager.NetworkCallback networkCallback;
    private Boolean isNetworkConnected = false;
    private Snackbar snackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_travel, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        if (activity != null) {
            requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        startLocationUpdates(activity);
                        Log.d("LAB", "PERMISSION GRANTED");
                    } else {
                        Log.d("LAB", "PERMISSION NOT GRANTED");
                        showDialog(activity);
                    }
                }
            });

            initializeLocation(activity);

            snackbar = Snackbar.make(activity.findViewById(R.id.fragment_container_view),
                    "No Internet Available",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                activity.startActivity(intent);
                            }
                        }
                    });

            networkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isNetworkConnected = true;
                    snackbar.dismiss();
                    if (requestingLocationUpdates){
                        startLocationUpdates(activity);
                    }
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isNetworkConnected = false;
                    snackbar.show();
                }
            };

            requestQueue = Volley.newRequestQueue(activity);

            Utilities.setUpToolbar((AppCompatActivity) activity, "Add Travel");

            usernameTextInputEditText = activity.findViewById(R.id.placeTextInputEditText);
            descriptionTextInputEditText = activity.findViewById(R.id.descriptionTextInputEditText);
            birthdayTextInputEditText = activity.findViewById(R.id.dateTextInputEditText);

            view.findViewById(R.id.captureButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there is a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });

            ImageView imageView = view.findViewById(R.id.imageView);
            AddViewModel addViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(AddViewModel.class);
            addViewModel.getBitmap().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
                @Override
                public void onChanged(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            });

            view.findViewById(R.id.gpsButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestingLocationUpdates = true;
                    registerNetworkCallback(activity);
                    startLocationUpdates(activity);
                }
            });

            view.findViewById(R.id.fab_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Bitmap bitmap = addViewModel.getBitmap().getValue();
                        String imageUriString;
                        if (bitmap != null) {
                            //method to save the image in the gallery of the device
                            imageUriString = String.valueOf(saveImage(bitmap, activity));
                            //Toast.makeText(activity,"Image Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            imageUriString = "ic_launcher_foreground";
                        }

                        addViewModel.addCardItem(new CardItem(imageUriString,
                                usernameTextInputEditText.getText().toString(),
                                birthdayTextInputEditText.getText().toString(),
                                descriptionTextInputEditText.getText().toString(),
                                birthdayTextInputEditText.getText().toString()));

                        addViewModel.setImageBitmpap(null);

                        ((AppCompatActivity) activity).getSupportFragmentManager().popBackStack();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.app_bar_search).setVisible(false);
    }

    /**
     * Method called to save the image taken as a file in the gallery
     * @param bitmap the image taken
     * @throws IOException if there are some issue with the creation of the image file
     * @return the Uri of the image saved
     */
    private Uri saveImage(Bitmap bitmap, Activity activity) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date());
        String name = "JPEG_" + timeStamp + "_.png";

        ContentResolver resolver = activity.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.d("LAB-AddFragment", String.valueOf(imageUri));
        OutputStream fos = resolver.openOutputStream(imageUri);

        //for the jpeg quality, it goes from 0 to 100
        //for the png, the quality is ignored
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (fos != null) {
            fos.close();
        }
        return imageUri;
    }

    /**
     * Method called to initialize the 3 elements for the Location:
     * FusedLocationProviderClient
     * LocationRequest
     * LocationRequest
     *
     * @param activity the current Activity
     */
    private void initializeLocation(Activity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create();
        // Set the desired interval for active location updates, in milliseconds.
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Update UI with location data
                Location location = locationResult.getLastLocation();

                if (isNetworkConnected) {
                    //if internet connection is available, I can make the request
                    sendVolleyRequest(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                    requestingLocationUpdates = false;
                    stopLocationUpdates();
                } else {
                    //if internet connection is not available, I'll show the user a snackbar
                    snackbar.show();
                }
            }
        };
    }

    /**
     * Method called to start requesting the updates for the Location
     * It checks also the permission fo the Manifest.permission.ACCESS_FINE_LOCATION
     *
     * @param activity the current Activity
     */
    private void startLocationUpdates(Activity activity) {
        final String PERMISSION_REQUESTED = Manifest.permission.ACCESS_FINE_LOCATION;
        //permission granted
        if (ActivityCompat.checkSelfPermission(activity, PERMISSION_REQUESTED) == PackageManager.PERMISSION_GRANTED) {
            statusGPSCheck(activity);
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_REQUESTED)) {
            //if the permission was denied before
            showDialog(activity);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(PERMISSION_REQUESTED);
        }
    }

    /**
     *Method called to create a new Dialog to check the user for previously denied permission
     *
     * @param activity the current Activity
     */
    private void showDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("Permission was denied, but is needed for the gps functionality.")
                .setCancelable(false) //Sets whether this dialog is cancelable with the BACK key.
                .setPositiveButton("OK", (dialog, id) -> activity.startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates && getActivity() != null) {
            startLocationUpdates(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Method called to stop the updates for the Location
     */
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Method called to check the status of the GPS (on or off)
     * If the GPS is off, a dialog will be displayed to the user to turn it on
     *
     * @param activity the current Activity
     */
    private void statusGPSCheck(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //if gps is off, show the alert message
            new AlertDialog.Builder(activity)
                    .setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> activity.startActivity(
                            new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .create()
                    .show();
        }
    }

    /**
     * Method called to query the OpenStreetMap API
     * @param latitude latitude of the device
     * @param longitude longitude of the device
     */
    private void sendVolleyRequest(String latitude, String longitude){
        String url ="https://nominatim.openstreetmap.org/reverse?lat="+latitude+
                "&lon="+longitude+"&format=jsonv2&limit=1";

        // Request a jsonObject response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    usernameTextInputEditText.setText(response.get("name").toString());
                    unRegisterNetworkCallback();
                } catch (JSONException e) {
                    usernameTextInputEditText.setText("/");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LAB", error.toString());
            }
        });

        jsonObjectRequest.setTag(OSM_REQUEST_TAG);
        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(OSM_REQUEST_TAG);
        }
        unRegisterNetworkCallback();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && requestingLocationUpdates) {
            registerNetworkCallback(getActivity());
        }
    }

    /**
     * Method called to register the NetworkCallback in the ConnectivityManager (SDK >= N) or
     * to get info about the network with NetworkInfo (Android 6)
     *
     * @param activity the current Activity
     */
    private void registerNetworkCallback(Activity activity) {
        Log.d("LAB","registerNetworkCallback");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                //Class deprecated since API 29 (android 10) but used for android 6
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    /**
     * Method called to unregister the NetworkCallback (SDK >= N) or
     * to dismiss the snackbar in Android 6 (it works only if the snackbar is still visible)
     *
     */
    private void unRegisterNetworkCallback() {
        if (getActivity() != null) {
            Log.d("LAB", "unRegisterNetworkCallback");
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connectivityManager.unregisterNetworkCallback(networkCallback);
                } else {
                    snackbar.dismiss();
                }
            }
        }
    }
}
