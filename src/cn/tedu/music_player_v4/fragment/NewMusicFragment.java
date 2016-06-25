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
 * ��ʾ�¸��������б�
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
		//��ʼ���ؼ�
		setViews(view);
		//��listView����¼�����
		setListeners();
		//��ѯ�¸������� List<Music>
		model.findNewMusicList(new MusicModel.Callback() {
			public void onMusicListLoaded(List<Music> musics) {
				//�������б������Ϻ�ִ��
				NewMusicFragment.this.musics = musics;
				setAdapter(musics);
			}
		}, 0, 20);
		return view;
	}
	
	/**
	 * ���ü���
	 */
	private void setListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//�ѵ�ǰ�����б���position���浽MusicApplication��
				MusicApplication app = (MusicApplication) getActivity().getApplication();
				app.setMusicPlayList(musics);
				app.setPosition(position);
				
				final Music m=musics.get(position);
				String songId = m.getSong_id();
				//����songId�����ø�������ϸ��Ϣ
				model.getSongInfoBySongId(songId, new MusicModel.SongInfoCallback() {
					public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
						//�жϻ�ȡ���������Ƿ���null 
						if(urls == null || info==null){
							Toast.makeText(getActivity(), "���ּ���ʧ��, �������", Toast.LENGTH_SHORT).show();
							return;
						}
						//��ʼ׼����������
						m.setUrls(urls);
						m.setSongInfo(info);
						//��ȡ��ǰ��Ҫ���ŵ����ֵ�·��
						SongUrl url = urls.get(0);
						String musicpath=url.getShow_link();
						//Log.i("info", "path:"+musicpath);
						//��ʼ��������
						musicBinder.playMusic(musicpath);
					}
				});
			}
		});
	}

	/**
	 * ��ʼ��
	 * @param view
	 */
	private void setViews(View view) {
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	/**
	 * ��listView����������
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
		//��adapter�е��߳�ͣ��
		adapter.stopThread();
	}
	
}


