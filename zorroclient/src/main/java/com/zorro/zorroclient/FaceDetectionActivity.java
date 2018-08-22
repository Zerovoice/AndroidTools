package com.zorro.zorroclient;

import com.zorro.base.BaseMobileActivity;

public class FaceDetectionActivity extends BaseMobileActivity{

//    public final static String TYPE_CAMERA = "type_camera";
//    public final static String TYPE_GALLERY = "type_gallery";
//    public final static String KEY_ID = "_id";
//    public final static String KEY_USER_NAME = "user_id";
//    public final static String KEY_FACE_ORIGIN = "face_origin";
//    public final static String KEY_AVATAR = "avatar";
//    public final static String KEY_IS_FACE = "is_face";
//    public final static String KEY_ACTION = "key_action";
//    public final static String KEY_IMAGE_PATH = "key_image_path";
//    public final static String KEY_IMAGE_TYPE = "key_image_type";
//
//    public final static String ACTION_UPLOAD_AVATAR = "uploadAvatar";
//    public final static String ACTION_UPLOAD_FACE = "uploadFace";
//
//    private ClipImageLayout mClipImageLayout;
//    private ImageView ivCancel;
//    private ImageView ivSure;
//    private View markView;
//    private RelativeLayout faceDetectionLayout;
//    private ImageView faceDetectionMarkTop;
//    private ImageView faceDetectionMarkBottom;
//    private TextView tvFaceDetecting;
//    private AnimatorSet detectAs;
//    private File tempFile = null;
//    private float btnX = 0;
//
//    private long mNemoId;
//    private String mUserName;
//    private String mFaceOrigin;
//
//    private String strAction;//操作
//
//    private byte[] imageBytes;//缓存裁剪后的图片
//
//    private String imageType;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LOGGER.info("onCreate");
//
//        setContentView(R.layout.activity_face_detection);
//        Bitmap bitmap = null;
//        try {
//            imageType = getIntent().getType();
//
//            String imagePath = null;
//            if (TYPE_CAMERA.equals(imageType)) {
//                Uri imageUri = getIntent().getData();
//                imagePath = imageUri.getPath();
//            } else if (TYPE_GALLERY.equals(imageType)) {
//                imagePath = getIntent().getStringExtra(KEY_IMAGE_PATH);
//            }
//
//            if (TextUtils.isEmpty(imagePath)) {
//                finish();
//                return;
//            }
//
//            bitmap = getDownsampledBitmap(imagePath, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
//        } catch (Exception e) {
//            LOGGER.info("exception : " + e.getMessage());
//            setResult(RESULT_CANCELED);
//            finish();
//        }
//        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
//        mClipImageLayout.setZoomImageBitmap(bitmap);
//        mClipImageLayout.setParamters(14, 83, 1.0f, new ClipImageRoundBorderView(this));
//
//        mId = getIntent().getLongExtra(KEY_ID, 0);
//        mUserName = getIntent().getStringExtra(KEY_USER_NAME);
//        mFaceOrigin = getIntent().getStringExtra(KEY_FACE_ORIGIN);
//        strAction = getIntent().getStringExtra(KEY_ACTION);
//
//        ivCancel = (ImageView) findViewById(R.id.cancel_button);
//        ivSure = (ImageView) findViewById(R.id.sure_button);
//        markView = findViewById(R.id.mark_view);
//        faceDetectionLayout = (RelativeLayout) findViewById(R.id.detection_layout);
//        faceDetectionMarkTop = (ImageView) findViewById(R.id.face_detection_mark_top);
//        faceDetectionMarkBottom = (ImageView) findViewById(R.id.face_detection_mark_bottom);
//        tvFaceDetecting = (TextView) findViewById(R.id.tv_face_detecting);
//
//        ivCancel.setOnClickListener(this);
//        ivSure.setOnClickListener(this);
//    }
//
//    @Override
//    protected void onViewAndServiceReady(IServiceAIDL service) {
//        super.onViewAndServiceReady(service);
//        ivCancel.post(new Runnable() {
//            @Override
//            public void run() {
//                btnX = ivCancel.getX();
//                initDetectAnimation();
//                startAnimation();
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (detectAs != null) {
//            detectAs.cancel();
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.cancel_button:
//                ivCancel.setEnabled(false);
//                finish();
//                break;
//            case R.id.sure_button:
//                ivSure.setEnabled(false);
//                cropAndSave();
//                startDetect();
//                break;
//        }
//    }
//
//    private Bitmap getDownsampledBitmap(String imagePath, int targetWidth, int targetHeight) {
//        Bitmap bitmap = null;
//        try {
//
//            int rotation = imagePath == null ? 0 : readPictureDegree(imagePath);
//            BitmapFactory.Options outDimens = getBitmapDimensions(imagePath);
//
//            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);
//
//            bitmap = downsampleBitmap(imagePath, sampleSize);
//
//            bitmap = rotateImage(bitmap, rotation);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return bitmap;
//    }
//
//    private Bitmap downsampleBitmap(String imagePath, int sampleSize) throws FileNotFoundException, IOException {
//        Bitmap resizedBitmap;
//        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
//        outBitmap.inJustDecodeBounds = false;
//        outBitmap.inSampleSize = sampleSize;
//
//        resizedBitmap = BitmapFactory.decodeFile(imagePath, outBitmap);
//
//        return resizedBitmap;
//    }
//
//    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
//        int inSampleSize = 1;
//
//        if (height > targetHeight || width > targetWidth) {
//
//            final int heightRatio = Math.round((float) height / (float) targetHeight);
//            final int widthRatio = Math.round((float) width / (float) targetWidth);
//
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//
//            if (inSampleSize == 1) {
//                inSampleSize = 2;
//            }
//        }
//        return inSampleSize;
//    }
//
//    private BitmapFactory.Options getBitmapDimensions(String imagePath) throws FileNotFoundException, IOException {
//        BitmapFactory.Options outDimens = new BitmapFactory.Options();
//        outDimens.inJustDecodeBounds = true; // the decoder will return null (no
//        // bitmap)
//
//        // if Options requested only the size will be returned
//        BitmapFactory.decodeFile(imagePath, outDimens);
//
//        return outDimens;
//    }
//
//    // And to convert the image URI to the direct file system path of the image
//    // file
//    public String getRealPathFromURI(Uri contentUri) {
//        if (null == contentUri) {
//            return null;
//        }
//        // 在小米4手机上读相册返回的直接就是文件地址(file:///storage/emulated/0/DCIM/Camera/IMG_20150830_131756.jpg),其他手机都是(content://media/external/images/media/27577)
//        String uriStr = contentUri.toString();
//        if (uriStr.startsWith("file://")) {
//            return uriStr.substring(uriStr.indexOf("file://"), uriStr.length());
//        }
//        // can post image
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(contentUri, proj, // Which
//                // columns
//                // to
//                // return
//                null, // WHERE clause; which rows to return (all rows)
//                null, // WHERE clause selection arguments (none)
//                null); // Order-by clause (ascending by name)
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//    public int readPictureDegree(String path) {
//        int degree = 0;
//        try {
//            ExifInterface exifInterface = new ExifInterface(path);
//            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    degree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    degree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    degree = 270;
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return degree;
//    }
//
//    private Bitmap rotateImage(Bitmap bitmap, int rotation) {
//        Bitmap transformed = bitmap;
//        if (rotation != 0) {
//            Matrix m = new Matrix();
//            m.setRotate(rotation);
//            transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//        }
//        return transformed;
//    }
//
//    private void cropAndSave() {
//        Bitmap croppedImage = mClipImageLayout.clip();
//        if (croppedImage != null) {
//            imageBytes = CommonUtils.bitmap2Bytes(CommonUtils.scaleBitmap(croppedImage, 320, 320));
//            if (imageBytes != null && imageBytes.length > 0) {
//                if (strAction.equals(ACTION_UPLOAD_AVATAR)) {
//                    uploadAvatar();
//                } else if (strAction.equals(ACTION_UPLOAD_FACE)) {
//                    uploadFace(mId, mUserName, mFaceOrigin, imageBytes);
//                }
//            } else {
//                onError();
//            }
//        } else {
//            onError();
//        }
//        if (tempFile != null) {
//            if (!tempFile.delete()) {
//                L.e("del file error");
//            }
//        }
//    }
//
//    private void startAnimation() {
//        int duration = 300;
//        AnimatorSet as = new AnimatorSet();
//
//        List<Animator> anis = new ArrayList<>();
//        AnimatorSet markViewAs = new AnimatorSet();
//        markViewAs.play(getAnimation(markView, "alpha", 1.0f, 0f, duration));
//        AnimatorSet markLytAs = new AnimatorSet();
//
//        List<Animator> markLytAnis = new ArrayList<>();
//        markLytAnis.add(getAnimation(ivCancel, "scaleX", 0f, 1.0f, duration));
//        markLytAnis.add(getAnimation(ivCancel, "scaleY", 0f, 1.0f, duration));
//        markLytAnis.add(getAnimation(ivSure, "scaleX", 0f, 1.0f, duration));
//        markLytAnis.add(getAnimation(ivSure, "scaleY", 0f, 1.0f, duration));
//        markLytAs.playTogether(markLytAnis);
//
//        AnimatorSet btnAs = new AnimatorSet();
//        List<Animator> btnAnis = new ArrayList<>();
//        btnAnis.add(getAnimation(ivCancel, "x", btnX, btnX - UITools.dp2px(this, 60), duration));
//        btnAnis.add(getAnimation(ivSure, "x", btnX, btnX + UITools.dp2px(this, 60), duration));
//        btnAs.playTogether(btnAnis);
//
//        anis.add(markViewAs);
//        anis.add(markLytAs);
//        anis.add(btnAs);
//
//        as.playSequentially(anis);
//        as.start();
//    }
//
//    private void startDetect() {
//        AnimatorSet as = new AnimatorSet();
//        Animator tvFaceDetectAni = getAnimation(tvFaceDetecting, "alpha", tvFaceDetecting.getAlpha(), 1.0f, 500);
//        Animator ivCancelAni = getAnimation(ivCancel, "alpha", ivCancel.getAlpha(), 0f, 500);
//        Animator ivSureAni = getAnimation(ivSure, "alpha", ivSure.getAlpha(), 0f, 500);
//        as.playTogether(tvFaceDetectAni, ivCancelAni, ivSureAni, detectAs);
//        as.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                ivSure.setEnabled(false);
//                ivCancel.setEnabled(false);
//                mClipImageLayout.setEnabled(false);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        as.start();
//    }
//
//    private void initDetectAnimation() {
//        detectAs = new AnimatorSet();
//        int duration = 2000;
//        Animator animatorTop = getAnimation(faceDetectionMarkTop, "y", faceDetectionMarkTop.getTop(), faceDetectionLayout.getHeight(), duration);
//        Animator animatorBottom = getAnimation(faceDetectionMarkBottom, "y", faceDetectionMarkBottom.getTop(), -faceDetectionMarkBottom.getHeight(), duration);
//        detectAs.playSequentially(animatorTop, animatorBottom);
//        detectAs.addListener(new Animator.AnimatorListener() {
//
//            private boolean isCancel = false;
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                isCancel = false;
//                faceDetectionLayout.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (!isCancel) {
//                    detectAs.start();
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                isCancel = true;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//    }
//
//    private void startReDetectAs() {
//
//        if (detectAs != null) {
//            detectAs.cancel();
//        }
//
//        AnimatorSet as = new AnimatorSet();
//        Animator tvFaceDetectAni = getAnimation(tvFaceDetecting, "alpha", tvFaceDetecting.getAlpha(), 0f, 500);
//        Animator ivCancelAni = getAnimation(ivCancel, "alpha", ivCancel.getAlpha(), 1.0f, 500);
//        Animator ivSureAni = getAnimation(ivSure, "alpha", ivSure.getAlpha(), 1.0f, 500);
//        as.playTogether(tvFaceDetectAni, ivCancelAni, ivSureAni);
//        as.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                faceDetectionLayout.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                ivSure.setEnabled(true);
//                ivCancel.setEnabled(true);
//                mClipImageLayout.setEnabled(true);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        as.start();
//    }
//
//    private Animator getAnimation(View view, String propertyName, float from, float to, int duration) {
//        ObjectAnimator oa = ObjectAnimator.ofFloat(view, propertyName, from, to);
//        oa.setDuration(duration);
//        return oa;
//    }
//
//    private void uploadAvatar() {
//        try {
//            getAIDLService().uploadProfilePictureFacade(imageBytes);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean shouldReverseSelection() {
//        return mFaceOrigin.equals(BusinessConst.KEY_FACE_REFERER_KIDGUARD);
//    }
//
//    private void uploadFace(long Id, String name, String origin, byte[] picture) {
//        boolean enabled = shouldReverseSelection() ? false : true;
//        URI uri = Uris.getFaceUploadUri(Id, name, origin, enabled);
//        Post post = new Post(uri);
//        post.getHeaders().put("Content-Type", ContentTxUtils.getContentType());
//        post.setData(ContentTxUtils.buildImageData(picture));
//        final WeakReference<FaceDetectionActivity> weakSelf = new WeakReference<>(this);
//        HttpConnector.postExecute(post,
//                new HttpConnector.Callback() {
//                    @Override
//                    public void onDone(HttpResponse resp) {
//                        if (resp.isSuccess()) {
//                            LOGGER.info("uploadFace isSuccess");
//                            if (weakSelf.get() != null) {
//                                FaceMetaResponse responseData = JsonUtil.toObject(resp.getData(), FaceMetaResponse.class);
//                                weakSelf.get().didFinishUploadingFace(responseData);
//                            }
//                        } else {
//                            LOGGER.info("uploadFace isfail");
//                            if (weakSelf.get() != null) {
//                                weakSelf.get().onError();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onException(Exception e) {
//                        LOGGER.info("uploadFace exception : " + e.getMessage());
//                        e.printStackTrace();
//                        if (weakSelf.get() != null) {
//                            weakSelf.get().onError();
//                        }
//                    }
//                });
//    }
//
//    private void didFinishUploadingFace(FaceMetaResponse metaResponse) {
//        Intent data = new Intent();
//
//        if (metaResponse != null
//                && metaResponse.getData() != null
//                && metaResponse.getData().size() > 0) {
//            FaceMetaData face = metaResponse.getData().get(0);
//            data.putExtra(BusinessConst.KEY_FACE_META, (Parcelable) face);
//        }
//
//        setResult(RESULT_OK, data);
//        finish();
//    }
//
//    private void didFinishUploadingAvatar(Message msg) {
//        LOGGER.info("uploadAvatar resp : " + msg);
//        if (msg.arg1 == 200) {
//
//            UploadProfilePictureFacadeResponse resp = (UploadProfilePictureFacadeResponse) msg.obj;
//
//            Intent data = new Intent();
//            data.putExtra(KEY_AVATAR, imageBytes);
//            data.putExtra(KEY_IS_FACE, resp.isFace);
//            data.putExtra(KEY_IMAGE_TYPE, imageType);
//            setResult(RESULT_OK, data);
//            finish();
//            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);// 淡出淡入动画效果
//        } else {
//            onError();
//        }
//
//    }
//
//    private void onError() {
//        if (strAction.equals(ACTION_UPLOAD_AVATAR)) {
//            AlertUtil.toastText(R.string.upload_picture_failure);
//            startReDetectAs();
//        } else {
//            didFinishUploadingFace(null);
//        }
//    }
//
//    @Override
//    public void onMessage(Message msg) {
//
//        switch (msg.what) {
//            case Msg.Business.BS_UPLOAD_PROFILE_PICTURE_FACADE_RESPONSE:
//                didFinishUploadingAvatar(msg);
//                break;
//        }
//    }

}