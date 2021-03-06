package com.KLK.photogallery.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.ImageEncoderDecoder;
import com.KLK.photogallery.helper.ServerRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class NextActivity extends AppCompatActivity {
    // For debugging
    private static final int STYLE_IMAGE_REQUEST = 123;
    private static final String TAG = "NextActivity";
    RadioRealButton button1, button2, button3;
    RadioRealButtonGroup groupButtons;
    ImageView imageNext, savedChanged;
    private final String mAppend = "file://";
    private int buttonID = 0;
    ServerRequest server;
    String chosenStyleID = "0";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Activity activity = this;
        groupButtons = (RadioRealButtonGroup)findViewById(R.id.groupButtons);
        button1 = (RadioRealButton)findViewById(R.id.button1);
        button2 = (RadioRealButton)findViewById(R.id.button2);
        button3 = (RadioRealButton)findViewById(R.id.button3);
        imageNext = (ImageView) findViewById(R.id.imageNext);
        savedChanged = (ImageView) findViewById(R.id.saveChanges);
        server = new ServerRequest(this);
        Intent intent = getIntent();
        String imgURL = intent.getStringExtra("selected_image");
        displayCurrentImage(imgURL, imageNext, mAppend);

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        /** print notification **/
        groupButtons.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                buttonID = position;

                Intent intent = new Intent(NextActivity.this, StyleActivity.class);
                startActivityForResult(intent, STYLE_IMAGE_REQUEST);
            }});

        groupButtons.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int currentPosition, int lastPosition) {
            }
        });

        savedChanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable imgDrawable = (BitmapDrawable) imageNext.getDrawable();
                Bitmap imgBitmap = imgDrawable.getBitmap();
                int maxImageSize = Integer.parseInt(getResources().getString(R.string.image_size));
                Bitmap scaleImgBitmap = Bitmap.createScaledBitmap(imgBitmap, maxImageSize, maxImageSize, false);
                String imageBase64 = ImageEncoderDecoder.encodeBitmapToBase64(scaleImgBitmap);

                switch (buttonID){
                    case 0:
                        Log.d(TAG, "Click on DEFAULT button");
                        sendDefaultImageRequest(imageBase64);
                        break;
                    case 1:
                        Log.d(TAG, "Click on STYLE button");

                        sendStyleImageRequest(imageBase64, chosenStyleID);
                        break;
                    case 2:
                        break;
                }

                activity.finish();
            }
        });
    }

    private void sendStyleImageRequest(String imageBase64, String id){
        String url = getResources().getString(R.string.style_url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("image", imageBase64);
        params.put("chosenStyleID", id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                server.sendRequestToServer(url, params);
            }
        }).start();
    }

    private void sendDefaultImageRequest(String imageBase64){
        String url = getResources().getString(R.string.upload_url);
        Map<String, String> params = new HashMap<String, String>();
        params.put("image", imageBase64);
        new Thread(new Runnable() {
            @Override
            public void run() {
                server.sendRequestToServer(url, params);
            }
        }).start();
    }

    private void displayCurrentImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STYLE_IMAGE_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                chosenStyleID = data.getStringExtra("chosenStyleID");
            }
        }
    }//onActivityResult

}


