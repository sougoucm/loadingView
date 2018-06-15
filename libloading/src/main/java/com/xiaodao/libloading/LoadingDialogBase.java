package com.xiaodao.libloading;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 自定义LoadingView
 *
 * @author dsj:
 * @date 20180615
 * @version 1.1
 */
public abstract  class LoadingDialogBase extends Dialog {

	private static final String TAG = LoadingCrane.class.getSimpleName();
	private TextView libui_loading_msg;
	private ProgressBar mProgressBar;
	private String text;
	protected Context context;


	public LoadingDialogBase(Context context) {
		this(context, null);
	}

	public LoadingDialogBase(Context context, String text) {
		super(context, R.style.dialogTheme);
		this.context = context;
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState );
		init(context, text);
	}

	protected void init(Context context, String text) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.lib_loadingdialog, null);
		setContentView(viewGroup);
		libui_loading_msg = (TextView) viewGroup.findViewById(R.id.libui_loading_msg);
		if (!TextUtils.isEmpty(text)) {
			libui_loading_msg.setVisibility(View.VISIBLE);
			libui_loading_msg.setText(text);
		}else {
			libui_loading_msg.setVisibility(View.GONE);
		}

		setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					dismiss();
				}
				return false;
			}
		});
		setCancelable(false);

		//init progressbar
		mProgressBar = (ProgressBar) viewGroup.findViewById(R.id.libui_loading_progress);
		Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
		mProgressBar.setIndeterminateDrawable(getProgressDrawable());
		mProgressBar.getIndeterminateDrawable().setBounds(bounds);


	}

	@Override
	public void show() {
		super.show();
		onShow(mProgressBar);

	}

	/**
	 * 给progress添加动画，然后执行它
	 * @param progressBar
     */
	public abstract void onShow(ProgressBar progressBar) ;

	/**
	 * 返回一个要添加到progresssBar对象中的图片，也可以是frame等等动画
	 * @return
     */
	public abstract Drawable getProgressDrawable();
}
