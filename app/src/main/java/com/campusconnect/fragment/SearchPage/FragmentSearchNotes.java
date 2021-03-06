package com.campusconnect.fragment.SearchPage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.campusconnect.POJO.NoteBookList;
import com.campusconnect.R;
import com.campusconnect.adapter.SearchNotesListAdapter;

import java.util.ArrayList;

/**
 * Created by RK on 05/06/2016.
 */
public class FragmentSearchNotes extends Fragment {

    ImageView no_course;
    RecyclerView course_list;
    public static SearchNotesListAdapter mSearchNotesAdapter;
    LinearLayoutManager mLayoutManager;
    public static ArrayList<NoteBookList> noteBookLists = new ArrayList<>();
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static RecyclerView fragment_search_notes;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_courses, container, false);

        no_course = (ImageView) v.findViewById (R.id.iv_no_course);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        course_list = (RecyclerView) v.findViewById (R.id.rv_courses);
        fragment_search_notes = (RecyclerView) v.findViewById(R.id.rv_courses);
        no_course.setVisibility(View.GONE);

        //Setting the recyclerView
        noteBookLists = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(v.getContext());
        mSearchNotesAdapter = new SearchNotesListAdapter(v.getContext(),noteBookLists);
        course_list.setLayoutManager(mLayoutManager);
        course_list.setItemAnimator(new DefaultItemAnimator());
        course_list.setAdapter(mSearchNotesAdapter);
        return v;
    }






}
