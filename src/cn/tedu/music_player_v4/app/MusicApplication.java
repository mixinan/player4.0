package cn.tedu.music_player_v4.app;

import java.util.List;

import cn.tedu.music_player_v4.entity.Music;

import android.app.Application;

/**
 * 当app启动时创建
 */
public class MusicApplication extends Application {
	private List<Music> musicPlayList;
	private int position;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public List<Music> getMusicPlayList() {
		return musicPlayList;
	}

	public void setMusicPlayList(List<Music> musicPlayList) {
		this.musicPlayList = musicPlayList;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
