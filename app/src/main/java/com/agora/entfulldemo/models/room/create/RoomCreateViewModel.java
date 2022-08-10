package com.agora.entfulldemo.models.room.create;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.entfulldemo.api.ApiException;
import com.agora.entfulldemo.api.ApiManager;
import com.agora.entfulldemo.api.ApiSubscriber;
import com.agora.entfulldemo.api.apiutils.SchedulersUtil;
import com.agora.entfulldemo.api.base.BaseResponse;
import com.agora.entfulldemo.api.model.RoomListModel;
import com.agora.entfulldemo.base.BaseRequestViewModel;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.manager.RTMManager;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.data.model.AgoraRoom;
import com.agora.entfulldemo.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

public class RoomCreateViewModel extends BaseRequestViewModel {
    private int current = 1;
    private int size = 30;

    public void loginRTM() {
        RTMManager.getInstance().doLoginRTM();
    }

    public void logOutRTM() {
        RTMManager.getInstance().doLogoutRTM();
    }

    /**
     * 加载房间列表
     */
    public void loadRooms() {
        ApiManager.getInstance().requestRoomList(current, size)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<RoomListModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<RoomListModel> data) {
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_GET_ROOM_LIST_SUCCESS
                                , data.getData().records);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        //创建失败
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_GET_ROOM_LIST_FAIL, null);
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    /**
     * 创建房间
     *
     * @param isPrivate 是否是私密 1 是 0 否
     * @param name      房间名称
     * @param password  房间密码
     * @param userNo    用户id
     * @param icon  icon图
     */
    public void requestCreateRoom(int isPrivate, String name,
                                  String password, String userNo, String icon) {
        ApiManager.getInstance().requestCreateRoom(isPrivate, name, password, userNo, icon)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        AgoraRoom room = new AgoraRoom();
                        room.roomNo = data.getData();
                        room.creatorNo = UserManager.getInstance().getUser().userNo;
                        room.isPrivate = isPrivate;
                        room.name = name;
                        room.belCanto = "0";
                        room.icon = icon;
                        if (isPrivate == 1) {
                            room.password = password;
                        }
                        RoomManager.getInstance().setAgoraRoom(room);
                        //创建成功 直接加入房间
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_CREATE_SUCCESS, null);
                        ToastUtils.showToast("创建房间成功");
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        //创建失败
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_CREATE_FAIL, null);
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }


}
