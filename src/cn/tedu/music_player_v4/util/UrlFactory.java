package cn.tedu.music_player_v4.util;

import android.util.Log;

/**
 * URL����  �������е�url��ַ
 */
public class UrlFactory {
	/**
	 * ��ȡ�¸��������ַ
	 * @param offset  ��ʼ��Ŀ��С��
	 * @param size   �������ֵĸ���
	 * @return
	 */
	public static String getNewMusicListUrl(int offset, int size){
		String path ="http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offset+"&size="+size;
		return path;
	}

	/**
	 * ��ѯ������ϸ��Ϣ��url��ַ
	 * @param songId
	 * @return
	 */
	public static String getSongInfoUrl(String songId) {
		String path = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="+songId+"&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
		return path;
	}
}
