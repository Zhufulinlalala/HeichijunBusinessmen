package com.hk.lang_data_manager.utils.sortedcontact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hk.lang_data_manager.R;
import java.util.List;

/**
 * @Description:用来处理集合中数据的显示与排序
 * @author http://blog.csdn.net/finddreams
 */ 
public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> list = null;
	private Context mContext;
	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
	}
	SortAdapter_test.GetDataListener getDataListener;
	public void addOnItemClick(SortAdapter_test.GetDataListener getDataListener){
		this.getDataListener=getDataListener;
	}
	public void addOnItemLongClick(SortAdapter_test.GetDataListener getDataListener){
		this.getDataListener=getDataListener;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.phone_constacts_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
		//	viewHolder.devider=view.findViewById(R.id.devider);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
	//		viewHolder.devider.setVisibility(View.GONE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
	//		viewHolder.devider.setVisibility(View.VISIBLE);
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		Glide.with(mContext).load(this.list.get(position).getUrl()).into(viewHolder.icon);
		//SingletonRequestQueue.getInstance().loadImageByVolleyRound(viewHolder.icon,this.list.get(position).getUrl());
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(getDataListener!=null) {
					getDataListener.onGetData(list.get(position).getUrl(),list.get(position).getId(),list.get(position).getMemo(),list.get(position).getName1(),list.get(position).getPhone());
				}
			}
		});
		return view;
	}



	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView icon;
		View devider;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}