package com.my.afarycode.OnlineShopping.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.loader.content.CursorLoader;
import com.my.afarycode.OnlineShopping.Model.LoginModel;
import com.my.afarycode.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.alirezabdn.wp7progress.WP10ProgressBar;

public class DataManager {


    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    private Dialog mDialog;
    private boolean isProgressDialogRunning = false;
    WP10ProgressBar progressBar;

    public void showProgressMessage(Activity dialogActivity, String msg) {
        try {
            if (isProgressDialogRunning) {
                hideProgressMessage();
            }
            isProgressDialogRunning = true;
            mDialog = new Dialog(dialogActivity);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_loading);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView textView = mDialog.findViewById(R.id.textView);
            progressBar = mDialog.findViewById(R.id.progressBar);
            textView.setText(msg);
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.dimAmount = 0.8f;
            progressBar.showProgressBar();
            mDialog.getWindow().setAttributes(lp);
            mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hideProgressMessage() {
        isProgressDialogRunning = true;
        try {
            if (mDialog != null) {
                mDialog.dismiss();
                progressBar.hideProgressBar();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public LoginModel getUserData(Context context) {
        // LoginModel userData = new Gson().fromJson(SessionManager.readString(context, Constant.USER_INFO, ""), LoginModel.class);
        // return userData;
        return null;
    }

    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        //TODO: get realpath from uri
        String stringPath = null;
        try {
            if (contentUri.getScheme().toString().compareTo("content") == 0) {
                String[] proj = {MediaStore.Images.Media.DATA};
                CursorLoader loader = new CursorLoader(activity, contentUri, proj, null, null, null);
                Cursor cursor = loader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                stringPath = cursor.getString(column_index);
                cursor.close();
            } else if (contentUri.getScheme().compareTo("file") == 0) {
                stringPath = contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringPath;
    }

    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String convertDateToString(long l) {
        String str = "";
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        str = dateFormat.format(date);
        return str;
    }

  /*
        public static boolean isValidEmail(CharSequence target) {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
        }


        public static String getCurrent() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  hh:mm aa");
            String currentDateandTime = sdf.format(new Date());
            return currentDateandTime;
        }*/


    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }



    public static File saveBitmapToFile(Context context, Bitmap bitmap, String fileName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Use JPEG or PNG based on your needs
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public Bitmap getImageAngle(String photoPath, Bitmap rotatedBitmap) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);//ORIENTATION_UNDEFINED

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(rotatedBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(rotatedBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(rotatedBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = rotatedBitmap;
            }
            return rotatedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try{
            URL url = new URL(src);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static String getCurrent() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  hh:mm aa");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate =   mdformat.format(calendar.getTime());
        return strDate;
    }

    public static String currentDate(){
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return currentDate;
    }

    public String getAddress(Context context, double latitude, double longitute) {
        List<Address> addresses;
        String addressStreet="";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitute, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addressStreet = addresses.get(0).getAddressLine(0);
            // address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            //  city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String region = addresses.get(0).getAdminArea();
            // Log.e("addressStreet====", addressStreet);
            //   Log.e("city====", city);
            Log.e("region====", region);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressStreet;

    }


    public String getCountry(Context context, double latitude, double longitute) {
        List<Address> addresses;
        String countryName="";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitute, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
          //  addressStreet = addresses.get(0).getAddressLine(0);
            // address2 = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            //  city = addresses.get(0).getLocality();
         //   String state = addresses.get(0).getAdminArea();
            countryName = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String region = addresses.get(0).getAdminArea();
            // Log.e("addressStreet====", addressStreet);
            //   Log.e("city====", city);
            Log.e("region====", countryName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return countryName;

    }





    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }


}
