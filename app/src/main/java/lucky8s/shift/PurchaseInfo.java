package lucky8s.shift;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import org.json.JSONException;

/**
 * Created by Christian on 6/1/2015.
 */
public class PurchaseInfo extends IntentService {

    public static final String Object = "Purchase";

    public PurchaseInfo() {
        super(PurchaseInfo.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final String option = intent.getStringExtra(Object);
        final String msgResponsePurchase;
        final String msgResponseError;
        try {
            if (option.equals("Purchase")) {
                SendGrid sendgrid = new SendGrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);

                final SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts

                email.addTo(Config.ERRORS_EMAIL);
                email.setFrom(Config.SENDGRID_EMAIL);
                email.setFromName(Config.SENDGRID_NAME);
                email.setSubject(getSharedPreferences("PurchaseInfo", MODE_PRIVATE).getString("subject", "Purchase"));
                email.setText(User.debug);
                email.addCategory("Purchases");

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                msgResponsePurchase = response.getMessage();

                Log.i("SendAppExample", msgResponsePurchase);
            }if(option.equals("Error")) {
                SendGrid sendgrid = new SendGrid(Config.SENDGRID_USERNAME, Config.SENDGRID_PASSWORD);

                final SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts

                email.addTo(Config.ERRORS_EMAIL);
                email.setFrom(Config.SENDGRID_EMAIL);
                email.setFromName(Config.SENDGRID_NAME);
                email.setSubject("Uncaught Exception");
                email.setText(getSharedPreferences("Exception", MODE_PRIVATE).getString("Uncaught", "Error"));
                email.addCategory("Errors");

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                msgResponseError = response.getMessage();

                Log.i("SendAppExample", msgResponseError);
                if(msgResponseError.contains("success")) {
                    getSharedPreferences("Exception", MODE_PRIVATE).edit().putString("Uncaught", "").apply();
                }
            }
            }catch(SendGridException e){
                Log.e("SendAppExample", e.toString());
            }catch(JSONException e){
                Log.e("SendAppExample", e.toString());
            }
        }
}