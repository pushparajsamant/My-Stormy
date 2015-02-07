package weather.pushparaj.mystormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;


public class MainActivity extends ActionBarActivity {
    public static String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summary) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        final double latitude = 36.8404;
        final double longitude = 174.7399;
        getLatestWeatherInfo(latitude,longitude);
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestWeatherInfo(latitude,longitude);
            }
        });


    }

    private void getLatestWeatherInfo(double latitude,double longitude) {
        String apiKey = "b7179a56277dc2b38e1f80ff8457256b";

        String url = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;
        if(isNetworkAvailable()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRefreshImageView.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });

            Log.d(TAG,"I am checking network");
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getJSONfromResponseData(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                    mRefreshImageView.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {
                            alertUserAboutError(getString(R.string.dialog_window_title),
                                    getString(R.string.error_dialog_message),
                                    getString(R.string.button_message));
                        }
                    } catch (IOException e) {
                        Log.e(TAG,"Exception Caught: ",e);
                    } catch (JSONException e) {
                        Log.e(TAG,"Exception Caught: ",e);
                    }
                }
            });
        }
        else{
            Log.d(TAG,"No internet connection");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRefreshImageView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });

            Toast.makeText(this,"Network is unavailable",Toast.LENGTH_LONG).show();
            //alertUserAboutError(getString(R.string.dialog_window_title),
            //getString(R.string.error_dialog_message),
            //getString(R.string.button_message));
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mHumidityValue.setText(mCurrentWeather.getHumidity()+"");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance()+"%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime());
        mIconImageView.setImageDrawable(getResources().getDrawable(mCurrentWeather.getIconID()));

    }

    private CurrentWeather getJSONfromResponseData(String jsonData) throws JSONException {
        JSONObject forecast= new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject(getString(R.string.currently_key));

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTime(currently.getLong("time"));

        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setTimezone(forecast.getString("timezone"));
        Log.i(TAG,"Current time" + currentWeather.getFormattedTime());
        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            isConnected = true;
        }
        return isConnected;
    }

    private void alertUserAboutError(String title, String message, String buttonText) {
        AlertDialogFragment alertDialog = new AlertDialogFragment(title,message,buttonText);
        alertDialog.show(getFragmentManager(),getString(R.string.alert_dialog));
    }

}
