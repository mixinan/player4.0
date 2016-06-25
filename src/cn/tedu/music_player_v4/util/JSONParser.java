package cn.tedu.music_player_v4.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.tedu.music_player_v4.entity.Music;
import cn.tedu.music_player_v4.entity.SongInfo;
import cn.tedu.music_player_v4.entity.SongUrl;

/**
 * ���ڽ���json
 */
public class JSONParser {

	/**
	 * ����json�ַ��� ��ȡ���ֵĻ�����Ϣ
	 * @param json
	 * { songurl: {url: [{},{},{},{}] }  , songinfo:  {} }
	 * @return
	 * @throws JSONException 
	 */
	public static Music parseSongInfo(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONObject urlObj = obj.getJSONObject("songurl");
		JSONObject infoObj = obj.getJSONObject("songinfo");
		JSONArray urlAry = urlObj.getJSONArray("url");
		//����SongUrl����
		List<SongUrl> urls=parseUrls(urlAry);
		//����SongInfo
		SongInfo info=parseInfo(infoObj);
		Music music = new Music();
		music.setUrls(urls);
		music.setSongInfo(info);
		return music;
	}

	/**
	 * �������ֵĻ�����Ϣ
	 * @param infoObj  {}
	 * @return
	 * @throws JSONException 
	 */
	private static SongInfo parseInfo(JSONObject infoObj) throws JSONException {
		SongInfo info = new SongInfo(
				infoObj.getString("pic_huge"), 
				infoObj.getString("album_1000_1000"), 
				infoObj.getString("album_500_500"), 
				infoObj.getString("compose"), 
				infoObj.getString("bitrate"), 
				infoObj.getString("artist_500_500"), 
				infoObj.getString("album_title"), 
				infoObj.getString("title"), 
				infoObj.getString("pic_radio"), 
				infoObj.getString("language"), 
				infoObj.getString("lrclink"), 
				infoObj.getString("pic_big"), 
				infoObj.getString("pic_premium"), 
				infoObj.getString("artist_480_800"), 
				infoObj.getString("country"), 
				infoObj.getString("artist_id"), 
				infoObj.getString("album_id"), 
				infoObj.getString("ting_uid"), 
				infoObj.getString("artist_1000_1000"), 
				infoObj.getString("all_artist_id"), 
				infoObj.getString("artist_640_1136"), 
				infoObj.getString("publishtime"), 
				infoObj.getString("share_url"), 
				infoObj.getString("author"), 
				infoObj.getString("pic_small"), 
				infoObj.getString("song_id")
				);
		return info;
	}

	/**
	 * [{},{},{},{},{}]
	 * @param urlAry
	 * @return
	 * @throws JSONException 
	 */
	private static List<SongUrl> parseUrls(JSONArray urlAry) throws JSONException {
		List<SongUrl> urls = new ArrayList<SongUrl>();
		for(int i = 0; i<urlAry.length(); i++){
			JSONObject obj=urlAry.getJSONObject(i);
			SongUrl url = new SongUrl(
					obj.getString("show_link"), 
					obj.getString("song_file_id"), 
					obj.getString("file_size"), 
					obj.getString("file_extension"), 
					obj.getString("file_duration"), 
					obj.getString("file_bitrate"), 
					obj.getString("file_link")
			);
			urls.add(url);
		}
		return urls;
	}

	
}
