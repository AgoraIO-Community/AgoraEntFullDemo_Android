package com.agora.entfulldemo.models.room.live.fragment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.entfulldemo.api.ApiException;
import com.agora.entfulldemo.api.ApiManager;
import com.agora.entfulldemo.api.ApiSubscriber;
import com.agora.entfulldemo.api.apiutils.GsonUtils;
import com.agora.entfulldemo.api.apiutils.SchedulersUtil;
import com.agora.entfulldemo.api.base.BaseResponse;
import com.agora.entfulldemo.base.BaseRequestViewModel;
import com.agora.entfulldemo.bean.MemberMusicModel;
import com.agora.entfulldemo.bean.Page;
import com.agora.entfulldemo.bean.PageModel;
import com.agora.entfulldemo.bean.room.RTMMessageBean;
import com.agora.entfulldemo.dialog.RoomChooseSongDialog;
import com.agora.entfulldemo.event.ReceivedMessageEvent;
import com.agora.entfulldemo.manager.RTMManager;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.entfulldemo.models.room.live.RoomLivingViewModel;
import com.agora.data.model.BaseMusicModel;
import com.agora.data.model.MusicModelNew;
import com.agora.entfulldemo.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class SongsViewModel extends BaseRequestViewModel {

    public int size = 20;

    public void onStart() {

    }

    public void onStop() {

    }

    public int pageSearch = 1;
    public PageModel pageModel;

    public void searchSong(String searchKey) {
        if (pageModel == null) {
            pageModel = new PageModel();
            pageModel.page = new Page();
            pageModel.page.current = pageSearch;
            pageModel.page.size = 100;
        }
        pageModel.name = searchKey;
        ApiManager.getInstance().requestSearchSong(pageModel)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<BaseMusicModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<BaseMusicModel> data) {
                        getISingleCallback().onSingleCallback(2, data.getData());
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }


    public void getSongOrdersList() {
        ApiManager.getInstance().requestGetSongsOrderedList(RoomManager.mRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<List<MemberMusicModel>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<List<MemberMusicModel>> data) {
                        RoomManager.getInstance().onMusicEmpty(false);
                        for (MemberMusicModel model : data.getData()) {
                            RoomManager.getInstance().onMusicAdd(model);
                        }
                        getISingleCallback().onSingleCallback(0, null);

                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    public int page = 1;
    public boolean haveMore = true;

    public void getHotBinkList(int type) {
        ApiManager.getInstance().requestGetSongsList(page, type)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<BaseMusicModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<BaseMusicModel> data) {
                        if (data.getData().size == page) {
                            haveMore = false;
                        }
                        getISingleCallback().onSingleCallback(1, data.getData());
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    //点歌
    public void requestChooseSong(MusicModelNew musicModelNew) {
        String userNo = UserManager.getInstance().getUser().userNo;
        String roomNo = RoomManager.mRoom.roomNo;
        int isChorus = RoomChooseSongDialog.isChorus ? 1 : 0;
        ApiManager.getInstance().requestChooseSong(musicModelNew.imageUrl, isChorus, 0, musicModelNew.singer, musicModelNew.songName,
                String.valueOf(musicModelNew.songNo), musicModelNew.imageUrl, userNo, roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        getSongOrdersList();

                        RTMMessageBean bean = new RTMMessageBean();
                        bean.messageType = RoomLivingViewModel.MESSAGE_ROOM_TYPE_CHOOSE_SONG;
                        bean.roomNo = RoomManager.mRoom.roomNo;
                        String json = GsonUtils.Companion.getGson().toJson(bean);
                        RTMManager.getInstance().sendMessage(json);
                        EventBus.getDefault().post(new ReceivedMessageEvent(json));
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    //删歌
    public void requestDeleteSong(MemberMusicModel memberMusicModel, int position) {
        String userNo = UserManager.getInstance().getUser().userNo;
        String roomNo = RoomManager.mRoom.roomNo;
        ApiManager.getInstance().requestDeleteSong(memberMusicModel.sort, memberMusicModel.songNo, userNo, roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        Log.d("cwtsw", "删除歌曲");
                        //删歌推送 推送
                        RTMMessageBean bean = new RTMMessageBean();
                        bean.headUrl = UserManager.getInstance().getUser().headUrl;
                        bean.messageType = RoomLivingViewModel.MESSAGE_ROOM_TYPE_SWITCH_SONGS;
                        bean.userNo = UserManager.getInstance().getUser().userNo;
                        bean.name = UserManager.getInstance().getUser().name;

                        RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                        RoomManager.getInstance().onMusicDelete(memberMusicModel.songNo, position);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    //置顶
    public void requestTopSong(MemberMusicModel memberMusicModel) {
        String userNo = UserManager.getInstance().getUser().userNo;
        String roomNo = RoomManager.mRoom.roomNo;
        ApiManager.getInstance().requestTopSong(memberMusicModel.sort, memberMusicModel.songNo, userNo, roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        RoomManager.getInstance().onTopMusic(memberMusicModel.songNo);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }
}
