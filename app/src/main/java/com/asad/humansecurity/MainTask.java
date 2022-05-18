package com.asad.humansecurity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainTask extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    AutoCompleteTextView searchView;
    ImageButton dangerbtn, dangerbtn2;
    String phonenumber0, phonenumber1;
    TextView safetv;
    boolean isaBoolean = false;
    ArrayList<String> bloodprofile;
    String value2;
    int i = 0;
    boolean chechk2;
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    public static final String mylocation = "I am in emergency situation,current location is";
    ArrayList arrayList;
    boolean aBoolean = false;
    FirebaseAuth firebaseAuth;
    LatLon latLon;
    MydatabaseHelper mydatabaseHelper;
    sqlitedatabase sqlitedatabase1;
    Sensor sensor;
    LocationManager locationManager;
    LocationListener locationListener;
    SensorManager sensorManager;
    TriggerEventListener triggerEventListener;
    TextView textView;
    ArrayList<Object> elist;
    private float acevalue;
    private float acelast;
    private float shake;
    List list;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_task);
        phonenumber0="0161327532";
        phonenumber1="0161327532";
        startService(new Intent(this, MyService.class));
        startForegroundService(new Intent(this, MyService.class));
        mydatabaseHelper = new MydatabaseHelper(this);
        sqlitedatabase1 = new sqlitedatabase(this);
        latLon = new LatLon();
//        getemergencydata();
        Cursor ecursor = mydatabaseHelper.show();
        if (ecursor.getCount() == 0) {
            Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
        } else {
            elist = new ArrayList<>();
            while (ecursor.moveToNext()) {
                elist.add(ecursor.getString(1));
            }
        }
        list = new ArrayList();
        listmake();
        arrayList = new ArrayList();
//        getSupportActionBar().hide();
//        phonenumber1= (String) elist.get(1);
//        phonenumber0= (String) elist.get(0);
//        Toast.makeText(this, phonenumber1, Toast.LENGTH_SHORT).show();
        searchView = findViewById(R.id.search);
        dangerbtn = findViewById(R.id.danger);
        dangerbtn2 = findViewById(R.id.danger2);
        safetv = findViewById(R.id.safe);
        firebaseAuth = FirebaseAuth.getInstance();
        getlocation();
        check(firebaseAuth.getCurrentUser().getUid());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        searchView.setAdapter(adapter);
        searchView.setThreshold(1);

    }

    private void listmake() {
        Cursor getCursor = mydatabaseHelper.show();
        if (getCursor.getCount() == 0) {
            Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
        } else {
            list = new ArrayList<>();
            while (getCursor.moveToNext()) {
                list.add(getCursor.getString(1));
            }
        }

        list.add("Thana");
        list.add("Location");
        list.add("Police");
        list.add("Rab");
        list.add("Map");
        list.add("Hospital");
        list.add("Help");
        list.add("ATM");
        list.add("Bank");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getemergencydata() {
        getlocation();
        String uid = firebaseAuth.getUid().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    Profile emergencyContact = dataSnapshot.getValue(Profile.class);
                    phonenumber0 = emergencyContact.getPriority1();
                    JavaMailAPI javaMailAPI = new JavaMailAPI(MainTask.this, emergencyContact.getEmail(), "I am In Danger", mylocation);
                    javaMailAPI.execute();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
//                dialContactPhone(emergencyContact.getNumber1());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getlocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                latLon = new LatLon(lat, lon);
                String uid = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Profile profile = dataSnapshot.getValue(Profile.class);
                        String phone = profile.getEmail().substring(0, 10);
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(phone);
                        databaseReference1.setValue(latLon).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
                                    databaseReference2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Profile emergencyContact = dataSnapshot.getValue(Profile.class);
                                                JavaMailAPI javaMailAPI = new JavaMailAPI(MainTask.this, emergencyContact.getPriority3(), "I am " + profile.getName(), mylocation + " https://www.google.com/maps/place/" + latLon.getLat() + "," + latLon.getLon());
                                                javaMailAPI.execute();
                                                if (aBoolean == false) {
                                                    sendSMSMessage(emergencyContact.getPriority2(), "I am " + profile.getName() + "\n" + mylocation + " https://www.google.com/maps/place/" + latLon.getLat() + "," + latLon.getLon());
//                                                    dialContactPhone(emergencyContact.getNumber1());
                                                    vibrateapp();
                                                    aBoolean = true;
                                                }
//                                                do{
////                                                    dialContactPhone(phonenumber0);
////                                                    sendSMSMessage(phonenumber1, mylocation+"https://www.google.com/maps/place/"+latLon.getLat()+","+latLon.getLon());
//                                                    i++;
//                                                }while (i<2);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
//                                    getemergencydata2(latLon);

                                    Toast.makeText(MainTask.this, "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainTask.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(MainTask.this, latLon.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(new String[]{

                    Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 10);
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                configButton();
            }
        }
    }

    private void sendSMSMessage(String number2, String s) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number2, null, s, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void dialContactPhone(String number1) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number1));
        if (ActivityCompat.checkSelfPermission(MainTask.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
        startService(new Intent(this, MyService.class));
        startForegroundService(new Intent(new Intent(this, MyService.class)));
    }

    private void check(String uid) {
        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(uid);
        databaseReference1.child("mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String a = dataSnapshot.getValue(String.class);
                    if (a.equals("1")) {
                        safetv.setText("Danger Mode Active");
                        safetv.setBackgroundColor(Color.RED);
                        chechk2 = true;
//                   dotask();
//                   layout.setBackgroundColor(Color.RED);
//                   dangerbtn.setBackgroundColor(Color.GREEN);
                        sensoractivity(chechk2);
                    } else {
                        chechk2 = false;
//                   dangerbtn.setBackgroundColor(Color.RED);
//                   layout.setBackgroundColor(Color.GREEN);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void Search(View view) {
        String value = searchView.getText().toString();
        Cursor cursor = mydatabaseHelper.search(value);
        if (cursor.getCount() == 0) {
            mydatabaseHelper.insert(value);
        }
        searchWeb(value);

    }

    public void showMap(String geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(geoLocation));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
                signout();
                return true;
            case R.id.editprofile:
                profieedit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void profieedit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.profile, null);
        builder.setView(view1).show();
    }

//    private void emergencyedit() {
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        View view1=getLayoutInflater().inflate(R.layout.emergencycontact, null);
//        builder.setView(view1).show();
//    }

    private void signout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(MainTask.this, MainActivity.class));
        finish();
    }

    public void Police(View view) {
        searchWeb("Police Station");
    }

    public void RAB(View view) {
        searchWeb("RAB");
    }

    public void Hospital(View view) {
        searchWeb("Hospital");
    }

    public void ATM(View view) {
        searchWeb("ATM");
    }

//    public void Message(View view) {
//        startActivity(new Intent(MainTask.this,Message.class));
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Danger(View view) {
        String uid = firebaseAuth.getUid();
        dangerbtn.setEnabled(false);
        DatabaseReference check = FirebaseDatabase.getInstance().getReference(uid).child("mode");
        String c = safetv.getText().toString();
        if (c.contains("Danger Mode Active")) {
            dangerbtn.setEnabled(false);
            chechk2 = false;
            check.setValue("0");
            stopService(new Intent(MainTask.this, MyService.class));
            safetv.setText("Safe Mode Active");
            safetv.setBackgroundColor(Color.GREEN);
//            dangerbtn.setBackgroundColor(Color.GREEN);
        } else {
            startService(new Intent(MainTask.this, MyService.class));
            startForegroundService(new Intent(MainTask.this, MyService.class));
            check.setValue("1");
            safetv.setText("Danger Mode Active");
            safetv.setBackgroundColor(Color.RED);
            dangerbtn2.setEnabled(true);
//            dangerbtn.setBackgroundColor(Color.GREEN);
            chechk2 = true;
            sensoractivity(chechk2);
        }


    }

    public void sensoractivity(Boolean a) {
        if (a == true) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(sensorlistener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            acevalue = SensorManager.GRAVITY_EARTH;
            acelast = SensorManager.GRAVITY_EARTH;
            shake = 0.00f;
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CALL_LOG)) {
                    ActivityCompat.requestPermissions(MainTask.this,
                            new String[]{
                                    Manifest.permission.READ_CALL_LOG
                            }, 1);
                } else {
                    ActivityCompat.requestPermissions(MainTask.this,
                            new String[]{
                                    Manifest.permission.READ_CALL_LOG
                            }, 1);

                }
            }

        }

    }

    private final SensorEventListener sensorlistener = new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            acelast = acevalue;
            acevalue = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = acevalue - acelast;
            shake = shake * 0.9f + delta;
            if (shake > 11 && chechk2 == true) {
                boolean state = isNetworkAvailable();
                if (state == true) {
                    getlocation();
                    if (phonenumber0.isEmpty() && phonenumber1.isEmpty()) {
                        Toast.makeText(MainTask.this, "Please Edit Your Database", Toast.LENGTH_SHORT).show();
                    } else {
                        sendSMSMessage(phonenumber1, "I am in Danger!");
                        dialContactPhone(phonenumber0);
                    }

                    DatabaseReference dialmsg = FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
                    dialmsg.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Profile emergencyContact = dataSnapshot.getValue(Profile.class);
                            sendSMSMessage(emergencyContact.getPriority2(), mylocation + " https://www.google.com/maps/place/" + latLon.getLat() + "," + latLon.getLon());
                            dialContactPhone(emergencyContact.getPriority1());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                    getemergencydata();

                } else {
                    sendSMSMessage(phonenumber1, "I am in Danger!");
                    dialContactPhone(phonenumber0);
//                    startActivity(new Intent(SplashScreen.this,Messege.class));
                    Toast.makeText(MainTask.this, "net nai", Toast.LENGTH_SHORT).show();
                }

                startService(new Intent(MainTask.this, MyService.class));
                startForegroundService(new Intent(new Intent(MainTask.this, MyService.class)));

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configButton() {

//        startService(new Intent(MainTask.this,MyService.class));
        //Toast.makeText(Share.this,reference, Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(MainTask.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainTask.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates("gps",
                10000, 0, locationListener);
        //locationManager2.requestLocationUpdates("gps",
        //10000, 0, locationListener);
    }

    private long insertmethod(String id) {
        long rowid = mydatabaseHelper.insert(id);
        if (rowid > 0) {
        } else {
            Toast.makeText(MainTask.this, "Failed", Toast.LENGTH_SHORT).show();
        }
        return rowid;
    }

    public void showresult(String title, String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.setCancelable(true);
        builder.show();
    }

    public StringBuffer show() {
        Cursor cursor = mydatabaseHelper.show();
        if (cursor.getCount() == 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (cursor.moveToNext()) {
            stringBuffer.append("ID: " + cursor.getString(0) + "\n");
            stringBuffer.append("UserCode: " + cursor.getString(1) + "\n");
        }
        return stringBuffer;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Dial(View view) {
        dialContactPhone("999");
//        getlocation();
//        mediarecord();
    }


    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainTask.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


//    public void Record(View view) {
////        ProgressDialog progressDialog=new ProgressDialog(this);
////        progressDialog.setMessage("Recording....");
//        if(isaBoolean==false){
//            mediarecord();
//            isaBoolean=true;
//        }else {
//            mRecorder.stop();
//            mRecorder.release();
//            mRecorder = null;
//            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
//        }
////        progressDialog.show();
//    }

//    public void capturePhoto() {
//        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == VIDEO_CAPTURE) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Video saved to:\n" +
//                        data.getData(), Toast.LENGTH_LONG).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Video recording cancelled.",
//                        Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Failed to record video",
//                        Toast.LENGTH_LONG).show();
//            }
//        }
//        switch (requestCode) {
//            case 10:
//                if (resultCode == RESULT_OK && data != null) {
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    if (result.get(0).equals("come")) {
//                        Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
//                        sensoractivity(chechk2);
//                    } else {
//                        dotask();
//                    }
//                }
//                break;
//        }
//    }

//    public void Camera(View view) {
//        capturePhoto();
//    }

//    public void VIdeo(View view) {
//        capturePhoto();
//    }

    //    public void Blood(View view) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        View view1=getLayoutInflater().inflate(R.layout.bloodfinder, null);
//        final AutoCompleteTextView district;
//        final Spinner spinner;
//        Button button;
//        final ListView listView=view1.findViewById(R.id.listfind);
//        button=view1.findViewById(R.id.bloodfindbtn);
//        ImageButton imageButton=view1.findViewById(R.id.bloodbank);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchWeb("Blood Bank");
//            }
//        });
//        district=view1.findViewById(R.id.bloodauto);
//        spinner=view1.findViewById(R.id.bloodfinderspin);
//        String[] d = getResources().getStringArray(R.array.district);
//        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,d);
//        district.setAdapter(adapter);
//        district.setThreshold(0);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String b,d;
//                b=spinner.getSelectedItem().toString();
//                d=district.getText().toString();
//                String value=b+d;
//                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(value);
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        bloodprofile.clear();
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                                Profile profile=dataSnapshot1.getValue(Profile.class);
//                                bloodprofile.add(profile.getBlood()+"\n"+profile.getName()+"\n"+profile.getMobilenumber()+"\n"+profile.getSubdistrict());
//                            }
//                            ArrayAdapter arrayAdapter=new ArrayAdapter(MainTask.this,android.R.layout.simple_list_item_1,bloodprofile);
//                            listView.setAdapter(arrayAdapter);
//                        }
//                        else {
//                            Toast.makeText(MainTask.this, "Null", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//        builder.setView(view1).show();
//    }
    public void vibrateapp() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(1000);
        }
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(MainTask.this, MyService.class));
        System.exit(0);
//        startActivity(new Intent(MapTask.this,MainTaskForOthers.class));
//        finish();
        super.onBackPressed();
    }

    private void dotask() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    public void Safe(View view) {
        String uid = firebaseAuth.getUid();
        DatabaseReference check = FirebaseDatabase.getInstance().getReference(uid).child("mode");
        chechk2 = false;
        check.setValue("0");
        stopService(new Intent(MainTask.this, MyService.class));
        safetv.setText("Safe Mode Active");
        safetv.setBackgroundColor(Color.GREEN);
        dangerbtn2.setEnabled(false);
        dangerbtn.setEnabled(true);
    }

    public void messengercall() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Use and Rate this app");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "I am in Danger,Please Help me to protect." + "\n" + "to find me,please go to Ally app and search my number.");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");
        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainTask.this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
        }
    }

    public void Messenger(View view) {
        messengercall();
    }

    public void GoogleSearch(View view) {
        dotask();
    }
}
