package com.intech.topfindprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.intech.topfindprovider.Activities.Provider.ProviderLoginActivity;
import com.intech.topfindprovider.Activities.Service.FinderLoginActivity;
import com.intech.topfindprovider.Adapters.SlideAdpter;

public class MainActivity extends AppCompatActivity {

    private ViewPager mSliderViewPager;
    private LinearLayout mDotsLayout,ChooseLayout;

    private TextView[] mDots;
    TextView privacy;
    private SlideAdpter slideAdpter;
    private FloatingActionButton NextBtn,BackBtn;
    FirebaseAuth mAuth;

    private  int mCurrentPage;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private Button get_started;


    Button button;
    RadioButton genderradioButton;
    RadioGroup radioGroup;
    private String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mSliderViewPager = (ViewPager) findViewById(R.id.Slide_viewPager);
        mDotsLayout = (LinearLayout) findViewById(R.id.Dot_layout);
        NextBtn  = (FloatingActionButton) findViewById(R.id.Next);
        BackBtn = (FloatingActionButton) findViewById(R.id.Back);
        radioGroup = findViewById(R.id.radioGroup);


        mAuth = FirebaseAuth.getInstance();
        get_started = findViewById(R.id.get_Started);
        slideAdpter = new SlideAdpter(this);
        linearLayout = findViewById(R.id.Launch_Layout);
        ChooseLayout = findViewById(R.id.LayoutChoose);
        addDotsIndicator(0);
        mSliderViewPager.setAdapter(slideAdpter);
        mSliderViewPager.addOnPageChangeListener(viewListener);


        radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_started.setVisibility(View.VISIBLE);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId){
                    case R.id.radio1:
                        // do operations specific to this selection
                        get_started.setVisibility(View.VISIBLE);
                        TAG = "Provider";
                        break;
                    case R.id.radio2:
                        // do operations specific to this selection
                        get_started.setVisibility(View.VISIBLE);
                        TAG = "Service";
                        break;
                }
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPage == 2){
                    mSliderViewPager.setCurrentItem(-1);
                }else if (mCurrentPage == 1){
                    mSliderViewPager.setCurrentItem(-1);
                }

            }
        });





        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentPage == 0){
                    mSliderViewPager.setCurrentItem(+1);
                }else if (mCurrentPage == 1){
                    mSliderViewPager.setCurrentItem(+2);
                }

            }
        });

        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TAG.equals("Provider")){

                    Intent toCreate = new Intent(getApplicationContext(), ProviderLoginActivity.class);
                    startActivity(toCreate);

                }else if (TAG.equals("Service")){

                    Intent toCreate = new Intent(getApplicationContext(), FinderLoginActivity.class);
                    startActivity(toCreate);


                }



            }
        });




    }



    private void addDotsIndicator(int position) {


        mDots = new TextView[2];
        mDotsLayout.removeAllViews();

        for (int i = 0 ; i< mDots.length; i++ ){


            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(50);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparent));
            mDotsLayout.addView(mDots[i]);

        }

        if (mDots.length > 0)
        {
            mDots[position].setTextColor(getResources().getColor(R.color.secondary));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mCurrentPage= position;
        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0)
            {
                NextBtn.setEnabled(true);
                BackBtn.setEnabled(false);
                BackBtn.setVisibility(View.GONE);
                NextBtn.setVisibility(View.VISIBLE);
                get_started.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                ChooseLayout.setVisibility(View.GONE);
                get_started.setEnabled(false);


            }else if (position == 1){

                NextBtn.setEnabled(true);
                BackBtn.setEnabled(true);
                BackBtn.setVisibility(View.GONE);
                NextBtn.setVisibility(View.GONE);

                linearLayout.setVisibility(View.VISIBLE);
                get_started.setEnabled(true);
                ChooseLayout.setVisibility(View.VISIBLE);

            } else {

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}