package com.hyphenate.easemob.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hyphenate.easemob.R;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Extend menu when user want send image, voice clip, etc
 *
 */
public class EaseChatExtendMenu extends ViewPager {

    protected Context context;
    private List<ChatMenuItemModel> itemModels = new ArrayList<ChatMenuItemModel>();
    private int numColumns;
    private int stretchMode;
    private int gravity;
    private int verticalSpacing;
    private List<GridView> viewList;

    public EaseChatExtendMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatExtendMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatExtendMenu(Context context) {
        super(context);
        init(context, null);
    }
    
    private void init(Context context, AttributeSet attrs){
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatExtendMenu);
        int numColumns = ta.getInt(R.styleable.EaseChatExtendMenu_numColumns, 4);
        ta.recycle();

        this.numColumns = numColumns;
        stretchMode = GridView.STRETCH_COLUMN_WIDTH;
        gravity = Gravity.CENTER_VERTICAL;
        verticalSpacing = getResources().getDimensionPixelSize(R.dimen.dp_8);
    }
    
    /**
     * init
     */
    public void init(){
        int num = itemModels.size()/8;
        if(itemModels.size()%8 > 0){
            num = num +1;
        }
        viewList = new ArrayList<>();
        for(int i = 1; i <= num; i ++){
            GridView gridView = new GridView(context);
            gridView.setNumColumns(numColumns);
            gridView.setStretchMode(stretchMode);
            gridView.setGravity(gravity);
            gridView.setVerticalSpacing(verticalSpacing);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = DensityUtil.dip2px(context, 180);
            gridView.setLayoutParams(params);
            if(itemModels.size() >= i*8){
                gridView.setAdapter(new ItemAdapter(context, itemModels.subList(i*8 - 8, i*8)));
            } else {
                gridView.setAdapter(new ItemAdapter(context, itemModels.subList(i*8 - 8, itemModels.size())));
            }
            viewList.add(gridView);
        }
        PagerAdapter pagerAdapter = new PagerAdapter(){

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override  //从当前container中删除指定位置（position）的View;
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(viewList.get(position));
            }

            @NonNull
            @Override  //第一：将当前视图添加到container中，第二：返回当前View
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        setAdapter(pagerAdapter);
    }
    
    /**
     * register menu item
     * 
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerMenuItem(String name, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        ChatMenuItemModel item = new ChatMenuItemModel();
        item.name = name;
        item.image = drawableRes;
        item.id = itemId;
        item.clickListener = listener;
        itemModels.add(item);
    }
    
    /**
     * register menu item
     * 
     * @param nameRes
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *             on click event of item
     */
    public void registerMenuItem(int nameRes, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        registerMenuItem(context.getString(nameRes), drawableRes, itemId, listener);
    }
    
    
    private class ItemAdapter extends ArrayAdapter<ChatMenuItemModel>{

        private Context context;

        public ItemAdapter(Context context, List<ChatMenuItemModel> objects) {
            super(context, 0, objects);
            this.context = context;
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ChatMenuItem menuItem = null;
            if(convertView == null){
                convertView = new ChatMenuItem(context);
            }
            menuItem = (ChatMenuItem) convertView;
            menuItem.setImage(getItem(position).image);
            menuItem.setText(getItem(position).name);
            menuItem.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if(getItem(position).clickListener != null){
                        getItem(position).clickListener.onClick(getItem(position).id, v);
                    }
                }
            });
            return convertView;
        }
        
        
    }
    
    
    public interface EaseChatExtendMenuItemClickListener{
        void onClick(int itemId, View view);
    }
    
    
    class ChatMenuItemModel{
        String name;
        int image;
        int id;
        EaseChatExtendMenuItemClickListener clickListener;
    }
    
    class ChatMenuItem extends LinearLayout {
        private ImageView imageView;
        private TextView textView;

        public ChatMenuItem(Context context, AttributeSet attrs, int defStyle) {
            this(context, attrs);
        }

        public ChatMenuItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        public ChatMenuItem(Context context) {
            super(context);
            init(context, null);
        }

        private void init(Context context, AttributeSet attrs) {
            LayoutInflater.from(context).inflate(R.layout.ease_chat_menu_item, this);
            imageView = (ImageView) findViewById(R.id.image);
            textView = (TextView) findViewById(R.id.text);
        }

        public void setImage(int resid) {
            imageView.setBackgroundResource(resid);
        }

        public void setText(int resid) {
            textView.setText(resid);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}
