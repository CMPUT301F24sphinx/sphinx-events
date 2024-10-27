
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
    List<String> parents;  // List of ex-list parents/headers
    Map<String, List<C>> children;  // Map of each parent to list of children

    ExListAdapter(Context context, List<String> parents, Map<String, List<C>> children) {
        this.context = context;
        this.parents = parents;
        this.children = children;
    }


    /**
     * Returns number of groups in ex-list
     *
     * @return group count
     */
    @Override
    public int getGroupCount() {
        return parents.size();
    }


    /**
     * Returns number of children in group
     *
     * @param groupPosition index of group
     * @return number of children
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(parents.get(groupPosition)).size();
    }


    /**
     * Returns group parent/header
     *
     * @param groupPosition index of group
     * @return parent
     */
    @Override
    public Object getGroup(int groupPosition) {
        return parents.get(groupPosition);
    }


    /**
     * Returns child
     *
     * @param groupPosition index of group
     * @param childPosition index of child within group
     * @return child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(parents.get(groupPosition)).get(childPosition);
    }


    /**
     * Returns id of group, which is just it's index
     *
     * @param groupPosition index of group
     * @return group id
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    /**
     * Returns id of child, which is just it's index
     *
     * @param groupPosition index of group
     * @param childPosition index of child within group
     * @return child id
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    /**
     * Returns whether the id's do not change, which is false
     *
     * @return false
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }


    /**
     * Displays the groups/headers/parents
     *
     * @param groupPosition index of group
     * @param isExpanded boolean of whether list is expanded
     * @param convertView reference to existing view if exists
     * @param parent parent view
     * @return view of group
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        String header = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ex_list_parent, parent, false);
        }
        else {
            view = convertView;
        }

        TextView parentTextView = view.findViewById(R.id.parent_text_view);
        parentTextView.setText(header);

        return view;
    }

    @Override
    public abstract View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup parent);


    /**
     * Returns whether child can be selected, which is true
     * @param groupPosition index of group
     * @param childPosition index of child within group
     * @return true
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
