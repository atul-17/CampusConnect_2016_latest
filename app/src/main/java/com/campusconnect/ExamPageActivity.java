package com.campusconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.campusconnect.POJO.ModelTest;
import com.campusconnect.POJO.MyApi;
import com.campusconnect.fragment.Drawer.FragmentAbout;
import com.campusconnect.fragment.Drawer.FragmentAddCourse;
import com.campusconnect.fragment.Drawer.FragmentInvite;
import com.campusconnect.fragment.Drawer.FragmentRate;
import com.campusconnect.fragment.Drawer.FragmentTerms;
import com.campusconnect.fragment.Home.FragmentCourses;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import io.doorbell.android.Doorbell;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RK on 04/06/2016.
 */
public class ExamPageActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.frame)
    FrameLayout fragment_frame;

    @Bind(R.id.iv_exam)
    ImageView exam_last_page;

    @Bind(R.id.container_exam)
    RelativeLayout exam_container;

    @Bind(R.id.tv_exam_name)
    TextView testName;
    @Bind(R.id.tv_date_posted)
    RelativeTimeTextView date;
    @Bind(R.id.tv_uploader)
    TextView uploader;
    @Bind(R.id.tv_description)
    TextView desc;
    @Bind(R.id.tv_views_count)
    TextView views;
    @Bind(R.id.tv_due_date)
    TextView due;

    @Bind(R.id.ib_back)
    ImageButton back_button;
    @Bind(R.id.ib_edit)
    ImageButton edit_note_button;
    @Bind(R.id.ib_share)
    ImageButton share_note_button;
    @Bind(R.id.ib_flag)
    ImageButton flag_button;

    @Bind(R.id.exam_remind)
    ToggleButton remind_button;

    private String testId;
    int courseColor;
    Intent intent;

    //Flags
    boolean doubleBackToExitPressedOnce = false;
    boolean at_home=true;
    String frag_title="";
    private Toolbar toolbar;
    private Fragment fragment = null;
    Fragment homefrag;
    View headerView;
    public static TextView home_title;
    GoogleApiClient mGoogleApiClient;
    String courseNamePlaceHolder = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);

        //Drawer stuff
        home_title = (TextView) findViewById(R.id.tv_title);
        toolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar(toolbar);
//        homefrag = new FragmentHome();
        //Setting up Header View
        headerView = getLayoutInflater().inflate(R.layout.header, navigationView, false);
        navigationView.addHeaderView(headerView);
        ImageView imageView = (ImageView) headerView.findViewById(R.id.profile_image);

        Picasso.with(ExamPageActivity.this)
                .load(getSharedPreferences("CC",MODE_PRIVATE).getString("photourl","fakedesu")).error(R.mipmap.ic_launcher)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.ccnoti)
                .into(imageView);
        ((TextView)headerView.findViewById(R.id.tv_username)).setText(getSharedPreferences("CC",MODE_PRIVATE).getString("profileName","PLACEHOLDER"));

        //Unchecking all the drawer menu items before going back to home in case the app crashes
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
//Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
//Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);
//Closing drawer on item click
                drawerLayout.closeDrawers();
//Fragment selection and commits
                displayView(menuItem.getItemId());
                return true;
            }
        });
//Initializing ActionBarToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
                fragment_frame.setTranslationX((drawerLayout.getWidth() * slideOffset) / 4);
                fragment_frame.setTranslationX((drawerLayout.getWidth() * slideOffset) / 4);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else the hamburger icon won't show up
        actionBarDrawerToggle.syncState();
        //OnClickListener Header View
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_temp = new Intent(getApplicationContext(), ProfilePageActivity.class);
                startActivity(intent_temp);
            }
        });
        //Drawer ends

        courseColor = getIntent().getIntExtra("CourseColor", Color.rgb(224,224,224));
        exam_container.setBackgroundColor(courseColor);



        //OnClickListeners
        back_button.setOnClickListener(this);
        edit_note_button.setOnClickListener(this);
        share_note_button.setOnClickListener(this);
        flag_button.setOnClickListener(this);
        exam_last_page.setOnClickListener(this);
        Drawable drawable = getResources().getDrawable(R.drawable.app_faces);
        Picasso.with(this).load(R.drawable.app_faces).placeholder(R.drawable.app_faces).fit().into(exam_last_page);
        remind_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(testId);
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(testId);
                }
            }
        });
        //GoogleSignIn stuff
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ib_back:
                finish();
                break;

            case R.id.ib_share:
                share_link();
                break;

            case R.id.ib_flag:
                Retrofit retrofit = new Retrofit.
                        Builder()
                        .baseUrl(MyApi.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                MyApi myApi = retrofit.create(MyApi.class);
                MyApi.reportRequest body= new MyApi.reportRequest(getSharedPreferences("CC",MODE_PRIVATE).getString("profileId",""),testId,"");
                Call<Void> call = myApi.report(body);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(ExamPageActivity.this,"Thank you for the feedback. We will get back to you shortly",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

                break;

            case R.id.iv_exam:
//                intent = new Intent(getApplicationContext(), NotesSliderActivity.class);
//                startActivity(intent);
                break;
            default:
                break;
        }

    }
    void share_link()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("examId", testId);

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("whatsapp")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "http://campusconnect-2016.herokuapp.com/exam?id=" + testId);

        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        final String shareText = "Hey, check out the exam for " + courseNamePlaceHolder + " on Campus Connect!\n";
        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("MyApp", "got my Branch link to share: " + url);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,shareText + url);
                    progressDialog.dismiss();
                    startActivityForResult(Intent.createChooser(sendIntent, "Share with..."),1);
                }
            }
        });
    }

    //Function for fragment selection and commits
    public void displayView(int viewId){
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (viewId) {
            case R.id.item_timetable:
                at_home=true;
                Intent intent_home = new Intent(ExamPageActivity.this,HomeActivity2.class);
                startActivity(intent_home);
                finish();
                break;
            case R.id.item_add_course:
                fragment = new FragmentAddCourse();
                frag_title = "Add Course";
                at_home=false;
                break;
            case R.id.item_bookmark:
                Intent intent_profile = new Intent(ExamPageActivity.this,ProfilePageActivity.class);
                startActivity(intent_profile);
                at_home=true;
                fragment = null;
                frag_title = "Exam";
                break;

            case R.id.item_invite:
                fragment = null;
                frag_title = "Exam";
                at_home=true;
                BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);

                LinkProperties linkProperties = new LinkProperties()
                        .setChannel("Invite")
                        .setFeature("Invite")
                        .addControlParameter("$desktop_url", "http://campusconnect.cc")
                        .addControlParameter("$android_url", "bit.ly/campusconnectandroid");
                final Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                final String shareText = " Hey, check out this cool app called Campus Connect!\n";
                branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error == null) {
                            sendIntent.putExtra(Intent.EXTRA_TEXT,shareText+url);
                            Log.i("MyApp", "got my Branch link to share: " + url);
                            startActivityForResult(Intent.createChooser(sendIntent, "Invite through..."),666);
                        }
                    }
                });
                break;
            case R.id.item_logout:
                at_home=true;
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Intent intent = new Intent(ExamPageActivity.this,GoogleSignInActivity.class);
                intent.putExtra("logout","temp");
                FirebaseAuth.getInstance().signOut();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.item_t_and_c:
                frag_title = "Exam";
                at_home=true;
                fragment = null;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://campusconnect.cc/faq#terms"));
                startActivity(browserIntent);
                break;
            case R.id.item_rate:
                fragment = null;
                frag_title = "Exam";
                //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                final String appPackageName = "com.campusconnect";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                at_home=true;
                break;
            case R.id.item_feedback:
                Doorbell doorbellDialog = new Doorbell(this, 2764, "czPslyxNo9JTzQog5JcrWBlRbHVSQKyqnieLG8QDVZNK1hesEJtPD9E0MRuBbeW0");
                doorbellDialog.setEmail(getSharedPreferences("CC",MODE_PRIVATE).getString("email","")); // Prepopulate the email address field
                doorbellDialog.setName(getSharedPreferences("CC",MODE_PRIVATE).getString("profileName","")); // Set the name of the user (if known)
                doorbellDialog.show();
                doorbellDialog.addProperty("loggedIn", true); // Optionally add some properties
                doorbellDialog.setEmailFieldVisibility(View.GONE); // Hide the email field, since we've filled it in already
                doorbellDialog.setPoweredByVisibility(View.GONE);
                doorbellDialog.setMessageHint("Feel free to tell us anything!");
                doorbellDialog.setOnFeedbackSentCallback(new io.doorbell.android.callbacks.OnFeedbackSentCallback() {
                    @Override
                    public void handle(String message) {
                        // Show the message in a different way, or use your own message!
                        Toast.makeText(ExamPageActivity.this, "Thanks for writing to us!", Toast.LENGTH_LONG).show();
                    }
                });
                fragment =null;
                frag_title = "Exam";
                at_home=true;
                break;

            default:
                Toast.makeText(getApplicationContext(), "Something's Wrong", Toast.LENGTH_SHORT).show();
                break;
        }
        if (fragment != null) {
            home_title.setText(frag_title);
            //fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.frame));
            Fragment temp  = getSupportFragmentManager().findFragmentById(R.id.frame);

            if(temp==homefrag) {
                if(!at_home) {
                    fragmentTransaction.add(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }
            }
            else
            {
                if(!at_home) {
                    fragmentTransaction.remove(temp);
                    fragmentTransaction.add(R.id.frame, fragment);
                    fragmentTransaction.commit();
                }
                else
                {
                    fragmentTransaction.remove(temp);
                    fragmentTransaction.commit();
                }
            }
        }
        else
        {
            Fragment temp  = getSupportFragmentManager().findFragmentById(R.id.frame);
            if(temp!=null)
            {
                fragmentTransaction.remove(temp).commit();
                home_title.setText(frag_title);
            }
        }
    }

    @Override
    public void onBackPressed() {
//Go to home if the drawer is closed and the we are not on the HomeFragment (at_home flag checks for the latter)
        if(at_home==false && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
//Unchecking all the drawer menu items before going back to home
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
//Opening the HomeFragment
            frag_title="Exam";
            home_title.setText(frag_title);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.frame));
            fragmentTransaction.commit();
            at_home = true;
        }else if(at_home==true && !drawerLayout.isDrawerOpen(GravityCompat.START)){
//Implementation of "Click back again to exit"

            super.onBackPressed();
        }
        else
            drawerLayout.closeDrawers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.activityResumed();
        ConnectionChangeReceiver.broadcast(this);

        if (Branch.isAutoDeepLinkLaunch(this)) {
            try {
                String autoDeeplinkedValue = Branch.getInstance().getLatestReferringParams().getString("examId");
                testId = autoDeeplinkedValue;
                Log.i("sw32Deep","Launched by Branch on auto deep linking!"
                        + "\n\n" + autoDeeplinkedValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            testId = getIntent().getStringExtra("testId");
        }
        Log.i("sw32test",testId);

        Retrofit retrofit = new Retrofit.
                Builder()
                .baseUrl(MyApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApi myApi = retrofit.create(MyApi.class);
        MyApi.getTestRequest body = new MyApi.getTestRequest(testId, getSharedPreferences("CC", Context.MODE_PRIVATE).getString("profileId",""));
        Call<ModelTest> call = myApi.getTest(body);
        call.enqueue(new Callback<ModelTest>() {
            @Override
            public void onResponse(Call<ModelTest> call, Response<ModelTest> response) {
                ModelTest modelTest = response.body();
                testName.setText(modelTest.getExamTitle());
                desc.setText(modelTest.getExamDesc());
                uploader.setText(modelTest.getUploaderName());
                due.setText(modelTest.getDueDate());
                views.setText(modelTest.getViews());


                try {
                    String time = modelTest.getLastUpdated();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    Date lastUpdated  = df.parse(time);
                    Log.d("atul","lastUpd"+lastUpdated);
                    Log.d("atul:",time);
                    date.setReferenceTime(lastUpdated.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                /*
                int days = 0, hours = 0, minutes = 0, seconds = 0;
                try {
                    Calendar a = Calendar.getInstance();
                    Calendar b = Calendar.getInstance();
                    b.setTime(df.parse(time));
                    long difference = a.getTimeInMillis() - b.getTimeInMillis();
                    days = (int) (difference / (1000 * 60 * 60 * 24));
                    hours = (int) (difference / (1000 * 60 * 60));
                    minutes = (int) (difference / (1000 * 60));
                    seconds = (int) (difference / 1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (days == 0) {
                    if (hours == 0) {
                        if (minutes == 0) {
                            if (seconds == 0) {
                                date.setText("Just now");
                            } else {
                                if (seconds == 1) date.setText(seconds + " second ago");
                                else date.setText(seconds + " seconds ago");
                            }
                        } else {
                            if (minutes == 1) date.setText(minutes + " minute ago");
                            date.setText(minutes + " minutes ago");
                        }
                    } else {
                        if (hours == 1) date.setText(hours + " hour ago");
                        else date.setText(hours + " hours ago");
                    }
                } else {
                    if (days == 1) date.setText(days + " day ago");
                    else date.setText(days + " days ago");
                }*/
            }

            @Override
            public void onFailure(Call<ModelTest> call, Throwable t) {

            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        MyApp.activityPaused();
    }
}

