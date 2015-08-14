package ati.lunarmessages;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

public class CouponActivity extends Activity
{
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    ImageView iView;
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_coupon);
        contentView = findViewById(R.id.fullscreen_content);
        iView = (ImageView)findViewById(R.id.imgView);
        TextView txtDays=(TextView)findViewById(R.id.txtDays);
    //read installed date back
        Date installedOn = new Date(MyPreference.getInstalledOn(this));
        Date today=new Date(System.currentTimeMillis());

        txtDays.setText("Eltelt napok: "+(int) Controller.calcElapsedDays(installedOn,today));
        Bitmap bitmap = null;

        //setupActionBar();

    // barcode data
        String barcode_data = "Zetor-Vas";

        // barcode image
        try
        {
            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 400, 100);
            iView.setImageBitmap(bitmap);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height)
            throws WriterException
    {
        String contentsToEncode = contents;
        if (contentsToEncode == null)
        {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null)
        {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try
        {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        }
        catch (IllegalArgumentException iae)
        {
    // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++)
        {
            int offset = y * width;
            for (int x = 0; x < width; x++)
            {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents)
    {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++)
        {
            if (contents.charAt(i) > 0xFF)
            {
                return "UTF-8";
            }
        }
        return null;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
