package com.agora.entfulldemo.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;

import com.agora.data.model.AgoraMember;
import com.agora.entfulldemo.R;
import com.agora.entfulldemo.api.model.User;
import com.agora.entfulldemo.bean.MemberMusicModel;
import com.agora.entfulldemo.utils.ToastUtils;

import io.agora.musiccontentcenter.IAgoraMusicPlayer;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SingleMusicPlayer extends BaseMusicPlayer {

    public SingleMusicPlayer(Context mContext, int role, IAgoraMusicPlayer mPlayer) {
        super(mContext, role, mPlayer);
        RTCManager.getInstance().getRtcEngine().setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO);
    }

    @Override
    public void switchRole(int role) {
        mLogger.d("switchRole() called with: role = [%s]", role);

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishMediaPlayerId = mPlayer.getMediaPlayerId();
        if (role == Constants.CLIENT_ROLE_BROADCASTER) {
            options.clientRoleType = role;
            if (RoomManager.mMine.isSelfMuted == 0) {
                options.publishAudioTrack = true;
            }
            options.publishMediaPlayerAudioTrack = true;
        } else if (RoomManager.mMine.role != AgoraMember.Role.Listener) {
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            if (RoomManager.mMine.isSelfMuted == 0) {
                options.publishAudioTrack = true;
            }
            options.publishMediaPlayerAudioTrack = true;
        } else {
            options.clientRoleType = role;
            options.publishAudioTrack = false;
            options.publishMediaPlayerAudioTrack = false;
        }
        mRole = options.clientRoleType;
        RTCManager.getInstance().getRtcEngine().updateChannelMediaOptions(options);
    }

    @Override
    public void prepare(@NonNull MemberMusicModel music) {
        User mUser = UserManager.getInstance().getUser();
        if (mUser == null) {
            return;
        }
        onPrepareResource();
        if (ObjectsCompat.equals(music.userNo, mUser.userNo)) {
            switchRole(Constants.CLIENT_ROLE_BROADCASTER);
            ResourceManager.Instance(mContext)
                    .download(music, true)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<MemberMusicModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull MemberMusicModel musicModel) {
                            onResourceReady(musicModel);
                            RoomManager.getInstance().mMusicModel.fileLrc = musicModel.fileLrc;
                            if (RTCManager.getInstance().preLoad(musicModel.songNo)) {
                                open(musicModel);
                            }
//
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastUtils.showToast(R.string.ktv_lrc_load_fail);
                        }
                    });
        } else {
            switchRole(Constants.CLIENT_ROLE_AUDIENCE);
            ResourceManager.Instance(mContext)
                    .download(music, true)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<MemberMusicModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull MemberMusicModel musicModel) {
                            onResourceReady(musicModel);
                            onMusicPlaingByListener();
                            playByListener(musicModel);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastUtils.showToast(R.string.ktv_lrc_load_fail);
                        }
                    });
        }
    }
}
