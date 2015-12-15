package com.search.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.goodocom.gocsdk.R;
import com.tchip.call.MainActivity;
import com.tchip.contact.CallLog;
import com.tchip.contact.Contact;
import com.tchip.indexablelistview.StringMatcher;
import com.tchip.util.HanyuToPingyin;

/**
 * 自定义Adapter
 */
public class ContactListAdapter extends ArrayAdapter<Contact> implements SectionIndexer {

	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	List<Contact> list;
	int textViewResourceId;
	Context context;

	public ContactListAdapter(Context context, int textViewResourceId, List<Contact> list) {
		super(context, textViewResourceId, list);
		this.list = list;
		this.textViewResourceId = textViewResourceId;
		this.context = context;
	}

	@Override
	public int getCount() {
		// 需要遍历集合的size
		if(list != null)
			return list.size();
		else 
			return 0;
	}

	@Override
	public Contact getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
    
    /*public void reSetAdapter(List<Contact> list){
    	this.list = list;
    	notifyDataSetChanged();
    }*/

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// View.inflate对布局XML文件进行填充，获得View对象（相当于这个R.layout.activity_main）
		// 参数1：容器的Context 参数2：表示填充的哪个布局文件XML
		// 参数3：表示具体填充到哪个容器中，这里由Adapter指定，所以填null
		View v = View.inflate(context, R.layout.contact_list, null);
		// tv.findViewById(R.id.id))获得填充后的布局文件中的具体哪个ID的对象，并赋值
		if(list != null && list.size() > position){
			((TextView) v.findViewById(R.id.name)).setText(list.get(position).getName());
	
			String str = list.get(position).getPhone();
			// str =
			// str.replaceFirst(tv.getText().toString(),"<font color=#5db43b>"+tv.getText().toString()+"</font>");
			// 测试一下简单的字体样式
			//String html = "<html><head><title>TextView 使用HTML</title></head><body><p><strong>强 调</strong></p><p><em>斜体</em></p>"
			//		+ "<p><a href=\"http://www.dreamdu.com /xhtml/\">超链接HTML入门</a>学习HTML!< /p><p><font color=\"#aabb00\">颜色1"
			//		+ "</p><p><font color=\"#00bbaa \">颜色2</p><h1>标题1</h1><h3>标题2< /h3><h6>标题3</h6><p>大于>小于<</p><p>"
			//		+ "下面是网络图片</p><img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/></body></html>";
			// ((TextView)v.findViewById(R.id.phone)).setText(Html.fromHtml(str));
			((TextView) v.findViewById(R.id.phone)).setText(str);
		}
		return v;
	}

	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be
		// selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher.match(String
								.valueOf(((Contact) getItem(j)).getName()
										.charAt(0)), String.valueOf(k)))
							return j;
					}
				} else {
					String name = HanyuToPingyin
							.converterToFirstSpell(((Contact) getItem(j))
									.getName());
					if (StringMatcher.match(String.valueOf(name.charAt(0)),
							String.valueOf(mSections.charAt(i)).toLowerCase()))
						return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

}