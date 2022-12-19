package com.myExpandableListAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, ArrayList<String>> info_clases;
    private ArrayList<String> groupList;

    public MyExpandableListAdapter(Context context, ArrayList<String> groupList,
                                   Map<String, ArrayList<String>> info_clases){
        this.context = context;
        this.info_clases = info_clases;
        this.groupList = groupList;
    }

    @Override
    public int getGroupCount() {
        return info_clases.size();
    }

    @Override
    public int getChildrenCount(int i) {
        int size;

        try {
            size = info_clases.get(groupList.get(i)).size();
        }catch (Exception e){
            size = 0;
        }
        return size;
    }

    @Override
    public Object getGroup(int i) {
        return groupList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        Object hijo;
        try {
            hijo = info_clases.get(groupList.get(i)).get(i1);
        }catch (Exception e){
            hijo = new Object();
        }
        return hijo;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) { return view; }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) { return view;}

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}