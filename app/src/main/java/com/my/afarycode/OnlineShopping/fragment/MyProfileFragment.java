package com.my.afarycode.OnlineShopping.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.gson.Gson;
import com.my.afarycode.OnlineShopping.ChangePassword;
import com.my.afarycode.OnlineShopping.Model.GetProfileModal;
import com.my.afarycode.OnlineShopping.Model.UpdateProfileModal;
import com.my.afarycode.OnlineShopping.OrderHistoryScreen;
import com.my.afarycode.OnlineShopping.helper.DataManager;
import com.my.afarycode.OnlineShopping.myorder.MyOrderScreen;
import com.my.afarycode.OnlineShopping.PrivacyPolicy;
import com.my.afarycode.OnlineShopping.TermsCondition;
import com.my.afarycode.OnlineShopping.UpdateProfile;
import com.my.afarycode.OnlineShopping.WishListActivity;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.R;
import com.my.afarycode.Splash;
import com.my.afarycode.databinding.FragmentMyprofileBinding;
import com.my.afarycode.ratrofit.AfaryCode;
import com.my.afarycode.ratrofit.ApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyProfileFragment extends Fragment {
public String TAG ="MyProfileFragment";
    FragmentMyprofileBinding binding;
    Fragment fragment;
    private AfaryCode apiInterface;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int MY_PERMISSION_CONSTANT2 = 5;
    GetProfileModal data;
    Bitmap oneBitmap = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myprofile, container, false);
        apiInterface = ApiClient.getClient(getContext()).create(AfaryCode.class);


        showLang(PreferenceConnector.readString(requireActivity(), PreferenceConnector.LANGUAGE, ""));

        binding.RRback.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });


        binding.txtMyOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OrderHistoryScreen.class));
        });


        binding.RRChangeLanguage.setOnClickListener(v -> {
            showLanguageChangeDialog();
        });






        binding.RRLogout.setOnClickListener(v -> {
          //  Logout(PreferenceConnector.readString(getActivity(),PreferenceConnector.User_id,""),getActivity());
            showLogoutDialog();
        });

        binding.txtWishList.setOnClickListener(v -> {

            fragment = new WishListActivity();
            loadFragment(fragment);


        });

        binding.txtChangePassword.setOnClickListener(v -> {
            fragment = new ChangePassword();
            loadFragment(fragment);

        });

        binding.txtUpdate.setOnClickListener(v -> {
            fragment = new UpdateProfile();
            loadFragment(fragment);

        });


        binding.txtOnlineOrder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyOrderScreen.class));

        });


        binding.txtPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PrivacyPolicy.class));

        });




        binding.txtTerms.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TermsCondition.class));
        });


        binding.imgUser.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 33) {
                if (checkPermissionFor12Above()) showImageSelection();
            } else {
                if (checkPermissionForReadStorage11()) showImageSelection();
            }

        });




        GetProfile();

        return binding.getRoot();

    }

    private void showLang(String language) {
        if(language.equalsIgnoreCase("en"))
        binding.txtLang.setText(getString(R.string.english));
        else if(language.equalsIgnoreCase("fr")) binding.txtLang.setText(getString(R.string.french));
        else binding.txtLang.setText(getString(R.string.english));

    }


    public boolean loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .addToBackStack("Home")
                    .replace(R.id.fragment_homeContainer, fragment)//, tag)
                    .commit();
            return true;
        }
        return false;
    }


    private void showLanguageChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.change_language)
                .setItems(new CharSequence[]{
                        getString(R.string.english),
                        getString(R.string.french)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // English
                                changeLocale("en");
                                break;
                            case 1:
                                // French
                                changeLocale("fr");
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void changeLocale(String en) {
        updateResources(requireActivity(),en);
        PreferenceConnector.writeString(requireActivity(), PreferenceConnector.LANGUAGE, en);
        updateLanguage(PreferenceConnector.readString(getActivity(),PreferenceConnector.User_id,""),en,requireActivity());

    }


    private void updateResources(Context wellcomeScreen, String en) {
        Locale locale = new Locale(en);
        Locale.setDefault(locale);
        Resources resources = wellcomeScreen.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }


    public  void updateLanguage(String id,String language, Context context) {
        DataManager.getInstance().showProgressMessage(getActivity(),getString(R.string.please_wait));
        AfaryCode apiInterface = ApiClient.getClient(context.getApplicationContext()).create(AfaryCode.class);
        Map<String, String> map = new HashMap<>();
        map.put("user_id",id);
        map.put("language",language);
        Log.e(TAG,"Update Language Request "+map);
        Call<ResponseBody> loginCall = apiInterface.updateLanguageApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if(jsonObject.getString("status").equals("1")){
                        showLang(language);
                        Intent intent = new Intent(requireActivity(), Splash.class);
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    else if(jsonObject.getString("status").equals("0")){
                        //App.showToast(context,"data not available", Toast.LENGTH_SHORT);
                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        logttt();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    public  void Logout(String id, Context context) {
        DataManager.getInstance().showProgressMessage(getActivity(),getString(R.string.please_wait));

        AfaryCode apiInterface = ApiClient.getClient(context.getApplicationContext()).create(AfaryCode.class);
        Map<String, String> map = new HashMap<>();
        map.put("user_id",id);
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        Log.e(TAG,"User Logout Request "+map);
        Call<ResponseBody> loginCall = apiInterface.logoutApi(map);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")){
                        logttt();
                    }
                    else if(jsonObject.getString("status").equals("0")){
                        //App.showToast(context,"data not available", Toast.LENGTH_SHORT);
                    }
                    else if (jsonObject.getString("status").equals("5")) {
                        logttt();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });
    }

    private  void logttt() {

    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.are_you_sure_logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle cancel action
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void GetProfile() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.User_id, ""));
        map.put("register_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.Register_id, ""));
        Call<GetProfileModal> loginCall = apiInterface.get_profile(map);

        loginCall.enqueue(new Callback<GetProfileModal>() {
            @Override
            public void onResponse(Call<GetProfileModal> call,
                                   Response<GetProfileModal> response) {

                DataManager.getInstance().hideProgressMessage();

                try {

                    data = response.body();
                    String dataResponse = new Gson().toJson(response.body());

                    Log.e("MapMap", "GET RESPONSE" + dataResponse);

                    if (data.status.equals("1")) {

                        if (!data.getResult().getName().equalsIgnoreCase(""))   {
                            binding.tvName.setVisibility(View.VISIBLE);
                            binding.tvName.setText(data.getResult().getName());
                        }
                        else {
                            binding.tvName.setVisibility(View.VISIBLE);
                        }

                        binding.tvEmail.setText(data.getResult().email);
                        binding.tvMobile.setText(data.getResult().mobile);
                        Log.e("image>>>", data.getResult().image);

                        Picasso.get().load(data.getResult().image).error(R.drawable.user_default)
                                .into(binding.imgUser);

                    /*    if (!data.getResult().image.equalsIgnoreCase("http://technorizen.com/afarycode/uploads/images/")) {
                            Picasso.get().load(data.getResult().image).into(binding.imgUser);

                        } else {

                        }*/

                    } else if (data.status.equals("0")) {
                        Toast.makeText(getActivity(), data.message /*getString(R.string.wrong_username_password)*/, Toast.LENGTH_SHORT).show();
                    }

                    else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }




    public void showImageSelection() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_show_image_selection1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        LinearLayout layoutCamera = (LinearLayout) dialog.findViewById(R.id.layoutCemera);
        LinearLayout layoutGallary = (LinearLayout) dialog.findViewById(R.id.layoutGallary);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                openCamera();
            }
        });
        layoutGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                getPhotoFromGallary();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getPhotoFromGallary() {
      /*  Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);*/


        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.my.afarycode.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" /*+ timeStamp + "_"*/;
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        str_image_path = image.getAbsolutePath();


        return image;
    }

    //CHECKING FOR Camera STATUS
    public boolean checkPermissionForReadStorage11() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)


            ) {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT2);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT2);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }


    public boolean checkPermissionFor12Above() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED

        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.READ_MEDIA_IMAGES)
            ) {


                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);

            } else {

                //explain("Please Allow Location Permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        101);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_CONSTANT2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(getActivity(), " permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "  permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage) {
                        showImageSelection();
                    } else {
                        Toast.makeText(requireActivity(), "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "12 permission denied, boo! Disable the functionality that depends on this permission.", Toast.LENGTH_SHORT).show();
                }
                // return;
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
/*
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE_ADDRESS) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    Log.e("addressStreet====", place.getAddress());
                    address = place.getAddress();
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    //  city = DataManager.getInstance().getAddress(SignupAct.this,latitude,longitude);
                    //  binding.tvCity.setVisibility(View.VISIBLE);
                    //   binding.tvCity.setText(city);
                    binding.tvAddress.setText(place.getAddress());
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            }
*/
            if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (requestCode == SELECT_FILE) {
                str_image_path = DataManager.getInstance().getRealPathFromURI(getActivity(), data.getData());

                try {
                    oneBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                       /* if(oneBitmap!=null) {
                            oneBitmap = resizeBitmap(oneBitmap, 3000, 3000);
                        }*/

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Log.e("bitmap===", oneBitmap + "");
                Glide.with(getActivity())
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.imgUser);

                UpDateAPi();


            } else if (requestCode == REQUEST_CAMERA) {
                Glide.with(getActivity())
                        .load(str_image_path)
                        .centerCrop()
                        .into(binding.imgUser);


                Glide.with(requireActivity())
                        .asBitmap()
                        .load(str_image_path)  // URL or file path
                        .centerCrop()
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Bitmap is now ready to use
                                // Do something with the Bitmap
                                oneBitmap = resource;
                                binding.imgUser.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Handle cleanup if necessary
                            }
                        });


                UpDateAPi();

            }


        }


    }

    private void UpDateAPi() {

        DataManager.getInstance().showProgressMessage(getActivity(), getString(R.string.please_wait));
        MultipartBody.Part filePart;

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + PreferenceConnector.readString(getActivity(), PreferenceConnector.access_token, ""));
        headerMap.put("Accept", "application/json");

        if (oneBitmap != null) {
            File file =  persistImage(oneBitmap, generateString(12),requireActivity());  //DataManager.saveBitmapToFile(requireActivity(), oneBitmap, "image.jpg");
            //filePart1 = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image_1/*"), file));

            //  File file = DataManager.getInstance().saveBitmapToFile(new File((str_image_path)));

            filePart = MultipartBody.Part.createFormData("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));

            Log.e("str_image_path1>>>", "" + str_image_path);

        } else {

            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getName());
        RequestBody user_name = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getUserName());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getEmail());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), data.getResult().getMobile());
        RequestBody etAddresslogin = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody regisId = RequestBody.create(MediaType.parse("text/plain"), PreferenceConnector.readString(getContext(), PreferenceConnector.Register_id, ""));


        Call<UpdateProfileModal> signupCall = apiInterface.update_profile(headerMap, user_id, name, user_name, email, mobile, etAddresslogin, regisId, filePart);

        signupCall.enqueue(new Callback<UpdateProfileModal>() {
            @Override
            public void onResponse(Call<UpdateProfileModal> call, Response<UpdateProfileModal> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    UpdateProfileModal data = response.body();

                    if (data.status.equals("1")) {
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        // Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                        GetProfile();
                    } else if (data.status.equals("0")) {
                        Toast.makeText(getContext(), data.message, Toast.LENGTH_SHORT).show();
                    } else if (data.status.equals("5")) {
                        PreferenceConnector.writeString(getActivity(), PreferenceConnector.LoginStatus, "false");
                        startActivity(new Intent(getActivity(), Splash.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileModal> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }

        });


    }


    private File persistImage(Bitmap bitmap, String name, Context context) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        Log.e("image name===", name);
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "persistImage: " + e.getMessage());
        }

        return imageFile;

    }


    public String generateString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }



}