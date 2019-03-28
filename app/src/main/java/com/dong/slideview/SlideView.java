package com.dong.slideview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pd
 * time     2019/3/28 08:10
 */
public class SlideView extends RelativeLayout {
    private View rootView;
    private Context context;
    private ExpandableListView.OnChildClickListener onChildClickListener;

    private RelativeLayout rl_center_letter;
    private TextView tv_center_letter;

    private RecyclerView rv_slide;
    private SlideAdapter slideAdapter;
    private List<String> slideList;

    private ExpandableListView expandableListView;
    private BaseExpandableListAdapter expandableListAdapter;
    private List<String> groupList;

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.slide_view, this);
        initView();
    }

    /**
     * 初始化控件相关
     */
    private void initView() {
        expandableListView = rootView.findViewById(R.id.expandable_list_view);
        rl_center_letter = rootView.findViewById(R.id.rl_center_letter);
        tv_center_letter = rootView.findViewById(R.id.tv_center_letter);
        rv_slide = rootView.findViewById(R.id.rv_slide);

        //屏蔽Group的点击展开和收起事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });

        rv_slide.setLayoutManager(new LinearLayoutManager(context));
        //处理滑动事件
        rv_slide.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    //根据触摸的位置，找到对应的子项
                    View child = recyclerView.findChildViewUnder(event.getX(), event.getY());
                    if (child != null) {
                        TextView textView = child.findViewById(R.id.tv_letter);
                        tv_center_letter.setText(textView.getText());
                        rl_center_letter.setVisibility(View.VISIBLE);
                        scrollTo(textView.getText().toString());//根据选中的字符串匹配列表中的位置，并且将其滚动到屏幕范围内

                        //设置背景色
                        recyclerView.setBackgroundColor(Color.parseColor("#68808080"));
                        Log.d("dong", "当前触摸:" + textView.getText().toString());
                    } else {
                        Log.d("dong", "没找到对应的child");
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    rl_center_letter.setVisibility(View.GONE);
                    recyclerView.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    //根据触摸的位置，找到对应的子项
                    View child = recyclerView.findChildViewUnder(event.getX(), event.getY());
                    if (child != null) {
                        TextView textView = child.findViewById(R.id.tv_letter);
                        tv_center_letter.setText(textView.getText());
                        rl_center_letter.setVisibility(View.VISIBLE);
                        scrollTo(textView.getText().toString());//根据选中的字符串匹配列表中的位置，并且将其滚动到屏幕范围内

                        //设置背景色
                        recyclerView.setBackgroundColor(Color.parseColor("#68808080"));
                        Log.d("dong", "当前触摸:" + textView.getText().toString());
                    } else {
                        Log.d("dong", "没找到对应的child");
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    rl_center_letter.setVisibility(View.GONE);
                    recyclerView.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
    }

    /**
     * 设置二级列表的适配器
     *
     * @param exListViewAdapter 适配器
     */
    public void setExListViewAdapter(BaseExpandableListAdapter exListViewAdapter) {
        this.expandableListAdapter = exListViewAdapter;
        expandableListView.setAdapter(exListViewAdapter);
        expandAllGroup();
    }

    /**
     * 设置滑动条数据源，如果没有设置则默认使用Group的数据源
     *
     * @param data 数据源
     */
    public void setSlideData(List<String> data) {
        if (slideList == null) {
            slideList = new ArrayList<>();
        } else {
            slideList.clear();
        }
        slideList.addAll(data);
        Log.d("dong","滑动数据源："+slideList.toString());
        slideAdapter = new SlideAdapter(slideList, context);
        rv_slide.setAdapter(slideAdapter);
        slideAdapter.notifyDataSetChanged();
    }

    /**
     * 通知数据源改动
     */
    public void notifyDataSetChanged() {
        if (expandableListAdapter == null) {
            throw new NullPointerException("expandableListAdapter not found!!");
        } else {
            expandableListAdapter.notifyDataSetChanged();
            expandAllGroup();
        }
    }

    /**
     * 设置子项点击监听器
     *
     * @param listener 监听器
     */
    public void setOnChildClickListener(ExpandableListView.OnChildClickListener listener) {
        onChildClickListener = listener;
        expandableListView.setOnChildClickListener(onChildClickListener);
    }

    /**
     * 展开二级列表
     */
    private void expandAllGroup() {
        if (expandableListAdapter != null) {
            //获取父项数据源
            if (groupList != null) {
                groupList.clear();
            } else {
                groupList = new ArrayList<>();
            }
            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                expandableListView.expandGroup(i);
                groupList.add((String) expandableListAdapter.getGroup(i));
            }
            //如果没设置滑动条的数据源，则默认使用Group的数据源
            if (slideList == null) {
                setSlideData(groupList);
            }
        }
    }

    /**
     * 根据Tag名滚动到对应的Group
     *
     * @param tag 需要滚动到的Group名
     * @return 成功返回true否则为false
     */
    public boolean scrollTo(String tag) {
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).equals(tag)) {
                expandableListView.setSelectedGroup(i);
                return true;
            }
        }
        return false;
    }

}
