package com.petworld_madebysocialworld;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.FileNotFoundException;
import java.util.Calendar;

public class CreateMeetingActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    public static final int PICK_IMAGE = 1;
    boolean isCreating = true, toLocationPicker = false;
    private static final String CERO = "0";
    private static final String BARRA = "/";
    private static final String DOS_PUNTOS = ":";

    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    //Widgets
    EditText etFecha;
    ImageButton ibObtenerFecha;
    EditText etHora;
    ImageButton ibObtenerHora;

    //map
    private final static int FRAGMENT_ID = 0x101;
    private GoogleMap mMap;
    private MapView mapV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        //Widget EditText donde se mostrara la fecha obtenida
        etFecha = (EditText) findViewById(R.id.et_mostrar_fecha_picker);
        //Widget ImageButton del cual usaremos el evento clic para obtener la fecha
        ibObtenerFecha = (ImageButton) findViewById(R.id.ib_obtener_fecha);
        //Evento setOnClickListener - clic
        ibObtenerFecha.setOnClickListener(this);
        //Widget EditText donde se mostrara la hora obtenida
        etHora = (EditText) findViewById(R.id.et_mostrar_hora_picker);
        //Widget ImageButton del cual usaremos el evento clic para obtener la hora
        ibObtenerHora = (ImageButton) findViewById(R.id.ib_obtener_hora);
        //Evento setOnClickListener - clic
        ibObtenerHora.setOnClickListener(this);

        MapsInitializer.initialize(this);

        mapV = (MapView) findViewById(R.id.mapCreateMeeting);
        setUpMapIfNeeded();

        /*
        location = (LatLng) getIntent().getParcelableExtra("location");
        TextView tvLat = (TextView)findViewById(R.id.coordLat);
        tvLat.setText("Lat: " + location.latitude);
        TextView tvLong = (TextView)findViewById(R.id.coordLong);
        tvLong.setText("Long: " + location.longitude);
        */
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                TextView tvNoInternet = new TextView(this);
                tvNoInternet.setGravity(Gravity.CENTER_HORIZONTAL);
                tvNoInternet.setText(getString(R.string.no_net_info));
                ((MapView) findViewById(R.id.map)).addView(tvNoInternet);
            }

            mapV = ((MapView) findViewById(R.id.mapCreateMeeting));
            mapV.getMapAsync(this);
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
            }
        });

        //Click Llarg

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng point) {
                //detectar nuevo sitio de ubicación automáticamente
            }
        });
    }

    private void setUpMap() {

        double lat = Double.parseDouble("0");
        double lon = Double.parseDouble("0");
        LatLng coords = new LatLng(lon, lat);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 10));
        mMap.addMarker(new MarkerOptions().position(coords));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Uri selectedImage = data.getData();
            Toast.makeText(this, "Imatge Pillada + URI: " + selectedImage, Toast.LENGTH_SHORT).show();
            try {
                Toast.makeText(this, "Dins Try", Toast.LENGTH_SHORT).show();
                Bitmap  bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
                ImageView imgView = (ImageView)findViewById(R.id.foto1);
                imgView.setImageDrawable(bitmapDrawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CreateMeetingActivity.this, MapActivity.class);
        switch (v.getId()){
            case R.id.ib_obtener_fecha:
                obtenerFecha();
                break;
            case R.id.ib_obtener_hora:
                obtenerHora();
                break;
            case R.id.X_create_meeting:
                startActivity(intent);
                break;
            case R.id.g_create_meeting:
                //guardar todo lo que se ha hecho en firebase e ir al mapa

                //ir al mapa
                startActivity(intent);
                break;
            case R.id.load_image:
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                startActivityForResult(getIntent, PICK_IMAGE);
                break;
        }
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                etFecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                //Muestro la hora con el formato deseado
                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }
}
