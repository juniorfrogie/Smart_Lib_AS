 /**
 * <pre>
 * 사진(이미지) 크게보기 화면
 * @COPYRIGHT (c) 2014 Webcash, Inc. All Right Reserved.
 *
 * @author       : 스마트프레임워크팀 (최은경)
 * @Description  : 
 * @History      : 
 * [2015-07-31 : dilky] 
 * - 라이브러리 변경 AUIL --> Glide 
 * - Glide 가 성능이 좋고 Gif 가 지원됨.
 * - 참고사이트 http://dev2.prompt.co.kr/31
 *
 * </pre>
 **/
package com.webcash.sws.comm.picture;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.webcash.sws.comm.define.Msg;
import com.webcash.sws.comm.define.Msg.Exp;
import com.webcash.sws.comm.extras.Extras_Picture;
import com.webcash.sws.comm.ui.DlgAlert;
import com.webcash.sws.comm.util.Convert;
import com.webcash.sws.lib.R;

/**
 * 콜라보 Ver 2.0 부터 사용함.
 *
 */
public class PictureViewActivity extends Activity {
	
	public static final int ACTIVITY_REQ_CD = 32760;
	
	private TextView mTvPage;
	
	private ViewPager mViewPager;
	
	/*
	 * 사진 파라미터
	 */
	private Extras_Picture mExtrasPhoto;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
        	mExtrasPhoto = new Extras_Picture(this, getIntent());
	        
	        setContentView(R.layout.picture_view_activity);
            
	        mTvPage = (TextView) findViewById(R.id.tv_Page);
	        
	        
	        mViewPager = (ViewPager) findViewById(R.id.view_pager);
	        ImagePagerAdapter adapter = new ImagePagerAdapter();
	        mViewPager.setAdapter(adapter);
	        
	        
	        if (mExtrasPhoto.Param.getPHOTO_CURRENTITEM() > 0 && mExtrasPhoto.Param.getPHOTO_CURRENTITEM() < mExtrasPhoto.Param.getPHOTOUUIDLIST().size()) {
	        	mViewPager.setCurrentItem(mExtrasPhoto.Param.getPHOTO_CURRENTITEM());
		        setPagerIndicator(mExtrasPhoto.Param.getPHOTO_CURRENTITEM() + 1);
	        } else {
	        	setPagerIndicator(1);
	        }	       
	        
	        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override public void onPageSelected(int position) {
					try {
						setPagerIndicator(position + 1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }				
				@Override public void onPageScrollStateChanged(int arg0) { }
			});
	        
	        // 창 닫기
	        findViewById(R.id.btn_Close).setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					finish();
				}
			});
	        
	        // 앨범에 저장 (이미지가 정상적으로 표현되면 보여준다.)
	        findViewById(R.id.btn_SaveToAlbum).setVisibility(View.INVISIBLE);
	        	
	        
        } catch (Exception e) {
			DlgAlert.Error(this, Msg.Exp.DEFAULT, e);
		}
	}
    
	
	protected void onDestroy() {
		super.onDestroy();
	};
	
	private class SaveToAlbum extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {

			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();

			final String imageUrl = mExtrasPhoto.Param.getPHOTOUUIDLIST().get(params[0]);
			final String extention = imageUrl.substring(imageUrl.lastIndexOf(".") + 1, imageUrl.length());

			try {

				URL url = new URL(imageUrl);
				InputStream input = url.openStream();
				OutputStream output = null;
				try {
					String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
							.getAbsolutePath() + "/" + String.valueOf(Convert.ComDate.getCurrentTime()) + "." + extention;

					output = new FileOutputStream(storagePath);
					byte[] buffer = new byte[1024];
					int bytesRead = 0;
					while ((bytesRead = input.read(buffer, 0,
							buffer.length)) >= 0) {
						output.write(buffer, 0, bytesRead);
					}

					runOnUiThread(new Runnable() {
						public void run() {
							 Toast.makeText(PictureViewActivity.this,"사진을 저장했습니다.", Toast.LENGTH_SHORT).show();
							
						}
					});
				} finally {
					input.close();
					output.close();
				}
			} catch (Exception e) {
				runOnUiThread( new Runnable() {
					public void run() {
						DlgAlert.Error(PictureViewActivity.this, Exp.DEFAULT);
					}
				});
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	
	/**
	 * ViewPager 현재 선택된 페이지 이미지 
	 */
	private void setPagerIndicator(int position)  throws Exception {
		
		if (mTvPage != null) {
			mTvPage.setText(String.valueOf(position) + "/" + mExtrasPhoto.Param.getPHOTOUUIDLIST().size());			
		}
	}
	
	private class ImagePagerAdapter extends PagerAdapter {
		
		private ArrayList<String> mPHOTOUUIDLIST;
		
		private PhotoViewAttacher mAttacher;
	   
		public ImagePagerAdapter() {
			mPHOTOUUIDLIST = mExtrasPhoto.Param.getPHOTOUUIDLIST();
		}

	    @Override
	    public int getCount() {
	      return mPHOTOUUIDLIST.size();
	    }


	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	    	Context context = PictureViewActivity.this;
	    	
	    	final ImageView imageView = new ImageView(context);	
	    	int padding = 10;
	    	imageView.setPadding(padding, padding, padding, padding);
	    	imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	    	
	    	Glide.with(PictureViewActivity.this).load(mPHOTOUUIDLIST.get(position)).into(new GlideDrawableImageViewTarget(imageView){
	    		@Override
	    		public void onResourceReady(GlideDrawable arg0, GlideAnimation<? super GlideDrawable> arg1) {
	    			super.onResourceReady(arg0, arg1);
	    			
					mAttacher = new PhotoViewAttacher(imageView);
			        // 앨범에 저장
					findViewById(R.id.btn_SaveToAlbum).setVisibility(View.VISIBLE);
		        	findViewById(R.id.btn_SaveToAlbum).setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							int position = mViewPager.getCurrentItem();
							new SaveToAlbum().execute(position);
						}
					});

	    		}
	    		
	    		@Override
	            public void onLoadFailed(Exception e, Drawable errorDrawable) {
	                // TO DO :
	            }
	    	});
	      
	    	((ViewPager) container).addView(imageView, 0);
	    	
	    	return imageView;
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	    	((ViewPager) container).removeView((ImageView) object);
	    }

		@Override
		public boolean isViewFromObject(View view, Object object) {
			 return view == ((ImageView) object);
		}
	}
}
