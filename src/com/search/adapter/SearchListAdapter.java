package com.search.adapter;

import java.util.ArrayList;

import org.apache.http.impl.conn.DefaultClientConnection;

import com.goodocom.gocsdk.R;
import com.goodocom.gocsdk.service.GocsdkService;
import com.search.util.ContactInfo;
import com.search.util.Dfine;
import com.search.util.Util;
import com.tchip.call.MainActivity;
import com.tchip.util.OperateCommand;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter implements Filterable {

	private Context mContext ;
	private LayoutInflater mInflater;
	public SearchListAdapter(Context mC){
		mInflater = LayoutInflater.from(mC);
		mContext = mC;
	}
	
	@Override
	public int getCount() {
		return Dfine.searchUser.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return Dfine.searchUser.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public View getView(int index, View view, ViewGroup parent) {
		view = mInflater.inflate(R.layout.list_item, null);
		HolderView hv = new HolderView();
		hv.nameView = (TextView)view.findViewById(R.id.contact_name);
		hv.bodyView = (TextView)view.findViewById(R.id.contact_body);
		hv.phoneView = (TextView)view.findViewById(R.id.contact_phone);
		
		ContactInfo item = (ContactInfo)getItem(index);
		view.setTag(item.number);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = v.getTag().toString();
				if(number != null && number.length() > 0){
					Intent intent = new Intent(mContext,GocsdkService.class);  
					intent.putExtra("command", OperateCommand.CALL_DEAIL+number);
				    mContext.startService(intent);
				}
			}
		});
		hv.nameView.setText(item.name);
		
		switch(item.index){
		case 0:
		case 1:{
			hv.phoneView.setText(item.number);
			hv.bodyView.setText(Util.formatHtml(item.lastNamePy, item.namePy,item.input, item.lastNameToNumber, item.index));
			break;
		}
		case 2:{
			hv.phoneView.setText(item.number);
			hv.bodyView.setText(Util.formatHtml(item.namePy, null,item.input, item.nameToNumber, item.index));
			break;
		} 
		case 3:{
			hv.phoneView.setText(Util.formatHtml(item.number,null,item.input,null,item.index));
			hv.bodyView.setText(item.namePy);
			break;
		}
		default :{
			hv.phoneView.setText(item.number);
			hv.bodyView.setText(item.namePy);
			break;
		}
		}
		
		return view;
	}

	class HolderView{
		public TextView nameView;
		public TextView bodyView ;
		public TextView phoneView ;
	}
	
	
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				Dfine.searchUser.clear();
				ArrayList<ContactInfo> list = (ArrayList<ContactInfo>)results.values;
				if(list != null && list.size() > 0){
					Dfine.searchUser.addAll(list);
					notifyDataSetChanged();
				}else{
					notifyDataSetInvalidated();
				}
			}
			
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				String input = constraint.toString();
				FilterResults results = new FilterResults();
				ArrayList<ContactInfo> list = new ArrayList<ContactInfo>();
				boolean py = false;
				for(ContactInfo item : Dfine.user){
					item.input = input;
					if((item.name.equals(input) || item.name.contains(input)) && !list.contains(item)){
						list.add(item);
					}else if(item.lastNamePy.contains(input) && !list.contains(item)){
						list.add(item);
					}else if(item.namePy.contains(input) && !list.contains(item)){
						list.add(item);
					}else if(item.lastNameToNumber.equals(input) && !list.contains(item)){
						item.index = 0;
						list.add(item);
					}else if(item.lastNameToNumber.contains(input) && !list.contains(item)){
						item.index = 1;
						list.add(item);
					}else if(item.nameToNumber.contains(input) && !list.contains(item)){
						char[] chars = item.lastNameToNumber.toCharArray();
						for(char c : chars){
							if(c == input.toCharArray()[0] && !list.contains(item)){
								item.index = 2;
								item.matchIndex = item.nameToNumber.indexOf(input);
								list.add(item);
								py = true;
							}
						}
					}else if(item.number.contains(input) && !list.contains(item)){
						item.index = 3;
						list.add(item);
					}
				}
				//������˳������
				for (int i = 0; i < list.size(); ++i) {
					for (int j = 0; j < list.size() - i - 1; ++j) {
						if (list.get(j).index > list.get(j + 1).index) {
							ContactInfo temp = list.get(j + 1);
							list.set(j + 1 , list.get(j));
							list.set(j,temp);
						}
					}
				}
				//ȫƴ��ƥ��˳������
				if(py){
					for (int i = 0; i < list.size(); ++i) {
						for (int j = 0; j < list.size() - i - 1; ++j) {
							if(list.get(j).index == 2 && list.get(j + 1).index == 2){
								if (list.get(j).matchIndex > list.get(j + 1).matchIndex) {
									ContactInfo temp = list.get(j + 1);
									list.set(j + 1 , list.get(j));
									list.set(j,temp);
								}
							}
						}
					}
				}
				
				results.count = list.size();
				results.values = list;
				return results;
			}
		};
		return filter;
	}

}
