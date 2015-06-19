package com.maogousoft.logisticsmobile.driver;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import com.baidu.mapapi.SDKInitializer;
import com.maogousoft.logisticsmobile.driver.api.AjaxCallBack;
import com.maogousoft.logisticsmobile.driver.api.ApiClient;
import com.maogousoft.logisticsmobile.driver.api.ResultCode;
import com.maogousoft.logisticsmobile.driver.db.DriverSqliteOpenHelper;
import com.maogousoft.logisticsmobile.driver.im.KBBinder;
import com.maogousoft.logisticsmobile.driver.im.ServiceManager;
import com.maogousoft.logisticsmobile.driver.model.DriverInfo;
import com.maogousoft.logisticsmobile.driver.model.DictInfo;
import com.maogousoft.logisticsmobile.driver.model.ShipperInfo;
import com.maogousoft.logisticsmobile.driver.model.UserInfo;
import com.maogousoft.logisticsmobile.driver.service.SharedPreferencesProvider;
import com.maogousoft.logisticsmobile.driver.utils.ExtendedImageDownloader;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ybxiang.driver.util.CrashHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * 程序主入口
 * 
 * @author maogousoft
 */
public class MGApplication extends Application {

	private final String sp = "user";
	private final String AUTOLOGIN = "auto";
	// 城市数据库地址
	private String dbUrl;
	// // 授权码
	// private String token;
	private SQLiteDatabase sdb, citySDB;
	private ImageLoader mImageLoader;
	// activity堆栈
	private Stack<Activity> stack = new Stack<Activity>();
	private SharedPreferences mSharedPreferences;
	// 字典集合
	private List<DictInfo> mDictList = null;
	// XMPP服务
	private ServiceManager manager;
	private KBBinder mBinder;
	private boolean isAnonymous = false;
    private DriverInfo driverInfo = null;
    private ShipperInfo shipperInfo = null;
    private UserInfo userInfo = null;

    @Override
	public void onCreate() {
		super.onCreate();
        //全局异常捕获
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        //
        JPushInterface.init(this);
		initImageLoader();
		initCityDB();
		initService();
		//LocHelper.getInstance(this).init();
        //初始化百度sdk
        SDKInitializer.initialize(this);
		//初始化环境地址,彩蛋功能,用于调试切换
		//initBaseUrl();
		LogUtil.d("MGApplication", Constants.BASE_URL);
	}

	private void initBaseUrl() {
		/*Constants.BASE_URL = SharedPreferencesProvider.getInstance(this).getBaseUrl();
		Constants.DRIVER_SERVER_URL = Constants.BASE_URL + "/driver";
		Constants.SHIPPER_SERVER_URL = Constants.BASE_URL + "/user";
		Constants.COMMON_SERVER_URL = Constants.BASE_URL + "/common";
		Constants.UPLOAD_FILE_URL = Constants.BASE_URL + "/upload/";
		Constants.HELP_SERVER_URL = Constants.BASE_URL + "/help.html";*/
	}

	/** 初始化图片加载工具 **/
	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_img_loading)
				.showImageForEmptyUri(R.drawable.ic_img_loading)
				.cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(2 * 1024 * 1024)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.imageDownloader(new ExtendedImageDownloader(this))
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(config);
	}

	/** 初始化城市数据库 **/
	private void initCityDB() {
		sdb = new DriverSqliteOpenHelper(this).getWritableDatabase();

		mSharedPreferences = getSharedPreferences(sp, Context.MODE_PRIVATE);

		dbUrl = new StringBuilder().append("/data/data/")
				.append(getPackageName()).append("/databases/place.db")
				.toString();
		if (!new File(dbUrl).exists()) {
			return;
		}
		citySDB = SQLiteDatabase.openOrCreateDatabase(dbUrl, null);

	}

	/** 初始化服务 **/
	private void initService() {
		manager = new ServiceManager(getApplicationContext());
	}

	/** 获取binder对象 **/
	public KBBinder getBinder() {
		return this.mBinder;
	}

	/** 获取图片加载工具 **/
	public ImageLoader getImageLoader() {
		if (mImageLoader == null) {
			initImageLoader();
		}
		return mImageLoader;
	}

	public SQLiteDatabase getDB() {
		return this.sdb;
	}

	public SQLiteDatabase getCitySDB() {
		if (this.citySDB == null) {
			initCityDB();
		}
		return this.citySDB;
	}

	/** 将activity压入栈内 **/
	public void addTask(Activity activity) {
		if (stack == null) {
			stack = new Stack<Activity>();
		}
		stack.add(activity);
	}

	/** 获取最后压入的activity **/
	public Activity currentActivity() {
		return stack.lastElement();
	}

	/** 获取倒数第二个压入的activity **/
	public Activity lastSecondActivity() {
		if (stack.size() > 1) {
			return stack.get(stack.size() - 2);
		}
		return null;
	}

	/** 结束最后压入activity **/
	public void finishLastActivity() {
		finishActivity(stack.lastElement());
	}

	/** 结束指定activity **/
	public void finishActivity(Activity activity) {
		if (activity != null) {
			stack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/** 结束指定类名的activity **/
	public void finishActivity(Class<?> cls) {
		for (Activity activity : stack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/** 结束所有activity **/
	public void finishAllActivity() {
		for (Activity activity : stack) {
			if (activity != null) {
				activity.finish();
			}
		}
		stack.clear();
	}

	/** 结束除当前的所有activity **/
	public void finishUnlessCurrentAllActivity(Activity activityCurrent) {

		for (int i = 0; i < stack.size() - 1; i++) {
			Activity activity = stack.get(i);
			if (activity != null) {
				activity.finish();
			}
		}

		// for (Activity activity : stack) {
		// if (activity != null) {
		// activity.finish();
		// }
		// }
		stack.clear();

		if (stack == null) {
			stack = new Stack<Activity>();
		}
		stack.add(activityCurrent);
	}

	/** 注销登录 **/
	public void logout() {
		mSharedPreferences.edit().remove(Constants.XMPP_USERNAME)
				.remove(Constants.XMPP_PASSWORD).remove("token")
				.remove(Constants.XMPP_DRIVER_ID).commit();
		finishAllActivity();
	}

	/** 写入用户帐号信息 **/
	public void writeUserInfo(String userName, String password, int driver_id, int user_id) {
		mSharedPreferences.edit().putString(Constants.XMPP_USERNAME, userName)
				.putString(Constants.XMPP_PASSWORD, password)
				.putString(Constants.XMPP_DRIVER_ID, "d" + driver_id)
                .putString(Constants.USER_ID, "" + user_id).commit();

		String driverId = getDriverId();
		if (!TextUtils.isEmpty(driverId)) {
			JPushInterface.init(this);
			JPushInterface.setAliasAndTags(this, driverId, null);
		}
	}

	/** 写入数据 **/
	public void writeInfo(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}
	// PR112 begin
	/**
	 * 写入账户类型数据
	 * @param userType
	 */
	public void writeUserType(int userType) {
		mSharedPreferences.edit().putInt(Constants.USER_TYPE, userType).commit();
	}
	/**
	 * 获取账户类型数据 
	 * @return
	 */
	public int getUserType() {
		return mSharedPreferences.getInt(Constants.USER_TYPE, 1);
	}
	// PR112 end

	// /**
	// * 是否是新版本覆盖安装
	// */
	// public void checkIsUpdateInstallVer() {
	// int vercode = mSharedPreferences.getInt("VERSIONCODE", -1);
	//
	// int versionCode = 0;
	// PackageManager pm = this.getPackageManager();
	// try {
	// versionCode = pm.getPackageInfo(this.getPackageName(), 0).versionCode;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// int currentVerCode = versionCode;
	//
	// if (vercode != currentVerCode) {
	// // 删除
	// }
	//
	// mSharedPreferences.edit().putInt("VERSIONCODE", currentVerCode)
	// .commit();
	// }

	/** 获取保存的手机号 **/
	public String getUserName() {

		String result = "";
		try {
			result = mSharedPreferences
					.getString(Constants.XMPP_USERNAME, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/** 获取保存的密码 **/
	public String getPassword() {

		String result = "";
		try {
			result = mSharedPreferences
					.getString(Constants.XMPP_PASSWORD, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/** 获取保存的司机编号XMPP_ID **/
    public String getDriverId() {
        String s = "";
        try {
            s = mSharedPreferences.getString(Constants.XMPP_DRIVER_ID, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /** 获取保存的用户编号 **/
    public String getUserId() {
        String s = "";
        try {
            if(getUserType() == Constants.USER_DRIVER) {
                s = mSharedPreferences.getString(Constants.XMPP_DRIVER_ID, null).replace("d", "");
            } else {
                s = mSharedPreferences.getString(Constants.USER_ID, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

	/** 获取登录用户姓名 **/
	public String getDriverName() {
		String result = "";
		try {
			result = mSharedPreferences.getString("name", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/** 设置授权码 **/
	public void setToken(String token) {
		// this.token = token;
		mSharedPreferences.edit().putString("token", token).commit();
	}

	/** 获取授权码 **/
	public String getToken() {
		// if (TextUtils.isEmpty(token)) {
		// token = mSharedPreferences.getString("token", "");
		// }
		return mSharedPreferences.getString("token", "");
	}

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

	/** 写入自动登录标识 **/
	public void writeAutoLogin(boolean auto) {
		mSharedPreferences.edit().putBoolean(AUTOLOGIN, auto).commit();
	}

	/** 检测是否开启自动登录 **/
	public boolean checkAutoLogin() {
		return mSharedPreferences.getBoolean(AUTOLOGIN, true);
	}

	/** 写入 是否已经完善了资料 **/
	public void writeIsRegOptional(boolean auto) {
		mSharedPreferences.edit().putBoolean(Constants.IS_REG_OPTIONAL, auto)
				.commit();
	}

	/** 检测 是否已经完善了资料 **/
	public boolean checkIsRegOptional() {
		return mSharedPreferences.getBoolean(Constants.IS_REG_OPTIONAL, false);
	}

	/** 写入 是否已经通过了 诚信认证 **/
	public void writeIsThroughRezheng(boolean auto) {
		mSharedPreferences.edit()
				.putBoolean(Constants.IS_THROUGH_REZHENG, auto).commit();
	}

	/** 检测 是否已经通过了 诚信认证 **/
	public boolean checkIsThroughRezheng() {
		return mSharedPreferences.getBoolean(Constants.IS_THROUGH_REZHENG,
				false);
	}

	/** 写入 新货源推送，是否响铃 **/
	public void writeIsRingNewSource(boolean auto) {
		mSharedPreferences.edit()
				.putBoolean(Constants.IS_RING_NEW_SOURCE, auto).commit();
	}

	/** 检测新货源推送，是否响铃 **/
	public boolean checkIsRingNewSource() {
		return mSharedPreferences.getBoolean(Constants.IS_RING_NEW_SOURCE,
				false);
	}

	/** 获取字典数据 **/
	public void getDictList(AjaxCallBack callBack) {
		if (mDictList != null)
			callBack.receive(ResultCode.RESULT_OK, mDictList);
		else {
			try {
				final JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.ACTION, Constants.COMMON_GET_DICT_LIST);
				jsonObject.put(Constants.TOKEN, getToken());
				jsonObject.put(Constants.JSON, null);
				ApiClient.doWithObject(Constants.COMMON_SERVER_URL, jsonObject,
						DictInfo.class, callBack);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// 绑定服务
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (KBBinder) service;
			System.out.println("服务绑定成功");
		}
	};

	/** 打开xmpp服务 **/
	public void startXMPPService() {
		manager.startService(mConnection);
	}

	/** 停止xmpp服务 **/
	public void stopXMPPService() {
		manager.stopService(mConnection);
	}

	/** 判断网络是否可用 **/
	public boolean checkNetWork() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		sdb.close();
	}

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }

	public ShipperInfo getShipperInfo() {
		return shipperInfo;
	}

	public void setShipperInfo(ShipperInfo shipperInfo) {
		this.shipperInfo = shipperInfo;
	}

	public Class preClass;
    public Class getPreClass() {
        return preClass;
    }

    public void setPreClass(Class preClass) {
        this.preClass = preClass;
    }
}
