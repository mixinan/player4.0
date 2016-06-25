package cn.tedu.music_player_v4.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import cn.tedu.music_player_v4.R;
import cn.tedu.music_player_v4.adapter.MusicAdapter;
import cn.tedu.music_player_v4.app.MusicApplication;
import cn.tedu.music_player_v4.entity.Music;
import cn.tedu.music_player_v4.entity.SongInfo;
import cn.tedu.music_player_v4.entity.SongUrl;
import cn.tedu.music_player_v4.model.MusicModel;
import cn.tedu.music_player_v4.service.PlayMusicService.MusicBinder;

/**
 * 显示新歌榜的数据列表
 */
public class NewMusicFragment extends Fragment{
	private ListView listView;
	private MusicAdapter adapter;
	private List<Music> musics;
	MusicModel model = new MusicModel();
	private MusicBinder musicBinder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_list, null);
		//初始化控件
		setViews(view);
		//给listView添加事件监听
		setListeners();
		//查询新歌榜榜单数据 List<Music>
		model.findNewMusicList(new MusicModel.Callback() {
			public void onMusicListLoaded(List<Music> musics) {
				//将会在列表加载完毕后执行
				NewMusicFragment.this.musics = musics;
				setAdapter(musics);
			}
		}, 0, 20);
		return view;
	}
	
	/**
	 * 设置监听
	 */
	private void setListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//把当前播放列表与position保存到MusicApplication中
				MusicApplication app = (MusicApplication) getActivity().getApplication();
				app.setMusicPlayList(musics);
				app.setPosition(position);
				
				final Music m=musics.get(position);
				String songId = m.getSong_id();
				//根据songId搜索该歌曲的详细信息
				model.getSongInfoBySongId(songId, new MusicModel.SongInfoCallback() {
					public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
						//判断获取到的数据是否是null 
						if(urls == null || info==null){
							Toast.makeText(getActivity(), "音乐加载失败, 检查网络", Toast.LENGTH_SHORT).show();
							return;
						}
						//开始准备播放音乐
						m.setUrls(urls);
						m.setSongInfo(info);
						//获取当前需要播放的音乐的路径
						SongUrl url = urls.get(0);
						String musicpath=url.getShow_link();
						//Log.i("info", "path:"+musicpath);
						//开始播放音乐
						musicBinder.playMusic(musicpath);
					}
				});
			}
		});
	}

	/**
	 * 初始化
	 * @param view
	 */
	private void setViews(View view) {
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	/**
	 * 给listView设置适配器
	 */
	public void setAdapter(List<Music> musics){
		adapter = new MusicAdapter(getActivity(), musics, listView);
		listView.setAdapter(adapter);
	}
	
	public void setMusicBinder(MusicBinder binder){
		this.musicBinder = binder;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//把adapter中的线程停掉
		adapter.stopThread();
	}
	
}


