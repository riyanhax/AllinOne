package com.parasme.swopinfo.helper;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.AvatarAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.parasme.swopinfo.helper.Utils.fixExif;

public class ImagePicker extends Activity{

    public static Picker picker;
    private static final int PICK_FROM_CAMERA = 4;
    private static final int CROP_FROM_CAMERA = 5;
    private static final int PICK_FROM_FILE = 6;
    private Uri mImageCaptureUri;
    private String strMyImagePath="null";
    private AlertDialog dialog, dialogCameraVideo;
    private String TAG = this.getClass().getName();
    private boolean isPathSet=false;
    private boolean isCameraVideoDialogOpened = false;
    private File file, videoFile;
    private boolean isCapturing = false;
    private String [] items	= new String [] {"Camera","Gallery","Cancel"};
    private String [] itemsProfile	= new String [] {"Camera","Gallery","Avatars","Cancel"};
    private Uri videoUri;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        editText = (EditText) findViewById(R.id.edit);

        file = new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        if(getIntent().hasExtra("selectType")){
            int selectType = getIntent().getIntExtra("selectType",0);
            if(selectType == 0){
                isCameraVideoDialogOpened = true;
                showCameraVideoDialog(new String[]{"Picture","Video","Cancel"});}
            else
                startGalleryPickActivity();
        }
    }

    public interface Picker{
        void onImagePicked(Bitmap bitmap, String imagePath);
        void onVideoPicked(String videoPath);
    }

    public void getImage(String[] items) {
        Log.e(TAG,"get Image");
        ArrayAdapter<String> adapter	= new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        // Defining Alert Dialog box to pop up options Either Take from Camera or Select from Gallery

        builder.setTitle("Choose image from");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()

        {
            public void onClick(DialogInterface dialog, int item)

            { //pick from camera
                if (item == 0)
                {
                    startCameraCaptureActivity();
                } else if(item ==1) {
                    // Gallery Pick Intent
                    startGalleryPickActivity();
                }
                else
                    finish();
            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    public void dialogProfilePic(String[] items) {
        Log.e(TAG,"get Image");
        ArrayAdapter<String> adapter	= new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        // Defining Alert Dialog box to pop up options Either Take from Camera or Select from Gallery

        builder.setTitle("Choose image from");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()

        {
            public void onClick(DialogInterface dialog, int item)

            { //pick from camera
                if (item == 0)
                {
                    startCameraCaptureActivity();
                } else if(item ==1) {
                    // Gallery Pick Intent
                    startGalleryPickActivity();
                }
                else if(item ==2)
                {
                    showAvatarDialog();
                    dialog.dismiss();
                }
                else
                    finish();
            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showAvatarDialog() {
        final Dialog dialogAvatar = new Dialog(ImagePicker.this);
        dialogAvatar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAvatar.setContentView(R.layout.dialog_avatar);
        dialogAvatar.setCancelable(false);

        final ArrayList<File> fileArrayList = loadFileList();
        final GridView gridAvatar = (GridView) dialogAvatar.findViewById(R.id.gridAvatar);
        gridAvatar.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        final AvatarAdapter adapter = new AvatarAdapter(ImagePicker.this,R.layout.item_avatar,fileArrayList);
        gridAvatar.setAdapter(adapter);

        gridAvatar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
                if(adapter.selectedPosition!=-1)
                    gridAvatar.getChildAt(adapter.selectedPosition).setBackgroundResource(0);
                view.setBackgroundResource(R.color.colorPrimary);
                adapter.selectedPosition = position;
*/
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
            }
        });

        TextView textCancel = (TextView) dialogAvatar.findViewById(R.id.cancel);
        TextView textSelect = (TextView) dialogAvatar.findViewById(R.id.select);
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAvatar.dismiss();
                finish();
            }
        });

        textSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap myBitmap = BitmapFactory.decodeFile(fileArrayList.get(adapter.selectedPosition).getAbsolutePath());
                dialogAvatar.dismiss();
                picker.onImagePicked(myBitmap,fileArrayList.get(adapter.selectedPosition).getAbsolutePath());
                finish();
            }
        });

        dialogAvatar.show();
    }

    private ArrayList<File> loadFileList() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (int i = 2; i < 17; i++) {
            File imgFile = new  File(getApplicationContext().getFilesDir().getAbsolutePath()+"/avatars/avatar"+i+".png");
            fileArrayList.add(imgFile);
        }
        return fileArrayList;
    }

    private void showCameraVideoDialog(String[] items) {
        ArrayAdapter<String> adapter	= new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        // Defining Alert Dialog box to pop up options Either Take from Camera or Select from Gallery

        builder.setTitle("Choose image from");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()

        {
            public void onClick(DialogInterface dialog, int item)

            { //pick from camera
                if (item == 0){
                    startCameraCaptureActivity();
                }
                else if(item ==1) {
                    // Gallery Pick Intent
                    startVideoRecordActivity();
                }
                else{
                    finish();
                }
            }
        });

        dialogCameraVideo = builder.create();
        dialogCameraVideo.setCancelable(false);
        dialogCameraVideo.show();
    }

    private void startVideoRecordActivity() {
        isCameraVideoDialogOpened = false;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // create a file to save the video
        videoUri = getOutputMediaFileUri();

        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

        // set the video image quality to high
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    private Uri getOutputMediaFileUri() {
        videoFile = new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".mp4");

        Uri videoUri = FileProvider.getUriForFile(ImagePicker.this, getApplicationContext().getPackageName() + ".provider"
                , videoFile);

        return videoUri;
    }

    private File getOutputMediaFile() {
        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraVideo");


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){


                Toast.makeText(ImagePicker.this, "Failed to create directory.",
                        Toast.LENGTH_LONG).show();

                Log.e("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;


        // For unique video file name appending current timeStamp with file name
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4");

        return mediaFile;
    }

    private void startGalleryPickActivity() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //startActivityForResult(i, RESULT_LOAD_IMAGE);
        startActivityForResult(i, PICK_FROM_FILE);
    }

    private void startCameraCaptureActivity() {
        isCameraVideoDialogOpened = false;

        file = new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        isCapturing = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Taken Picture from camera will have a temp name


        mImageCaptureUri = FileProvider.getUriForFile(ImagePicker.this, getApplicationContext().getPackageName() + ".provider"
                , file);

//                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
//                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));


        //path=file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            intent.putExtra("return-data", true);

            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)

    {
        Log.e(TAG, "CHECK: "+requestCode + ")))"+resultCode );
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            //If Pick intent is fired then start camera and calling cropping method
            case PICK_FROM_CAMERA:
                doCrop(Uri.fromFile(new File(fixExif(file.getPath()))));

                break;

            //If Pick from gallery intent is started then image is selected and cropping method is called
            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();
/*
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(mImageCaptureUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                cursor.close();
*/

                doCrop(mImageCaptureUri);

                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                Log.e(TAG, "onActivityResult: cropped" );
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    String imagePath = Utils.saveFileFromBitmap(bitmap,file,false);
                    picker.onImagePicked(bitmap, imagePath);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE:
                picker.onVideoPicked(videoFile.getPath());
                finish();
                break;
            case -1:
                finish();
        }
    }


    public void doCrop(Uri uri)

    {
        isCameraVideoDialogOpened = true;
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        strMyImagePath = "null";
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                isPathSet=true;
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.jpg";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            Log.e("iamgep",strMyImagePath);
            //Bitmap bitmap = BitmapFactory.decodeFile(strMyImagePath);
            //picker.onImagePicked(bitmap,strMyImagePath);
            isPathSet=true;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;
    }



    public  Bitmap decodeResource(Resources res, int resId, int dstWidth, int dstHeight,
                                  ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeResource(res, resId, options);

        return unscaledBitmap;
    }

    public  Bitmap decodeFile(String path, int dstWidth, int dstHeight,
                              ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeFile(path, options);

        return unscaledBitmap;
    }

    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap Bitmap to scale
     * @param dstWidth Wanted width of destination bitmap
     * @param dstHeight Wanted height of destination bitmap
     * @param scalingLogic Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    public  Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
                                      ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    /**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     *
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     *
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    enum ScalingLogic {
        CROP, FIT
    }

    /**
     * Calculate optimal down-sampling factor given the dimensions of a source
     * image, the dimensions of a destination area and a scaling logic.
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal down scaling sample size for decoding
     */
    public  int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                    ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    public  Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                  ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (int)(srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    public  Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                  ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!getIntent().hasExtra("selectType")){
            if (getIntent().hasExtra("avatar"))
                dialogProfilePic(itemsProfile);
            else
                getImage(items);
        }
        else{
            if(!isCameraVideoDialogOpened)
            onActivityResult(-1, -1, null);
/*
            if(isCameraVideoDialogOpened) {
                Log.e(TAG, "onResume: " );
                dialogCameraVideo.show();
                //finish();
            }
*/
        }
    }
}