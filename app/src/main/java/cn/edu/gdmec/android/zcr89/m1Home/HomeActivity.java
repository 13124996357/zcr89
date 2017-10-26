package cn.edu.gdmec.android.zcr89.m1Home;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import cn.edu.gdmec.android.zcr89.R;
import cn.edu.gdmec.android.zcr89.m1Home.adapter.HomeAdapter;
import cn.edu.gdmec.android.zcr89.m2theftguard.LostFindActivity;
import cn.edu.gdmec.android.zcr89.m2theftguard.dialog.InterPasswordDialog;
import cn.edu.gdmec.android.zcr89.m2theftguard.dialog.SetUpPasswordDialog;
import cn.edu.gdmec.android.zcr89.m2theftguard.receiver.MyDeviceAdminReciever;
import cn.edu.gdmec.android.zcr89.m2theftguard.utils.MD5Utils;


public class HomeActivity extends AppCompatActivity {

    private long mExitTime;
    private GridView gv_home;
    private SharedPreferences msharedPreferences;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        msharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //初始化gridView
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        //设置条目的点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://手机防盗
                        if (isSetUpPassword()) {
                            showInterPswdDialog();
                        } else {
                            showSetUpPswdDialog();
                        }
                        break;
                    case 1://通讯卫士
                        //startActivity(SecurityPhoneActivity.class);
                        break;
                    case 2://软件管家
                        //startActivity(AppManagerActivity.class);
                        break;
                    case 3://病毒查杀
                        //startActivity(VirusScanActivity.class);
                        break;
                    case 4://缓存清理
                        //startActivity(CacheClearListActivity.class);
                    case 5://进程管理
                        //startActivity(ProcessManagerActivity.class);
                        break;
                    case 6://流量统计
                        //startActivity(TrafficMonitoringActivity.class);
                        break;
                    case 7://高级工具
                        //startActivity(AdvancedToolsActivity.class);
                        break;
                    case 8://设置中心
                        //startActivity(SettingsActivity.class);
                        break;

                }
            }
        });
        policyManager=(DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName=new ComponentName(this, MyDeviceAdminReciever.class);
        boolean active=policyManager.isAdminActive(componentName);
        if (!active){
            Intent intent= new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"获取超级管理员权限，用于远程锁屏和清除数据");
            startActivity(intent);
        }
    }


    public void startActivity(Class<?> cls){
        Intent intent=new Intent(HomeActivity.this,cls);
       startActivity(intent);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if((System.currentTimeMillis()-mExitTime)>2000){
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime=System.currentTimeMillis();
            }else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showSetUpPswdDialog(){
        final SetUpPasswordDialog setUpPassWordDialog =new SetUpPasswordDialog(HomeActivity.this);
        setUpPassWordDialog.setCallBack(new SetUpPasswordDialog.MyCallBack(){
            @Override
            public void ok(){
                String firstPwsd= setUpPassWordDialog.mFirstPWDET.getText().toString().trim();
                String affirmPwsd= setUpPassWordDialog.mAffirmET.getText().toString().trim();
                if(!TextUtils.isEmpty(firstPwsd)&&!TextUtils.isEmpty(affirmPwsd)){
                    if(firstPwsd.equals(affirmPwsd)){
                        savePswd(affirmPwsd);
                        setUpPassWordDialog.dismiss();
                        showInterPswdDialog();
                    }else {
                        Toast.makeText(HomeActivity.this,"两次密码不一致",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this,"两次不能为空",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void cancle() {
                setUpPassWordDialog.dismiss();
            }
        });
        setUpPassWordDialog.setCancelable(true);
        setUpPassWordDialog.show();
    }
    private void showInterPswdDialog() {
        final String password=getPassword();
        final InterPasswordDialog mInPswdDialog=new InterPasswordDialog(HomeActivity.this);
        mInPswdDialog.setCallBack(new InterPasswordDialog.MyCallBack(){
            @Override
            public void confirm(){
                if(TextUtils.isEmpty(mInPswdDialog.getPassword())){
                    Toast.makeText(HomeActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
                    }else if(password.equals(MD5Utils.encode(mInPswdDialog.getPassword()))){
                        mInPswdDialog.dismiss();
                        startActivity(LostFindActivity.class);
                        Toast.makeText(HomeActivity.this,"可以进入手机防盗模块",Toast.LENGTH_LONG).show();
                    }else {
                    mInPswdDialog.dismiss();
                    Toast.makeText(HomeActivity.this,"密码有误，请重新输入",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void cancel() {
                mInPswdDialog.dismiss();
            }
        });
        mInPswdDialog.setCancelable(true);
        mInPswdDialog.show();
    }


    private void savePswd(String affirmPwsd) {
        SharedPreferences.Editor edit=msharedPreferences.edit();
        edit.putString("PhoneAntiTheftPWD",MD5Utils.encode(affirmPwsd));
        edit.commit();
    }

    private String getPassword() {
        String password=msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if (TextUtils.isEmpty(password)){
            return "";
        }
        return password;
    }

    private boolean isSetUpPassword() {
        String password=msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if (TextUtils.isEmpty(password)){
            return false;
        }
        return true;
    }
}
