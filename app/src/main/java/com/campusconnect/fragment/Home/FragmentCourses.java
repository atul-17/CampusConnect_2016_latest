package com.campusconnect.fragment.Home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.campusconnect.CoursePageActivity;
import com.campusconnect.POJO.AvailableCourseList;
import com.campusconnect.POJO.ModelFeed;
import com.campusconnect.POJO.MyApi;
import com.campusconnect.POJO.SubscribedCourseList;
import com.campusconnect.R;
import com.campusconnect.adapter.CourseListAdapter;
import com.campusconnect.adapter.TimetableAdapter;
import com.google.common.base.Joiner;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by RK on 05/06/2016.
 */
public class FragmentCourses extends Fragment{

    ImageView no_course_view;
    RecyclerView course_list;
    Boolean resumeHasRun = false;
    CourseListAdapter mCourseAdapter;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout cell_container;
    View course_indicator;

    Retrofit retrofit = new Retrofit.
            Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    MyApi myApi;
    Call<ModelFeed> call;
    ConnectivityManager cm;
    RecyclerView fragment_courses;
    NetworkInfo activeNetwork;
    private FirebaseAnalytics mFirebaseAnalytics;
    boolean isConnected;
    //testing url:https://uploadingtest-2016.appspot.com
    // production url:https://uploadnotes-2016.appspot.com
    public static final String BASE_URL = "https://uploadingtest-2016.appspot.com/_ah/api/notesapi/v1/";
    public static final String uploadURL = "https://uploadnotes-2016.appspot.com/img";
    public static final String django = "https://campusconnect-2016.herokuapp.com";
    //public static final String django = "http://10.75.133.109:8000";

    public static  String profileName = "";
    public static  String profilePoints = "";
    public static ArrayList<String> courseNames;
    public static ArrayList<String> courseIds;
    public static HashMap<String,ArrayList<String>> timeTableViews;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_courses, container, false);
        myApi = retrofit.create(MyApi.class);
        fragment_courses = (RecyclerView) v.findViewById(R.id.rv_courses);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });
        courseNames = new ArrayList<>();
        courseIds = new ArrayList<>();
        no_course_view = (ImageView) v.findViewById(R.id.iv_no_course);
        course_list = (RecyclerView) v.findViewById (R.id.rv_courses);

        BitmapFactory.Options bm_opts = new BitmapFactory.Options();
        bm_opts.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_value_courses, bm_opts);
        no_course_view.setImageBitmap(bitmap);

        ArrayList<SubscribedCourseList> courses = new ArrayList<>();
        timeTableViews = new HashMap<>();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        //Setting the recyclerView

        mLayoutManager = new LinearLayoutManager(v.getContext());
        mCourseAdapter = new CourseListAdapter(v.getContext(),courses);
        course_list.setLayoutManager(mLayoutManager);
        course_list.setItemAnimator(new DefaultItemAnimator());
        course_list.setAdapter(mCourseAdapter);
        call= myApi.getFeed(getActivity().getSharedPreferences("CC", Context.MODE_PRIVATE).getString("profileId",""));
        cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork =  cm.getActiveNetworkInfo();
        isConnected= activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.i("sw32call","create");
        if(isConnected) {
            swipeRefreshLayout.post(new Runnable() {
                @Override public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    // directly call onRefresh() method
                    refreshPage();
                }
            });
        }else{
            resumePage();
            Toast.makeText(getActivity(),"Check your connection and try again",Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!resumeHasRun)
        {
            resumeHasRun = true;
            return;
        }
        Log.i("sw32call","onresume");
        cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        isConnected= activeNetwork != null && activeNetwork.isConnected();
        List<SubscribedCourseList> aa = SubscribedCourseList.listAll(SubscribedCourseList.class);
        if(!isConnected)
        {


        }
        else {

            if (aa.size() < courseIds.size()) {
                courseNames.clear();
                courseIds.clear();
                mCourseAdapter.clear();
                for (SubscribedCourseList x : aa) {
                    courseNames.add(x.getCourseName());
                    courseIds.add(x.getCourseId());
                    mCourseAdapter.add(x);
                    x.save();
                    FirebaseMessaging.getInstance().subscribeToTopic(x.getCourseId());
                }
            } else if (aa.size() > courseIds.size()) {
                Log.i("sw32onresume", aa.size() + " : " + courseIds.size());
                refreshPage();
            }
        }


    }

    void resumePage()
    {

        List<SubscribedCourseList> aa = SubscribedCourseList.listAll(SubscribedCourseList.class);
        courseNames.clear();
        courseIds.clear();
        mCourseAdapter.clear();
        for (SubscribedCourseList x : aa) {
            courseNames.add(x.getCourseName());
            courseIds.add(x.getCourseId());
            mCourseAdapter.add(x);
            x.save();
            FirebaseMessaging.getInstance().subscribeToTopic(x.getCourseId());
        }
    }

    void refreshPage(){

        isConnected= activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.i("sw32","callonrefresh");
        if(isConnected){
            SubscribedCourseList.deleteAll(SubscribedCourseList.class);
            for(String key : timeTableViews.keySet())
            {
                ArrayList<String> viewIds = timeTableViews.get(key);
                for(String viewId : viewIds)
                {
                    LinearLayout a = ((LinearLayout)TimetableAdapter.itemView.findViewById(Integer.parseInt(viewId)));
                    if(a!=null) {
                        a.removeAllViews();
                        a.setBackgroundColor(Color.rgb(223, 223, 223));
                    }
                }
            }

            /**Setting alarm with time being set to first hour the class starts*/
            final List<String> st_timeDay1 = new ArrayList<String>();
            final List<String> st_timeDay2 = new ArrayList<String>();
            final List<String> st_timeDay3 = new ArrayList<String>();
            final List<String> st_timeDay4 = new ArrayList<String>();
            final List<String> st_timeDay5 = new ArrayList<String>();
            final List<String> st_timeDay6 = new ArrayList<String>();

        call= myApi.getFeed(getActivity().getSharedPreferences("CC", Context.MODE_PRIVATE).getString("profileId",""));
        call.enqueue(new Callback<ModelFeed>() {
            @Override
            public void onResponse(Call<ModelFeed> call, Response<ModelFeed> response) {
                ModelFeed modelFeed = response.body();
                new FragmentTimetable();
                if(response.code() == 503){
                    BitmapFactory.Options bm_opts = new BitmapFactory.Options();
                    bm_opts.inScaled = false;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error_five_zero_three, bm_opts);
                    no_course_view.setImageBitmap(bitmap);
                    no_course_view.setVisibility(View.VISIBLE);
                }
                if(modelFeed !=null) {
                    courseNames.clear();
                    courseIds.clear();
                    timeTableViews = new HashMap<>();
                    mCourseAdapter.clear();
                    profileName = modelFeed.getProfileName();
                    profilePoints = modelFeed.getPoints();
                    List<AvailableCourseList> availableCourseList = modelFeed.getAvailableCourseList();
                    List<SubscribedCourseList> subscribedCourseList = modelFeed.getSubscribedCourseList();
                    if(subscribedCourseList.isEmpty())
                    {
                        no_course_view.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        no_course_view.setVisibility(View.GONE);
                    }
                    for (final SubscribedCourseList x : subscribedCourseList) {
                        courseNames.add(x.getCourseName());
                        courseIds.add(x.getCourseId());
                        mCourseAdapter.add(x);
                        x.save();

                        int i = x.getDate().size()-1;
                        while(i>=0) {
                            if (x.getDate().get(i).equals("1")) {
                                st_timeDay1.add(x.getStartTime().get(i));
                                Collections.sort(st_timeDay1);
                                SharedPreferences sp = getActivity().getSharedPreferences("CC",Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("alarmMon",st_timeDay1.get(0))
                                        .apply();
                                // Log.d("atul ", courseList.getStartTime().get(j) + "day:" + courseList.getDate().get(j) + "course: " + courseList.getCourseName());
                            }

                            else if (x.getDate().get(i).equals("2")){
                                st_timeDay2.add(x.getStartTime().get(i));
                                Collections.sort(st_timeDay2);
                                SharedPreferences sp = getActivity().getSharedPreferences("CC",Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("alarmTue",st_timeDay2.get(0))
                                        .apply();
                            }

                            else if (x.getDate().get(i).equals("3")){
                                st_timeDay3.add(x.getStartTime().get(i));
                                Collections.sort(st_timeDay3);
                                SharedPreferences sp = getActivity().getSharedPreferences("CC",Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("alarmWed",st_timeDay3.get(0))
                                        .apply();
                            }

                            else if (x.getDate().get(i).equals("4")){
                                st_timeDay4.add(x.getStartTime().get(i));
                                Collections.sort(st_timeDay4);
                                SharedPreferences sp = getActivity().getSharedPreferences("CC",Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("alarmThu",st_timeDay4.get(0))
                                        .apply();
                            }

                            else if (x.getDate().get(i).equals("5")){
                                st_timeDay5.add(x.getStartTime().get(i));
                                Collections.sort(st_timeDay5);
                                SharedPreferences sp = getActivity().getSharedPreferences("CC",Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("alarmFri",st_timeDay5.get(0))
                                        .apply();
                            }

                            else if (x.getDate().get(i).equals("6")){
                                st_timeDay6.add(x.getStartTime().get(i));
                                Collections.sort(st_timeDay6);
                                SharedPreferences sp = getActivity().getSharedPreferences("CC",Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("alarmSat",st_timeDay6.get(0))
                                        .apply();
                            }

                            int start = Integer.parseInt(x.getStartTime().get(i).substring(0, 2));
                            int end = Integer.parseInt(x.getEndTime().get(i).substring(0, 2));
                            String date = x.getDate().get(i);

                                //  Log.d("atul", day);
                            for (int ii = start; ii < end; ii++) {
                                View cell = LayoutInflater.from(getContext()).inflate(R.layout.timetable_cell_layout, cell_container, false);
                                if (cell != null){
                                    String viewId = date + "" + (ii - 6);
                                if (timeTableViews.containsKey(x.getCourseId())) {
                                    timeTableViews.get(x.getCourseId()).add(viewId);
                                } else {
                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add(viewId);
                                    timeTableViews.put(x.getCourseId(), temp);
                                }
                                cell_container = (LinearLayout) TimetableAdapter.itemView.findViewById(Integer.parseInt(viewId));
                                if (cell_container != null) {
                                    cell_container.setBackgroundColor(Color.WHITE);
                                    ((View) cell.findViewById(R.id.course_indicator)).setBackgroundColor(Color.parseColor(x.getColour()));
                                    ((TextView) cell.findViewById(R.id.cellText)).setText(x.getCourseCode());
                                    cell.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent coursePage = new Intent(getActivity(), CoursePageActivity.class);
                                            coursePage.putExtra("courseId", x.getCourseId());
                                            coursePage.putExtra("courseColor", Color.parseColor(x.getColour()));
                                            startActivity(coursePage);
                                        }
                                    });
                                    cell_container.removeAllViews();
                                    cell_container.addView(cell);
                                }
                            }
                            }
                            i--;
                        }
                        FirebaseMessaging.getInstance().subscribeToTopic(x.getCourseId());


                    }

                    Log.i("sw32", "" + subscribedCourseList.size() + " : " + subscribedCourseList.isEmpty());
                    swipeRefreshLayout.setRefreshing(false);

                    //set alarm day1
                    setAlarm(Calendar.MONDAY,st_timeDay1,1);
                    //set alarm day2
                    setAlarm(Calendar.TUESDAY,st_timeDay2,2);
                    //set alarm day3
                    setAlarm(Calendar.WEDNESDAY,st_timeDay3,3);
                    //set alarm day4
                    setAlarm(Calendar.THURSDAY,st_timeDay4,4);
                    //set alarm day5
                    setAlarm(Calendar.FRIDAY,st_timeDay5,5);
                    //set alarm day6
                    setAlarm(Calendar.SATURDAY,st_timeDay6,6);

                }



                }



            @Override
            public void onFailure(Call<ModelFeed> call, Throwable t) {
                Toast.makeText(getActivity(),"Oops! Something went wrong!",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }else
        {
            Toast.makeText(getActivity(),"Check your connection and try again",Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }




     private void setAlarm(int dayOfTheWeek,List<String> st_time,int UNIQUE_ID){


         Intent intent = new Intent("com.campusconnect.AlertDialogActivity");
         PendingIntent operation = PendingIntent.getActivity(getActivity().getBaseContext(), UNIQUE_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
         AlarmManager alarmManager = (AlarmManager) getActivity().getBaseContext().getApplicationContext().getSystemService(Context.ALARM_SERVICE);

         String startTime = st_time.get(0);

            String pattern = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern,Locale.getDefault());
             Calendar alarmCalender = Calendar.getInstance();

         try {
             Date date = sdf.parse(startTime);
                int hours = date.getHours();
                int mins = date.getMinutes();
              //      hours = hours - 60*60*1000;
            // Log.d("atul",String.valueOf(hours));

             alarmCalender.setTimeInMillis(System.currentTimeMillis());
             alarmCalender.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
             alarmCalender.set(Calendar.HOUR_OF_DAY, hours);
             alarmCalender.set(Calendar.MINUTE, mins);
             alarmCalender.set(Calendar.SECOND,0);

             long alarm_time = alarmCalender.getTimeInMillis();
                        //   Log.d("atul", "millis" + alarm_time);
             long timeToAlarm = alarmCalender.getTimeInMillis();
             if (alarmCalender.getTimeInMillis() < System.currentTimeMillis())
             {
                 timeToAlarm += (24*7*60*60*1000);
             }

             alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,timeToAlarm,24 * 7 * 60 * 60 * 1000,operation);





            Log.d("atul","alarm set at" + getDate(alarm_time,"HH:mm"));



       } catch (ParseException e) {
             e.printStackTrace();
         }
     }


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat,Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}




