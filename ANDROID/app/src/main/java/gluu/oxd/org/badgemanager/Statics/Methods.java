package gluu.oxd.org.badgemanager.Statics;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gluu.oxd.org.badgemanager.Application;
import gluu.oxd.org.badgemanager.R;

/**
 * Created by lcom76 on 4/11/16.
 */

public class Methods {
    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyBoard(Context context, View view) {
//        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean isValidEmail(String target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private static Typeface t1;

    public static Typeface getNexaBold(Context context) {
        if (t1 == null) {
            t1 = Typeface.createFromAsset(Application.applicationContext.getAssets(), "NEXABOLD.TTF");
        }
        return t1;
    }

    public static Typeface getNexaBold() {
        if (t1 == null) {
            t1 = Typeface.createFromAsset(Application.applicationContext.getAssets(), "NEXABOLD.TTF");
        }
        return t1;
    }

    private static Typeface t2;

    public static Typeface getNexaLight(Context context) {
        if (t2 == null) {
            t2 = Typeface.createFromAsset(Application.applicationContext.getAssets(), "NEXALIGHT.TTF");
        }
        return t2;
    }

    public static Typeface getNexaLight() {
        if (t2 == null) {
            t2 = Typeface.createFromAsset(Application.applicationContext.getAssets(), "NEXALIGHT.TTF");
        }
        return t2;
    }


    public static void setOnclick(final Context context, final View v, View.OnClickListener listener) {
//        Typeface typeface=Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.fontName));
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setOnclick(context, child, listener);
                }
            } else if (v instanceof View) {
                v.setOnClickListener(listener);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void AddFragmentWithBackStack(AppCompatActivity activity, int container, Fragment fragment) {

        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            mgr.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }


        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.content_home_screen).getTag() != fragment.getTag()) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(container, fragment, fragment.getClass().getName())
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }

    }


    public static void AddFragment(AppCompatActivity activity, int container, Fragment fragment) {

        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            mgr.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }


        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.content_home_screen).getTag() != fragment.getTag()) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(container, fragment, fragment.getClass().getName())
                    .commit();
        }

    }

    public static void Replace(AppCompatActivity activity, int container, Fragment fragment) {

        if (activity.getCurrentFocus() != null) {
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        FragmentManager fragmentManager = activity.getSupportFragmentManager();


        if (fragmentManager.findFragmentById(R.id.content_home_screen) == null || fragmentManager.findFragmentById(R.id.content_home_screen).getClass().getName() != fragment.getClass().getName()) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(container, fragment, fragment.getClass().getName())
                    .commit();
        }

    }

    public static void internetDialog(Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("You are offline.Check your internet connection");
        alertDialog.setTitle("Sorry");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public static void BottomBarOnclickListner(View v, Context context, View.OnClickListener listener) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                vg.setOnClickListener(listener);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    BottomBarOnclickListner(child, context, listener);
                }
            } else if (v instanceof View) {
                v.setOnClickListener(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void OpenDatePicker(Context context, final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // editText.setText(String.format(Application.applicationContext.getResources().getString(R.string.lbl_date), dayOfMonth, (monthOfYear + 1), year));
//                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                editText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);


            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static void OpenDateWithoutDate(Context context, final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // editText.setText(String.format(Application.applicationContext.getResources().getString(R.string.lbl_date), dayOfMonth, (monthOfYear + 1), year));
//                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                editText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);


            }
        };
        DatePickerDialog dpd = new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }

        dpd.show();
    }

    public static void OpenDatePicker(Context context, final Button editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    public static void phoneNoVerification(final EditText editText, final TextInputLayout textInputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (PhoneNumberUtils.isGlobalPhoneNumber(editText.getText().toString())) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
//                    if(editText.getText().toString().length()==10)
//                    PhoneNumberUtils.formatNumber(s,PhoneNumberUtils.FORMAT_NANP);

                } else {
                    textInputLayout.setError("Not a valid Phone Number");

                }
            }
        });
    }


    public static String setFirstCapital(String s) {
        if (s.length() > 0) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
            return s;
        } else {
            return s;
        }
    }

    public static void EmailValidationListner(final EditText text, final TextInputLayout textInputLayout) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(text.getText().toString()).matches()) {
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);

                } else {
                    textInputLayout.setError("Enter a valid email address");
                }
            }
        });

    }


    public static void firstCapitalListner(final EditText text, final TextInputLayout textInputLayout) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (text.getText().toString().length() == 1) {
                    String edittexttext = text.getText().toString();

                    edittexttext.toUpperCase();
                    text.setText(edittexttext);
                }
            }
        });

    }


    public static void performeDone(EditText editText, final View view) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    view.performClick();
                }
                return true;
            }
        });
    }

    public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception ex) {
//            Log.e("Exception in BimapUtils","getPath");
            ex.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Log.e("ExifInteface .........", "rotation =" + orientation);

            // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                // m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }


    public static void setColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        /*
         * for ( int i = 0; i < listAdapter.getCount( ); i++ ) { View listItem =
         * listAdapter.getView( i, null, listView );
         *
         * if ( listItem != null ) { // This next line is needed before you call measure or else you
         * won't get measured // height at all. The listitem needs to be drawn first to know the
         * height. listItem.setLayoutParams( new LinearLayout.LayoutParams(
         * LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ) );
         * listItem.measure( desiredWidth, MeasureSpec.UNSPECIFIED ); // totalHeight +=
         * listItem.getMeasuredHeight( );
         *
         * } }
         */


        View listItem = listAdapter.getView(0, null, listView);
        ;
        listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

        totalHeight = totalHeight + (listItem.getMeasuredHeight() * (listAdapter.getCount()));


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static boolean isTabletDevice(Context context) {
        /**
         * Verifies if the GeneralizedGeneralized Size of the device is XLARGE to be considered a Tablet.
         */
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
//        return xlarge;
        return false;
    }

    public static boolean isTabletDeviceforLayout(Context context) {
        /**
         * Verifies if the GeneralizedGeneralized Size of the device is XLARGE to be considered a Tablet.
         */
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);
        return xlarge;
//        return false;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static AlertDialog.Builder showOkInternerDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

        return alertDialog;
    }


    public static boolean isValidPassword(String target) {
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})");
        return target != null && pattern.matcher(target).matches();
    }


    public static AlertDialog.Builder showOkAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

        return alertDialog;
    }


    public static void slideToRight(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from right to left
    public static void slideToLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from top to bottom
    public static void slideToBottom(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public static void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static String stringToDecimal(String string) {
        if (string.length() > 2) {
            return String.format("%.2f", Double.parseDouble(string));
        } else {
            return string;
        }
    }

    public static void clearNotifications(String requestId) {
        try {

            NotificationManager mNotificationManager = (NotificationManager) Application.applicationContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(requestId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
