package com.google.android.exoplayer.demo;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class IngrainSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

		public int  mVideoWidth;
		public int  mVideoHeight;
		public int  widthNew;
		public int  heightNew;

		public IngrainSurfaceView(Context context) {
			super(context);

			 mVideoWidth = this.getWidth();
			 mVideoHeight = this.getHeight();

		}
		
		public IngrainSurfaceView(Context context, AttributeSet attrs) {
			super(context, attrs);

			 mVideoWidth = this.getWidth();
			 mVideoHeight = this.getHeight();
		}
		
		public IngrainSurfaceView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);

			 mVideoWidth = this.getWidth();
			 mVideoHeight = this.getHeight();

		}

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	    {
	        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
	        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

            if ( mVideoWidth * height  > width * mVideoHeight ) {

                height = width * mVideoHeight / mVideoWidth;
            } else if ( mVideoWidth * height  < width * mVideoHeight ) {

                width = height * mVideoWidth / mVideoHeight;
            }

	        heightNew = height;
	        widthNew= width;
	        setMeasuredDimension(width, height);
	    }

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		} 
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);


		}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}