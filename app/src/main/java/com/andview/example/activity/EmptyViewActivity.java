package com.andview.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andview.example.DensityUtil;
import com.andview.example.R;
import com.andview.example.recylerview.Person;
import com.andview.example.recylerview.SimpleAdapter;
import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;

import java.util.ArrayList;
import java.util.List;

public class EmptyViewActivity extends Activity {
    private RecyclerView recyclerView;
    private SimpleAdapter recyclerviewAdapter;
    private List<Person> personList = new ArrayList<Person>();
    private XRefreshView xRefreshView1;
    private LinearLayoutManager layoutManager;
    private boolean isBottom = false;

    private LinearLayout linearLayout;
    private XRefreshView xRefreshView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emptyview);
        initRecyclerView();
        initScrollView();
        configXRfreshView(xRefreshView1, new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xRefreshView1.stopRefresh();
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        for (int i = 0; i < 1; i++) {
                            recyclerviewAdapter.insert(new Person("More ", "21" + recyclerviewAdapter.getAdapterItemCount()),
                                    recyclerviewAdapter.getAdapterItemCount());
                        }
                        // ?????????????????????????????????????????????
                        xRefreshView1.stopLoadMore();
                    }
                }, 1000);
            }
        });
        configXRfreshView(xRefreshView2, new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xRefreshView2.stopRefresh();
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // ?????????????????????????????????????????????
                        xRefreshView2.stopLoadMore();
                    }
                }, 1000);
            }
        });
        setEmptyViewClickListener(xRefreshView1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRecyclerViewData();
            }
        });
        setEmptyViewClickListener(xRefreshView2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???Recyclerview??????????????????????????????????????????????????????enableEmptyView????????????????????????emptyView???
                xRefreshView2.enableEmptyView(false);
            }
        });
        requestRecyclerViewData();
        requestScrllViewData();
    }

    private void setEmptyViewClickListener(XRefreshView xRefreshView, View.OnClickListener listener) {
        View emptyView = xRefreshView.getEmptyView();
        if (emptyView != null) {
            emptyView.setOnClickListener(listener);
        }
    }

    private void initRecyclerView() {
        xRefreshView1 = (XRefreshView) findViewById(R.id.xrefreshview);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);

        recyclerviewAdapter = new SimpleAdapter(personList, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerviewAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
    }

    private void initScrollView() {
        xRefreshView2 = (XRefreshView) findViewById(R.id.xrefreshview2);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

    }

    private void configXRfreshView(XRefreshView xRefreshView, XRefreshView.SimpleXRefreshListener listener) {
        xRefreshView.setPullLoadEnable(true);
        //???????????????????????????headerview???????????????
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setAutoLoadMore(true);
        //????????????????????????????????????????????????view??????????????????id?????????
//        TextView textView = new TextView(this);
//        textView.setText("???????????????????????????");
//        textView.setGravity(Gravity.CENTER);
//        xRefreshView.setEmptyView(textView);
        xRefreshView.setEmptyView(R.layout.layout_emptyview);
        xRefreshView.setXRefreshViewListener(listener);
    }

    private void requestScrllViewData() {
        for (int i = 0; i < 50; i++) {
            TextView tv = new TextView(this);
            tv.setTextSize(16);
            int padding = DensityUtil.dip2px(this, 20);
            tv.setPadding(padding, padding, 0, 0);
            tv.setTextIsSelectable(true);
            tv.setText("??????" + i);
            linearLayout.addView(tv);
        }
    }

    private void requestRecyclerViewData() {
        personList.clear();
        for (int i = 0; i < 6; i++) {
            Person person = new Person("name" + i, "" + i);
            personList.add(person);
        }
        recyclerviewAdapter.setData(personList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ????????????
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        boolean showingRecyclerView = xRefreshView1.getVisibility() == View.VISIBLE;
        switch (menuId) {
            case R.id.menu_change:
                xRefreshView1.setVisibility(showingRecyclerView ? View.GONE : View.VISIBLE);
                xRefreshView2.setVisibility(showingRecyclerView ? View.VISIBLE : View.GONE);
                toast(showingRecyclerView ? "?????????ScrollView" : "?????????Recyclerview");
                break;
            case R.id.menu_clear_or_fill:
                if (showingRecyclerView) {
                    if (recyclerviewAdapter.getAdapterItemCount() != 0) {
                        recyclerviewAdapter.clear();
                    } else {
                        requestRecyclerViewData();
                    }
                } else {
                    //???Recyclerview??????????????????????????????????????????????????????enableEmptyView????????????????????????emptyView???
                    xRefreshView2.enableEmptyView(!xRefreshView2.isEmptyViewShowing());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}