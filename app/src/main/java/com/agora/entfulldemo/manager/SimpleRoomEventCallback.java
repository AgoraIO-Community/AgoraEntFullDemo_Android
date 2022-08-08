package com.agora.entfulldemo.manager;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.agora.entfulldemo.base.BaseRequestViewModel;
import com.agora.entfulldemo.bean.MemberMusicModel;
import com.agora.data.model.AgoraMember;
import com.agora.data.model.AgoraRoom;

@MainThread
public class SimpleRoomEventCallback extends BaseRequestViewModel implements RoomEventCallback {

    @Override
    public void onRoomInfoChanged(@NonNull AgoraRoom room) {

    }

    @Override
    public void onRoomClosed(@NonNull AgoraRoom room, boolean fromUser) {

    }

    @Override
    public void onMemberJoin(@NonNull AgoraMember member) {

    }

    @Override
    public void onMemberLeave(@NonNull AgoraMember member) {

    }

    @Override
    public void onRoleChanged(@NonNull AgoraMember member) {

    }

    @Override
    public void onAudioStatusChanged(@NonNull AgoraMember member) {

    }

    @Override
    public void onVideoStatusChanged(@NonNull AgoraMember member) {

    }

    @Override
    public void onRoomError(int error, String msg) {

    }

    @Override
    public void onMusicAdd(@NonNull MemberMusicModel music) {

    }

    @Override
    public void onMusicDelete(@NonNull MemberMusicModel music) {

    }

    @Override
    public void onMusicChanged(@NonNull MemberMusicModel music) {

    }

    @Override
    public void onMusicEmpty() {

    }

    @Override
    public void onMemberApplyJoinChorus(@NonNull MemberMusicModel music) {

    }

    @Override
    public void onMemberJoinedChorus(@NonNull MemberMusicModel music) {

    }

    @Override
    public void onMemberChorusReady(@NonNull MemberMusicModel music) {

    }

    @Override
    public void onMusicProgress(long total, long cur) {

    }

    @Override
    public void onLocalPitch(double pitch) {

    }
}
