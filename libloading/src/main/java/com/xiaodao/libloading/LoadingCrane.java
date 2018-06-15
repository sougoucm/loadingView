package com.xiaodao.libloading;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ProgressBar;


/**
 * 丹顶鹤加载框
 * @author dsj:
 * @date 2018.06.15
 * @version 1.1
 */
public class LoadingCrane extends LoadingDialogBase {

	private static final String TAG =LoadingCrane.class.getSimpleName();


	public LoadingCrane(Context context) {
		this(context, null);
	}

	public LoadingCrane(Context context, String text) {
		super(context, text);
	}

	@Override
	public void onShow(ProgressBar progressBar) {

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public Drawable getProgressDrawable() {
		Drawable progressDrawable =  context.getDrawable(R.drawable.frame_animl);
		return progressDrawable;
	}
}
