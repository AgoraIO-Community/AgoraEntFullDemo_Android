package com.agora.entfulldemo.provider;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.api.ApiManager;
import com.agora.data.model.KTVBaseResponse;
import com.agora.data.model.MusicModelBase;
import com.agora.data.provider.DataRepositoryImpl;

import io.reactivex.Observable;

public class DataRepositoryImpl2 extends DataRepositoryImpl {
    @Override
    public Observable<KTVBaseResponse<MusicModelBase>> getMusic(@NonNull String musicId) {
        return ApiManager.getInstance().requestSongsDetail(musicId);
    }
}
