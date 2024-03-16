package com.example.examencomplete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.examencomplete.ml.BanderaModel;
import com.example.examencomplete.models.PaisModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;

import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements OnSuccessListener<Text>,
        OnFailureListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {
    public RequestQueue requestQueue;
    GoogleMap mMap;
    public static int REQUEST_CAMERA = 111;
    public static int REQUEST_GALLERY = 222;
    String pais="";
    Bitmap mSelectedImage;
    ImageView mImageView;
    TextView txtResults;
    TextView txtCapital;
    ImageView imgflag;
    TextView txtPaisB;
    PaisModel paisModel;
    TextView txtcodeiso;
    TextView txtprefix;
    TextView txtcodeiso3;
    TextView txtfips;
    TextView txtisonum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.image_view);
        txtResults = findViewById(R.id.txtresults);
        txtCapital=findViewById(R.id.txtCapital);
        imgflag=findViewById(R.id.imgflag);
        txtPaisB=findViewById(R.id.txtPaisB);
        txtcodeiso=findViewById(R.id.txtiso2);
        txtprefix=findViewById(R.id.txtprefix);
        txtcodeiso3=findViewById(R.id.txtciso);
        txtfips=findViewById(R.id.txtfips);
        txtisonum=findViewById(R.id.txtisonum);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void abrirGaleria (View view){
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_GALLERY);
    }
    public void abrirCamera (View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            try {
                if (requestCode == REQUEST_CAMERA)
                    mSelectedImage = (Bitmap) data.getExtras().get("data");
                else
                    mSelectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                mImageView.setImageBitmap(mSelectedImage);
                Banderas();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void onFailure(@NonNull Exception e) {

    }

    @Override
    public void onSuccess(Text text) {
        List<Text.TextBlock> blocks = text.getTextBlocks();
        String resultados="";
        if (blocks.size() == 0) {
            resultados = "No hay Texto";
        }else{
            for (int i = 0; i < blocks.size(); i++) {
                List<Text.Line> lines = blocks.get(i).getLines();
                for (int j = 0; j < lines.size(); j++) {
                    List<Text.Element> elements = lines.get(j).getElements();
                    for (int k = 0; k < elements.size(); k++) {
                        resultados = resultados + elements.get(k).getText() + " ";
                    }
                }
                resultados=resultados + "\n";
            }
            resultados=resultados + "\n";
        }
        txtResults.setText(resultados);
    }

    public ByteBuffer convertirImagenATensorBuffer(Bitmap mSelectedImage){

        Bitmap imagen = Bitmap.createScaledBitmap(mSelectedImage, 224, 224, true);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[224 * 224];
        imagen.getPixels(intValues, 0, imagen.getWidth(), 0, 0, imagen.getWidth(), imagen.getHeight());

        int pixel = 0;

        for(int i = 0; i <  imagen.getHeight(); i ++){
            for(int j = 0; j < imagen.getWidth(); j++){
                int val = intValues[pixel++]; // RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
            }
        }
        return  byteBuffer;
    }
    public void cargarbandera(String alpha2code) {
        String url = "http://www.geognos.com/api/en/countries/flag/" + alpha2code + ".png";
        Picasso.get().load(url).into(imgflag);
    }

    public void obtenerpais(String[] etiCode, float[] probabilidades) {
        float valorMayor = Float.MIN_VALUE;
        int pos = -1;
        for (int i = 0; i < probabilidades.length; i++) {
            if (probabilidades[i] > valorMayor) {
                valorMayor = probabilidades[i];
                pos = i;
            }
        }
        pais=etiCode[pos];
        String url = "http://www.geognos.com/api/en/countries/info/"+pais+".json";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            paisModel = new PaisModel(response);
                            cargarDatospais();
                            cargarUbicacion();
                            cargarbandera(pais);
                        } catch (JSONException e) {
                            txtResults.setText("Error al procesar el JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtResults.setText("Error al obtener el JSON: " + error.getMessage());
                    }
                }
        );
        queue.add(request);
    }

    public void Banderas(){
        try {
            String[] etiquetas = {"AR", "BE","BR", "CO", "CR","EC","ES","FR","GB","MX","PT","SR","UY"};

            BanderaModel model = BanderaModel.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(convertirImagenATensorBuffer(mSelectedImage));

            BanderaModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            obtenerpais(etiquetas, outputFeature0.getFloatArray());

            model.close();
        } catch (Exception e) {
            txtResults.setText(e.getMessage());
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    public void cargarUbicacion(){
        mMap.clear();
        PolylineOptions lineas = new
                PolylineOptions()
                .add(new LatLng(paisModel.getSouth(), paisModel.getEast()))
                .add(new LatLng(paisModel.getNorth(), paisModel.getEast()))
                .add(new LatLng(paisModel.getNorth(), paisModel.getWest()))
                .add(new LatLng(paisModel.getSouth(), paisModel.getWest()))
                .add(new LatLng(paisModel.getSouth(), paisModel.getEast()));
        lineas.width(8);
        lineas.color(Color.BLUE);
        mMap.addPolyline(lineas);
        double[] centro=paisModel.getgeoPt();
        centrarMapa(centro[0],centro[1]);
    }
    public void centrarMapa(double lat, double longi){
        LatLng ubicacion = new LatLng(lat, longi);
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(ubicacion, 3);
        mMap.moveCamera(camUpd1);
    }
    public void cargarDatospais(){
        txtResults.setText("País: " + paisModel.getName());
        txtCapital.setText(paisModel.getNameCapital());
        txtPaisB.setText(paisModel.getName());
        txtcodeiso.setText(paisModel.getIso2());
        txtprefix.setText(paisModel.getPrefix());
        txtcodeiso3.setText(paisModel.getIso3());
        txtfips.setText(paisModel.getFips());
        txtisonum.setText(String.valueOf(paisModel.getIsoN()));
    }
}