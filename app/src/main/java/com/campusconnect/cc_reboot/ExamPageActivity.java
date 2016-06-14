package com.campusconnect.cc_reboot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.campusconnect.cc_reboot.POJO.MyApi;
import com.campusconnect.cc_reboot.POJO.Test;
import com.campusconnect.cc_reboot.fragment.Home.FragmentCourses;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RK on 04/06/2016.
 */
public class ExamPageActivity extends AppCompatActivity {

    @Bind(R.id.test_name)
    TextView testName;
    @Bind(R.id.tv_date_posted)
    TextView date;
    @Bind(R.id.tv_uploader)
    TextView uploader;
    @Bind(R.id.tv_description)
    TextView desc;
    @Bind(R.id.tv_views_count)
    TextView views;
    @Bind(R.id.tv_due_date)
    TextView due;

    private String testId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);
        testId = getIntent().getStringExtra("testId");
        Log.i("sw32test",testId);

        Retrofit retrofit = new Retrofit.
                Builder()
                .baseUrl(MyApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApi myApi = retrofit.create(MyApi.class);
        MyApi.getTestRequest body = new MyApi.getTestRequest(testId, FragmentCourses.profileId);
        Call<Test> call = myApi.getTest(body);
        call.enqueue(new Callback<Test>() {
            @Override
            public void onResponse(Call<Test> call, Response<Test> response) {
                Test test = response.body();
                testName.setText(test.getTestTitle());
                desc.setText(test.getTestDesc());
                uploader.setText(test.getUploaderName());
                date.setText(test.getLastUpdated());
                due.setText(test.getDueDate());
                views.setText(test.getViews());
            }

            @Override
            public void onFailure(Call<Test> call, Throwable t) {

            }
        });


    }
}

