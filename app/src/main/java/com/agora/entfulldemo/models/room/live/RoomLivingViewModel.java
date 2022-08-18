package com.agora.entfulldemo.models.room.live;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.api.ApiException;
import com.agora.entfulldemo.api.ApiManager;
import com.agora.entfulldemo.api.ApiSubscriber;
import com.agora.entfulldemo.api.apiutils.GsonUtils;
import com.agora.entfulldemo.api.apiutils.SchedulersUtil;
import com.agora.entfulldemo.api.base.BaseResponse;
import com.agora.entfulldemo.api.model.User;
import com.agora.entfulldemo.bean.CommonBean;
import com.agora.entfulldemo.bean.MemberMusicModel;
import com.agora.entfulldemo.bean.MusicSettingBean;
import com.agora.entfulldemo.bean.room.RTMMessageBean;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.dialog.MusicSettingDialog;
import com.agora.entfulldemo.event.PlayerStatusEvent;
import com.agora.entfulldemo.event.ReceivedMessageEvent;
import com.agora.entfulldemo.manager.BaseMusicPlayer;
import com.agora.entfulldemo.manager.MultipleMusicPlayer;
import com.agora.entfulldemo.manager.RTCManager;
import com.agora.entfulldemo.manager.RTMManager;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.manager.SimpleRoomEventCallback;
import com.agora.entfulldemo.manager.SingleMusicPlayer;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.entfulldemo.utils.ToastUtils;
import com.agora.entfulldemo.widget.LrcControlView;
import com.agora.data.model.AgoraMember;
import com.agora.data.model.AgoraRoom;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import io.agora.lrcview.LrcLoadUtils;
import io.agora.lrcview.bean.LrcData;
import io.agora.musiccontentcenter.IAgoraMusicPlayer;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngineEx;
import io.reactivex.disposables.Disposable;

public class RoomLivingViewModel extends SimpleRoomEventCallback {

    /**
     * 音乐播放器
     */
    private BaseMusicPlayer mMusicPlayer;

    /**
     * 播放器
     */
    protected IAgoraMusicPlayer mPlayer;

    /**
     * 播放器配置
     */
    MusicSettingBean mSetting;

    /**
     * 房间信息
     */
    AgoraRoom agoraRoom = RoomManager.getInstance().getRoom();

    public void requestRTMToken() {
        ApiManager.getInstance().requestRTMToken(RoomManager.mMine.id)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<CommonBean>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<CommonBean> data) {
                                RTCManager.getInstance().initMcc(data.getData().token);
                                getSongOrdersList(true);
                                mPlayer = RTCManager.getInstance().createMediaPlayer();
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {

                            }
                        });
    }

    /**
     * 加入房间
     */
    public void joinRoom() {
        RoomManager.getInstance().joinRoom((type, o) -> {
            if (type == 0) {
                requestGetRoomInfo(agoraRoom.password);
            } else if (type == RoomManager.ROOM_TYPE_ON_MUSIC_EMPTY) {
                onMusicEmpty();
            } else if (type == RoomManager.ROOM_TYPE_ON_MEMBER_JOINED_CHORUS) {
                onMemberJoinedChorus((MemberMusicModel) o);
            } else if (type == RoomManager.ROOM_TYPE_ON_RTM_COUNT_UPDATE) {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_MEMBER_COUNT_UPDATE, null);
            }
        });
    }

    /**
     * 退出房间
     */
    public void exitRoom() {
        if (RoomManager.mMine.isMaster) {
            ApiManager.getInstance().requestCloseRoom(agoraRoom.roomNo)
                    .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                            new ApiSubscriber<BaseResponse<String>>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    addDispose(d);
                                }

                                @Override
                                public void onSuccess(BaseResponse<String> data) {
                                    RTMMessageBean bean = new RTMMessageBean();
                                    bean.messageType = MESSAGE_ROOM_TYPE_CREATOR_EXIT;
                                    bean.roomNo = agoraRoom.roomNo;
                                    RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                                    getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_EXIT, null);
                                }

                                @Override
                                public void onFailure(@Nullable ApiException t) {
                                    if (t.getMessage().equals("无法关闭房间")) {
                                        RTMMessageBean bean = new RTMMessageBean();
                                        bean.messageType = MESSAGE_ROOM_TYPE_CREATOR_EXIT;
                                        bean.roomNo = agoraRoom.roomNo;
                                        RTMManager.getInstance()
                                                .sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_EXIT,
                                                null);
                                    } else {
                                        ToastUtils.showToast(t.getMessage());
                                    }
                                }
                            });
        } else {
            ApiManager.getInstance().requestExitRoom(agoraRoom.roomNo)
                    .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                            new ApiSubscriber<BaseResponse<String>>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    addDispose(d);
                                }

                                @Override
                                public void onSuccess(BaseResponse<String> data) {
                                    RTMMessageBean bean = new RTMMessageBean();
                                    bean.headUrl = UserManager.getInstance().getUser().headUrl;
                                    bean.messageType = MESSAGE_ROOM_TYPE_LEAVE_SEAT;
                                    bean.roomNo = agoraRoom.roomNo;
                                    bean.userNo = UserManager.getInstance().getUser().userNo;
                                    bean.onSeat = RoomManager.getInstance().getMine().onSeat;
                                    bean.name = UserManager.getInstance().getUser().name;
                                    RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                                    getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_EXIT, null);
                                }

                                @Override
                                public void onFailure(@Nullable ApiException t) {
                                    ToastUtils.showToast(t.getMessage());
                                }
                            });
        }
    }

    @Override
    protected boolean isNeedEventBus() {
        return true;
    }

    private void onJoinRoom() {
        if (agoraRoom.roomUserInfoDTOList.get(0).isMaster
                && agoraRoom.roomUserInfoDTOList.get(0).userNo
                        .equals(UserManager.getInstance().getUser().userNo)) {
            RoomManager.mMine.isMaster = true;
            RoomManager.mMine.role = AgoraMember.Role.Owner;
            RTCManager.getInstance().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        } else if (RoomManager.mMine.role == AgoraMember.Role.Speaker) {
            RTCManager.getInstance().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        } else {
            RTCManager.getInstance().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        }
        if (RoomManager.getInstance().isOwner()) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_SEAT_STATUS, View.VISIBLE);
        } else {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_SEAT_STATUS, View.GONE);
        }
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_SHOW_MEMBER_STATUS,
                agoraRoom.roomUserInfoDTOList);
        RoomManager.getInstance().loadMemberStatus();
    }

    /**
     * 获取房间信息
     */
    private void requestGetRoomInfo(String password) {
        ApiManager.getInstance().requestGetRoomInfo(agoraRoom.roomNo, password)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<AgoraRoom>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<AgoraRoom> data) {
                                agoraRoom = data.getData();
                                RoomManager.getInstance().setAgoraRoom(data.getData());
                                if (RoomManager.mMine.userNo.equals(agoraRoom.creatorNo)) {
                                    RoomManager.mMine.isMaster = true;
                                    RoomManager.mMine.role = AgoraMember.Role.Owner;
                                    RTCManager.getInstance().getRtcEngine()
                                            .setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                                }
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_JOIN_SUCCESS,
                                        null);
                                onJoinRoom();
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_JOIN_FAIL, null);
                                ToastUtils.showToast("无法加入房间 " + t.getMessage());
                            }
                        });
    }

    /**
     * 离开麦位
     */
    public void leaveSeat(AgoraMember agoraMember) {
        ApiManager.getInstance().requestRoomLeaveSeatRoomInfo(agoraRoom.roomNo, agoraMember.userNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<AgoraRoom>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<AgoraRoom> data) {
                                if (agoraMember.userNo.equals(RoomManager.mMine.userNo)) {
                                    RoomManager.mMine.role = AgoraMember.Role.Listener;
                                    RoomManager.mMine.onSeat = -1;
                                    if (mMusicPlayer != null) {
                                        mMusicPlayer.switchRole(Constants.CLIENT_ROLE_AUDIENCE);
                                    }
                                    RTCManager.getInstance().getRtcEngine()
                                            .setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                                }
                                // 下麦成功 推送
                                RTMMessageBean bean = new RTMMessageBean();
                                bean.headUrl = UserManager.getInstance().getUser().headUrl;
                                bean.messageType = MESSAGE_ROOM_TYPE_LEAVE_SEAT;
                                bean.roomNo = agoraRoom.roomNo;
                                bean.userNo = agoraMember.userNo;
                                bean.onSeat = agoraMember.onSeat;
                                bean.name = agoraMember.name;
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LEAVE_SEAT, bean);
                                RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));

                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                            }
                        });
    }

    /**
     * 上麦
     */
    public void haveSeat(int seat) {
        ApiManager.getInstance()
                .requestRoomHaveSeatRoomInfo(agoraRoom.roomNo, seat, UserManager.getInstance().getUser().userNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<AgoraRoom>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<AgoraRoom> data) {
                                Log.d("cwtsw", "haveSeat onSuccess");
                                // 上麦成功 推送
                                RTMMessageBean bean = new RTMMessageBean();
                                bean.headUrl = UserManager.getInstance().getUser().headUrl;
                                bean.messageType = MESSAGE_ROOM_TYPE_ON_SEAT;
                                bean.userNo = UserManager.getInstance().getUser().userNo;
                                bean.roomNo = agoraRoom.roomNo;
                                bean.onSeat = seat;
                                bean.id = UserManager.getInstance().getUser().id;
                                bean.name = UserManager.getInstance().getUser().name;
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_ON_SEAT, bean);
                                RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));

                                RoomManager.mMine.role = AgoraMember.Role.Speaker;
                                RTCManager.getInstance().getRtcEngine()
                                        .setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                                // mMusicPlayer.switchRole(Constants.CLIENT_ROLE_BROADCASTER);
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                            }
                        });
    }

    /**
     * 设置背景
     */
    public void setMV_BG(int bgPosition) {
        agoraRoom.bgOption = String.valueOf(bgPosition);
        ApiManager.getInstance().requestRoomInfoEdit(agoraRoom.roomNo, null, String.valueOf(bgPosition), null)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<String> data) {
                                RTMMessageBean bean = new RTMMessageBean();
                                bean.headUrl = UserManager.getInstance().getUser().headUrl;
                                bean.messageType = MESSAGE_ROOM_TYPE_CHANGE_MV;
                                bean.userNo = UserManager.getInstance().getUser().userNo;
                                bean.roomNo = agoraRoom.roomNo;
                                bean.bgOption = agoraRoom.bgOption;
                                RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));

                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                            }
                        });
    }

    public void requestRoomCancelChorus() {
        ApiManager.getInstance()
                .requestRoomCancelChorus(RoomManager.mMine.userNo, RoomManager.getInstance().mMusicModel.songNo,
                        agoraRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<String> data) {

                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                            }
                        });
    }

    public void onStart() {
        if (mPlayer != null) {
            mPlayer.mute(false);
        }
        RoomManager.getInstance().addRoomEventCallback(this);
    }

    public void onStop() {
        if (mPlayer != null) {
            mPlayer.mute(true);
        }
        RoomManager.getInstance().removeRoomEventCallback(this);
    }

    public void initData() {
        mSetting = new MusicSettingBean(false, 100, 100, 0, new MusicSettingDialog.Callback() {
            @Override
            public void onEarChanged(boolean isEar) {
                RTCManager.getInstance().getRtcEngine().enableInEarMonitoring(isEar);
            }

            @Override
            public void onMicVolChanged(int vol) {
                mMusicPlayer.setMicVolume(vol);
            }

            @Override
            public void onMusicVolChanged(int vol) {
                mMusicPlayer.setMusicVolume(vol);
            }

            @Override
            public void onEffectChanged(int effect) {
                RTCManager.getInstance().getRtcEngine().setAudioEffectPreset(getEffectIndex(effect));
            }

            @Override
            public void onBeautifierPresetChanged(int effect) {
                switch (effect) {
                    case 0:
                        RTCManager.getInstance().getRtcEngine()
                                .setVoiceBeautifierParameters(Constants.VOICE_BEAUTIFIER_OFF, 0, 0);
                    case 1:
                        RTCManager.getInstance().getRtcEngine()
                                .setVoiceBeautifierParameters(Constants.SINGING_BEAUTIFIER, 1, 2);
                    case 2:
                        RTCManager.getInstance().getRtcEngine()
                                .setVoiceBeautifierParameters(Constants.SINGING_BEAUTIFIER, 1, 1);
                    case 3:
                        RTCManager.getInstance().getRtcEngine()
                                .setVoiceBeautifierParameters(Constants.SINGING_BEAUTIFIER, 2, 2);
                    case 4:
                        RTCManager.getInstance().getRtcEngine()
                                .setVoiceBeautifierParameters(Constants.SINGING_BEAUTIFIER, 2, 1);
                }
            }

            @Override
            public void setAudioEffectParameters(int param1, int param2) {
                if (param1 == 0) {
                    RTCManager.getInstance().getRtcEngine().setAudioEffectParameters(Constants.VOICE_CONVERSION_OFF,
                            param1, param2);
                } else {
                    RTCManager.getInstance().getRtcEngine().setAudioEffectParameters(Constants.PITCH_CORRECTION, param1,
                            param2);
                }
            }

            @Override
            public void onToneChanged(int newToneValue) {
                mMusicPlayer.setAudioMixingPitch(newToneValue);
            }
        });
    }

    private int getEffectIndex(int index) {
        switch (index) {
            case 0:
                return Constants.AUDIO_EFFECT_OFF;
            case 1:
                return Constants.ROOM_ACOUSTICS_KTV;
            case 2:
                return Constants.ROOM_ACOUSTICS_VOCAL_CONCERT;
            case 3:
                return Constants.ROOM_ACOUSTICS_STUDIO;
            case 4:
                return Constants.ROOM_ACOUSTICS_PHONOGRAPH;
            case 5:
                return Constants.ROOM_ACOUSTICS_SPACIAL;
            case 6:
                return Constants.ROOM_ACOUSTICS_ETHEREAL;
            case 7:
                return Constants.STYLE_TRANSFORMATION_POPULAR;
            case 8:
                return Constants.STYLE_TRANSFORMATION_RNB;
        }
        return Constants.AUDIO_EFFECT_OFF;
    }

    @Override
    public void onLocalPitch(double pitch) {
        super.onLocalPitch(pitch);
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_LOCAL_PITCH, (float) pitch);
    }

    @Override
    public void onRoomError(int error, String msg) {
        super.onRoomError(error, msg);
        ToastUtils.showToast(msg);
    }

    @Override
    public void onRoomInfoChanged(@NonNull AgoraRoom room) {
        super.onRoomInfoChanged(room);
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_ROOM_INFO_CHANGED, room);
    }

    @Override
    public void onMemberLeave(@NonNull AgoraMember member) {
        if (ObjectsCompat.equals(member, RoomManager.getInstance().getOwner())) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_EXIT, member);
            return;
        }
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MEMBER_LEAVE, member);
        if (RoomManager.getInstance().isOwner()) {
            MemberMusicModel musicModel = RoomManager.getInstance().getMusicModel();
            if (musicModel != null && ObjectsCompat.equals(member.userNo, musicModel.userNo)) {
                changeMusic();
            }
        }
    }

    /**
     * 麦位上的用户状态改变
     *
     * @param member 用户
     */
    @Override
    public void onRoleChanged(@NonNull AgoraMember member) {
        super.onRoleChanged(member);
        if (member.role == AgoraMember.Role.Owner
                || member.role == AgoraMember.Role.Speaker) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MEMBER_JOIN, member);
            AgoraMember mMine = RoomManager.getInstance().getMine();
            if (ObjectsCompat.equals(member, mMine)) {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_SEAT_STATUS,
                        View.VISIBLE);
                int role = Constants.CLIENT_ROLE_BROADCASTER;
                if (mMusicPlayer != null) {
                    mMusicPlayer.switchRole(role);
                }
                RTCManager.getInstance().getRtcEngine().setClientRole(role);
            }
        } else if (member.role == AgoraMember.Role.Listener) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MEMBER_LEAVE, member);
            if (RoomManager.getInstance().isOwner()) {
                if (RoomManager.getInstance().isMainSinger(member)) {
                    changeMusic();
                }
            }
            AgoraMember mMine = RoomManager.getInstance().getMine();
            if (ObjectsCompat.equals(member, mMine)) {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_SEAT_STATUS, member);
                int role = Constants.CLIENT_ROLE_AUDIENCE;
                if (mMusicPlayer != null) {
                    mMusicPlayer.switchRole(role);
                }
                RTCManager.getInstance().getRtcEngine().setClientRole(role);
                if (RoomManager.getInstance().isFollowSinger()) {
                    if (mMusicPlayer != null) {
                        mMusicPlayer.stop();
                        MemberMusicModel mMusicModel = RoomManager.getInstance().getMusicModel();
                        if (mMusicModel != null) {
                            mMusicPlayer.playByListener(mMusicModel);
                        }
                    }
                }
            }
        }
    }

    /**
     * 声音状态改变
     *
     * @param member
     */
    @Override
    public void onAudioStatusChanged(@NonNull AgoraMember member) {
        super.onAudioStatusChanged(member);
        AgoraMember mMine = RoomManager.getInstance().getMine();
        if (ObjectsCompat.equals(member, mMine)) {
            if (member.isSelfMuted == 1) {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MIC_STATUS, false);
            } else {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MIC_STATUS, true);
            }
        }
    }

    /**
     * 视频状态改变
     *
     * @param member
     */
    @Override
    public void onVideoStatusChanged(@NonNull AgoraMember member) {
        super.onVideoStatusChanged(member);
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_VIDEO_STATUS_CHANGED, member);
    }

    /**
     * 音乐被删除
     *
     * @param music
     */
    @Override
    public void onMusicDelete(@NonNull MemberMusicModel music) {
        super.onMusicDelete(music);
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MUSIC_DEL, music);
    }

    /**
     * 切歌页面刷新
     *
     * @param music
     */
    @Override
    public void onMusicChanged(@NonNull MemberMusicModel music) {
        super.onMusicChanged(music);
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MUSIC_CHANGED, music);
    }

    /**
     * 音乐停止
     */
    public void musicStop() {
        if (mMusicPlayer != null) {
            mMusicPlayer.stop();
            mMusicPlayer.destroy();
        }
    }

    /**
     * 切歌逻辑
     */
    public void onMusicStaticChanged(Context context, MemberMusicModel music) {
        musicStop();
        int role;
        AgoraMember mMine = RoomManager.getInstance().getMine();
        if (music.userNo.equals(mMine.userNo)) {
            role = Constants.CLIENT_ROLE_BROADCASTER;
        } else {
            role = Constants.CLIENT_ROLE_AUDIENCE;
        }
        if (music.getType() == MemberMusicModel.SingType.Single || music.status == 2) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                    KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_PREPARE);
            mMusicPlayer = new SingleMusicPlayer(context, role, mPlayer);
        } else if (music.getType() == MemberMusicModel.SingType.Chorus) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                    KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_WAIT_CHORUS);
            if (music.user1Id != null
                    && music.user1Id.equals(mMine.userNo)) {
                role = Constants.CLIENT_ROLE_BROADCASTER;
            }
            mMusicPlayer = new MultipleMusicPlayer(context, role, mPlayer);
        }
        mMusicPlayer.switchRole(Constants.CLIENT_ROLE_BROADCASTER);
        mMusicPlayer.registerPlayerObserver(mMusicCallback);
        mMusicPlayer.prepare(music);
    }

    private final BaseMusicPlayer.Callback mMusicCallback = new BaseMusicPlayer.Callback() {

        @Override
        public void onPrepareResource() {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                    KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_PREPARE);
        }

        @Override
        public void onResourceReady(@NonNull MemberMusicModel music) {
            File lrcFile = music.fileLrc;
            LrcData data = LrcLoadUtils.parse(lrcFile);
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_PITCH_LRC_DATA,
                    data);
        }

        @Override
        public void onMusicOpening() {
        }

        @Override
        public void onMusicOpenCompleted(long duration) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_TOTAL_DURATION,
                    duration);
        }

        @Override
        public void onMusicOpenError(int error) {

        }

        @Override
        public void onMusicPlaying() {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                    KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_PLAY_STATUS);
        }

        @Override
        public void onMusicPause() {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                    KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_PAUSE_STATUS);
        }

        @Override
        public void onMusicStop() {

        }

        @Override
        public void onMusicCompleted() {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                    KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_LRC_RESET);
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_PLAY_COMPLETED, null);
            changeMusic();
        }

        @Override
        public void onMusicPositionChanged(long position) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_UPDATE_TIME,
                    position);
        }

        @Override
        public void onReceivedCountdown(int time) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_COUNT_DOWN,
                    time);
        }
    };

    @Override
    public void onMusicEmpty() {
        super.onMusicEmpty();
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_MUSICEMPTY, null);
        if (mMusicPlayer != null && RoomManager.mMine.role == AgoraMember.Role.Speaker) {
            mMusicPlayer.switchRole(Constants.CLIENT_ROLE_BROADCASTER);
        }
        musicStop();
    }

    @Override
    public void onMemberApplyJoinChorus(@NonNull MemberMusicModel music) {
        super.onMemberApplyJoinChorus(music);
        // RoomActivity.this.onMemberApplyJoinChorus(music);
    }

    @Override
    public void onMemberJoinedChorus(@NonNull MemberMusicModel music) {
        super.onMemberJoinedChorus(music);
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_JOINED_CHORUS, null);
    }

    @Override
    public void onMemberChorusReady(@NonNull MemberMusicModel music) {
        super.onMemberChorusReady(music);
    }

    public void setOnLrcActionListener(LrcControlView lrcControlView) {
        lrcControlView.setOnLrcClickListener(new LrcControlView.OnLrcActionListener() {
            @Override
            public void onProgressChanged(long time) {
                mMusicPlayer.seek(time);
            }

            @Override
            public void onStartTrackingTouch() {

            }

            @Override
            public void onStopTrackingTouch() {

            }

            @Override
            public void onSwitchOriginalClick() {
                toggleOriginal(lrcControlView);
            }

            @Override
            public void onMenuClick() {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_SHOW_MUSIC_MENU_DIALOG, null);
            }

            @Override
            public void onPlayClick() {
                toggleStart();
            }

            @Override
            public void onChangeMusicClick() {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_SHOW_CHANGE_MUSIC_DIALOG, null);
            }

            @Override
            public void onStartSing() {
                changeSingType(lrcControlView.getContext());
            }

            @Override
            public void onJoinChorus() {
                joinChorus();
            }

            @Override
            public void onWaitTimeOut() {
                changeSingType(lrcControlView.getContext());
            }

            @Override
            public void onCountTime(int time) {
                if (mMusicPlayer != null) {
                    mMusicPlayer.sendCountdown(time);
                }
            }
        });
    }

    private void changeSingType(Context context) {
        MemberMusicModel music = RoomManager.getInstance().getMusicModel();
        if (music == null) {
            return;
        }
        music.isChorus = false;
        music.setType(MemberMusicModel.SingType.Single);
        onMusicStaticChanged(context, music);

        RTMMessageBean bean = new RTMMessageBean();
        bean.messageType = RoomLivingViewModel.MESSAGE_ROOM_TYPE_NO_JOIN_CHORUS;
        bean.userNo = UserManager.getInstance().getUser().userNo;
        bean.roomNo = agoraRoom.roomNo;
        bean.songNo = music.songNo;
        RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
        requestRoomCancelChorus();
    }

    /**
     * 点击加入合唱
     */
    private void joinChorus() {
        AgoraRoom mRoom = RoomManager.getInstance().getRoom();
        if (mRoom == null) {
            return;
        }
        MemberMusicModel musicModel = RoomManager.getInstance().getMusicModel();
        if (musicModel == null) {
            return;
        }
        User mUser = UserManager.getInstance().getUser();
        if (agoraRoom.creatorNo.equals(RoomManager.mMine.userNo)) {
            RoomManager.mMine.role = AgoraMember.Role.Owner;
        }
        ApiManager.getInstance().requestJoinChorus(musicModel.songNo, mUser.userNo, mRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<String> data) {
                                musicModel.user1Id = mUser.userNo;
                                // int uid = (int) (Math.random() * (Integer.MAX_VALUE / 2));
                                musicModel.user1bgId = (long) mUser.id;
                                // 通知主唱 有人加入了合唱
                                RTMMessageBean bean = new RTMMessageBean();
                                bean.messageType = RoomLivingViewModel.MESSAGE_ROOM_TYPE_APPLY_JOIN_CHORUS;
                                bean.userNo = UserManager.getInstance().getUser().userNo;
                                bean.name = UserManager.getInstance().getUser().name;
                                bean.roomNo = agoraRoom.roomNo;
                                bean.bgUid = musicModel.user1bgId;
                                RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                                mMusicPlayer.switchRole(Constants.CLIENT_ROLE_BROADCASTER);

                                // 合唱执行加入
                                RoomManager.getInstance().onMemberJoinedChorus(musicModel);
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                            }
                        });
        if (RoomManager.mMine.role == AgoraMember.Role.Listener) {
            ToastUtils.showToast(R.string.ktv_need_up);
            return;
        }
    }

    public void toggleSelfVideo(int isVideoMuted) {
        ApiManager.getInstance().requestOpenCamera(RoomManager.mMine.userNo, agoraRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<String> data) {
                                RtcEngineEx engine = RTCManager.getInstance().getRtcEngine();
                                engine.enableLocalVideo(isVideoMuted == 1);
                                // 发送通知
                                RTMMessageBean bean = new RTMMessageBean();
                                bean.messageType = MESSAGE_ROOM_TYPE_TO_VIDEO;
                                bean.userNo = UserManager.getInstance().getUser().userNo;
                                bean.id = RoomManager.mMine.getStreamId();
                                bean.roomNo = agoraRoom.roomNo;
                                bean.isVideoMuted = isVideoMuted;
                                RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_TOGGLE_MIC, false);
                            }
                        });
    }

    public void toggleMic(int isSelfMuted) {
        AgoraMember mMine = RoomManager.getInstance().getMine();
        if (mMine == null) {
            return;
        }
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_TOGGLE_MIC, false);
        mMine.isSelfMuted = isSelfMuted;
        boolean newValue = mMine.isSelfMuted == 0;
        // 同步静音状态
        ApiManager.getInstance().requestToggleMic(mMine.userNo, agoraRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<String> data) {
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_TOGGLE_MIC, true);
                                // ChannelMediaOptions options = new ChannelMediaOptions();
                                // options.publishAudioTrack = newValue;
                                // options.publishMediaPlayerAudioTrack = newValue;
                                RTCManager.getInstance().getRtcEngine().enableLocalAudio(newValue);
                                // RTCManager.getInstance().getRtcEngine().updateChannelMediaOptions(options);
                                // 发送通知
                                RTMMessageBean bean = new RTMMessageBean();
                                bean.messageType = MESSAGE_ROOM_TYPE_TO_MUTE;
                                bean.userNo = UserManager.getInstance().getUser().userNo;
                                bean.roomNo = agoraRoom.roomNo;
                                bean.isSelfMuted = isSelfMuted;
                                bean.id = RoomManager.mMine.getStreamId();
                                RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_TOGGLE_MIC, false);
                            }
                        });
    }

    private void toggleOriginal(LrcControlView lrcControlView) {
        if (mMusicPlayer == null) {
            return;
        }
        if (mMusicPlayer.hasAccompaniment()) {
            mMusicPlayer.toggleOrigle();
        } else {
            lrcControlView.setSwitchOriginalChecked(true);
            ToastUtils.showToast(R.string.ktv_error_cut);
        }
    }

    private void toggleStart() {
        if (mMusicPlayer == null) {
            return;
        }
        mMusicPlayer.togglePlay();
    }

    /**
     * 获取已点列表
     */
    public void getSongOrdersList(boolean isUpdateUi) {
        ApiManager.getInstance().requestGetSongsOrderedList(RoomManager.mRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<List<MemberMusicModel>>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<List<MemberMusicModel>> data) {
                                RoomManager.getInstance().onMusicEmpty(isUpdateUi);
                                for (MemberMusicModel model : data.getData()) {
                                    if (model.isChorus) {
                                        model.setType(MemberMusicModel.SingType.Chorus);
                                    }
                                    RoomManager.getInstance().onMusicAdd(model);
                                }
                                if (data.getData() != null && !data.getData().isEmpty() && isUpdateUi) {
                                    onMusicChanged(data.getData().get(0));
                                }
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                ToastUtils.showToast(t.getMessage());
                            }
                        });
    }

    /**
     * 开始切歌
     */
    public void changeMusic() {
        AgoraRoom mRoom = RoomManager.getInstance().getRoom();
        if (mRoom == null) {
            return;
        }

        MemberMusicModel musicModel = RoomManager.getInstance().getMusicModel();
        if (musicModel == null) {
            return;
        }

        if (mMusicPlayer != null) {
            mMusicPlayer.selectAudioTrack(1);
            mMusicPlayer.stop();
        }
        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_ENABLED, false);
        ApiManager.getInstance()
                .requestSwitchSong(UserManager.getInstance().getUser().userNo, musicModel.songNo, mRoom.roomNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                        new ApiSubscriber<BaseResponse<String>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                addDispose(d);
                            }

                            @Override
                            public void onSuccess(BaseResponse<String> data) {
                                startChangeMusic();
                            }

                            @Override
                            public void onFailure(@Nullable ApiException t) {
                                if ("歌曲不存在".equals(t.getMessage())) {
                                    startChangeMusic();
                                } else {
                                    ToastUtils.showToast(t.getMessage());
                                }
                                getISingleCallback().onSingleCallback(
                                        KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_ENABLED, true);

                            }
                        });
    }

    private void startChangeMusic() {
        // 推送切歌逻辑
        RTMMessageBean bean = new RTMMessageBean();
        bean.headUrl = UserManager.getInstance().getUser().headUrl;
        bean.messageType = MESSAGE_ROOM_TYPE_SWITCH_SONGS;
        bean.roomNo = agoraRoom.roomNo;
        bean.userNo = UserManager.getInstance().getUser().userNo;
        RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));

        RoomManager.getInstance().onMusicDelete(RoomManager.getInstance().getMusicModel().songNo, 0);
        if (RoomManager.getInstance().getMusics().isEmpty()) {
            onMusicEmpty();
        } else {
            onMusicChanged(RoomManager.getInstance().getMusicModel());
        }
    }

    public void release() {
        musicStop();
        if (mPlayer != null) {
            mPlayer.destroy();
            mPlayer = null;
        }
        RoomManager.getInstance().removeRoomEventCallback(this);
        if (mMusicPlayer != null) {
            mMusicPlayer.unregisterPlayerObserver();
            mMusicPlayer.destroy();
            mMusicPlayer = null;
        }
    }

    // ======推送消息处理=======

    public static final String MESSAGE_ROOM_TYPE_ON_SEAT = "0"; // 上麦
    public static final String MESSAGE_ROOM_TYPE_LEAVE_SEAT = "1"; // 下麦
    public static final String MESSAGE_ROOM_TYPE_CHOOSE_SONG = "2";// 点歌
    public static final String MESSAGE_ROOM_TYPE_SWITCH_SONGS = "3";// 切歌
    public static final String MESSAGE_ROOM_TYPE_CREATOR_EXIT = "4";// 房主退出 关闭房间
    public static final String MESSAGE_ROOM_TYPE_CHANGE_MV = "5";// 切换背景

    public static final String MESSAGE_ROOM_TYPE_TO_MUTE = "9";// 静音
    public static final String MESSAGE_ROOM_TYPE_TO_VIDEO = "10";// 摄像头
    public static final String MESSAGE_ROOM_TYPE_APPLY_JOIN_CHORUS = "11";// 通知主唱有人加入合唱uid
    public static final String MESSAGE_ROOM_TYPE_APPLY_SEND_CHORUS = "12";// 通知合唱者 主唱uid
    public static final String MESSAGE_ROOM_TYPE_NO_JOIN_CHORUS = "13";// 无人加入合唱

    public static final String MESSAGE_ROOM_TYPE_SYNCHRO_PITCH = "14";// 同步打分信息

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@Nullable ReceivedMessageEvent event) {
        if (getISingleCallback() != null) {
            RTMMessageBean bean = GsonUtils.Companion.getGson().fromJson(event.message, RTMMessageBean.class);
            if (!agoraRoom.roomNo.equals(bean.roomNo))
                return;
            if (bean.messageType.equals(MESSAGE_ROOM_TYPE_LEAVE_SEAT)) {
                if (bean.userNo.equals(RoomManager.mMine.userNo)) {
                    changeMusic();
                    RTCManager.getInstance().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                }
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LEAVE_SEAT, bean);
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_ON_SEAT)) {
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_ON_SEAT, bean);
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_SWITCH_SONGS)) {
                // 切歌成功
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_ENABLED,
                        true);
                getSongOrdersList(true);
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_SEAT_CHANGE, null);
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_CHOOSE_SONG)) {
                getSongOrdersList(RoomManager.getInstance().getMusics().isEmpty());
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_TO_MUTE)) {
                getISingleCallback().onSingleCallback(KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_MIC_MUTE, bean);
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_TO_VIDEO)) {
                getISingleCallback().onSingleCallback(KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_VIDEO, bean);
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_CREATOR_EXIT)) {
                if (agoraRoom.roomNo.equals(bean.roomNo)) {
                    release();
                    getISingleCallback().onSingleCallback(KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_CREATOR_EXIT, bean);
                }
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_APPLY_JOIN_CHORUS)) {
                if (RoomManager.mMine.role != AgoraMember.Role.Listener) {
                    if (RoomManager.getInstance().mMusicModel == null)
                        return;
                    if (RoomManager.getInstance().mMusicModel.userNo.equals(RoomManager.mMine.userNo)) {
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_JOINED_CHORUS,
                                null);
                        RoomManager.getInstance().mMusicModel.applyUser1Id = bean.userNo;
                        RoomManager.getInstance().mMusicModel.user1Id = bean.userNo;
                        RoomManager.getInstance().mMusicModel.user1bgId = bean.bgUid;
                        int uid = (int) (Math.random() * (Integer.MAX_VALUE / 2));
                        RoomManager.getInstance().mMusicModel.userbgId = (long) uid;
                        RoomManager.getInstance().onMemberApplyJoinChorus(RoomManager.getInstance().mMusicModel);

                        RTMMessageBean bean2 = new RTMMessageBean();
                        bean2.messageType = RoomLivingViewModel.MESSAGE_ROOM_TYPE_APPLY_SEND_CHORUS;
                        // 收到主唱推的 的uid
                        bean2.userNo = RoomManager.getInstance().mMusicModel.user1Id;
                        bean2.name = UserManager.getInstance().getUser().name;
                        bean.roomNo = agoraRoom.roomNo;
                        bean2.bgUid = (long) uid;
                        RTMManager.getInstance().sendMessage(GsonUtils.Companion.getGson().toJson(bean));
                    } else {
                        Log.d("cwtsw", "屏蔽 " + bean.bgUid.intValue());
                        RTCManager.getInstance().getRtcEngine().muteRemoteAudioStream(bean.bgUid.intValue(), true);
                        RoomManager.getInstance().mMusicModel.isChorus = false;
                        RoomManager.getInstance().mMusicModel.isJoin = true;
                        onMusicChanged(RoomManager.getInstance().getMusicModel());
                        // getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS,
                        // KtvConstant.TYPE_CONTROL_VIEW_STATUS_ON_PLAY_STATUS);
                    }
                }
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_NO_JOIN_CHORUS)) {
                if (RoomManager.getInstance().mMusicModel == null)
                    return;
                RoomManager.getInstance().mMusicModel.isChorus = false;
                RoomManager.getInstance().mMusicModel.setType(MemberMusicModel.SingType.Single);
                onMusicChanged(RoomManager.getInstance().getMusicModel());
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_APPLY_SEND_CHORUS)) {
                if (RoomManager.getInstance().mMusicModel == null)
                    return;
                if (RoomManager.mMine.userNo.equals(bean.userNo)) {
                    RoomManager.getInstance().mMusicModel.userbgId = bean.bgUid;
                    RoomManager.getInstance().onMemberJoinedChorus(RoomManager.getInstance().mMusicModel);
                }
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_SYNCHRO_PITCH)) {
                if (RoomManager.getInstance().mMusicModel == null)
                    return;
                if (bean.userNo == null)
                    return;
                if (RoomManager.getInstance().mMusicModel != null && (!RoomManager.getInstance().mMusicModel.isChorus
                        || RoomManager.mMine.userNo.equals(RoomManager.getInstance().mMusicModel.user1Id))) {
                    if (bean.userNo.equals(RoomManager.getInstance().mMusicModel.userNo)) {
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_LIVING_ON_LOCAL_PITCH,
                                Float.parseFloat(String.valueOf(bean.pitch)));
                    }
                }
            } else if (bean.messageType.equals(MESSAGE_ROOM_TYPE_CHANGE_MV)) {
                RoomManager.getInstance().getRoom().bgOption = bean.bgOption;
                getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_ROOM_BG_CHANGE, bean.bgOption);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@Nullable PlayerStatusEvent event) {
        if (event.isPlay && RoomManager.getInstance().mMusicModel.status != 2) {
            ApiManager.getInstance().requestRoomSongBegin(
                    RoomManager.getInstance().mMusicModel.sort,
                    UserManager.getInstance().getUser().userNo,
                    RoomManager.getInstance().mMusicModel.songNo,
                    agoraRoom.roomNo).compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                            new ApiSubscriber<BaseResponse<String>>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    addDispose(d);
                                }

                                @Override
                                public void onSuccess(BaseResponse<String> data) {
                                    Log.d("cwtsw", "歌曲开始播放");
                                    RoomManager.getInstance().mMusicModel.status = 2;
                                }

                                @Override
                                public void onFailure(@Nullable ApiException t) {
                                }
                            });
        } else {
            ApiManager.getInstance().requestRoomSongOver(
                    RoomManager.getInstance().mMusicModel.sort,
                    UserManager.getInstance().getUser().userNo,
                    RoomManager.getInstance().mMusicModel.songNo,
                    agoraRoom.roomNo).compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                            new ApiSubscriber<BaseResponse<String>>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    addDispose(d);
                                }

                                @Override
                                public void onSuccess(BaseResponse<String> data) {
                                    Log.d("cwtsw", "歌曲停止播放");
                                    RoomManager.getInstance().mMusicModel.status = 1;
                                    changeMusic();
                                }

                                @Override
                                public void onFailure(@Nullable ApiException t) {
                                }
                            });
        }
    }

}
