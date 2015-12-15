package com.tchip.view;

import com.goodocom.gocsdk.Config;
import com.goodocom.gocsdk.R;
import com.goodocom.gocsdk.service.GocsdkService;
import com.tchip.call.MainActivity;
import com.tchip.util.OperateCommand;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;


/**
 * 
 * @author wwj
 *
 */
public class TchipToast{
	Context context;
	Toast toast;
	
	public TchipToast(Context context){
		this.context = context;
	}
	
	public void initLayout(int volume){
		toast = Toast.makeText(context, "蓝牙音量", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		SeekBar sb = new SeekBar(context);
		sb.setMax(18);
		sb.setProgress(volume);
		sb.setPressed(false);
		sb.setThumb(null);
		toastView.addView(sb, 300, 50);
	}
	
	public void show(int volume){
		try{
			toast.cancel();
		}catch(Exception e){
			//toast还没有
		}
		initLayout(volume);
		toast.show();
	}
}