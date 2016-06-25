package cn.tedu.music_player_v4.service;

import java.io.IOException;

import cn.tedu.music_player_v4.util.GlobalConsts;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;

/**
 * �������ֵ�service
 */
public class PlayMusicService extends Service{
	private MediaPlayer player;
	
	@Override
	public void onCreate() {
		//��ʼ��������
		player = new MediaPlayer();
		player.setOnPreparedListener(new OnPreparedListener() {
			//prepare��ɺ�  ִ�и÷���
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				//�����Զ���㲥  ����Activity �����Ѿ���ʼ����
				Intent i = new Intent(GlobalConsts.ACTION_START_PLAY);
				sendBroadcast(i);
			}
		});
	}
	
	/**
	 * ���пͻ��˰󶨸�serviceʱ  ִ�� 
	 * context.bindService()
	 */
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}

	/**
	 * ���ظ��ͻ��˵�binder����
	 * �������Ÿ��ͻ��˵��õĽӿڷ���
	 */
	public class MusicBinder extends Binder{
		/**
		 * ��������
		 * @param url  ���ֵ�·��
		 */
		public void playMusic(String url){
			try {
				player.reset();
				player.setDataSource(url);
				//�첽����������Ϣ
				player.prepareAsync();
				//��player׼����ɺ�  ִ��start����
				//��player���ü���
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}


