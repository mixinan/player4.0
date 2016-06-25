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
		//控件初始化
		setViews();
		//给viewPager设置适配器
		setViewPagerAdapter();
		//实现tab标签与viewpager的联动
		setListeners();
		//绑定Service
		bindMusicService();
		//注册组件
		registComponent();
	}
	
	/**
	 * 注册各种组件
	 */
	private void registComponent() {
		//注册广播接收器
		receiver = new UpdateMusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_START_PLAY);
		this.registerReceiver(receiver, filter);
	}

	/**
	 * 绑定service
	 */
	private void bindMusicService() {
		Intent intent = new Intent(this, PlayMusicService.class);
		conn = new ServiceConnection() {
			//异常断开时 执行
			public void onServiceDisconnected(ComponentName name) {
			}
			//当与service绑定成功后 执行
			public void onServiceConnected(ComponentName name, IBinder service) {
				musicBinder = (MusicBinder) service;
				//绑定成功后  把musicBinder 给Fragment
				NewMusicFragment f = (NewMusicFragment) fragments.get(0);
				f.setMusicBinder(musicBinder);
			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		//解除与Service的绑定
		this.unbindService(conn);
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	/**
	 * 监听
	 */
	private void setListeners() {
		//滑动viewpager 控制 导航栏
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					Log.i("info","滚到了第1页..");
					rbNew.setChecked(true);
					break;
				case 1:
					Log.i("info","滚到了第2页..");
					rbHot.setChecked(true);
					break;
				}
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		//点击导航 控制viewpager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioNew:
					Log.i("info", "选择了radioNew..");
					viewPager.setCurrentItem(0);
					break;
				case R.id.radioHot:
					Log.i("info", "选择了radioHot..");
					viewPager.setCurrentItem(1);
					break;
				}
			}
		});
	}

	/**
	 * 给viewPager设置适配器
	 */
	private void setViewPagerAdapter() {
		//构建Fragment数据源
		fragments = new ArrayList<Fragment>();
		//向fragments集合中添加Fragment
		fragments.add(new NewMusicFragment());
		fragments.add(new HotMusicFragment());
		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
	}

	/**
	 * 控件初始化
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
	 * 编写viewPager的Adapter
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
	 * 接受用于更新音乐进度的广播接收器
	 */
	class UpdateMusicInfoReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			//音乐已经开始播放
			if(action.equals(GlobalConsts.ACTION_START_PLAY)){
				//获取到当前正在播放的music对象
				MusicApplication app = (MusicApplication) getApplication();
				List<Music> list = app.getMusicPlayList();
				int position = app.getPosition();
				Music m = list.get(position);
				//更新CircleImageView   TextView
				String picPath = m.getPic_small();
				String title = m.getTitle();
				tvCMTitle.setText(title);
				BitmapUtils.loadBitmap(MainActivity.this, picPath, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						if(bitmap != null){
							ivCMPic.setImageBitmap(bitmap);
							//启动旋转动画
							RotateAnimation anim = new RotateAnimation(0, 360, ivCMPic.getWidth()/2, ivCMPic.getHeight()/2);
							anim.setDuration(20000);
							//匀速旋转
							anim.setInterpolator(new LinearInterpolator());
							anim.setRepeatCount(Animation.INFINITE); //一直转
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
