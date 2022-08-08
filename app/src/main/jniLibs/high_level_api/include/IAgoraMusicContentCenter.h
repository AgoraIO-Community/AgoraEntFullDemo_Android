//
//  Agora Media SDK
//
//  Created by FanYuanYuan in 2022-05.
//  Copyright (c) 2022 Agora IO. All rights reserved.
//

#pragma once

#include <AgoraRefPtr.h>
#include "IAgoraMediaPlayer.h"

namespace agora {
namespace rtc {

typedef enum
{
    /**
     * 0: No error occurs and preload successed.
     */
    kPreloadStatusCompleted = 0,

    /**
     * 1: A general error occurs.
     */
    kPreloadStatusFailed = 1,

    /**
     * 2: The media is preloading.
     */
    kPreloadStatusPreloading = 2,
} PreloadStatusCode;

typedef enum
{
    kMusicContentCenterStatusOk = 0,
    kMusicContentCenterStatusErr = 1,
} MusicContentCenterStatusCode;

typedef enum
{
    /**
     * 1: Audio media.
     */
    kMusicMediaTypeAudio = 1,

    /**
     * 2: MV media.
     */
    kMusicMediaTypeMv = 2,
} MusicMediaType;

typedef struct 
{
    /**
     * Name of the music chart
     */
    const char* chartName;
    /**
     * Id of the music chart, use to get music list
     */
    int32_t id;
} MusicChartInfo;

class MusicChartCollection : public RefCountInterface {
public:
    virtual int getCount() = 0;
    virtual MusicChartInfo* get(int index) = 0;
protected:
    virtual ~MusicChartCollection() = default;
};

struct MvProperty
{
    /**
     * the resolution of the mv
     */
    const char* resolution;
    /**
     * the bandwidth of the mv
     */
    const char* bandwidth;
};

struct ClimaxSegment
{
    /**
     * the start time of climax segment
     */
    int32_t startTimeMs;
    /**
     * the end time of climax segment
     */
    int32_t endTimeMs;
};

struct Music
{
    /**
     * the songCode of music
     */
    int64_t songCode;
    /**
     * the name of music
     */
    const char* name;
    /**
     * the singer of music
     */
    const char* singer;
    /**
     * the poster url of music
     */
    const char* poster;
    /**
     * the release time of music
     */
    const char* releaseTime;
    /**
     * the duration of music, second
     */
    int32_t durationS;
    /**
     * the type of music
     * 1, mp3 with accompany and original
     * 2, mp3 only with accompany
     * 3, mp3 only with original
     * 4, mp4 with accompany and original
     * 5, mv only
     * 6, new type mp4 with accompany and original
     * detail at document of music media center
     */
    int32_t type;
    /**
     * the lyric count of music
     */
    int32_t lyricCount;
    /**
     * the lyric list of music
     * 0, xml
     * 1, lrc
     */
    int32_t* lyricList;
    /**
     * the climax segment count of music
     */
    int32_t climaxSegmentCount;
    /**
     * the climax segment list of music
     */
    ClimaxSegment* climaxSegmentList;
    /**
     * the mv property count of music
     * this music has mv resource if this count great than zero.
     */
    int32_t mvPropertyCount;
    /**
     * the mv property list of music
     */
    MvProperty* mvPropertyList;
};

class MusicCollection : public RefCountInterface {
public:
    virtual int getCount() = 0;
    virtual int getTotal() = 0;
    virtual int getPage() = 0;
    virtual int getPageSize() = 0;
    virtual Music* getMusic(int32_t index) = 0;
protected:
    virtual ~MusicCollection() = default;
};


class IMusicContentCenterEventHandler {
public:
    /**
     * music chart collection, callback of getMusicCharts.
     * @param requestId the request id same with return from getMusicCharts.
     * @param status callback result,  success or fail
     * @param result the result of music chart collection
     */
    virtual void onMusicChartsResult(const char* requestId, MusicContentCenterStatusCode status, agora_refptr<MusicChartCollection> result) = 0;

    /**
     * music collection, callback of getMusicCollectionByMusicChartId and searchMusic
     * @param requestId the request id same with return from getMusicCollectionByMusicChartId and searchMusic
     * @param status callback result,  success or fail
     * @param result the result of music collection
     */
    virtual void onMusicCollectionResult(const char* requestId, MusicContentCenterStatusCode status, agora_refptr<MusicCollection> result) = 0;

    /**
     * lyric url callback of getLyric
     * @param requestId the request id same with return from getLyric
     * @param lyricUrl  the lyric url for download
     */
    virtual void onLyricResult(const char* requestId, const char* lyricUrl) = 0;
    /**
     * preload callback
     *
     * @param songCode song code
     * @param percent preload progress (0 ~ 100)
     * @param status preload status (occurred err or done)
     * @param msg The extra information
     */
    virtual void onPreLoadEvent(int64_t songCode, int percent, PreloadStatusCode status, const char* msg, const char* lyricUrl = nullptr) = 0;

    virtual ~IMusicContentCenterEventHandler() {};
};

struct MusicContentCenterConfiguration {
    /**
     * appId of music content center
     */
    const char *appId;
    /**
     * music content center need rtmToken to connect with server
     */
    const char *rtmToken;
    /**
     * mccUid is who using the music content center, can be different with uid of rtc.
     */
    int64_t mccUid;
    /**
     * event handler to get callback result.
     */
    IMusicContentCenterEventHandler* eventHandler;
    MusicContentCenterConfiguration():appId(nullptr),rtmToken(nullptr),eventHandler(nullptr),mccUid(0){}
    MusicContentCenterConfiguration(const char*appid,const char* token,int64_t id,IMusicContentCenterEventHandler* handler):
        appId(appid),rtmToken(token),mccUid(id),eventHandler(handler){}
};

class IMusicPlayer : public IMediaPlayer {
protected:
    virtual ~IMusicPlayer() {};

public:
    IMusicPlayer() {};
    /**
    * open a media file with specified parameters.
    *
    * @param songCode The identify of the media file that you want to play.
    * @param type The type of the media file that you want to play. may be MP3 or MV.
    * @param resolution The resolution of the mv media file. defeult is null, take effect only the media type is MV.
    * @param startPos The playback position (ms) of the music file.
    * @return
    * - 0: Success.
    * - < 0: Failure.
    */
    virtual int open(int64_t songCode, MusicMediaType type, const char* resolution, int64_t startPos = 0) = 0;
};

class IMusicContentCenter
{
protected:
    virtual ~IMusicContentCenter(){};
public:
    IMusicContentCenter() {};

    /**
     * set coryright music token and other param
     *
     * @param configuration
     * @return
     * - 0: Success.
     * - < 0: Failure.
     */
    virtual int initialize(const MusicContentCenterConfiguration & configuration) = 0;

    /**
     * release music content center resource.
     * 
     */
    virtual void release() = 0;

    /**
    *  register event handler, only the last eventHandler is working.
    */
    virtual int registerEventHandler(IMusicContentCenterEventHandler* eventHandler) = 0;
    
    /**
    *  unregister event handler.
    */
    virtual int unregisterEventHandler() = 0;

    /**
     * Creates a music player source object and return its pointer.
     * @return
     * - The pointer to \ref rtc::IMusicPlayer "IMusicPlayer",
     *   if the method call succeeds.
     * - The empty pointer NULL, if the method call fails.
     */
    virtual agora_refptr<IMusicPlayer> createMusicPlayer() = 0;
    
    /**
     * Get music chart collection of music.
     * If the method call success, get result from the 
     * \ref agora::rtc::IMusicContentCenterEventHandler::onMusicChartsResult
     * "onMusicChartsResult" callback 
     * @return
     * - 0: Success.
     * - < 0: Failure.
     */
    virtual int getMusicCharts(agora::util::AString& requestId) = 0;
    
    /**
     * Get music collection of the music chart by musicChartId and page info. 
     * If the method call success, get result from the 
     * \ref agora::rtc::IMusicContentCenterEventHandler::onMusicCollectionResult
     * "onMusicCollectionResult" callback 
     * @param musicChartId The musicChartId id from getMusicCharts.
     * @param jsonOption The ext param, default is null.
     * @return
     * - 0: Success.
     * - < 0: Failure.
     */
    virtual int getMusicCollectionByMusicChartId(agora::util::AString& requestId, int32_t musicChartId, int32_t page, int32_t pageSize, const char* jsonOption = nullptr) = 0;
    
    /**
     * Search music by keyword and page info.
     * If the method call success, get result from the 
     * \ref agora::rtc::IMusicContentCenterEventHandler::onMusicCollectionResult
     * "onMusicCollectionResult" callback 
     * @param keyWord The key word to search.
     * @param jsonOption The ext param, default is null.
     * @return
     * - 0: Success.
     * - < 0: Failure.
     */
    virtual int searchMusic(agora::util::AString& requestId, const char* keyWord, int32_t page, int32_t pageSize, const char* jsonOption = nullptr) = 0;
    
    /**
     * preload a media file with specified parameters.
     *
     * @param songCode The identify of the media file that you want to play.
     * @param type The type of the media file that you want to play. may be MP3 or MV.
     * @param resolution The resolution of the mv media file. defeult is null, take effect only the media type is MV.
     * @return
     * - 0: Success.
     * - < 0: Failure.
     */
    virtual int preload(int64_t songCode, MusicMediaType type, const char* resolution) = 0;
    
    /**
     * check if the media file is preloaded
     *
     * @param songCode The identify of the media file that you want to play.
     * @param type The type of the media file that you want to play. may be MP3 or MV.
     * @param resolution The resolution of the mv media file. defeult is null, take effect only the media type is MV.
     * @return
     * - 0: Success, file is preloaded.
     * - < 0: Failure.
     */
    virtual int isPreloaded(int64_t songCode, MusicMediaType type, const char* resolution) = 0;

    /**
     * get lyric of the music.
     *
     * @param songCode The identify of the media file that you want to play.
     * @param LyricType The type of the lyric file. may be 0:xml or 1:lrc.
     * @return
     * - 0: Success.
     * - < 0: Failure.
     */
    virtual int getLyric(agora::util::AString& requestId, int64_t songCode, int32_t LyricType = 0) = 0;
};

}  // namespace rtc
}  // namespace agora
