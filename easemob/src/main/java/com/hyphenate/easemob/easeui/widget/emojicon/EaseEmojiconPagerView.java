package com.hyphenate.easemob.easeui.widget.emojicon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.adapter.EmojiconGridAdapter;
import com.hyphenate.easemob.easeui.adapter.EmojiconPagerAdapter;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon.Type;
import com.hyphenate.easemob.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.widget.sticker.widget.StickerGridView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EaseEmojiconPagerView extends ViewPager{

    private Context context;
    private List<EaseEmojiconGroupEntity> groupEntities;

    private PagerAdapter pagerAdapter;
    
    private int emojiconRows = 3;
    private int emojiconColumns = 7;
    
    private int bigEmojiconRows = 2;
    private int bigEmojiconColumns = 4;
    
    private int firstGroupPageSize;
    
    private int maxPageCount;
    private int previousPagerPosition;
	private EaseEmojiconPagerViewListener pagerViewListener;
    private List<View> viewpages; 

    public EaseEmojiconPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public EaseEmojiconPagerView(Context context) {
        this(context, null);
    }
    
    
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList, int emijiconColumns, int bigEmojiconColumns){
        if(emojiconGroupList == null){
            throw new RuntimeException("emojiconGroupList is null");
        }
        
        this.groupEntities = emojiconGroupList;
        this.emojiconColumns = emijiconColumns;
        this.bigEmojiconColumns = bigEmojiconColumns;
        
        viewpages = new ArrayList<View>();
        for(int i = 0; i < groupEntities.size(); i++){
            EaseEmojiconGroupEntity group = groupEntities.get(i);
            List<EaseEmojicon> groupEmojicons = group.getEmojiconList();
            List<View> gridViews = getGroupGridViews(group);
            if(i == 0){
                firstGroupPageSize = gridViews.size();
            }
            maxPageCount = Math.max(gridViews.size(), maxPageCount);
            viewpages.addAll(gridViews);
        }
        
        pagerAdapter = new EmojiconPagerAdapter(viewpages);
        setAdapter(pagerAdapter);
        setOnPageChangeListener(new EmojiPagerChangeListener());
//	    setOffscreenPageLimit(0);
        if(pagerViewListener != null){
            pagerViewListener.onPagerViewInited(maxPageCount, firstGroupPageSize);
        }
    }


    public void reset(){
	    groupEntities.clear();
	    viewpages.clear();
	    if (pagerAdapter != null){
		    ((EmojiconPagerAdapter)pagerAdapter).clear();
	    }
	    Iterator<StickerGridView> iterator = gridViews.iterator();
	    while (iterator.hasNext()){
	    	View view = iterator.next();
	    	removeView(view);
		    iterator.remove();
	    }
    }


    public void setPagerViewListener(EaseEmojiconPagerViewListener pagerViewListener){
    	this.pagerViewListener = pagerViewListener;
    }
    
    
    /**
     * set emojicon group position
     * @param position
     */
    public void setGroupPostion(int position){
    	if (getAdapter() != null && position >= 0 && position < groupEntities.size()) {
            int count = 0;
            for (int i = 0; i < position; i++) {
                count += getPageSize(groupEntities.get(i));
            }
            setCurrentItem(count);
        }
    }

    private List<StickerGridView> gridViews = new ArrayList<>();
    
    /**
     * get emojicon group gridview list
     * @param groupEntity
     * @return
     */
    public List<View> getGroupGridViews(EaseEmojiconGroupEntity groupEntity){
        List<EaseEmojicon> emojiconList = groupEntity.getEmojiconList();
        int itemSize = emojiconColumns * emojiconRows -1;
        int totalSize = emojiconList.size();
        Type emojiType = groupEntity.getType();
        if(emojiType == Type.BIG_EXPRESSION || emojiType == Type.STICKER){
            itemSize = bigEmojiconColumns * bigEmojiconRows;
        }
        int pageSize = totalSize % itemSize == 0 ? totalSize/itemSize : totalSize/itemSize + 1;   
        List<View> views = new ArrayList<View>();

        for(int i = 0; i < pageSize; i++){
            View view = LayoutInflater.from(context).inflate(R.layout.ease_expression_gridview, this, false);
	        StickerGridView gv = (StickerGridView) view.findViewById(R.id.gridview);
	        gridViews.add(gv);
            if(emojiType == Type.BIG_EXPRESSION || emojiType == Type.STICKER){
                gv.setNumColumns(bigEmojiconColumns);
            }else{
                gv.setNumColumns(emojiconColumns);
            }
            List<EaseEmojicon> list = new ArrayList<EaseEmojicon>();
            if(i != pageSize -1){
                list.addAll(emojiconList.subList(i * itemSize, (i+1) * itemSize));
            }else{
                list.addAll(emojiconList.subList(i * itemSize, totalSize));
            }
	        if(emojiType == Type.NORMAL){
		        EaseEmojicon deleteIcon = new EaseEmojicon();
		        deleteIcon.setEmojiText(EaseSmileUtils.DELETE_KEY);
		        deleteIcon.setType(Type.NORMAL);
		        deleteIcon.setIcon(R.drawable.ease_icon_emoji_delete);
		        list.add(deleteIcon);
	        }
            final EmojiconGridAdapter gridAdapter = new EmojiconGridAdapter(context, 1, list);
            gv.setAdapter(gridAdapter);
            gv.setSingleTapClick(new StickerGridView.SingleTapClickListener() {
	            @Override
	            public void onSingleTap(EaseEmojicon easeEmojicon) {
		            if(pagerViewListener != null){
			            String emojiText = easeEmojicon.getEmojiText();
			            if(emojiText != null && emojiText.equals(EaseSmileUtils.DELETE_KEY)){
				            pagerViewListener.onDeleteImageClicked();
			            }else if (emojiText != null && emojiText.equals(EaseSmileUtils.ADD_KEY)){
				            pagerViewListener.onAddImageClicked();
			            }else{
				            pagerViewListener.onExpressionClicked(easeEmojicon);
			            }

		            }
	            }
            });
            views.add(view);
        }
        return views;
    }
    

    /**
     * add emojicon group
     * @param groupEntity
     */
    public void addEmojiconGroup(EaseEmojiconGroupEntity groupEntity, boolean notifyDataChange) {
        int pageSize = getPageSize(groupEntity);
        if(pageSize != maxPageCount){
            maxPageCount = pageSize;
            if(pagerViewListener != null && pagerAdapter != null){
                pagerViewListener.onGroupMaxPageSizeChanged(maxPageCount);
            }
        }
        viewpages.addAll(getGroupGridViews(groupEntity));
        if(pagerAdapter != null && notifyDataChange){
            pagerAdapter.notifyDataSetChanged();
            postInvalidate();
        }
    }

    public void replaceEmojiconGroup(int position, EaseEmojiconGroupEntity groupEntity){
    	groupEntities.set(position, groupEntity);
    	int pageSize = getPageSize(groupEntity);
    	if (pageSize > maxPageCount){
    		maxPageCount = pageSize;
    		if (pagerViewListener != null && pagerAdapter != null){
    			pagerViewListener.onGroupMaxPageSizeChanged(maxPageCount);
		    }
	    }
	    viewpages.remove(viewpages.size() - 1);
//	    viewpages.clear();
	    viewpages.addAll(getGroupGridViews(groupEntity));
    	if (pagerAdapter != null){
    		pagerAdapter.notifyDataSetChanged();
	    }
    }
    
    /**
     * remove emojicon group
     * @param position
     */
    public void removeEmojiconGroup(int position){
        if(position > groupEntities.size() - 1){
            return;
        }
        groupEntities.remove(position);

        if(pagerAdapter != null){
            pagerAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * get size of pages
     * @param groupEntity
     * @return
     */
    private int getPageSize(EaseEmojiconGroupEntity groupEntity) {
        List<EaseEmojicon> emojiconList = groupEntity.getEmojiconList();
        int itemSize = emojiconColumns * emojiconRows -1;
        int totalSize = emojiconList.size();
        Type emojiType = groupEntity.getType();
        if(emojiType == Type.BIG_EXPRESSION || emojiType == Type.STICKER){
            itemSize = bigEmojiconColumns * bigEmojiconRows;
        }
        int pageSize = totalSize % itemSize == 0 ? totalSize/itemSize : totalSize/itemSize + 1;   
        return pageSize;
    }
    
    private class EmojiPagerChangeListener implements OnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
        	int endSize = 0;
        	int groupPosition = 0;
            for(EaseEmojiconGroupEntity groupEntity : groupEntities){
            	int groupPageSize = getPageSize(groupEntity);
            	//if the position is in current group
            	if(endSize + groupPageSize > position){
            		//this is means user swipe to here from previous page
            		if(previousPagerPosition - endSize < 0){
            			if(pagerViewListener != null){
            				pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
            				pagerViewListener.onGroupPagePostionChangedTo(0);
            			}
            			break;
            		}
            		//this is means user swipe to here from back page
            		if(previousPagerPosition - endSize >= groupPageSize){
            			if(pagerViewListener != null){
            				pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
            				pagerViewListener.onGroupPagePostionChangedTo(position - endSize);
            			}
            			break;
            		}
            		
            		//page changed
            		if(pagerViewListener != null){
            			pagerViewListener.onGroupInnerPagePostionChanged(previousPagerPosition-endSize, position-endSize);
            		}
            		break;
            		
            	}
            	groupPosition++;
            	endSize += groupPageSize;
            }
            
            previousPagerPosition = position;
        }
        
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }
    
    
    
    public interface EaseEmojiconPagerViewListener{
        /**
         * pagerview initialized
         * @param groupMaxPageSize --max pages size
         * @param firstGroupPageSize-- size of first group pages
         */
        void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize);
        
    	/**
    	 * group position changed
    	 * @param groupPosition--group position
    	 * @param pagerSizeOfGroup--page size of group
    	 */
    	void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup);
    	/**
    	 * page position changed
    	 * @param oldPosition
    	 * @param newPosition
    	 */
    	void onGroupInnerPagePostionChanged(int oldPosition, int newPosition);
    	
    	/**
    	 * group page position changed
    	 * @param position
    	 */
    	void onGroupPagePostionChangedTo(int position);
    	
    	/**
    	 * max page size changed
    	 * @param maxCount
    	 */
    	void onGroupMaxPageSizeChanged(int maxCount);
	    void onAddImageClicked();
    	void onDeleteImageClicked();
    	void onExpressionClicked(EaseEmojicon emojicon);
    	
    }

}
