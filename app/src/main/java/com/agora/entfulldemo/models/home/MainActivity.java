package com.agora.entfulldemo.models.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActivityKt;
import androidx.navigation.NavController;
import androidx.navigation.ui.BottomNavigationViewKt;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseViewBindingActivity;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.databinding.ActivityMainBinding;
import com.agora.entfulldemo.manager.PagePathConstant;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.manager.UserManager;
import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * 主页容器
 */
@Route(path = PagePathConstant.pageMainHome)
public class MainActivity extends BaseViewBindingActivity<ActivityMainBinding> {
    private NavController navController;
    /**
     * 主页接收消息
     */
    private MainViewModel mainViewModel;

    @Override
    protected ActivityMainBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setLifecycleOwner(this);
        navController = ActivityKt.findNavController(this, R.id.nav_host_fragment_activity_main);
        BottomNavigationViewKt.setupWithNavController(getBinding().navView, navController);
        mainViewModel.setISingleCallback((type, data) -> {
            if (type == KtvConstant.CALLBACK_TYPE_USER_LOGOUT) {
                UserManager.getInstance().logout();
                finish();
                PagePilotManager.pageWelcome();
            }
        });
    }

    @Override
    protected boolean isCanExit() {
        return true;
    }

    @Override
    public void initListener() {
        getBinding().navView.setItemIconTintList(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
