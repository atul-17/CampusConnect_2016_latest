package com.campusconnect;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.campusconnect.POJO.ModelFeed;
import com.campusconnect.POJO.MyApi;
import com.campusconnect.POJO.SubscribedCourseList;
import com.campusconnect.adapter.AlarmAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Atul on 18-08-2016.
 */
public class AlarmAlert extends Activity {

    public static final String BASE_URL = "https://uploadingtest-2016.appspot.com/_ah/api/notesapi/v1/";
    MyApi myApi;
    Call<ModelFeed> call;
    AlarmAdapter alarmAdapter;
    List<SubscribedCourseList> courses = new ArrayList<>();
    public TextView tv_course_scheduled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.alarmCardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        alarmAdapter = new AlarmAdapter(getBaseContext(),courses);
        recyclerView.setAdapter(alarmAdapter);



        tv_course_scheduled = (TextView)findViewById(R.id.tv_course_scheduled);
        Retrofit retrofit = new Retrofit.
                Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(MyApi.class);
        call = myApi.getFeed(getSharedPreferences("CC",MODE_PRIVATE).getString("profileId",""));
        call.enqueue(new Callback<ModelFeed>() {
            @Override
            public void onResponse(Call<ModelFeed> call, Response<ModelFeed> response) {
                ModelFeed modelFeed = response.body();
                Calendar calendar = Calendar.getInstance();
                final int day = calendar.get(Calendar.DAY_OF_WEEK);
              List<SubscribedCourseList> subscribedCourseList = modelFeed.getSubscribedCourseList();

                for ( SubscribedCourseList courseList : subscribedCourseList) {

                    int i = courseList.getDate().size() - 1;
                    while (i >= 0) {
                        switch (day) {
                            case Calendar.SUNDAY: {
                                alarmAdapter.clear();
                                break;
                            }
                            case Calendar.MONDAY:
                                if (courseList.getDate().get(i).equals("1")) {
                                    if (courseList.getStartTime().get(i).equals(getSharedPreferences("CC",MODE_PRIVATE).getString("alarmMon",""))){
                                       String course =  courseList.getCourseName();
                                       // Log.d("atul","scheduled:"+course);
                                        tv_course_scheduled.setText(course+ " is at "+getSharedPreferences("CC",MODE_PRIVATE).getString("alarmMon",""));
                                    }

                                    alarmAdapter.add(courseList);
                                    Log.d("atul","added:"+courseList.getCourseName());

                                }
                                break;

                            case Calendar.TUESDAY:
                                if (courseList.getDate().get(i).equals("2")) {
                                            if (courseList.getStartTime().get(i).equals(getSharedPreferences("CC",MODE_PRIVATE).getString("alarmTue",""))){
                                                String course =  courseList.getCourseName();
                                                tv_course_scheduled.setText(course + " is at "+getSharedPreferences("CC",MODE_PRIVATE).getString("alarmTue",""));
                                            }
                                    Log.d("atul", "course" + courseList.getCourseName() + " time :" + courseList.getStartTime().get(i));
                                    alarmAdapter.add(courseList);
                                }
                                break;

                            case Calendar.WEDNESDAY:
                                if (courseList.getDate().get(i).equals("3")){
                                    if (courseList.getStartTime().get(i).equals(getSharedPreferences("CC",MODE_PRIVATE).getString("alarmWed",""))){
                                        String course = courseList.getCourseName();
                                   tv_course_scheduled.setText(course + " is at"+getSharedPreferences("CC",MODE_PRIVATE).getString("alarmWed",""));
                                    }
                                    alarmAdapter.add(courseList);
                                }
                                break;
                            case Calendar.THURSDAY:
                                if (courseList.getDate().get(i).equals("4")){
                            if (courseList.getStartTime().get(i).equals(getSharedPreferences("CC",MODE_PRIVATE).getString("alarmThu",""))){
                                String course = courseList.getCourseName();
                                tv_course_scheduled.setText(course + " is at"+getSharedPreferences("CC",MODE_PRIVATE).getString("alarmThu",""));
                                }
                                    alarmAdapter.add(courseList);
                                }
                                break;
                            case Calendar.FRIDAY:
                                if (courseList.getDate().get(i).equals("5")){
                                    if (courseList.getStartTime().get(i).equals(getSharedPreferences("CC",MODE_PRIVATE).getString("alarmFri",""))){
                                        String course = courseList.getCourseName();
                                        tv_course_scheduled.setText(course + " is at "+ getSharedPreferences("CC",MODE_PRIVATE).getString("alarmFri",""));
                                    }
                                    alarmAdapter.add(courseList);

                                }
                                break;
                            case Calendar.SATURDAY:
                                if (courseList.getDate().get(i).equals("6")){
                                    if (courseList.getStartTime().get(i).equals(getSharedPreferences("CC",MODE_PRIVATE).getString("alarmSat",""))){
                                        String course = courseList.getCourseName();
                                        tv_course_scheduled.setText(course + " is at "+getSharedPreferences("CC",MODE_PRIVATE).getString("alarmSat",""));
                                    }
                                    alarmAdapter.add(courseList);
                                }
                        }
                        i--;
                    }

                    }
            }

            @Override
            public void onFailure(Call<ModelFeed> call, Throwable t) {

            }
        });

    }

    /** The application should be exit, if the user presses the back button */
    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

}
