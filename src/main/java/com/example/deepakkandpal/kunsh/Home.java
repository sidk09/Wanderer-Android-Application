package com.example.deepakkandpal.kunsh;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Home extends AppCompatActivity implements LocationListener{

    public static final String latitude="com.example.deepakkandpal.kunsh";
     public static final String  longitude="com.exapmle.deepakkandpal.kunsh";
    private TextView mTextMessage;
    ListView view;
    String[] ttd = new String[1000];
    int i=0;
    ArrayList<String> list=new ArrayList<>();
    ArrayList<String> news=new ArrayList<>();
    Intent intent;
    private int PLACE_PICKER_REQUEST=1;
    String city,ocity;
    public String lat,longi;
    LocationManager locationManager;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    String url;
    int count=0;

    Spinner spinner;


    DatabaseReference data;
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:


                    mTextMessage.setVisibility(View.VISIBLE);
                    list.clear();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,list);
                    view.setAdapter(adapter);

                    view.setVisibility(View.INVISIBLE);



                    try {
                        getLocation();
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    spinner.setVisibility(View.INVISIBLE);

                    return true;


                case R.id.navigation_dashboard:


                    spinner.setVisibility(View.VISIBLE);
                    mTextMessage.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.INVISIBLE);

                    list.clear();
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,news);
                    view.setAdapter(adapter1);

                    getfeed();

                    view.setVisibility(View.VISIBLE);


                    return true;








                case R.id.navigation_notifications:



                    mTextMessage.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                    String id = data.push().getKey();
                    User user = new User("","","","");
                    data.child(id).setValue(user);

                    new doit().execute();

                    return true;
                case R.id.navigation_weather:
                    mTextMessage.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                    Intent in=new Intent(Home.this,weather.class);
                   //in.putExtra("latitude", lat);
                   // in.putExtra("longitude", longi);
                    startActivity(in);

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        data = FirebaseDatabase.getInstance().getReference("users");

        mTextMessage = (TextView) findViewById(R.id.message);


        spinner =(Spinner)findViewById(R.id.spinner);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        view =(ListView)findViewById(R.id.lv);


        try {
            getLocation();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (city!=null) {

                        Log.v("inside........", "fghjkmjghfhjkhhcgfdjhjhgcfh2" + ds.getValue());
                        if (ds.getKey().toString().equals(city.split(" ")[1])) {

                            Log.v("inside........", "fghjkmjghfhjkhhcgfdjhjhgcfh" + ds.getValue());
                            url = ds.getValue().toString();

                            ocity = city;
                        }

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public class doit extends AsyncTask<Void,Void,Void> {


        Document doc;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                doc = Jsoup.connect(url).get();
                Elements element = doc.select("div.attraction_element");
                count=i;
                for (Element el : element) {
                    String[] split = el.text().split("[0-9]");
                    String firstSubString = split[0];


                    //Log.v("URL", "" + firstSubString);
                    ttd[i++] = firstSubString;

                }




            }catch (Exception e){
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //tv.setText(doc.toString());


            list.clear();
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(Home.this,android.R.layout.simple_list_item_1,list);
            view.setAdapter(adapter1);

            for (int j=count;j<i;j++) {
                //Log.v("ttd", "" + ttd[j])

                list.add(ttd[j]);
            }




            ArrayAdapter<String> Adapter=new ArrayAdapter<String>(Home.this,android.R.layout.simple_list_item_1, list);


            view.setAdapter(Adapter);



        }
    }

    void getfeed(){

        List<String> c = new ArrayList<String>();
        c.add("Select City Here");
        c.add("Agartala");
        c.add("Agra");
        c.add("Ajmer");
        c.add("Amravati");
        c.add("Ahmedabad");
        c.add("Allahabad");
        c.add("Amritsar");
        c.add("Aurangabad");
        c.add("Bangalore");
        c.add("Bareilly");
        c.add("Bhopal");
        c.add("Bhubaneshwar");
        c.add("Chandigarh");
        c.add("Delhi");
        c.add("Gurgaon");
        c.add("Hyderabad");
        c.add("Mumbai");
        c.add("Noida");
        c.add("Kolkata");
        c.add("Pune");
        c.add("Visakhapatanam");




        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, c);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String text = spinner.getSelectedItem().toString().toLowerCase();
                if(text.equals("Select City Here".toLowerCase())){
                    Toast.makeText(getApplicationContext(),"No Item Selected",Toast.LENGTH_SHORT).show();


                }else{
                    String link = "https://timesofindia.indiatimes.com/city/"+text;


                    Document d;
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        d = Jsoup.connect(link).get();

                        Elements element = d.select("span.w_tle");
                        int six = 0;
                        news.clear();

                        for (Element el : element) {
                            String s = el.text();

                            news.add(s);


                            six++;


                            if(six==15){
                                break;
                            }

                        }

                    }catch (Exception e){

                        e.printStackTrace();
                        Log.v("URL", "............................." + "Exception");
                    }

                    Log.v("URL", "............................." + news);






                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }


    void getLocation() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException e) {
        } catch (GooglePlayServicesRepairableException e) {
        }
    }
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this,data);
                    String toastMsg = String.format("Place: %s", place.getName());
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                    city=toastMsg;
                    Log.v("CITY............", "" + city.split(" ")[1]);
                }
            }
        }



    @Override
    public void onLocationChanged(Location location) {
        mTextMessage.setText("Current Location:\n\n"+"Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
        lat=Double.toString(location.getLatitude());
        longi=Double.toString(location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            mTextMessage.setText(mTextMessage.getText() + "\n"+addresses.get(0).getAddressLine(0));
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Home.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}
