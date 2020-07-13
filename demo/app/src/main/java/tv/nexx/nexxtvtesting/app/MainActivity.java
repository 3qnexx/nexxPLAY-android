package tv.nexx.nexxtvtesting.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.HashMap;
import java.util.LinkedHashMap;

import tv.nexx.nexxtvtesting.app.adapters.LinkedHashMapAdapter;


public class MainActivity extends Activity {

    EditText customerIDEditText;
    EditText mediaIDEditText;
    EditText streamingFilterEditText;
    Switch autoPlaySwitch;
    Switch staticSwitch;
    Switch revolverPlaySwitch;
    Spinner playModeSpinner;
    Spinner exitPlayModeSpinner;
    Spinner commercialsSpinner;
    Spinner loaderSpinner;
    private HashMap<String, String> commData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        customerIDEditText = (EditText) findViewById(R.id.customerIDInput);
        mediaIDEditText = (EditText) findViewById(R.id.mediaIDInput);
        streamingFilterEditText = (EditText) findViewById(R.id.streamingFilterInput);
        autoPlaySwitch = (Switch) findViewById(R.id.autoplaySwitch);
        revolverPlaySwitch = (Switch) findViewById(R.id.revolverPlaySwitch);
        staticSwitch = (Switch) findViewById(R.id.staticSwitch);
        playModeSpinner = (Spinner) findViewById(R.id.playModeSpinner);
        exitPlayModeSpinner = (Spinner) findViewById(R.id.exitPlayModeSpinner);
        commercialsSpinner = (Spinner) findViewById(R.id.commercialsSpinner);
        loaderSpinner = (Spinner) findViewById(R.id.loaderSpinner);

        ArrayAdapter<CharSequence> playModesAdapter = ArrayAdapter.createFromResource(this,
                R.array.playModes, android.R.layout.simple_spinner_item);
        playModesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playModeSpinner.setAdapter(playModesAdapter);

        ArrayAdapter<CharSequence> exitPlayModesAdapter = ArrayAdapter.createFromResource(this,
                R.array.exitPlayModes, android.R.layout.simple_spinner_item);
        exitPlayModesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exitPlayModeSpinner.setAdapter(exitPlayModesAdapter);


        ArrayAdapter<CharSequence> loaderSkinAdapter = ArrayAdapter.createFromResource(this,
                R.array.loaderSkins, android.R.layout.simple_spinner_item);
        loaderSkinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loaderSpinner.setAdapter(loaderSkinAdapter);

        // commercial spinner
        LinkedHashMap<String, String> commData = new LinkedHashMap<String, String>();

        commData.put("", "None");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-simple.xml", "Simple VAST");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-wrapper.xml", "VAST Wrapper");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-multiasset.xml", "VAST Multiassets");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-skippable.xml", "Skippable");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-fallback.xml", "Fallback");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-invalid-length.xml", "Invalid length");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-invalid-version.xml", "Invalid Version");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-pod.xml", "pod");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-buffet.xml", "buffet");
        commData.put("https://omnia.nexx.cloud/static/vast-samples/vast-buffet-necessary.xml", "buffet next");
        commData.put("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=", "Google Test Ad");
        commData.put("https://omnia-stage.nexxcdn.com/static/helpers/vastcdata.xml", "Test 1");
        commData.put("https://omnia-stage.nexxcdn.com/static/helpers/vast_invalid.xml", "Test 2");
        commData.put("https://omnia-stage.nexxcdn.com/static/helpers/pod.xml", "Test 3");
        commData.put("https://omnia-stage.nexxcdn.com/static/helpers/entertainvast.xml", "Test 4");

        LinkedHashMapAdapter<String, String> adapter = new LinkedHashMapAdapter<String, String>(this, android.R.layout.simple_spinner_item, commData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commercialsSpinner.setAdapter(adapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.onPlayButtonClicked(null);
            }
        }, 2000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * show player view with given settings
     */
    public void onPlayButtonClicked(View v) {
//        NexxPreviewAppWidget.configure(this, "484", PlayerActivity.class, null, null);

        Log.d("start", "Showing Player");

        String customerID = customerIDEditText.getText().toString();
        String mediaID = mediaIDEditText.getText().toString();
        String streamingFilter = streamingFilterEditText.getText().toString();
        String playType = playModeSpinner.getSelectedItem().toString();
        Boolean autoplay = autoPlaySwitch.isChecked();
        Boolean revolverPlay = revolverPlaySwitch.isChecked();
        Boolean staticDataMode = staticSwitch.isChecked();

        RadioGroup radioGroup = findViewById(R.id.adType);
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String adType = (String) radioButton.getText();

        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("customerID", customerID);
        intent.putExtra("mediaID", mediaID);
        intent.putExtra("streamingFilter", streamingFilter);
        intent.putExtra("playMode", playType);
        intent.putExtra("autoplay", autoplay);
        intent.putExtra("adType", adType);
        intent.putExtra("staticDataMode", staticDataMode);
        intent.putExtra("revolverPlay", revolverPlay);
        intent.putExtra("exitPlayMode", exitPlayModeSpinner.getSelectedItemPosition());
        intent.putExtra("loaderSkin", loaderSpinner.getSelectedItem().toString());
        String leString = commercialsSpinner.getSelectedItem().toString();
        leString = leString.substring(0, leString.lastIndexOf("="));
        intent.putExtra("commercial", leString);
        startActivity(intent);

    }

}
