package com.hyphenate.easemob.easeui.widget.listview.check;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easemob.R;
import com.hyphenate.util.DensityUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public class KylinCheckListView<T extends CheckListEntity> extends FrameLayout implements KylinCheckListImpl {

    // 单选与多选
    public static final int RADIO = 0;
    public static final int MULTISELECT = 1;
    public static final int NONE = 2;
    // 显示左/显示右
    public static final int CHECKLEFT = 0;
    public static final int CHECKRIGHT = 1;
    public static final int CHECKNONE = 2;

    // 列表控件
    protected RecyclerView mRecyclerView;
    // 显示位置
    protected int showLocation = CHECKNONE;
    // 单选/多选 ( 默认单选 )
    protected int showType = NONE;
    // 记录单选时的坐标
    protected int radioIndex = -1;
    // 记录多选的数量
    protected int multCount = 0;
    // 子布局
    protected Class<?> itemClass;
    // 数据
    protected List<T> datalist = new ArrayList<T>();
    // 适配器
    protected KylinCheckListAdapter mAdapter;
    // 监听点击
    protected KylinOnCheckListener kylinOnCheckListener;


    /**
     * 内部控件属性
     */
    protected float cbMarginLeft = 0;
    protected float cbMarginRight = 0;
    protected float cbMarginTop = 0;
    protected float cbMarginBottom = 0;
    protected float cbWidth = 0;
    protected float cbHeight = 0;
    protected int cbBackgroup = 0;
    protected int itemBackgroup = 0;
    protected int itemGravity;
    protected float itemDecoration = 0;

    // 单选情况下的观察者
    private Action1<Integer> radioCheckAct = new Action1<Integer>() {

        @Override
        public void call(Integer integer) {
            // 判断边界
            if (integer >= datalist.size()) {
                return;
            }

            // 重复选的话别浪费时间去刷新
            if (integer == radioIndex) {
                return;
            }

            if (radioIndex != -1 && radioIndex < datalist.size()) {
                datalist.get(radioIndex).isCheck = false;
            }
            datalist.get(integer).isCheck = true;
            radioIndex = integer;

            updataAdapter();

            if (kylinOnCheckListener != null) {
                kylinOnCheckListener.kylinCheckChange(radioIndex, true);
            }

        }
    };

    // 多选情况下的观察者
    private Action1<Integer> multCheckAct = new Action1<Integer>() {
        @Override
        public void call(Integer integer) {
            // 判断边界
            if (integer >= datalist.size()) {
                return;
            }
            datalist.get(integer).isCheck = !(datalist.get(integer).isCheck);

            if (datalist.get(integer).isCheck) {
                multCount++;
            } else {
                multCount--;
            }

            updataAdapter(); // todo 优化成局部刷新

            if (kylinOnCheckListener != null) {
                kylinOnCheckListener.kylinCheckChange(radioIndex, datalist.get(integer).isCheck);
            }

        }
    };

    public KylinCheckListView(Context context) {
        super(context);
    }

    public KylinCheckListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public KylinCheckListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray type = getContext().obtainStyledAttributes(attrs, R.styleable.KylinCheckListViewStyle);

        cbMarginLeft = type.getDimension(R.styleable.KylinCheckListViewStyle_cb_margin_left,
                DensityUtil.dip2px(getContext(), 0));
        cbMarginRight = type.getDimension(R.styleable.KylinCheckListViewStyle_cb_margin_right,
                DensityUtil.dip2px(getContext(), 0));
        cbMarginTop = type.getDimension(R.styleable.KylinCheckListViewStyle_cb_margin_top,
                DensityUtil.dip2px(getContext(), 0));
        cbMarginBottom = type.getDimension(R.styleable.KylinCheckListViewStyle_cb_margin_bottom,
                DensityUtil.dip2px(getContext(), 0));
        cbWidth = type.getDimension(R.styleable.KylinCheckListViewStyle_cb_width,
                DensityUtil.dip2px(getContext(), 0));
        cbHeight = type.getDimension(R.styleable.KylinCheckListViewStyle_cb_height,
                DensityUtil.dip2px(getContext(), 0));
        cbBackgroup = type.getResourceId(R.styleable.KylinCheckListViewStyle_cb_backgroup, 0);
        itemBackgroup = type.getResourceId(R.styleable.KylinCheckListViewStyle_item_backgroup, 0);
        itemGravity = type.getResourceId(R.styleable.KylinCheckListViewStyle_item_gravity, 0);
        itemDecoration = type.getDimension(R.styleable.KylinCheckListViewStyle_item_decoration,
                DensityUtil.dip2px(getContext(), 0));
        showLocation = type.getInt(R.styleable.KylinCheckListViewStyle_show_location, CHECKLEFT);
        showType = type.getInt(R.styleable.KylinCheckListViewStyle_show_type, RADIO);

        type.recycle();
    }

    /**
     * 初始化操作
     */
    public void create() {
        initView();
        initAdapter();
    }

    /**
     * 初始化View
     */
    protected void initView() {
        mRecyclerView = initList();
        this.addView(mRecyclerView);
    }

    /**
     * 初始化RecyclerView
     */
    protected RecyclerView initList() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(lp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // todo 自己自定义一个ItemDecoration
//        recyclerView.addItemDecoration(new BerItemDecoration((int) itemDecoration));
        return recyclerView;
    }


    /**
     * 初始化Adapter
     */
    protected void initAdapter() {
        mAdapter = new KylinCheckListAdapter();
    }

    /**
     * 更新adapter
     */
    public void updataAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(List<T> datas) {
        datalist.addAll(datas);
        updataAdapter();
    }

    /**
     * 添加数据
     */
    public void addData(T data) {
        datalist.add(data);
        updataAdapter();
    }

    /**
     * 删除
     */
    public void removeData(int position) {
        datalist.remove(position);
        updataAdapter();
    }

    public T getData(int position) {
        if (position >= datalist.size()) {
            return null;
        }
        return datalist.get(position);
    }


    private void updataAdapterItem(int position) {
        mAdapter.notifyItemChanged(position);
    }

    /**
     * 设置数据
     */
    public void setDataToView(List<T> datalist) {
        this.datalist = datalist;
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 单选的结果
     */
    @Override
    public int radioResult() {
        return radioIndex;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        if (mAdapter != null) {
            mAdapter.setItemClickListener(listener);
        }
    }

    public void setOnItemLongClickListener(OnLongItemClickListener listener) {
        if (mAdapter != null) {
            mAdapter.setItemLongClickListener(listener);
        }
    }


    /**
     * 多选的结果
     */
    @Override
    public List<Integer> multResults() {
        List<Integer> resultList = new ArrayList<>();
        for (int i = 0; i < datalist.size(); i++) {
            if (datalist.get(i).isCheck && datalist.get(i).enabled) {
                resultList.add(i);
            }
        }
        return resultList;
    }

    /**
     * 多选时已选择的数量 (可以用这个数量来判断是否全选)
     */
    @Override
    public int multCount() {
        int count = 0;
        for (int i = 0; i < datalist.size(); i++) {
            if (datalist.get(i).isCheck) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断是否全选
     */
    @Override
    public boolean isMultAll() {
        return multCount == datalist.size();
    }

    /**
     * 全选/全不选
     */
    @Override
    public void multAllCheck(boolean state) {
        for (int i = 0; i < datalist.size(); i++) {
            if (datalist.get(i).enabled) {
                datalist.get(i).isCheck = state;
            }
        }

        if (state) {
            multCount = datalist.size();
        } else {
            multCount = 0;
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 单选
     */
    @Override
    public void radioCheck(int position) {
        Observable.just(position).subscribe(radioCheckAct);
    }

    /**
     * 选择
     */
    @Override
    public void check(int position) {
        if (position < datalist.size()) {
            datalist.get(position).isCheck = !datalist.get(position).isCheck;
            if (datalist.get(position).isCheck) {
                multCount++;
            } else {
                multCount--;
            }
        }
    }

    /**
     * 懒更新
     */
    @Override
    public void lazyUpdate() {
        mAdapter.notifyDataSetChanged();
    }


    /**
     * RecyclerView 的Adapter
     */
    public class KylinCheckListAdapter extends RecyclerView.Adapter<KylinCheckListViewHolder> {

        private OnItemClickListener mItemClickListener;
        private OnLongItemClickListener mLongClickListener;

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        public void setItemLongClickListener(OnLongItemClickListener longClickListener) {
            mLongClickListener = longClickListener;
        }


        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public KylinCheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout itemLayout = initChildLayout();
            KylinCheckListViewHolder viewHolder = new KylinCheckListViewHolder(itemLayout);

            return viewHolder;
        }

        protected LinearLayout initChildLayout() {
            LinearLayout itemLayout = new LinearLayout(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // todo 可以在此设置padding等操作
            itemLayout.setLayoutParams(lp);
            itemLayout.setGravity(itemGravity);
            itemLayout.setBackgroundResource(itemBackgroup);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            return itemLayout;
        }

        @Override
        public void onBindViewHolder(@NonNull KylinCheckListViewHolder holder, final int position) {
            holder.setPosition(position);
            holder.setData(datalist.get(position));

            // 设置点击和长按事件
            if (mItemClickListener != null) {
                holder.itemView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(position);
                    }
                });
            }

            if (mLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return mLongClickListener.onLongClick(position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnLongItemClickListener {
        boolean onLongClick(int position);
    }


    /**
     * RecyclerView 的ViewHolder
     */
    public class KylinCheckListViewHolder extends RecyclerView.ViewHolder {

        protected CheckBox mCheckBox;
        protected T data;
        protected int position;
        protected CheckViewModel mViewModel;

        public KylinCheckListViewHolder(View itemView) {
            super(itemView);
            initView();
        }

        private void initView() {
            mCheckBox = initCheckBox();
            mViewModel = initViewModel();
            if (itemView instanceof LinearLayout) {
                LinearLayout llContent = (LinearLayout) itemView;
                // 没有核心布局的话没必要线束布局
                if (mViewModel != null) {
                    View contentChild = mViewModel.getContentView();
                    ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    contentChild.setLayoutParams(lp);
                    LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cbLp.gravity = Gravity.CENTER_VERTICAL;

                    if (showLocation == CHECKLEFT) {
                        llContent.addView(mCheckBox, cbLp);
                        llContent.addView(contentChild);
                    } else if (showLocation == CHECKRIGHT) {
                        llContent.addView(contentChild);

                        LinearLayout rightLayout = new LinearLayout(getContext());
                        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rightLayout.setOrientation(LinearLayout.VERTICAL);
                        rightParams.gravity = Gravity.CENTER_VERTICAL;
                        rightLayout.setGravity(Gravity.RIGHT);
                        rightLayout.setLayoutParams(rightParams);
//                        rightLayout.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.dp_15), 0);
                        rightLayout.addView(mCheckBox);

                        llContent.addView(rightLayout);
                        llContent.setBackgroundColor(Color.WHITE);
                    } else if (showLocation == CHECKNONE) {
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        llContent.addView(contentChild);
                    }

                }
            }
        }


        private CheckBox initCheckBox() {
            CheckBox checkBox = new CheckBox(getContext());
            LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cbLp.setMargins((int) cbMarginLeft, (int) cbMarginTop, (int) cbMarginRight, (int) cbMarginBottom);
            if (cbBackgroup != 0) {
                checkBox.setButtonDrawable(null);
                if(0 != cbHeight){
                    checkBox.setHeight((int)cbHeight);
                }else{
                    checkBox.setHeight(getResources().getDimensionPixelSize(R.dimen.dp_60));
                }
                if(0 != cbWidth){
                    checkBox.setWidth((int)cbWidth);
                }else{
                    checkBox.setWidth(getResources().getDimensionPixelSize(R.dimen.dp_60));
                }
            }
            checkBox.setLayoutParams(cbLp);
            checkBox.setBackgroundResource(cbBackgroup);

            checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showType == RADIO) {
                        // 单选情况
                        Observable.just(position).subscribe(radioCheckAct);
                    } else if (showType == MULTISELECT) {
                        Observable.just(position).subscribe(multCheckAct);
                    }
                }
            });
            return checkBox;
        }

        private CheckViewModel initViewModel() {
            try {
                // todo 添加Bundle情况的反射
                Class[] paramTypes = new Class[]{Context.class};
                Object[] params = new Object[]{getContext()};
                Constructor con = itemClass.getConstructor(paramTypes);
                mViewModel = (CheckViewModel) con.newInstance(params);
                return mViewModel;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }


        public void setData(T data) {
            this.data = data;
            // 设置Item数据
            if (mViewModel != null) {
                mViewModel.setData(data);
            }
            // 设置选框状态
            if (mCheckBox != null) {
                mCheckBox.setChecked(data.isCheck);
                mCheckBox.setEnabled(data.enabled);
            }
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    /**
     * get and set
     */
    public void setCbMarginLeft(float cbMarginLeft) {
        this.cbMarginLeft = cbMarginLeft;
    }

    public void setCbMarginRight(float cbMarginRight) {
        this.cbMarginRight = cbMarginRight;
    }

    public void setCbMarginTop(float cbMarginTop) {
        this.cbMarginTop = cbMarginTop;
    }

    public void setCbMarginBottom(float cbMarginBottom) {
        this.cbMarginBottom = cbMarginBottom;
    }

    public void setCbBackgroup(int cbBackgroup) {
        this.cbBackgroup = cbBackgroup;
    }

    public void setItemBackgroup(int itemBackgroup) {
        this.itemBackgroup = itemBackgroup;
    }

    public void setItemGravity(int itemGravity) {
        this.itemGravity = itemGravity;
    }

    public void setItemDecoration(float itemDecoration) {
        this.itemDecoration = itemDecoration;
    }

    // 返回列表
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    // 设置Item的布局类
    public void setItemClass(Class<?> itemClass) {
        this.itemClass = itemClass;
    }

    public void setKylinOnCheckListener(KylinOnCheckListener kylinOnCheckListener) {
        this.kylinOnCheckListener = kylinOnCheckListener;
    }

    public List<T> getDatalist() {
        return datalist;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public void setShowLocation(int showLocation) {
        this.showLocation = showLocation;
    }


}
