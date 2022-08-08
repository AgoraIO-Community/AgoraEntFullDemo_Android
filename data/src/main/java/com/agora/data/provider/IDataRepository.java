package com.agora.data.provider;

import com.agora.data.model.KTVBaseResponse;
import com.agora.data.model.MusicModelBase;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * 数据仓库接口
 *
 * @author chenhengfei(Aslanchen)
 */
public interface IDataRepository {

    Observable<KTVBaseResponse<MusicModelBase>> getMusic(@NonNull String musicId);

    Completable download(@NonNull File file, @NonNull String url);
}
