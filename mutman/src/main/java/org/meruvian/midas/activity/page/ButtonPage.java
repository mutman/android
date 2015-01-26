package org.meruvian.midas.activity.page;

import static org.meruvian.midas.service.MidasActions.PERSONS_ACTION;

import org.meruvian.midas.service.ServiceHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ButtonPage extends LinearLayout {
	private Context context;
	
	private ServiceHelper serviceHelper;
	
	public ButtonPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ButtonPage(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		Button button = new Button(getContext());
		button.setText("My Button");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				serviceHelper = ServiceHelper.getInstance(context);
				serviceHelper.startService(PERSONS_ACTION);
			}
		});
		setGravity(Gravity.CENTER);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		addView(button, params);
	}
}
