package com.hk.lang_data_manager.utils.sortedcontact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hk.lang_data_manager.R;
import com.hk.lang_data_manager.utils.SingletonRequestQueue;

import java.util.List;

/**
 * @Description:用来处理集合中数据的显示与排序
 * @author http://blog.csdn.net/finddreams
 */ 
public class SortAdapter_test extends  RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, SectionIndexer {
	private final LayoutInflater inflater;
	private List<SortModel> list = null;
	private RecyclerView mRecyclerView;

	public SortAdapter_test(Context mContext, List<SortModel> list) {
		this.list = list;
		inflater = LayoutInflater.from(mContext);
	}
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		mRecyclerView = recyclerView;
	}


	public int getCount() {
		return this.list.size();
	}



	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = null;
		itemView = inflater.inflate(R.layout.phone_constacts_item2, parent, false);
		itemView.setOnClickListener(this);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final SortModel mContent = list.get(position);
		TextView tvTitle = (TextView) holder.getView(R.id.title);
		TextView tvLetter = (TextView) holder.getView(R.id.catalog);
		ImageView icon = (ImageView) holder.getView(R.id.icon);
		int section = getSectionForPosition(position);


		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			tvLetter.setVisibility(View.VISIBLE);
			tvLetter.setText(mContent.getSortLetters());
		}else{
			tvLetter.setVisibility(View.GONE);
		}
		tvTitle.setText(this.list.get(position).getName());
		SingletonRequestQueue.getInstance().loadImageByVolley(icon,this.list.get(position).getUrl());
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return getCount();
	}



	@Override
	public void onClick(View v) {
		int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
		if(getDataListener!=null) {
			getDataListener.onGetData(list.get(childAdapterPosition).getPhone(),list.get(childAdapterPosition).getId(),list.get(childAdapterPosition).getMemo(),list.get(childAdapterPosition).getName(),list.get(childAdapterPosition).getUrl());
		}

	}

	public interface GetDataListener{
		void onGetData(String phone, String id, String memo, String url, String name);

	}

	GetDataListener getDataListener;
	public void addOnItemClick(GetDataListener getDataListener){
		this.getDataListener=getDataListener;
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


	@Override
	public Object[] getSections() {
		return null;
	}
}