package cn.tedu.music_player_v4.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.tedu.music_player_v4.R;
import cn.tedu.music_player_v4.app.MusicApplication;
import cn.tedu.music_player_v4.entity.Music;
import cn.tedu.music_player_v4.fragment.HotMusicFragment;
import cn.tedu.music_player_v4.fragment.NewMusicFragment;
import cn.tedu.music_player_v4.service.PlayMusicService;
import cn.tedu.music_player_v4.service.PlayMusicService.MusicBinder;
import cn.tedu.music_player_v4.util.BitmapUtils;
import cn.tedu.music_player_v4.util.BitmapUtils.BitmapCallback;
import cn.tedu.music_player_v4.util.GlobalConsts;

public class MainActivity extends FragmentActivity {
	private RadioGroup radioGroup;
	private ViewPager viewPager;
	private RadioButton rbNew;
	private RadioButton rbHot;
	private ImageView ivCMPic;
	private TextView tvCMTitle;
	private List<Fragment> fragments;
	private PagerAdapter pagerAdapter;
	private ServiceConnection conn;
	protected MusicBinder musicBinder;
	private UpdateMusicInfoReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//�ؼ���ʼ��
		setViews();
		//��viewPager����������
		setViewPagerAdapter();
		//ʵ��tab��ǩ��viewpager������
		setListeners();
		//��Service
		bindMusicService();
		//ע�����
		registComponent();
	}
	
	/**
	 * ע��������
	 */
	private void registComponent() {
		//ע��㲥������
		receiver = new UpdateMusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_START_PLAY);
		this.registerReceiver(receiver, filter);
	}

	/**
	 * ��service
	 */
	private void bindMusicService() {
		Intent intent = new Intent(this, PlayMusicService.class);
		conn = new ServiceConnection() {
			//�쳣�Ͽ�ʱ ִ��
			public void onServiceDisconnected(ComponentName name) {
			}
			//����service�󶨳ɹ��� ִ��
			public void onServiceConnected(ComponentName name, IBinder service) {
				musicBinder = (MusicBinder) service;
				//�󶨳ɹ���  ��musicBinder ��Fragment
				NewMusicFragment f = (NewMusicFragment) fragments.get(0);
				f.setMusicBinder(musicBinder);
			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		//�����Service�İ�
		this.unbindService(conn);
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	/**
	 * ����
	 */
	private void setListeners() {
		//����viewpager ���� ������
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					Log.i("info","�����˵�1ҳ..");
					rbNew.setChecked(true);
					break;
				case 1:
					Log.i("info","�����˵�2ҳ..");
					rbHot.setChecked(true);
					break;
				}
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		//������� ����viewpager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioNew:
					Log.i("info", "ѡ����radioNew..");
					viewPager.setCurrentItem(0);
					break;
				case R.id.radioHot:
					Log.i("info", "ѡ����radioHot..");
					viewPager.setCurrentItem(1);
					break;
				}
			}
		});
	}

	/**
	 * ��viewPager����������
	 */
	private void setViewPagerAdapter() {
		//����Fragment����Դ
		fragments = new ArrayList<Fragment>();
		//��fragments���������Fragment
		fragments.add(new NewMusicFragment());
		fragments.add(new HotMusicFragment());
		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
	}

	/**
	 * �ؼ���ʼ��
	 */
	private void setViews() {
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		rbNew = (RadioButton) findViewById(R.id.radioNew);
		rbHot = (RadioButton) findViewById(R.id.radioHot);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		ivCMPic = (ImageView) findViewById(R.id.ivCMPic);
		tvCMTitle = (TextView) findViewById(R.id.tvCMTitle);
	}

	/**
	 * ��дviewPager��Adapter
	 */
	class MyPagerAdapter extends FragmentPagerAdapter{
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}
	
	/**
	 * �������ڸ������ֽ��ȵĹ㲥������
	 */
	class UpdateMusicInfoReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			//�����Ѿ���ʼ����
			if(action.equals(GlobalConsts.ACTION_START_PLAY)){
				//��ȡ����ǰ���ڲ��ŵ�music����
				MusicApplication app = (MusicApplication) getApplication();
				List<Music> list = app.getMusicPlayList();
				int position = app.getPosition();
				Music m = list.get(position);
				//����CircleImageView   TextView
				String picPath = m.getPic_small();
				String title = m.getTitle();
				tvCMTitle.setText(title);
				BitmapUtils.loadBitmap(MainActivity.this, picPath, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						if(bitmap != null){
							ivCMPic.setImageBitmap(bitmap);
							//������ת����
							RotateAnimation anim = new RotateAnimation(0, 360, ivCMPic.getWidth()/2, ivCMPic.getHeight()/2);
							anim.setDuration(20000);
							//������ת
							anim.setInterpolator(new LinearInterpolator());
							anim.setRepeatCount(Animation.INFINITE); //һֱת
							ivCMPic.startAnimation(anim);
						}else{
							ivCMPic.setImageResource(R.drawable.ic_launcher);
						}
					}
				});
			}
		}
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
