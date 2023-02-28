package com.hyphenate.easemob.easeui.widget.listview.check;

import java.util.List;

/**
 * 和外界交互的东西
 */
public interface KylinCheckListImpl {

    /**
     *  单选的结果
     */
    int radioResult();
    /**
     *  多选的结果
     */
    List<Integer> multResults();
    /**
     *  多选时已选择的数量
     */
    int multCount();
    /**
     *  判断是否全选
     */
    boolean isMultAll();
    /**
     *  全选/全不选
     */
    void multAllCheck(boolean state);
    /**
     *  单选
     */
    void radioCheck(int position);
    /**
     *  设置选择
     */
    void check(int position);
    /**
     *  懒更新
     */
    void lazyUpdate();

}
