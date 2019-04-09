package com.example.strengthbutton;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button getLocationBtn;
    Button getMapBtn;
    TextView locationText,providerText,strengthText;
    SimpleDateFormat simpleDateFormat;
    Calendar c ;
    String currentTime;
    LocationManager locationManager;
    String provider;
    int st;
    double lat=0.0,longi=0.0;
    private static final int PERMISSION_ACCESS_COURSE_LOCATION = 0;
    FirebaseDatabase database;
    DatabaseReference myRef;
    //for db
    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;

    Data data_obj;

    private static final String TAG="MainActivity";
    private static final int ERROR_DAILOG_REQUEST=9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServiceOK()) {
            init();
        }

        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        locationText = (TextView)findViewById(R.id.locationText);
        providerText=(TextView)findViewById(R.id.providerText);
        strengthText=(TextView)findViewById(R.id.strengthText);


        Context context=this;
        getProvider(context);
        providerText.setText("provider: " + provider);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("data");

    }

    public void onMapClick(View v){
        Toast.makeText(MainActivity.this,"map was clicked" , Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,MapActivity.class);
        startActivity(intent);
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }*/


   }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        lat=  location.getLatitude();
        longi=location.getLongitude();
        //providerText.setText("provider: " + provider);
        String info="blank";
        info="Current Location: " + location.getLatitude() + ", " + location.getLongitude();
        Toast.makeText(MainActivity.this,"value"+info , Toast.LENGTH_SHORT).show();
        Context context=this;
        getCellSignalStrength(context);
        //String strength=String.valueOf(st)
        strengthText.setText("Current Strength: " + String.valueOf(st));

        Toast.makeText(MainActivity.this,"strength:" +String.valueOf(st) , Toast.LENGTH_SHORT).show();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        c = Calendar.getInstance();
        currentTime = simpleDateFormat.format(c.getTime());
        data_obj= new Data(currentTime, lat, longi, provider, st );
        String id= myRef.push().getKey();
        myRef.child(id).setValue(data_obj);

        //SQLiteDataBaseBuild();

        //SQLiteTableBuild();

        //InsertDataIntoSQLiteDatabase();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void getProvider(Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        provider = manager.getNetworkOperatorName();
    }

    public void getCellSignalStrength(Context context) {
        int strength = 0;
        //Context ct;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COURSE_LOCATION);
        } else {

            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile


            if (cellInfos != null && cellInfos.size() > 0) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        strength = cellSignalStrengthWcdma.getDbm();
                        Toast.makeText(getBaseContext(),"file saved",Toast.LENGTH_SHORT).show();
                        break;
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = cellSignalStrengthGsm.getDbm();
                        break;
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = cellSignalStrengthLte.getDbm();
                        break;
                    }
                }
            }
        }
        //txt.setText("hello");
        st=strength;


    }

    //db methods

    public void SQLiteDataBaseBuild(){

        sqLiteDatabaseObj = openOrCreateDatabase("AndroidJSonDataBase", Context.MODE_PRIVATE, null);

    }

    public void SQLiteTableBuild(){

        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " +
                "AndroidJSonTable(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,latitude REAL,longitude REAL,strength INTEGER,provider VARCHAR);");

    }

    public void InsertDataIntoSQLiteDatabase(){



            SQLiteDataBaseQueryHolder = "INSERT INTO AndroidJSonTable (latitude,longitude,strength,provider) VALUES('"+lat+"', '"+longi+"','"+st+"', '"+provider+"');";

            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

            Toast.makeText(MainActivity.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();



    }
    public void init(){

        Button btmMap=(Button)findViewById(R.id.getMapBtn);
        btmMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);

            }
        });
    }
    public boolean isServiceOK(){
        Log.d(TAG, "isServiceOK: checking google services version");
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available== ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DAILOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "YOu cant make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    protected void buildAlertMessageNoGps() {
        Toast.makeText(this, "here2", Toast.LENGTH_SHORT).show();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}
