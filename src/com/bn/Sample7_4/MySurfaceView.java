package com.bn.Sample7_4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.media.MediaMetadataRetriever;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.opengl.GLES20;
import android.os.Environment;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample7_4.Constant.*;

class MySurfaceView extends GLSurfaceView {
	private final float TOUCH_SCALE_FACTOR = 180.0f / 380;// �Ƕ����ű���//180.0f / 320
	private SceneRenderer mRenderer;// ������Ⱦ��

	private float mPreviousX;// �ϴεĴ���λ��X����
	private float mPreviousY;// �ϴεĴ���λ��Y����

	int textureIdEarth;// ϵͳ����ĵ�������id
	int textureIdEarthNight;// ϵͳ����ĵ���ҹ������id
	int textureIdMoon;// ϵͳ�������������id

	float yAngle = 0,radius=Constant.radius;// ̫���ƹ���y����ת�ĽǶ�
	float xAngle = 0;// �������X����ת�ĽǶ�
	float cameraYAngle=0;// �������y����ת�ĽǶ�

	float eAngle = 0;// ������ת�Ƕ�
	float cAngle = 0;// ������ת�ĽǶ�

	public MySurfaceView(Context context) {
		super(context);
		this.setEGLContextClientVersion(2); // ����ʹ��OPENGL ES2.0
		mRenderer = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(mRenderer); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ
	}

	// �����¼��ص�����
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// ���غ���λ��̫����y����ת
			float dx = x - mPreviousX;// ���㴥�ر�Xλ��
			yAngle += dx * TOUCH_SCALE_FACTOR;// ����̫����y����ת�ĽǶ�
			float sunx = (float) (Math.cos(Math.toRadians(yAngle)) * 100);
			float sunz = -(float) (Math.sin(Math.toRadians(yAngle)) * 100);
			MatrixState.setLightLocationSun(sunx, 5, sunz);

			// ��������λ���������x����ת -90��+90
			float dy = y - mPreviousY;// ���㴥�ر�Yλ��
			xAngle += dy * TOUCH_SCALE_FACTOR;// ����̫����y����ת�ĽǶ�
			cameraYAngle+= dx * TOUCH_SCALE_FACTOR;
			if (xAngle > 90) {
				xAngle = -90;
			} else if (xAngle < -90) {
				xAngle = 90;
			}
			if (cameraYAngle>360) {
				cameraYAngle=0;
			} else if(cameraYAngle<0){
				cameraYAngle=360;
			}
//            float cy=(float) (7.2*Math.sin(Math.toRadians(xAngle)));
//            float cz=(float) (7.2*Math.cos(Math.toRadians(xAngle)));
			float cx = (float) (radius *Math.cos(Math.toRadians(xAngle))* Math.cos(Math.toRadians(cameraYAngle)));//������
			float cy = (float) (radius *Math.cos(Math.toRadians(xAngle))* Math.sin(Math.toRadians(cameraYAngle)));//������
			float cz = (float) (radius * Math.sin(Math.toRadians(xAngle)));
//			float tx = (float) (radius *Math.cos(Math.toRadians(xAngle))* Math.cos(Math.toRadians(cameraYAngle)));//������
//			float ty = (float) (radius *Math.cos(Math.toRadians(xAngle))* Math.sin(Math.toRadians(cameraYAngle)));//������
//			float tz = (float) (radius * Math.sin(Math.toRadians(xAngle)));
//			float upy = (float) Math.cos(Math.toRadians(xAngle));
//			float upz = -(float) Math.sin(Math.toRadians(xAngle));
			MatrixState.setCamera(cx, cy, cz, 0, 0, 0, 0f, 1.0f, 0.0f);//3�����+3Ŀ���+3up����
//			MatrixState.setCamera(0, 0, 0, cx, cy, cz, 0f, 1.0f, 0.0f);//3�����+3Ŀ���+3up����
//			MatrixState.setCamera(cx/10, cy/10, cz/10, 0, 0, 0, 0f, 1.0f, 0.0f);//3�����+3Ŀ���+3up����
//			MatrixState.setLightLocationSun(cx/10,cy/10, cz/10);//��Դλ��//�ڲ�
			MatrixState.setLightLocationSun(0,0,0);//��Դλ��
//			MatrixState.scale(0,-1.0f, -1.0f, -1.0f);//�ڲ�
		}
		mPreviousX = x;// ��¼���ر�λ��
		mPreviousY = y;
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer {
		Earth earth;// ����
		Moon moon;// ����
		Celestial cSmall;// С��������
		Celestial cBig;// ����������

		public void onDrawFrame(GL10 gl) {
			// �����Ȼ�������ɫ����
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
					| GLES20.GL_COLOR_BUFFER_BIT);
			
			// �����ֳ�
			MatrixState.pushMatrix();
			
//			// ������ת
//			MatrixState.rotate(eAngle, 0, 1, 0);
			
			// ��������Բ��
			earth.drawSelf(textureIdEarth);
			
			// ������ϵ������λ��
//			MatrixState.transtate(2f, 0, 0);
			// //������ת
			// MatrixState.rotate(eAngle, 0, 1, 0);
			// //��������
			// moon.drawSelf(textureIdMoon);
			// �ָ��ֳ�
			MatrixState.popMatrix();

			// �����ֳ�
			MatrixState.pushMatrix();
			MatrixState.rotate(cAngle, 0, 1, 0);
			MatrixState.scale(0,-1.0f, -1.0f, -1.0f);//�ڲ�
//			cSmall.drawSelf();//���������
//			cBig.drawSelf();
			// �ָ��ֳ�
			MatrixState.popMatrix();
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// �����Ӵ���С��λ��
			GLES20.glViewport(0, 0, width, height);
			// ����GLSurfaceView�Ŀ�߱�
			ratio = (float) width / height;
			// ���ô˷����������͸��ͶӰ����
			// Matrix.frustumM(left, right, bottom, top, near,far)
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, Constant.ProNear, Constant.ProFar);// 1f,280f
			// ���ô˷������������9����λ�þ���//3*3������//3�����+3Ŀ���+3up����
			MatrixState.setCamera(0, 0, radius, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//			MatrixState.setCamera(0, 0, radius/10, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
			// �򿪱������
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			// ��ʼ������
			// textureIdEarth=initTexture(R.drawable.road1);//������Ƭ����
			// textureIdEarthNight=initTexture(R.drawable.road2);//ҹ����Ƭ����
			textureIdEarth = initTexture(R.drawable.p1_800);// ������Ƭ����
			
//			textureIdEarthNight = initTexture(R.drawable.road2min);// ҹ����Ƭ����
//			textureIdMoon = initTexture(R.drawable.moon);
			// ����̫���ƹ�ĳ�ʼλ��
			MatrixState.setLightLocationSun(100, 5, 0);

//			// ����һ���̶߳�ʱ��ת��������
//			new Thread() {
//				public void run() {
//					while (threadFlag) {
////						// ������ת�Ƕ�
////						eAngle = (eAngle + 2) % 360;
//						// ������ת�Ƕ�
//						cAngle = (cAngle + 0.2f) % 360;
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}.start();
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// ������Ļ����ɫRGBA
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			
			// �����������
			earth = new Earth(MySurfaceView.this, 12.0f);//ԭΪ2.0f
			// �����������
			moon = new Moon(MySurfaceView.this, 1.0f);
			// ����С�����������
			cSmall = new Celestial(1, 0, 1000, MySurfaceView.this);
			// �����������������
			cBig = new Celestial(2, 0, 500, MySurfaceView.this);
			// ����ȼ��
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			
			// ��ʼ���任����
			MatrixState.setInitStack();
		}
	}

	private InputStream Bitmap2IS(Bitmap bm) { // vedio������InputStream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	public void getBitmapsFromVideo()// vedio������bitmap
	{
		String strVideoURL = Environment.getExternalStorageDirectory()
		        + "/SDTestVedio/GoSong.flv";
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(strVideoURL);
		// ȡ����Ƶ�ĳ���(��λΪ����)
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// ȡ����Ƶ�ĳ���(��λΪ��)
		int seconds = Integer.valueOf(time) / 1000;
		// �õ�ÿһ��ʱ�̵�bitmap�����һ��,�ڶ���
		for (int i = 1; i <= seconds; i++) {
			Bitmap bmByTime = retriever.getFrameAtTime(i * 1000 * 1000,
					MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			Bitmap2IS(bmByTime);
		}
	}

	public int initTexture(int drawableId)// textureId
	{
		// ��������ID
		int[] textures = new int[1];
		GLES20.glGenTextures(1, // ����������id������
				textures, // ����id������
				0 // ƫ����
		);
		int textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);

		// ͨ������������ͼƬ===============begin===================
		InputStream is = this.getResources().openRawResource(drawableId);
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// ͨ������������ͼƬ===============end=====================

		// ʵ�ʼ�������
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // �������ͣ���OpenGL
													// ES�б���ΪGL10.GL_TEXTURE_2D
				0, // ����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
				bitmapTmp, // ����ͼ��
				0 // ����߿�ߴ�
		);
		bitmapTmp.recycle(); // ������سɹ����ͷ�ͼƬ

		return textureId;
	}
}
