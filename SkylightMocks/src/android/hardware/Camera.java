package android.hardware;

import android.util.Log;
import android.view.SurfaceHolder;

public class Camera {

	public static interface AutoFocusCallback {
		void onAutoFocus(boolean success, Camera camera);
	}

	public static interface ErrorCallback {
		void onError(int error, Camera camera);
	}

	public static class Parameters {
		String flatten() {
			throw new RuntimeException("Not implemented yet.");
		};

		String get(String key) {
			throw new RuntimeException("Not implemented yet.");
		};

		int getInt(String key) {
			throw new RuntimeException("Not implemented yet.");
		};

		int getPictureFormat() {
			throw new RuntimeException("Not implemented yet.");
		};

		Camera.Size getPictureSize() {
			throw new RuntimeException("Not implemented yet.");
		};

		int getPreviewFormat() {
			throw new RuntimeException("Not implemented yet.");
		};

		int getPreviewFrameRate() {
			throw new RuntimeException("Not implemented yet.");
		};

		Size getPreviewSize() {
			throw new RuntimeException("Not implemented yet.");
		};

		void remove(String key) {
			throw new RuntimeException("Not implemented yet.");
		};

		void set(String key, int value) {
			throw new RuntimeException("Not implemented yet.");
		};

		void set(String key, String value) {
			throw new RuntimeException("Not implemented yet.");
		};

		void setPictureFormat(int pixel_format) {
			throw new RuntimeException("Not implemented yet.");
		};

		void setPictureSize(int width, int height) {
			throw new RuntimeException("Not implemented yet.");
		};

		void setPreviewFormat(int pixel_format) {
			throw new RuntimeException("Not implemented yet.");
		};

		void setPreviewFrameRate(int fps) {
			throw new RuntimeException("Not implemented yet.");
		};

		void setPreviewSize(int width, int height) {
			throw new RuntimeException("Not implemented yet.");
		};

		void unflatten(String flattened) {
			throw new RuntimeException("Not implemented yet.");
		};
	}

	public static interface PictureCallback {
		abstract void onPictureTaken(byte[] data, Camera camera);
	}

	public static interface PreviewCallback {
		abstract void onPreviewFrame(byte[] data, Camera camera);
	}

	public static interface ShutterCallback {
		abstract void onShutter();
	}

	public static class Size {
		public int height;

		public int width;

		Size(int w, int h) {
			width = w;
			height = h;
		}
	}

	final static int CAMERA_ERROR_SERVER_DIED = 100;

	final static int CAMERA_ERROR_UNKNOWN = 1;

	ErrorCallback errorCallback;

	PictureCallback pictureCallback;

	PreviewCallback previewCallback;

	AutoFocusCallback autoFocusCallback;

	private byte[] rawPictureData;

	private byte[] jPEGPictureData;

	Camera() {
//		Log.w(Camera.class.getName(), "THIS IS THE MOCK OBJECT - DO NOT ALLOW IN APK");
	}

	void setRawPictureData(byte[] aRawPictureData) {
		rawPictureData = aRawPictureData;
	}

	void setJPEGPictureData(byte[] aJPEGPictureData) {
		jPEGPictureData = aJPEGPictureData;
	}

	public final void autoFocus(Camera.AutoFocusCallback cb) {
		autoFocusCallback = cb;
	}

	public static Camera open() {
		throw new RuntimeException("Use MockCameraFactory to obtain a Camera object.");
	}

	public final void release() {
	}

	public final void setErrorCallback(Camera.ErrorCallback cb) {
		errorCallback = cb;
	}

	public void setParameters(Camera.Parameters params) {
	}

	public final void setPreviewCallback(Camera.PreviewCallback cb) {
		previewCallback = cb;
	}

	public final void setPreviewDisplay(SurfaceHolder holder) {
	}

	public final void startPreview() {
	}

	public final void stopPreview() {
	}

	public final void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
			Camera.PictureCallback jpeg) {
		if (shutter != null) {
			shutter.onShutter();
		}
		if (raw != null) {
			raw.onPictureTaken(rawPictureData, this);
		}
		if (jpeg != null) {
			jpeg.onPictureTaken(jPEGPictureData, this);
		}
	}
}