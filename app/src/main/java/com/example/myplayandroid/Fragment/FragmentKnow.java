package com.example.myplayandroid.Fragment;

/**
 * Created by zhongzhiqiang on 19-4-8.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.myplayandroid.ContextApplication;
import com.example.myplayandroid.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentKnow extends Fragment {

    public LocationClient locationClient;

    private TextView address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_know, container, false);
        //导入布局
        Button lbsButton = (Button) view.findViewById(R.id.lbs_button);
        address = (TextView) view.findViewById(R.id.address);
        //初始化定位
        locationClient = new LocationClient(ContextApplication.getContext());
        locationClient.registerLocationListener(new MyLocationListener());

        initPermission();

        lbsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address.setText("正在定位中...");
                locationClient.start();
            }
        });
        return view;
    }

    private void initPermission() {

        List<String> permissionList = new ArrayList<>();
        //一次性申请多个权限
        if (ContextCompat.checkSelfPermission(ContextApplication.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(ContextApplication.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(ContextApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        initLocation();
    }

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(0);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setIsNeedAddress(true);
        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setOpenGps(true);
        // 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        //设置定位方式为设备传感器即GPS定位
        option.setIsNeedLocationDescribe(true);
        //可选，是否需要位置描述信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的位置信息，此处必须为true
        locationClient.setLocOption(option);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationClient.stop();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(ContextApplication.getContext(), "必须同意所有权限才能正常使用本程序", Toast.LENGTH_SHORT).show();
////                            finish();
//                            return;
//                        }
//                    }
//                    requestLocation();
//                } else {
//                    Toast.makeText(ContextApplication.getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
////                    finish();
//                }
//                break;
//            default:
//                break;
//        }
//    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            StringBuilder currentPosition = new StringBuilder();
//            获取经纬度和地址
            currentPosition.append("纬度： ").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度： ").append(bdLocation.getLongitude()).append("\n");
            currentPosition.append(bdLocation.getCountry());
            currentPosition.append(bdLocation.getProvince());
            currentPosition.append(bdLocation.getCity());
            currentPosition.append(bdLocation.getDistrict());
            currentPosition.append(bdLocation.getStreet()).append("\n");
            currentPosition.append(bdLocation.getLocationDescribe());

            address.setText(currentPosition);

            int errorCode = bdLocation.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            Log.d("errorCode", "onReceiveLocation: " + errorCode);
            locationClient.stop();
        }
    }
}
