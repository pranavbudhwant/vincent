package com.vincent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.fritz.core.Fritz;
import ai.fritz.fritzvisionstylemodel.ArtisticStyle;
import ai.fritz.fritzvisionstylemodel.FritzStyleResolution;
import ai.fritz.fritzvisionstylemodel.FritzVisionStylePredictor;
import ai.fritz.fritzvisionstylemodel.FritzVisionStylePredictorOptions;
import ai.fritz.fritzvisionstylemodel.FritzVisionStyleTransfer;
import ai.fritz.vision.inputs.FritzVisionImage;
import ai.fritz.vision.inputs.FritzVisionOrientation;
import ai.fritz.vision.predictors.FritzVisionPredictor;

public class MainActivity extends BaseCameraActivity implements ImageReader.OnImageAvailableListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 960);

    private AtomicBoolean computing = new AtomicBoolean(false);

    private FritzVisionImage styledImage;

    private FritzVisionStylePredictor predictor;

    private Size cameraViewSize;

    ImageView hdr;
    boolean hdr_val = false;

    Button style_option;

    ImageView capture;

    /*CameraManager camManager;
    ImageView flash;
    boolean flash_val = false;
*/

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Fritz
        Fritz.configure(this);

        predictor = null;

        hdr = findViewById(R.id.hdr);
        hdr.setVisibility(View.INVISIBLE);
        hdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hdr_val) {
                    hdr_val = false;
                    hdr.setImageResource(R.drawable.hdr_off);
                    FritzVisionStylePredictorOptions options = new FritzVisionStylePredictorOptions.Builder()
                            .imageResolution(FritzStyleResolution.NORMAL)
                            .build();
                    if(predictor!=null)
                        predictor.setOptions(options);
                }
                else{
                    hdr_val = true;
                    hdr.setImageResource(R.drawable.hdr_on);
                    FritzVisionStylePredictorOptions options = new FritzVisionStylePredictorOptions.Builder()
                            .imageResolution(FritzStyleResolution.HIGH)
                            .build();
                    if(predictor!=null)
                        predictor.setOptions(options);
                }
            }
        });

        style_option = findViewById(R.id.style_option);
        style_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu style_menu = new PopupMenu(MainActivity.this, style_option);
                style_menu.getMenuInflater().inflate(R.menu.style_menu, style_menu.getMenu());


                style_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        FritzVisionStylePredictorOptions options;
                        if(hdr_val) {
                             options = new FritzVisionStylePredictorOptions.Builder()
                                    .imageResolution(FritzStyleResolution.NORMAL)
                                    .build();
                        }
                        else{
                            options = new FritzVisionStylePredictorOptions.Builder()
                                    .imageResolution(FritzStyleResolution.HIGH)
                                    .build();
                        }
                        style_option.setText(menuItem.getTitle());
                        switch (menuItem.getTitle().toString()){
                            case "The Scream":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.THE_SCREAM, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "The Poppy Field":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.POPPY_FIELD, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Bicentennial Print":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.BICENTENNIAL_PRINT, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Les Femmes d'Alger":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.FEMMES, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Head of a Clown":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.HEAD_OF_CLOWN, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "The Starry Night":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.STARRY_NIGHT, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Horses":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.HORSES_ON_SEASHORE, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "The Trial":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.THE_TRAIL, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Pink and Blue":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.PINK_BLUE_RHOMBUS, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Ritmo Plastico":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.RITMO_PLASTICO, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "Kaleidoscope":
                                predictor = FritzVisionStyleTransfer.getPredictor(MainActivity.this, ArtisticStyle.KALEIDOSCOPE, options);
                                hdr.setVisibility(View.VISIBLE);
                                break;
                            case "None":
                                predictor = null;
                                hdr.setVisibility(View.INVISIBLE);
                                break;
                        }
                        return true;
                    }
                });

                style_menu.show();
            }
        });

        capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/Vincent/" + style_option.getText()+"/");
                myDir.mkdirs();
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                File file = new File(myDir, fname);
                Log.i(TAG, "" + file);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    styledImage.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(getApplicationContext(),"Image Saved to: " + myDir.getAbsolutePath(),Toast.LENGTH_LONG).show();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*camManager  = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        flash = findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flash_val){
                    flash_val = false;
                    flash.setImageResource(R.drawable.flash_off);
                    try {
                        camManager.setTorchMode(camManager.getCameraIdList()[0], false);
                    }
                    catch (Exception e){

                    }
                }
                else{
                    flash_val = true;
                    flash.setImageResource(R.drawable.flash_on);
                    try {
                        camManager.setTorchMode(camManager.getCameraIdList()[0], true);
                    }
                    catch (Exception e){

                    }
                }
            }
        });*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_stylize;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onPreviewSizeChosen(final Size previewSize, final Size cameraViewSize, final int rotation) {

        this.cameraViewSize = cameraViewSize;

        // Callback draws a canvas on the OverlayView
        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        if (styledImage != null) {
                            styledImage.drawOnCanvas(canvas);
                        }
                    }
                });
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        final Image image = reader.acquireLatestImage();

        if (image == null) {
            return;
        }

        if (!computing.compareAndSet(false, true)) {
            image.close();
            return;
        }

        int rotationFromCamera = FritzVisionOrientation.getImageRotationFromCamera(this, cameraId);
        final FritzVisionImage fritzImage = FritzVisionImage.fromMediaImage(image, rotationFromCamera);

        image.close();

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(predictor != null){
                                final long startTime = SystemClock.uptimeMillis();
                                styledImage = predictor.predict(fritzImage);
                                Log.d(TAG, "INFERENCE TIME:" + (SystemClock.uptimeMillis() - startTime));

                            }
                            else
                                styledImage = fritzImage;

                            styledImage.scale(cameraViewSize.getWidth(), cameraViewSize.getHeight());
                            // Fire callback to change the OverlayView
                            requestRender();
                            computing.set(false);
                        }
                        catch(java.nio.BufferOverflowException e){
                            this.run();
                        }
                    }
                });
    }
}