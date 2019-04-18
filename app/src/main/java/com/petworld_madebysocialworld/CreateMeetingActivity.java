package com.petworld_madebysocialworld;

import Models.User;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<Uri> urlImages;

    //strings for time and date for firestore
    String fechaFormateada = null;
    String tiempoFormateado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();

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
        if (requestCode == 1001) {
            Uri selectedImage = data.getData();
            uriImages.add(selectedImage);
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            if (bitmap != null) {
                ImageView img = (ImageView)findViewById(R.id.foto1);
                img.setImageBitmap(bitmap);
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
                //guardar todo lo que se ha hecho

                //TODO
                //(EditText) findViewById(R.id.title_create_meeting).getText()) == null ||
                if (((EditText) findViewById(R.id.des)).getText().toString().equals("")){
                    Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
                }
                else if (tiempoFormateado == null || fechaFormateada == null){
                    Toast.makeText(this, "Por favor, elija una fecha y hora correctas", Toast.LENGTH_SHORT).show();
                }
                else {
                    //ojo, hay que guardar todo en firestore
                    Map<String, Object> meeting = new HashMap<>();

                    meeting.put("creator", User.getInstance().getAccount().getId());
                    meeting.put("description", ((EditText)findViewById(R.id.des)).getText().toString());
                    meeting.put("images", Arrays.asList());
                    meeting.put("name", ((EditText)findViewById(R.id.title_create_meeting)).getText().toString());
                    LatLng loc = (LatLng) getIntent().getParcelableExtra("location");
                    meeting.put("placeLocation", new GeoPoint(loc.latitude, loc.longitude));
                    meeting.put("placeName", "");
                    meeting.put("start", Timestamp.valueOf(fechaFormateada + " " + tiempoFormateado));
                    meeting.put("visibility", "public");



                    FirebaseFirestore.getInstance().collection("meetings").add(meeting).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            final AtomicBoolean done = new AtomicBoolean(false);
                            //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
                            final DocumentReference docRAux = documentReference;
                            // do something with result.
                            for (int i = 0; i < uriImages.size(); i++) {
                                final int j = i;
                                final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("meetings/" + documentReference.getId() + "_" + i);
                                Uri file = uriImages.get(i);
                                UploadTask uploadTask = imagesRef.putFile(file);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imagesRef.child("meetings/" + documentReference.getId() + "_" + j).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                urlImages.add(uri);
                                                if (j == uriImages.size()-1)
                                                    done.set(true);
                                            }
                                        });
                                    }
                                });
                            }
                            synchronized (done) {
                                done.notifyAll(); // notify the main thread which is waiting
                            }
                            synchronized (done) {
                                while (done.get() == false) {
                                    try {
                                        done.wait(); // wait here until the listener fires
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            documentReference.update("images", urlImages);
                            for (Uri i: urlImages)
                                Log.d("URL", i.toString());
                            //guardar link ususario a meeting
                            FirebaseFirestore.getInstance().collection("users").document(User.getInstance().getDocSnap().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            ArrayList<DocumentReference> meetings = (ArrayList) document.get("meetings");
                                            meetings.add(docRAux);
                                            FirebaseFirestore.getInstance().collection("users").document(User.getInstance().getDocSnap().getId()).update("meetings", meetings);
                                        } else {
                                            Log.d("ERROR", "No such document");
                                        }
                                    } else {
                                        Log.d("ERROR", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                        });
                        //ir al mapa
                        startActivity(intent);
                         }
                break;
            case R.id.load_image:
                Intent in = new Intent();
                in.setType("image/*");
                in.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(in, "Select Picture"),
                        1001);
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
                fechaFormateada = year + "-" + mesFormateado  + "-" + diaFormateado;
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
                tiempoFormateado = horaFormateada + ":" + minutoFormateado + ":00";
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }
}
