package com.app.pashmak;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.app.pashmak.Adapter.BillboardsAdapter;
import com.app.pashmak.Adapter.PlaceAutocompleteAdapter;
import com.app.pashmak.Adapter.ViewPagerAdapter;
import com.app.pashmak.Model.Billboard;
import com.app.pashmak.Utils.NetUtils;
import com.app.pashmak.Utils.Utils;
import com.app.pashmak.Utils.VolleySingleton;

import java.util.ArrayList;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements NetUtils.Communicator {
    public static final String TAG = "tagg";
    private Toolbar toolbar;
    RecyclerView rvBillboards;

    BillboardsAdapter billboardsAdapter;
    SwipeRefreshLayout refreshBillboards;


    public static ArrayList billboards;


    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPagerBillboards;
    private ViewPagerAdapter viewPagerAdapter;
    private MyNestedScrollView nestedscrollView;
    private AutoCompleteTextView etSearch;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    public static int currentPage;
    private FragmentBillboardsList fList;
    private FragmentBillboardsMap fMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            new AlertDialog.Builder(this).setTitle("هشدار!")
                    .setMessage("در صورت عدم نمایش صحیح از اندروید 4.2 به بالا استفاده نمایید")
                    .setCancelable(true).setPositiveButton("تایید", null).show();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPagerBillboards = findViewById(R.id.viewPagerBillboards);
        toolbar = findViewById(R.id.toolbarBillboards);
        appBar = findViewById(R.id.appBarBillboards);
        tabLayout = findViewById(R.id.tabLayout);
        etSearch = findViewById(R.id.etSearch);


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);


        viewPagerBillboards.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPagerBillboards);

        viewPagerBillboards.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //0 list
                //1 map
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        fList = (FragmentBillboardsList) viewPagerAdapter.getItem(0);
        fMap = (FragmentBillboardsMap) viewPagerAdapter.getItem(1);

//        toolbar.setLogo(R.drawable.ic_launcher_foreground);
        toolbar.inflateMenu(R.menu.menu_billboards);
        //        toolbar.setNavigationIcon(R.drawable.ic_launcher_foreground);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(null);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Utils.changeAppbarFont(toolbar, tabLayout, this);
        Utils.hideSoftKeyboard(this);


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_billboards, menu);
//        return true;
//    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleySingleton.getInstance(this).cancelPendingRequests("billboard");
        VolleySingleton.getInstance(this).cancelPendingRequests("image");
    }


    @Override
    public void refreshMapFragment(ArrayList<Billboard> billboards) {
        if (null != fMap) {
            fMap.viewSerachedMarkers(billboards);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();

    }
}
