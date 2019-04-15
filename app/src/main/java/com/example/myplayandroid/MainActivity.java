package com.example.myplayandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<Item> itemList = new ArrayList<>();
    private DrawerLayout mDrawerLayout ;

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;//底部导航栏对象
    private List<Fragment> listFragment;//存储页面对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();//初始化界面

        //设置DrawerLayout内的ListView
        initItems(); //初始化滑动菜单内容
        final AdapterItem itemAdapter = new AdapterItem(MainActivity.this, R.layout.item_drawer, itemList);
        final ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = itemList.get(position);
                if (item.getName().equals( getString(R.string.menu_home) )) {
                    mDrawerLayout.closeDrawers(); //关闭侧滑菜单
                    viewPager.setCurrentItem(0); //滑动到第0页
                } else if (item.getName().equals( getString(R.string.menu_setting) )) {
                    Intent intent = new Intent(ContextApplication.getContext(), ActivityMenu.class);
                    startActivity(intent);
                } else if (item.getName().equals( getString(R.string.menu_exit) )) {
                    //退出应用
                    Intent intent = new Intent(ContextApplication.getContext(), MainActivity.class);
                    intent.putExtra("Exit_TAG", "SingleTASK");
                    startActivity(intent);
                }
            }
        });

    }

    //初始化页面， 加载ViewPager 和 BottomNavigationView
    private void initView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        //利用TextView设置Toolbar的标题居中
        initToolBar();

        // 解决当item大于三个时，非平均布局问题
        disableShiftMode();

        //向ViewPager添加各页面
        listFragment = new ArrayList<>();
        listFragment.add(new FragmentHome());
        listFragment.add(new FragmentKnow());
        listFragment.add(new FragmentProject());
        listFragment.add(new FragmentNavigation());
        AdapterFragment myAdapter = new AdapterFragment(getSupportFragmentManager(), this, listFragment);
        viewPager.setAdapter(myAdapter);

        //导航栏点击事件和ViewPager滑动事件,让两个控件相互关联
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //这里设置为：当点击到某子项，ViewPager就滑动到对应位置
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_dashboard:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_notifications:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.navigation_setting:
                        viewPager.setCurrentItem(3);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            TextView textView = (TextView) findViewById(R.id.toolbar_title);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //注意这个方法滑动时会调用多次，下面是参数解释：
                //position当前所处页面索引,滑动调用的最后一次绝对是滑动停止所在页面
                //positionOffset:表示从位置的页面偏移的[0,1]的值。
                //positionOffsetPixels:以像素为单位的值，表示与位置的偏移
            }

            @Override
            public void onPageSelected(int position) {
                //该方法只在滑动停止时调用，position滑动停止所在页面位置
//                当滑动到某一位置，导航栏对应位置被按下
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                //这里使用navigation.setSelectedItemId(position);无效，
                //setSelectedItemId(position)的官网原句：Set the selected
                // menu item ID. This behaves the same as tapping on an item
                //未找到原因
                MenuItem menuItem = bottomNavigationView.getMenu().getItem(position);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        textView.setText(R.string.title_home);
                        break;
                    case R.id.navigation_dashboard:
                        textView.setText(R.string.title_know);
                        break;
                    case R.id.navigation_notifications:
                        textView.setText(R.string.title_project);
                        break;
                    case R.id.navigation_setting:
                        textView.setText(R.string.title_navigation);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                这个方法在滑动是调用三次，分别对应下面三种状态
//                这个方法对于发现用户何时开始拖动，
//                何时寻呼机自动调整到当前页面，或何时完全停止/空闲非常有用。
//                state表示新的滑动状态，有三个值：
//                SCROLL_STATE_IDLE：开始滑动（空闲状态->滑动），实际值为0
//                SCROLL_STATE_DRAGGING：正在被拖动，实际值为1
//                SCROLL_STATE_SETTLING：拖动结束,实际值为2
            }
        });
    }

    //解决底部导航栏大小不均的问题
    @SuppressLint("RestrictedApi")
    private void disableShiftMode(){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    //导入菜单布局
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case  R.id.search:
                Toast.makeText(this, "You clicked Search", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //导入Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    //导入侧滑菜单内的内容
    private void initItems() {
        Item home = new Item(getString(R.string.menu_home), R.mipmap.home_message);
        itemList.add(home);
        Item heart = new Item(getString(R.string.menu_collection), R.mipmap.heart);
        itemList.add(heart);
        Item setting = new Item(getString(R.string.menu_setting), R.mipmap.setting);
        itemList.add(setting);
        Item information = new Item(getString(R.string.menu_information), R.mipmap.information);
        itemList.add(information);
        Item close = new Item(getString(R.string.menu_exit), R.mipmap.close);
        itemList.add(close);
    }

    //    判断是否获取所有权限，必须拥有全部权限程序才运行
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(ContextApplication.getContext(), "必须同意所有权限才能正常使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(ContextApplication.getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    //退出App
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tag = intent.getStringExtra("Exit_TAG");
        if (tag != null&& !TextUtils.isEmpty(tag)) {
            if ("SingleTASK".equals(tag)) {
                //退出程序
                finish();
            }
        }
    }
}
