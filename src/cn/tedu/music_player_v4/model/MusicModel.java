package cn.tedu.music_player_v4.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;
import cn.tedu.music_player_v4.entity.Music;
import cn.tedu.music_player_v4.entity.SongInfo;
import cn.tedu.music_player_v4.entity.SongUrl;
import cn.tedu.music_player_v4.fragment.NewMusicFragment;
import cn.tedu.music_player_v4.util.HttpUtils;
import cn.tedu.music_player_v4.util.JSONParser;
import cn.tedu.music_player_v4.util.UrlFactory;
import cn.tedu.music_player_v4.util.XmlParser;

/**
 * 音乐相关的业务类
 */
public class MusicModel{
	/**
	 * 异步发送请求   解析json获取：  List<SongUrl>  SongInfo
	 * 在主线程中调用callback.onSongInfoLoaded()
	 * @param songId
	 * @param callback
	 */
	public void getSongInfoBySongId(final String songId, final SongInfoCallback callback){
		AsyncTask<String, String, Music> task = new AsyncTask<String, String, Music>(){
			//在工作线程中发送请求  解析json
			protected Music doInBackground(String... params) {
				//发送请求
				String path = UrlFactory.getSongInfoUrl(songId);
				try {
					InputStream is = HttpUtils.get(path);
					String json=HttpUtils.isToString(is);
					//Log.i("info", ""+json);
					Music music = JSONParser.parseSongInfo(json);
					return music;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			//主线程中调用callback回调方法
			protected void onPostExecute(Music music) {
				if(music!=null){
					callback.onSongInfoLoaded(music.getUrls(), music.getSongInfo());
				}else{
					callback.onSongInfoLoaded(null, null);
				}
			}
		};
		//执行异步任务
		task.execute();
	}
	
	/**
	 * 查询新歌榜榜单 
	 * @param f
	 * @param offset
	 * @param size
	 */
	public void findNewMusicList(final Callback callback, final int offset, final int size){
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			//工作线程中执行   发送 http请求 解析List
			protected List<Music> doInBackground(String... params) {
				String path = UrlFactory.getNewMusicListUrl(offset, size);
				try {
					InputStream is = HttpUtils.get(path);
					List<Music> musics=XmlParser.parseMusicList(is);
					return musics;
					//String xml=HttpUtils.isToString(is);
					//Log.i("info", ""+xml);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
				return null;
			}
			//主线程中执行  调用callback的方法 执行后续操作
			protected void onPostExecute(List<Music> musics) {
				//Log.i("info", ""+musics);
				//更新UI界面
				callback.onMusicListLoaded(musics);
			}
		};
		task.execute(); //执行异步任务
	}
	
	/**
	 * 访问songInfo所需要的回调接口
	 */
	public interface SongInfoCallback{
		/**
		 * 当音乐的基本信息加载完毕后  
		 * 将会在主线程中自动执行
		 * @param url
		 * @param info
		 */
		void onSongInfoLoaded(List<SongUrl> url, SongInfo info);
	}
	
	public interface Callback {
		/**
		 * 当列表加载完毕后 将会调用该方法 
		 * 在该方法的实现中需要执行列表加载完毕后的业务逻辑
		 * @param musics
		 */
		void onMusicListLoaded(List<Music> musics);
	}
	
}

