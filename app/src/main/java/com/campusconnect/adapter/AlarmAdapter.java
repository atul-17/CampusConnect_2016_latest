package com.campusconnect.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.campusconnect.POJO.ModelFeed;
import com.campusconnect.POJO.MyApi;
import com.campusconnect.POJO.SubscribedCourseList;
import com.campusconnect.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Atul on 28-08-2016.
 */
public class AlarmAdapter extends  RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{
    Context context;
    private List<SubscribedCourseList>courseLists;
    public static final String BASE_URL = "https://uploadingtest-2016.appspot.com/_ah/api/notesapi/v1/";
    MyApi myApi;
    Call<ModelFeed> call;
    Retrofit retrofit = new Retrofit.
            Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public AlarmAdapter(Context _context,List<SubscribedCourseList> _courseLists){
        this.context = _context;
        this.courseLists = _courseLists;
    }

    public  void add(SubscribedCourseList v){

        courseLists.add(v);
        notifyDataSetChanged();
    }

    public SubscribedCourseList getItem(int position){
        return courseLists.get(position);
    }

    public SubscribedCourseList getItem(SubscribedCourseList subscribedCourseList) {
        return courseLists.get(courseLists.indexOf(subscribedCourseList));
    }

    public void clear() {

        courseLists.clear();
        notifyDataSetChanged();
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_adapter,parent,false);
        return new AlarmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlarmViewHolder holder, int position) {

        final SubscribedCourseList a = courseLists.get(position);
        int i = a.getDate().size() - 1;
        Map<String,Date>sorted = new HashMap<String,Date>();
        Map<String, Date> MonCourse = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_WEEK);
        while (i >= 0) {
            switch (day) {
                case Calendar.MONDAY: {
                    if (a.getDate().get(i).equals("1")) {
                        holder.tv_alarm_course_name.setText(a.getCourseName());
                        holder.tv_alarm_course_time.setText(a.getStartTime().get(i));
                    }else{
                        //do nothing

                    }
                    break;
                }
                case Calendar.TUESDAY: {
                     if (a.getDate().get(i).equals("2")) {
                        holder.tv_alarm_course_name.setText(a.getCourseName());
                        holder.tv_alarm_course_time.setText(a.getStartTime().get(i));
                    }else {
                         //do nothin
                     }
                          }
                    break;

                case Calendar.WEDNESDAY: {
                    if (a.getDate().get(i).equals("3")) {
                        holder.tv_alarm_course_name.setText(a.getCourseName());
                        holder.tv_alarm_course_time.setText(a.getStartTime().get(i));
                    }else {
                         //do nothing

                    }
                    break;
                }

                case Calendar.THURSDAY: {
                    if (a.getDate().get(i).equals("4")) {
                        holder.tv_alarm_course_name.setText(a.getCourseName());
                        holder.tv_alarm_course_time.setText(a.getStartTime().get(i));
                    }else{
                        //do nothing

                    }
                    break;
                }

                case Calendar.FRIDAY:{

                if (a.getDate().get(i).equals("5")) {
                    holder.tv_alarm_course_name.setText(a.getCourseName());
                    holder.tv_alarm_course_time.setText(a.getStartTime().get(i));
                }else{
                    //do nothing

                }
                    break;

                }

                case Calendar.SATURDAY:{
                if (a.getDate().get(i).equals("6")) {
                    holder.tv_alarm_course_name.setText(a.getCourseName());
                    holder.tv_alarm_course_time.setText(a.getStartTime().get(i));
                    }else{
                    //do nothing

                }
                }
                break;
            }
            i--;
        }
        // notifyItemInserted(position);

    }




    @Override
    public int getItemCount() {
        return courseLists.size();
    }

    public static  class  AlarmViewHolder extends RecyclerView.ViewHolder{
        protected TextView tv_alarm_course_name;
        protected TextView tv_alarm_course_time;
        protected CardView alarm_card_view;

        public AlarmViewHolder(View itemView){
            super(itemView);
            tv_alarm_course_name = (TextView)itemView.findViewById(R.id.tv_alarm_course_name);
            tv_alarm_course_time = (TextView)itemView.findViewById(R.id.tv_alarm_course_time);
            alarm_card_view = (CardView)itemView.findViewById(R.id.alarmCardList);
        }
    }
}
