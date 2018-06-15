package com.xiaodao.libloading;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

/**
 * 自定义LoadingView
 *
 * @author dsj:
 * @date 2018.06.15
 * @version 1.1
 */
public class LoadingDialog extends LoadingDialogBase {

	private static final String TAG = LoadingCrane.class.getSimpleName();


	public LoadingDialog(Context context) {
		this(context, null);
	}

	public LoadingDialog(Context context, String text) {
		super(context, text);
	}

	@Override
	public void onShow(ProgressBar progressBar) {
		RotateAnimation fastRotate;fastRotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		fastRotate.setDuration(2000);
		fastRotate.setInterpolator(new LinearInterpolator());
		fastRotate.setRepeatCount(Animation.INFINITE);
		progressBar.startAnimation(fastRotate);
	}

	@Override
	public Drawable getProgressDrawable() {
		Drawable progressDrawable = new FloatingCirclesDrawable.Builder(context).setVelocitySlow().build();
		return progressDrawable;
	}
}
