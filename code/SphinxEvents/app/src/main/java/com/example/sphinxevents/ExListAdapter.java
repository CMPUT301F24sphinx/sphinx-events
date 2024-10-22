
package com.example.sphinxevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public abstract class ExListAdapter<C> extends BaseExpandableListAdapter {

    Context context;
    List<String> parents;
    Map<String, List<C>> children;

    ExListAdapter(Context context, List<String> parents, Map<String, List<C>> children) {
        this.context = context;
        this.parents = parents;
        this.children = children;
    }

    @Override
    public int getGroupCount() {
        return parents.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return children.get(parents.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return parents.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return children.get(parents.get(i)).get(i1);
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
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        String header = (String) getGroup(i);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ex_list_parent, null);
        }
        else {
            view = convertView;
        }

        TextView parentTextView = view.findViewById(R.id.parent_text_view);
        parentTextView.setText(header);

        return view;
    }

    @Override
    public abstract View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup);

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
