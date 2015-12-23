package com.search.adapter;

import java.util.ArrayList;
import java.util.List;

import com.goodocom.gocsdk.R;
import com.tchip.contact.CallLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * 通话记录adapter
 * @author wwj
 *
 */
public class CallLogListAdapter extends BaseAdapter {   
    private Context context;                        //运行上下文   
    private List<CallLog> listItems;    //商品信息集合   
    private LayoutInflater listContainer;                //记录商品选中状态   

	public final class ListItemView { // 自定义控件集合
		public TextView name;
		public TextView time;
		public TextView type;
		public TextView duration;
	}
       
    public CallLogListAdapter(Context context, ArrayList<CallLog> listItems) {   
        this.context = context;            
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
        this.listItems =  (listItems == null) ? null : (ArrayList<CallLog>)listItems.clone();   
    }   
  
    public int getCount() {   
        // TODO Auto-generated method stub   
        return listItems.size();   
    }   
  
    public Object getItem(int arg0) {   
        // TODO Auto-generated method stub   
        return null;   
    }   
  
    public long getItemId(int arg0) {   
        // TODO Auto-generated method stub   
        return 0;   
    }   
    
    /*public void reSetAdapter(List<CallLog> listItems){
    	this.listItems = listItems;
    	notifyDataSetChanged();
    }*/
       
    /**  
     * ListView Item设置  
     */  
    public View getView(int position, View convertView, ViewGroup parent) {   
        // TODO Auto-generated method stub   
        //自定义视图   
        ListItemView  listItemView = null;   
        CallLog cl = listItems.get(position);
        if (convertView == null) {   
            listItemView = new ListItemView();    
            //获取list_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.call_history_item, null);   
            //获取控件对象   
            listItemView.name = (TextView)convertView.findViewById(R.id.call_history_name);   
            listItemView.time = (TextView)convertView.findViewById(R.id.call_history_time);  
            listItemView.type = (TextView)convertView.findViewById(R.id.call_history_type);  
            listItemView.duration = (TextView)convertView.findViewById(R.id.call_history_duration);  
            //设置控件集到convertView   
            convertView.setTag(listItemView);   
        }else {   
            listItemView = (ListItemView)convertView.getTag();   
        }   
        
        String contactName = cl.getName();
        if(contactName == null || contactName.length() == 0){
            listItemView.name.setText(cl.getNumber());
        }else{
            listItemView.name.setText(contactName);
        }
        listItemView.time.setText(cl.getTime());
        listItemView.type.setText(cl.getType());
        listItemView.duration.setText(cl.getDuration());
           
        return convertView;   
    }   
}  