package com.henteko07.yourself;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;


public class MyActivity extends Activity {
    private EditText mEditText;
    private MultiFormatWriter mMultiFormatWriter;
    private ImageView mImageView;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mEditText = (EditText) findViewById(R.id.editText);
        mMultiFormatWriter = new MultiFormatWriter();
        mImageView = (ImageView) findViewById(R.id.imageView);
        mBuilder = new NotificationCompat.Builder(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String url = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
                if (!url.isEmpty()) {
                    mEditText.setText(url);
                    showQrCode(url);
                }
            }
        }

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);

        Button sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mEditText.getText().toString();
                if (!inputText.isEmpty()) {
                    showQrCode(inputText);
                }
            }
        });
    }

    private void showQrCode(String url) {
        try {
            BitMatrix bitMatrix = mMultiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
            final int WHITE = 0xFFFFFFFF;
            final int BLACK = 0xFF000000;

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            mImageView.setImageBitmap(bitmap);


            mBuilder.setContentTitle("yourself")
                    .setContentText(url)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .setBigContentTitle(url)
                            .setSummaryText("yourself"))
                    .extend(new NotificationCompat.WearableExtender());

            mNotificationManager.notify(R.string.app_name, mBuilder.build());
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
