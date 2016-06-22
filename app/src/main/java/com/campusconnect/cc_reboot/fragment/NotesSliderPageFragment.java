package com.campusconnect.cc_reboot.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.campusconnect.cc_reboot.AssignmentPageActivity;
import com.campusconnect.cc_reboot.NotesSliderActivity;
import com.campusconnect.cc_reboot.R;
import com.campusconnect.cc_reboot.auxiliary.DepthPageTransformer;
import com.campusconnect.cc_reboot.viewpager.CustomPagerAdapter;
import com.campusconnect.cc_reboot.viewpager.ScreenSlidePagerAdapter;
import com.campusconnect.cc_reboot.NotesSliderActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 13/06/2016.
 */
public class NotesSliderPageFragment extends Fragment implements View.OnTouchListener{

    @Bind(R.id.view_touch_handler)
    View touch_handling_view;

    String class_no, total_pages, curr_page;

    ArrayList<ArrayList<String>> urls = NotesSliderActivity.urls;
    ArrayList<Integer> pages;
    int page_pos;
    int totalPages=0;
    ViewPager pager_img;
    Bundle fragArgs;

    int img_1[] = { R.mipmap.ic_due_18,
            R.mipmap.ic_launcher,
            R.mipmap.ic_share_24_black,
            R.mipmap.ic_sort_24 };

    int img_2[] = { R.mipmap.ic_notifications_24,
            R.mipmap.ic_notifications_none_24,
            R.mipmap.ic_pages_18 };

    public interface NotePageInfoToActivity{
        public void notePageInfo(String class_no, String curr_page, String total_pages);
        public void pageInfoVisibility(boolean flag);
    }
    NotePageInfoToActivity notePageInfoToActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_notes_slider_page, container, false);
        ButterKnife.bind(this,rootView);

        fragArgs = getArguments();
        pager_img = (ViewPager) rootView.findViewById(R.id.viewpager_images);

        page_pos = fragArgs.getInt("PagePos");
        for(ArrayList<String> a : urls)
        {
            totalPages+=a.size();
        }

        pager_img.setPageTransformer(true, new DepthPageTransformer());

        //String temp = urls.get(urls.size()-1).toString();
        //temp = temp.substring(1,temp.length()-1);
        //String[] urls = temp.split(",");

        pager_img.setAdapter(new CustomPagerAdapter(getActivity(),urls.get(page_pos),page_pos));

        class_no = fragArgs.getString("PageTitle");
        total_pages = Integer.toString(pager_img.getAdapter().getCount());
        curr_page = Integer.toString(1);


        pager_img.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                // TODO Auto-generated method stub
                curr_page = Integer.toString(index+1);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        notePageInfoToActivity.notePageInfo(class_no,curr_page,total_pages);

        touch_handling_view.setOnTouchListener(this);

        return rootView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){

            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_DOWN:

                notePageInfoToActivity.pageInfoVisibility(true);

                break;

            case MotionEvent.ACTION_MOVE:

                break;

        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            notePageInfoToActivity = (NotePageInfoToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement notePageInfoToActivity");
        }
    }

}
