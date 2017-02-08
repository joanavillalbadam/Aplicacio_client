package com.example.alumnedam.aplicacio_client;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

//import static android.app.Service.START_STICKY;

/**
 * Created by ALUMNEDAM on 31/01/2017.
 */
public class ServicioSegundoPlano extends Service
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener{

    int cod = 0;

    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private GoogleApiClient apiClient;

    private LocationRequest locRequest;


public void onCreate() {

    Toast.makeText(this, "Entro en la clase", Toast.LENGTH_SHORT).show();

        apiClient = new GoogleApiClient.Builder(this)
            .addOnConnectionFailedListener(this)
            .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
            .addApi(LocationServices.API)
            .build();


}

    public int onStartCommand(Intent intenc, int flags, int idArranque) {

        Toast.makeText(this, "Entro en la onStrartCommand", Toast.LENGTH_SHORT).show();

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        Toast.makeText(ServicioSegundoPlano.this, "Config correcta", Toast.LENGTH_SHORT).show();
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
                        break;
                }
            }
        });

    }

    private void startLocationUpdates() {
        if (Service.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, MainActivity.this);
        }
    }

    private void updateUI(Location loc) {

        Toast.makeText(this, "Estoy haciendo cosas", Toast.LENGTH_SHORT).show();
        int fecha = Calendar.getInstance().get(Calendar.DATE);
        String matric = "A00001";
        String x = String.valueOf(loc.getLatitude());
        String y = String.valueOf(loc.getAltitude());
        cod ++;
        //Aqui se conectara a la BD e introducira datos
        SQLiteDatabase db;

        BDAutobus usdbh =
                new BDAutobus(this,"BDAutobus", null, 1);

        db = usdbh.getWritableDatabase();

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("ID_LOC", cod);
        nuevoRegistro.put("LATITUD", x);
        nuevoRegistro.put("LONGITUD", y);
        nuevoRegistro.put("FECHA", fecha);
        nuevoRegistro.put("MATRICULA", matric);
        db.insert("Usuarios", null, nuevoRegistro);


    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }

    @Override

    public void onDestroy() {

        makeText(this,"Servicio detenido",
                LENGTH_SHORT).show();

    }



    public IBinder onBind(Intent intencion) {

        return null;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}


