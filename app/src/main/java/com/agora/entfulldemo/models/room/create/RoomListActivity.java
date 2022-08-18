package com.agora.entfulldemo.models.room.create;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.agora.data.model.AgoraRoom;
import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.base.BaseViewBindingActivity;
import com.agora.entfulldemo.base.OnItemClickListener;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.databinding.ActivityRoomListBinding;
import com.agora.entfulldemo.databinding.ItemRoomListBinding;
import com.agora.entfulldemo.dialog.InputPasswordDialog;
import com.agora.entfulldemo.manager.PagePathConstant;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.entfulldemo.models.room.create.holder.RoomHolder;
import com.agora.entfulldemo.utils.SPUtil;
import com.agora.entfulldemo.utils.ToastUtils;
import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.List;

/**
 * 房间列表
 */
@Route(path = PagePathConstant.pageRoomList)
public class RoomListActivity extends BaseViewBindingActivity<ActivityRoomListBinding> {
    private BaseRecyclerViewAdapter<ItemRoomListBinding, AgoraRoom, RoomHolder> mAdapter;
    private RoomCreateViewModel roomCreateViewModel;
    private InputPasswordDialog inputPasswordDialog;

    @Override
    protected ActivityRoomListBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityRoomListBinding.inflate(inflater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoomList();
    }

    private void loadRoomList() {
        SPUtil.putBoolean(KtvConstant.IS_AGREE, true);
        roomCreateViewModel.loadRooms();
    }

    @Override
    protected void onStart() {
        super.onStart();
        roomCreateViewModel.loginRTM();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        roomCreateViewModel.logOutRTM();
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        roomCreateViewModel = new ViewModelProvider(this).get(RoomCreateViewModel.class);
        roomCreateViewModel.setLifecycleOwner(this);
        mAdapter = new BaseRecyclerViewAdapter<>(null, new OnItemClickListener<AgoraRoom>() {
            @Override
            public void onItemClick(@NonNull AgoraRoom data, View view, int position, long viewType) {
                if (!TextUtils.isEmpty(data.password)
                        && !UserManager.getInstance().getUser().userNo.equals(data.creatorNo)) {
                    showInputPwdDialog(data);
                } else {
                    RoomManager.getInstance().setAgoraRoom(data);
                    PagePilotManager.pageRoomLiving();
                }

            }
        }, RoomHolder.class);
        getBinding().rvRooms.setLayoutManager(new GridLayoutManager(this, 2));
        getBinding().rvRooms.setAdapter(mAdapter);
        getBinding().smartRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void initListener() {
        getBinding().btnCreateRoom.setOnClickListener(view -> {
            PagePilotManager.pageCreateRoomStep1();
        });
        getBinding().btnCreateRoom2.setOnClickListener(view -> {
            PagePilotManager.pageCreateRoomStep1();
        });
        roomCreateViewModel.setISingleCallback((type, o) -> {
            hideLoadingView();
            getBinding().smartRefreshLayout.finishRefresh();
            if (type == KtvConstant.CALLBACK_TYPE_ROOM_GET_ROOM_LIST_SUCCESS) {
                if (o != null) {
                    mAdapter.setDataList((List<AgoraRoom>) o);
                    if (!mAdapter.dataList.isEmpty()) {
                        getBinding().rvRooms.setVisibility(View.VISIBLE);
                        getBinding().btnCreateRoom.setVisibility(View.VISIBLE);
                        getBinding().tvTips1.setVisibility(View.GONE);
                        getBinding().ivBgMobile.setVisibility(View.GONE);
                        getBinding().btnCreateRoom2.setVisibility(View.GONE);
                    } else {
                        getBinding().rvRooms.setVisibility(View.GONE);
                        getBinding().btnCreateRoom.setVisibility(View.GONE);
                        getBinding().tvTips1.setVisibility(View.VISIBLE);
                        getBinding().ivBgMobile.setVisibility(View.VISIBLE);
                        getBinding().btnCreateRoom2.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        getBinding().smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            loadRoomList();
        });
    }

    private void showInputPwdDialog(AgoraRoom data) {
        if (inputPasswordDialog == null) {
            inputPasswordDialog = new InputPasswordDialog(this);
        }
        inputPasswordDialog.iSingleCallback = (type, o) -> {
            if (data.password.equals(o)) {
                RoomManager.getInstance().setAgoraRoom(data);
                PagePilotManager.pageRoomLiving();
            } else {
                ToastUtils.showToast("密码不正确");
            }
        };
        inputPasswordDialog.show();
    }
}
