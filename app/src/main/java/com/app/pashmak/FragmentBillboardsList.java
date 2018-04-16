package com.app.pashmak;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.pashmak.Adapter.BillboardsAdapter;
import com.app.pashmak.Adapter.ProvinceCountyListAdapter;
import com.app.pashmak.Adapter.ViewPagerAdapter;
import com.app.pashmak.Model.Billboard;
import com.app.pashmak.Utils.NetUtils;
import com.app.pashmak.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBillboardsList extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "tagg";
    private Toolbar toolbar;
    RecyclerView rvBillboards;
    LinearLayoutManager manager;
    BillboardsAdapter billboardsAdapter;
    SwipeRefreshLayout refreshBillboards;
    boolean isScrolling = false;
    int currentItems, totalItems, ScrollOutItems;
    private ProgressBar progressBarBillboards;
    public static ArrayList billboards;
    static int page = 0;
    private NetUtils netUtils;
    public static boolean endOfBillboards = false;
    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPagerBillboards;
    private ViewPagerAdapter viewPagerAdapter;
    Context context;
    private View view;
    private AppCompatAutoCompleteTextView etSearch;
    private FragmentActivity activity;
    private ImageButton ibDeleteSearch;
    private String where = "";
    public static boolean onRefresh = false;
    private View bsFilterBillboards;
    private BottomSheetBehavior<View> bshBehavior;
    private View btnToggleFilterLayout;

    private Button btnFilterProvinceCounties;

    private View provinceCountyView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogProvinceCounties;
    private AlertDialog.Builder dialogBuilder2;
    private AlertDialog dialogStatus;
    private MyCardview cvStatus;
    private View statusFilterView;
    private GridView gvStatusSelection;
    private ImageView ivHolderStatusFilter;

    public FragmentBillboardsList() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

//    private Communicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_billboards_list, container, false);
        context = view.getContext();
        activity = getActivity();
        viewPagerBillboards = view.findViewById(R.id.viewPagerBillboards);
        appBar = view.findViewById(R.id.appBarBillboards);
        tabLayout = view.findViewById(R.id.tabLayout);
        rvBillboards = view.findViewById(R.id.recycler_view_billboards);
        progressBarBillboards = view.findViewById(R.id.progressBillboards);
        refreshBillboards = view.findViewById(R.id.swipeRefreshBillboards);
        etSearch = activity.findViewById(R.id.etSearch);
        ibDeleteSearch = activity.findViewById(R.id.iBDeleteSearch);

        bsFilterBillboards = activity.findViewById(R.id.billBoards_filter_bottomSheet);
        bshBehavior = BottomSheetBehavior.from(bsFilterBillboards);
        btnToggleFilterLayout = activity.findViewById(R.id.button_filterLayout_toggle);
        btnToggleFilterLayout.setOnClickListener(this);
        //remove it
        bshBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        LayoutInflater filterInflater = getLayoutInflater();

        provinceCountyView = filterInflater.inflate(R.layout.expandable_province_counties, null);
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(provinceCountyView);
        dialogBuilder.setCancelable(true);
        dialogProvinceCounties = dialogBuilder.create();
//        dialogProvinceCounties.show();

        statusFilterView = filterInflater.inflate(R.layout.status_filter_layout, null);
        dialogBuilder.setView(statusFilterView);
        dialogBuilder.setCancelable(true);
        dialogStatus = dialogBuilder.create();

        ExpandableListView expProvincecounties = provinceCountyView.findViewById(R.id.expandable_province_counties);
        ProvinceCountyListAdapter expProvinceCountiesAdapter = new ProvinceCountyListAdapter(context);
        expProvincecounties.setAdapter(expProvinceCountiesAdapter);
        expProvincecounties.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Utils.mToast(context, groupPosition + "," + childPosition, Toast.LENGTH_SHORT);
                Log.d(TAG, "onChildClick: " + groupPosition + "," + childPosition);
                return false;
            }
        });

        btnFilterProvinceCounties = activity.findViewById(R.id.btn_filter_provinceCounties);
        btnFilterProvinceCounties.setOnClickListener(this);

        cvStatus = activity.findViewById(R.id.cvShowStatusFilters);
        cvStatus.setOnClickListener(this);
        cvStatus.setOnInterceptTouchEventListener(new MyCardview.OnInterceptTouchEventListener() {
            @Override
            public boolean onInterceptTouchEvent(MyCardview view, MotionEvent ev, boolean disallowIntercept) {
                return true;
            }

            @Override
            public boolean onTouchEvent(MyCardview view, MotionEvent event) {
                dialogStatus.show();
                return true;
            }
        });

        ImageView iv = new ImageView(context);
        iv.setImageResource(R.drawable.status_auction_background);

//        ivHolderStatusFilter = new android.support.v7.widget.AppCompatImageView(context) {
//            @Override
//            public boolean onTouchEvent(MotionEvent event) {
//                return false;
//            }
//        };

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        int[] StatusDrawablesArray = Utils.getStatusDrawablesArray();
        for (int statusDrawable : StatusDrawablesArray) {
            HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("image", statusDrawable + "");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from = {"image"};//string array
        int[] to = {R.id.iv_selected_status};//int array of views id's
        SimpleAdapter statusAdapter = new SimpleAdapter(context, arrayList, R.layout.selected_status_filter_row, from, to);

        gvStatusSelection = activity.findViewById(R.id.gv_status);
        gvStatusSelection.setAdapter(statusAdapter);


        init();

//        getMoreData(where);

        rvBillboards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                ScrollOutItems = manager.findFirstVisibleItemPosition();

                if (!onRefresh && !endOfBillboards && isScrolling && (currentItems + ScrollOutItems == totalItems)) {
                    isScrolling = false;
                    Log.d(TAG, "onScrolled: ");
                    getMoreData(where);
                }

            }
        });
        refreshBillboards.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefresh = true;
                refresh();
                refreshBillboards.setRefreshing(false);
            }
        });

        return view;
    }

    private void init() {

        ibDeleteSearch.setOnClickListener(this);
        refreshBillboards.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorAccent,
                R.color.colorPrimaryDark);
        manager = new GridLayoutManager(context, getColumnFromDeviceSize());

        billboards = new ArrayList<Billboard>();

        billboardsAdapter = new BillboardsAdapter(billboards, context);

        rvBillboards.setLayoutManager(manager);
        rvBillboards.setAdapter(billboardsAdapter);

        netUtils = new NetUtils(view, billboardsAdapter);
        netUtils.setCommunicator((NetUtils.Communicator) activity);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || actionId == EditorInfo.IME_ACTION_GO
//                        || actionId == EditorInfo.IME_ACTION_NEXT
//                        || (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        || (keyEvent != null && keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)

                        ) {

                    search();
                }

                return false;
            }
        });


    }

    private void refresh() {
        Log.d(TAG, "refreshing ");
        page = 0;
        isScrolling = false;
        endOfBillboards = false;
        billboards.clear();
        getMoreData(where);
        refreshBillboards.setRefreshing(false);
    }


    private void search() {
        String str = etSearch.getText().toString();
        if (!str.equals(""))
            where = "address LIKE '%" + str + "%'";
        else where = "";
        refresh();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_billboards, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(activity);
                //add parent in child activity ( in manifest)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

    }

    private void getMoreData(String where) {
        if (!endOfBillboards) {
            page++;
            Log.d(TAG, "getMoreData: " + page);
            netUtils.getBillboards(String.valueOf(page), where);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.iBDeleteSearch:
                etSearch.setText(null);
                where = "";
                refresh();
                break;
            case R.id.button_filterLayout_toggle:
                if (bshBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bshBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bshBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
                break;
            case R.id.btn_filter_provinceCounties:
                dialogProvinceCounties.show();
                break;
            case R.id.cvShowStatusFilters:
                dialogStatus.show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private int getColumnFromDeviceSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            // 6.5inch device or bigger
            return 2;
        } else {
            // smaller device
            return 1;
        }

    }
}