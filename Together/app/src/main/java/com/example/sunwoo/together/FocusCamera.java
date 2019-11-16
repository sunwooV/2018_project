package com.example.sunwoo.together;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FocusCamera extends Activity implements TextToSpeech.OnInitListener{
    String TAG = "CAMERA";
    private Context mContext = this;
    private Camera mCamera;
    private RadioButton mCheck, mTouchView;
    private boolean isPhotoTaken = false;
    private boolean isFocused = false;
    public static String braille;
    int n= 1;

    private TextToSpeech myTTS;


    private PictureCallback mPicture = new PictureCallback() {
        @Override

        public void onPictureTaken(byte[] data, Camera camera) {
            // JPEG 이미지가 byte[] 형태로 들어옵니다
            isPhotoTaken = true;
            mCheck.setChecked(false);
            mCamera.startPreview();
            new ImageSaveTask().execute(data);

            Intent move = new Intent(getApplicationContext(),SendfileActivity.class);
            startActivity(move);

        }


    };

    private AutoFocusCallback mFocus = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // 포커싱이 제대로 되었다면 UI로 포커스 되었다는걸 알립니다
            if (success) {
                mCheck.setChecked(true);
                isFocused = true;
            } else
                mCheck.setChecked(false);
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "Range"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myTTS = new TextToSpeech(this, this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.focuscamera);
        mContext = this;

        ((Button) findViewById(R.id.home_button))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        braille = "지폐";
                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        startActivity(intent);
                    }
                });
        // 카메라 인스턴스 생성
        if (checkCameraHardware(mContext)) {
            mCamera = getCameraInstance();

            // 프리뷰창을 생성하고 액티비티의 레이아웃으로 지정합니다
            CameraPreview mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            // 터치를 입력 받을 영역은 굳이 라디오 버튼이 아니라 다른 View 위젯이면 됩니다만
            // 그냥 라디오 버튼 하나 만드는김에 하나 더 만들었을 뿐입니다
            mTouchView = (RadioButton) findViewById(R.id.touchListener);

            mTouchView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();


                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // 초점 조정 콜백 호출
                            mCamera.autoFocus(mFocus);
                            Log.d(TAG, "Down");

                            break;
                        case MotionEvent.ACTION_UP:
                            // JPEG 콜백 메소드로 이미지를 가져옵니다
                            if (isFocused){// 초점이 맞춰졌을때만 찍는다
                                mCamera.takePicture(null, null, mPicture);
                                Log.d(TAG, "UP");}
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // 초점 재설정
                            isFocused = true;
                            mCheck.setChecked(false);
                            Log.d(TAG, "MOVE");

                    }
                    return true;
                }
            });

            mCheck = (RadioButton) findViewById(R.id.focus);
            mCheck.setChecked(false);
            mCheck.setClickable(false);
            mCheck.setAlpha(15);
        } else {
            Toast.makeText(mContext, "no camera on this device!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 카메라 하드웨어 지원 여부 확인
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 카메라가 최소한 한개 있는 경우 처리
            Log.i(TAG, "Number of available camera : " + Camera.getNumberOfCameras());
            return true;
        } else {
            // 카메라가 전혀 없는 경우
            Toast.makeText(mContext, "No camera found!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 카메라 인스턴스를 안전하게 획득합니다
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        Camera.Parameters p;
        try {
            c = Camera.open();

            p = c.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            c.setParameters(p);

        } catch (Exception e) {
            // 사용중이거나 사용 불가능 한 경우
        }
        return c;
    }


    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }


    /**
     * 이미지 처리및 저장을 별도의 스레드에서 처리해서 바로바로 다음 사진을 찍을 준비를 해준다
     */
    class ImageSaveTask extends AsyncTask<byte[], Void, Boolean> {

        /* 실제 작업을 하는 메소드 */
        @Override
        protected Boolean doInBackground(byte[]... data) {

            /*
             * 이미지에 필터를 입히거나 특정 작업을 처리를 한다
             */

            // 처리가 끝나면 이미지를 파일로 저장한다
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return false;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data[0]);
                fos.close();

                refreshGallery(pictureFile);
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        /* 작업이 끝난후 알림 */
        @Override
        protected void onPostExecute(Boolean isDone) {
            if (isDone) {
                Toast.makeText(mContext, "Image saved!", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 이미지를 저장할 파일 객체를 생성합니다
         */
        private File getOutputMediaFile() {
            // SD카드가 마운트 되어있는지 먼저 확인해야합니다
            // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다

            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                return null;
            }

            else {
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "BF");
                // 굳이 이 경로로 하지 않아도 되지만 가장 안전한 경로이므로 추천함.

                // 없는 경로라면 따로 생성한다.
                if (!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdirs();

                }

                n++;
                File outputFile = new File(mediaStorageDir, "IMG_01.jpg");


                Log.d(TAG, "picture saved");
                return outputFile;

            }

            // 파일명을 적당히 생성. 여기선 시간으로 파일명 중복을 피한다.
            //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
          /*  File mediaFile;

            mediaFile = new File(mediaStorageDir  + "IMG_01"+ ".jpg");
            Log.i(TAG, "Saved at"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));

            return mediaFile;*/

        };
    }

    public static String getData(){
        return braille;
    }

    private static ImageClassifier instance = null;

    public static synchronized ImageClassifier getInstance(){
        return instance;
    }

    public void onInit(int status){

        String myText = "점자에 대고 화면을 터치해주세요.";

        myTTS.speak(myText, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myTTS.shutdown();
    }

}



