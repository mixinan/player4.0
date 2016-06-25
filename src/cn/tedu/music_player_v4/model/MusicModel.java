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
 * ������ص�ҵ����
 */
public class MusicModel{
	/**
	 * �첽��������   ����json��ȡ��  List<SongUrl>  SongInfo
	 * �����߳��е���callback.onSongInfoLoaded()
	 * @param songId
	 * @param callback
	 */
	public void getSongInfoBySongId(final String songId, final SongInfoCallback callback){
		AsyncTask<String, String, Music> task = new AsyncTask<String, String, Music>(){
			//�ڹ����߳��з�������  ����json
			protected Music doInBackground(String... params) {
				//��������
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
			//���߳��е���callback�ص�����
			protected void onPostExecute(Music music) {
				if(music!=null){
					callback.onSongInfoLoaded(music.getUrls(), music.getSongInfo());
				}else{
					callback.onSongInfoLoaded(null, null);
				}
			}
		};
		//ִ���첽����
		task.execute();
	}
	
	/**
	 * ��ѯ�¸��� 
	 * @param f
	 * @param offset
	 * @param size
	 */
	public void findNewMusicList(final Callback callback, final int offset, final int size){
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			//�����߳���ִ��   ���� http���� ����List
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
			//���߳���ִ��  ����callback�ķ��� ִ�к�������
			protected void onPostExecute(List<Music> musics) {
				//Log.i("info", ""+musics);
				//����UI����
				callback.onMusicListLoaded(musics);
			}
		};
		task.execute(); //ִ���첽����
	}
	
	/**
	 * ����songInfo����Ҫ�Ļص��ӿ�
	 */
	public interface SongInfoCallback{
		/**
		 * �����ֵĻ�����Ϣ������Ϻ�  
		 * ���������߳����Զ�ִ��
		 * @param url
		 * @param info
		 */
		void onSongInfoLoaded(List<SongUrl> url, SongInfo info);
	}
	
	public interface Callback {
		/**
		 * ���б������Ϻ� ������ø÷��� 
		 * �ڸ÷�����ʵ������Ҫִ���б������Ϻ��ҵ���߼�
		 * @param musics
		 */
		void onMusicListLoaded(List<Music> musics);
	}
	
}

