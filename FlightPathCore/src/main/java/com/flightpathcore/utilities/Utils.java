package com.flightpathcore.utilities;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static String DIRECTORY_NAME = "FlightPath";

	public static SimpleDateFormat getGlobalDateFormat(){
		return sdf;
	}

    public static String getUpdateApkPath(){
        return Environment.getExternalStorageDirectory()+"/"+ Utils.DIRECTORY_NAME+"/"+"update.apk";
    }

	public static void hideSoftKeyboard(final Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if ((inputMethodManager != null) && (activity.getCurrentFocus() != null)) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
	
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context ctx, Uri uri) {
        Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static String getPathFromUri(Uri uri, Context context) {
        String[] data = { Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getStringPathFromBmp(Bitmap bmp) {
        File file = null;
        FileOutputStream out = null;
        try {
            file = createEmptyImageFile();
            out = new FileOutputStream(file.getAbsolutePath());
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your
                                                               // Bitmap
                                                               // instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
//    Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Logger.DIRECTORY_NAME+"/"+

    public static String getUtcTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static File createEmptyImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "/"+DIRECTORY_NAME+"/"+"JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();

        // Create directory if doesnt exists
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + DIRECTORY_NAME);
        if (!directory.exists()) {
            directory.mkdir();
        }

        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public static String decodeUri(Context ctx, Uri selectedImage, int widthHeight) throws FileNotFoundException {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(selectedImage.getPath());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(selectedImage), null, o);
        int imageHeight = o.outHeight;
        int imageWidth = o.outWidth;
        int rotate = 0;
        int temp;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                temp = imageWidth;
                imageWidth = imageHeight;
                imageHeight = temp;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                temp = imageWidth;
                imageWidth = imageHeight;
                imageHeight = temp;
                break;
        }

        Matrix mtx = new Matrix();
        mtx.preRotate(rotate);


        // The new size we want to scale to
        final int REQUIRED_SIZE = widthHeight;

        // Find the correct scale value. It should be the power of 2.
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (imageWidth / 2 < REQUIRED_SIZE || imageHeight / 2 < REQUIRED_SIZE) {
                break;
            }
            imageWidth /= 2;
            imageHeight /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        o.inJustDecodeBounds = false;
        o.inSampleSize = scale;
        Bitmap b = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(selectedImage), null, o);
        Bitmap out = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), mtx, false);
        File file = new File(selectedImage.getPath());
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.recycle();
        return file.getAbsolutePath();
    }

    public static final String getBase64StringFromImageView(ImageView iv) {

        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        return getBase64StringFromBitmap(bitmap);
    }

    public static final String getBase64StringFromBitmap(Bitmap bmp) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        bmp.recycle();
        return encoded;
    }

    public static final Bitmap getBitmapFromBase64String(String encodedBmp) {

        byte[] decodedString = Base64.decode(encodedBmp, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }
    
    public static Bitmap getBitmapFromView(View view) {
	    Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(returnedBitmap);
	    Drawable bgDrawable =view.getBackground();
	    if (bgDrawable!=null) 
	        bgDrawable.draw(canvas);
	    else 
	        canvas.drawColor(Color.TRANSPARENT);
	    view.draw(canvas);
//	    ByteArrayOutputStream os = new ByteArrayOutputStream();
//	    returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
//	    byte[] array = os.toByteArray(); 
//	    x - 400
//	    y - ?					
//	    BitmapFactory.decodeByteArray(array, 0, array.length)
//	    Bitmap scaledBitmap = Bitmap.createScaledBitmap(returnedBitmap, returnedBitmap.getWidth()/2, returnedBitmap.getHeight()/2, true);
//	    return scaledBitmap;
	    
	    int width = 400;
    	int height = (int) Math.ceil(width * (float) returnedBitmap.getHeight() / returnedBitmap.getWidth());
    	return Bitmap.createScaledBitmap(returnedBitmap, width, height, true);
	}
    
    public static byte[] getBitmapBytesFromView(View view){
    	return getBytesFromBitmap(getBitmapFromView(view));
    }
    
    public static byte[] getBytesFromBitmap(Bitmap bmp){
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
    	return stream.toByteArray();
    	
    }
    
    public static String formatDate(Date date){
    	return sdf.format(date);
    }


}
