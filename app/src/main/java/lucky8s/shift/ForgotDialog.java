package lucky8s.shift;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Christian on 4/5/2015.
 */
public class ForgotDialog extends DialogFragment implements View.OnClickListener,DialogListener{

    SQL sql;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    DialogListener listener;
    ProgressDialog progress;
    AlertDialog alertDialog;

    Button cancel;
    Button change;
    Button sendCode;


    EditText code;
    EditText newPass;
    EditText confirmPass;
    EditText newHint;

    TextView codeError;
    TextView passwordError;
    TextView confirmError;
    TextView hintError;

    String username = "";
    String email = "";
    String generatedCode = "";

    @SuppressLint("ValidFragment")
    public ForgotDialog(DialogListener activityDialogListener) {
        this.listener = activityDialogListener;
    }
    public ForgotDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_prompt, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        sql = new SQL(getDialog().getContext());

        User.add("In ForgotDialog");

        cancel = (Button) view.findViewById(R.id.cancel);
        change = (Button) view.findViewById(R.id.change);
        sendCode = (Button) view.findViewById(R.id.send_code);

        codeError = (TextView) view.findViewById(R.id.code_error);
        passwordError = (TextView) view.findViewById(R.id.password_error);
        confirmError = (TextView) view.findViewById(R.id.confirm_password_error);
        hintError = (TextView) view.findViewById(R.id.hint_error);

        code = (EditText) view.findViewById(R.id.code);
        newPass = (EditText) view.findViewById(R.id.new_password);
        newPass.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        confirmPass = (EditText) view.findViewById(R.id.confirm_new_password);
        confirmPass.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        newHint = (EditText) view.findViewById(R.id.new_hint);

        codeError.setVisibility(View.INVISIBLE);
        passwordError.setVisibility(View.INVISIBLE);
        confirmError.setVisibility(View.INVISIBLE);
        hintError.setVisibility(View.INVISIBLE);


        cm = (ConnectivityManager) getDialog().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        cancel.setOnClickListener(this);
        change.setOnClickListener(this);
        sendCode.setOnClickListener(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialog().getContext());
        alertDialogBuilder.setCancelable(false)
                .setMessage(getResources().getString(R.string.forgot_message))
                .setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();

        return view;
    }
    public void onCloseDialog() {}
    public void showCoinsStore() {}
    public void onResume(){
        super.onResume();
        Context context = getActivity().getBaseContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        username = getDialog().getOwnerActivity().getSharedPreferences("FORGOT", Context.MODE_PRIVATE).getString("username", "");
        email = getDialog().getOwnerActivity().getSharedPreferences("FORGOT", Context.MODE_PRIVATE).getString("email", "");

        int height = size.y;
        int width = size.x;
        getDialog().getWindow().setLayout((width / 10) * 9, (height / 10) * 8);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    public void buttonClick(View button) {
        final View view = button;
        new Thread() {
            @Override
            public void run() {
                if(getDialog() != null && getDialog().getOwnerActivity() != null) {

                    getDialog().getOwnerActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getActivity() != null) {
                                view.getBackground().setColorFilter(getResources().getColor(R.color.gold_tint), PorterDuff.Mode.SRC_ATOP);
                            }
                        }
                    });
                }
            }
        }.start();
    }
    @Override
    public void onClick(View v){
        if(v instanceof Button){
            buttonClick(v);
        }
        switch (v.getId()) {
            case R.id.cancel:
                getDialog().dismiss();
                break;
            case R.id.change:
                changePassword();
                break;
            case R.id.send_code:
                sendResetCode();
                alertDialog.show();
                if(alertDialog.isShowing()) {
                    int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = alertDialog.findViewById(dividerId);
                    divider.setBackgroundColor(getResources().getColor(R.color.gold));
                }
                sendCode.setText(getString(R.string.send_again));
                break;
            default:
                break;
        }
        v.getBackground().clearColorFilter();
    }
    public void changePassword(){
        User.add("Changing Password...");
        passwordError.setVisibility(View.INVISIBLE);
        confirmError.setVisibility(View.INVISIBLE);
        hintError.setVisibility(View.INVISIBLE);
        codeError.setVisibility(View.INVISIBLE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            User.add("No Internet");
            Toast.makeText(getContext(), getResources().getString(R.string.enable_internet), Toast.LENGTH_SHORT).show();
        }else {
            User.add("Going to verify code");
            progress = new ProgressDialog(getDialog().getOwnerActivity(), R.style.MyTheme);
            progress.setIndeterminate(true);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(false);
            progress.show();
            new Thread() {
                @Override
                public void run() {
                    verify();
                }
            }.start();
        }
    }
    public void verify(){
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("user");
        userQuery.whereEqualTo("username", username);
        userQuery.orderByDescending("updatedAt");
        ParseObject user = null;
        try {
            user = userQuery.getFirst();
        } catch (ParseException e) {
            User.add(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        if (user == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getDialog().getContext(), getResources().getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                }
            });
            User.add("user == null");
        } else {
            if (newPass.getText().toString().equals("")) {
                showError(passwordError, getString(R.string.password_blank));
            }
            if (newHint.getText().toString().equals("")) {
                showError(hintError, getString(R.string.hint_blank));
            }
            if (code.getText().toString().equals("")) {
                showError(codeError, getString(R.string.code_blank));
            }else if (!code.getText().toString().equals(generatedCode)) {
                showError(codeError, getString(R.string.code_not_match));
            }else if (!newPass.getText().toString().trim().equals(confirmPass.getText().toString().trim())) {
                showError(confirmError, getString(R.string.password_confirmation_not_match));
            } else if (newPass.getText().toString().length() < 8) {
                showError(passwordError, getString(R.string.new_password_length));
            } else if (newPass.getText().toString().contains(newHint.getText().toString())) {
                showError(hintError, getString(R.string.hint_cannot_contain));
            } else {
                user.put("password", newPass.getText().toString().trim());
                user.put("hint", newHint.getText().toString().trim());
                try {
                    user.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), getResources().getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                });
                User.add("Password changed.");
            }
        }
        progress.dismiss();
    }
    public void showError(TextView field, String message){
        final TextView finalField = field;
        final String finalMessage = message;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finalField.setVisibility(View.VISIBLE);
                finalField.setText(finalMessage);
            }
        });
    }
    public void sendResetCode(){
            generatedCode = generateCode();
            getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).edit().putString("recipient", email).apply();
            getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).edit().putString("subject", getString(R.string.password_reset_code)).apply();
            getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).edit().putString("code", generatedCode).apply();
            getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).edit().putString("user", username).apply();
            SendEmailASyncTask task = new SendEmailASyncTask(getDialog().getOwnerActivity().getApplicationContext());
            task.execute();
    }
    public String generateCode(){
        User.add("Generating password reset code.");
        String genCode = "";
        Random r = new Random();
        int r1 = r.nextInt(52);
        int r2 = r.nextInt(10);
        int r3 = r.nextInt(52);
        int r4 = r.nextInt(10);
        int r5 = r.nextInt(52);
        int r6 = r.nextInt(10);
        int r7 = r.nextInt(52);
        int r8 = r.nextInt(10);

        genCode = mapInt(r1)+
                Integer.toString(r2)+
                mapInt(r3)+
                Integer.toString(r4)+
                mapInt(r5)+
                Integer.toString(r6)+
                mapInt(r7)+
                Integer.toString(r8);

        return genCode;
    }
    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }
    public String mapInt(int rand){
        String result = "";
        switch (rand){
            case 0:
                return "a";
            case 1:
                return "A";
            case 2:
                return "b";
            case 3:
                return "B";
            case 4:
                return "c";
            case 5:
                return "C";
            case 6:
                return "d";
            case 7:
                return "D";
            case 8:
                return "e";
            case 9:
                return "E";
            case 10:
                return "f";
            case 11:
                return "F";
            case 12:
                return "g";
            case 13:
                return "G";
            case 14:
                return "h";
            case 15:
                return "H";
            case 16:
                return "i";
            case 17:
                return "I";
            case 18:
                return "j";
            case 19:
                return "J";
            case 20:
                return "k";
            case 21:
                return "K";
            case 22:
                return "l";
            case 23:
                return "L";
            case 24:
                return "m";
            case 25:
                return "M";
            case 26:
                return "n";
            case 27:
                return "N";
            case 28:
                return "o";
            case 29:
                return "O";
            case 30:
                return "p";
            case 31:
                return "P";
            case 32:
                return "q";
            case 33:
                return "Q";
            case 34:
                return "r";
            case 35:
                return "R";
            case 36:
                return "s";
            case 37:
                return "S";
            case 38:
                return "t";
            case 39:
                return "T";
            case 40:
                return "u";
            case 41:
                return "U";
            case 42:
                return "v";
            case 43:
                return "V";
            case 44:
                return "w";
            case 45:
                return "W";
            case 46:
                return "x";
            case 47:
                return "X";
            case 48:
                return "y";
            case 49:
                return "Y";
            case 50:
                return "z";
            case 51:
                return "Z";
        }
        if(result.equals("")){
            result="q";
        }
        return result;
    }
    private class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context context;
        private String msgResponse;

        private SendEmailASyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);

                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts

                email.addTo(getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).getString("recipient", "noone@noone.com"));
                email.setFrom(Config.SENDGRID_EMAIL);
                email.setFromName(Config.SENDGRID_NAME);
                email.setHtml(getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).getString("code", ""));
                email.setSubject(getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).getString("subject", "Shift"));
                email.setTemplateId(Config.RESET_PASSWORD);
                //email.setText(getSharedPreferences("email", MODE_PRIVATE).getString("code", ""));
                email.replaceTag(":user", getDialog().getOwnerActivity().getSharedPreferences("email", Context.MODE_PRIVATE).getString("user", ""));
                email.replaceTag(":tag1", getString(R.string.forgot_your_password));
                email.replaceTag(":tag2", getString(R.string.no_problem));
                email.replaceTag(":tag3", getString(R.string.user_semi));
                email.replaceTag(":tag4", getString(R.string.code_semi));
                email.addCategory("Password");

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                msgResponse = response.getMessage();

                Log.i("SendAppExample", msgResponse);

            } catch (SendGridException e) {
                Log.e("SendAppExample", e.toString());
            } catch (JSONException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }
    }
    public void onBackPressed(){
        this.dismiss();
    }
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
    public void onDestroy(){
        super.onDestroy();

        System.gc();

    }
}
