package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;


import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R;
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TermsConditionsFramgment extends Fragment {
    ExpandableListView expandableListViewExample;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableTitleList;
    HashMap<String, List<String>> expandableDetailList;
    AppCompatTextView tv_selectLocation;

    private static final int[] EMPTY_STATE_SET = {};
    private static final int[] GROUP_EXPANDED_STATE_SET = { android.R.attr.state_expanded };
    private static final int[][] GROUP_STATE_SETS = { EMPTY_STATE_SET, // 0
            GROUP_EXPANDED_STATE_SET };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_terms_conditions_framgment, container, false);
        expendData(view);
        return  view;
    }

    private void expendData(View view) {

        tv_selectLocation=view.findViewById(R.id.tv_selectLocation1);
        tv_selectLocation.setText("Terms & Conditions");
        expandableListViewExample = (ExpandableListView)view.findViewById(R.id.elvListTearmCondition);
        expandableDetailList = ExpandableListDataItems.getData();
        expandableTitleList = new ArrayList<String>(expandableDetailList.keySet());
        expandableListAdapter = new CustomizedExpandableListAdapter(getContext(), expandableTitleList, expandableDetailList);
        expandableListViewExample.setAdapter(expandableListAdapter);
        view.findViewById(R.id.imgCloseIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity= (MainActivity) getActivity();
                mainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

    }

    public static class CustomizedExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableTitleList;
        private HashMap<String, List<String>> expandableDetailList;

        public CustomizedExpandableListAdapter(Context context, List<String> expandableListTitle,
                                               HashMap<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableTitleList = expandableListTitle;
            this.expandableDetailList = expandableListDetail;
        }

        @Override
        public Object getChild(int lstPosn, int expanded_ListPosition) {
            return this.expandableDetailList.get(this.expandableTitleList.get(lstPosn)).get(expanded_ListPosition);
        }

        @Override
         public long getChildId(int listPosition, int expanded_ListPosition) {
            return expanded_ListPosition;
        }

        @Override
        public View getChildView(int lstPosn, final int expanded_ListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = (String) getChild(lstPosn, expanded_ListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.hardiklist_item, null);
            }
            TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableDetailList.get(this.expandableTitleList.get(listPosition)).size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableTitleList.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableTitleList.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.hardiklist_group, null);
            }
            TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);


            View ind = convertView.findViewById(R.id.ivdown);
            View ind2 = convertView.findViewById(R.id.ivup);
            if (ind != null)
            {
                ImageView indicator = (ImageView) ind;
                if (getChildrenCount(listPosition) == 0)
                {
                    indicator.setVisibility(View.INVISIBLE);
                }
                else
                {
                    indicator.setVisibility(View.VISIBLE);
                    int stateSetIndex = (isExpanded ? 1 : 0);

                    if(stateSetIndex == 1){
                        ind.setVisibility(View.INVISIBLE);
                        ind2.setVisibility(View.VISIBLE);
                        Drawable drawable = indicator.getDrawable();
                        drawable.setState(GROUP_STATE_SETS[stateSetIndex]);
                    }
                    else if(stateSetIndex == 0){
                        ind.setVisibility(View.VISIBLE);
                        ind2.setVisibility(View.INVISIBLE);
                        Drawable drawable = indicator.getDrawable();
                        drawable.setState(GROUP_STATE_SETS[stateSetIndex]);
                    }
                }
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }

    public static class ExpandableListDataItems {
        public static HashMap<String, List<String>> getData() {
            HashMap<String, List<String>> expandableDetailList = new HashMap<String, List<String>>();
            List<String> Introduction = new ArrayList<String>();
            Introduction.add("Apple");

            List<String> Intellectual = new ArrayList<String>();
            Intellectual.add("Other than the content you own, under these Terms, Let’s Relate: Mind-Matching Test and/or its licensors own all the intellectual property rights and materials contained in this Website.");

            List<String> Restrictions= new ArrayList<String>();
            Restrictions.add("Cashews");

            List<String> Content= new ArrayList<String>();
            Content.add("Cashews");

            List<String> Privacy= new ArrayList<String>();
            Privacy.add("Cashews");

            List<String> Cookies= new ArrayList<String>();
            Cookies.add("Google Double Click DART Cookie");


            List<String> warranties= new ArrayList<String>();
            warranties.add("Google Double Click DART Cookie");

            List<String> Limitation= new ArrayList<String>();
            Limitation.add("Google Double Click DART Cookie");


            expandableDetailList.put("Introduction", Introduction);
            expandableDetailList.put("Intellectual Property Rights", Intellectual);
            expandableDetailList.put("Your Content", Content);
            expandableDetailList.put("Your Privacy", Privacy);
            expandableDetailList.put("Cookies and Web Beacons", Cookies);
            expandableDetailList.put("No warranties", warranties);
            expandableDetailList.put("Limitation of liability", Limitation);

            return expandableDetailList;
        }
    }
}