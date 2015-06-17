package com.maogousoft.logisticsmobile.driver;

public class Constants {
    /*
    易运宝信息平台（微信公众服务号）
        帐号：lanchong@1yunbao.com
        密码：yiyunkeji
    微信开发账号:
        帐号：395354735@qq.com
        密码：1yunbao.com
    友盟统计：
        账号：orientwindweibo@gmail.com
        密码：lanchong
        Key:  51c939ad56240b834e01d81e
    * */

	// 位置上报间隔
	public static final int LOC_UPDATE_TIME = 30 * 60 * 1000;
    // 拨打的货主电话
    public static String CALL_NUMBER = "";
    public static String CALL_NUMBER_SOURCE = "";
    public static Integer CALL_NUMBER_SOURCE_ORDER_ID = null;
	// 服务器请求参数
	public static final String ACTION = "action";

	public static final String TOKEN = "token";

	public static final String JSON = "json";

	// 车型
	public static final String CAR_TYPE = "car_type";

	// 货物类型
	public static final String CARGO_TYPE = "cargo_type";

	// 装卸方式
	public static final String DISBURDEN = "disburden";

	// 运输方式
	public static final String SHIP_TYPE = "ship_type";

	/** 版本类型，1android 2 ios,3 web **/
	public static final int DEVICE_TYPE = 1;

	public static final String SHARED_PREFERENCE_NAME = "client_preferences";

	public static final String PREFERENCE_NAME_PHONE_STATE = "phone_state";

	public static final String FIRST_USE = "first_use";

	public static final String BASE_URL_KEY = "base_url_key";

	// PR112 账户类型
	public static final String USER_TYPE = "USER_TYPE";

    //用户id
    public static final String USER_ID = "USER_ID";

    //用户账号
    public static final String DRIVER_PHONE = "DRIVER_PHONE";

    public static final String INVOICE_TYPE = "INVOICE_TYPE";
    public static final String INVOICE_USER_TYPE = "INVOICE_USER_TYPE";
	// PREFERENCE KEYS

	public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

	public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

	public static final String API_KEY = "API_KEY";

	public static final String VERSION = "VERSION";

	public static final String XMPP_HOST = "XMPP_HOST";

	public static final String XMPP_PORT = "XMPP_PORT";

	// xmpp服务名字
	public static final String XMPP_SERVER_NAME = "@www.1yunbao.com";

	public static final String XMPP_USERNAME = "XMPP_USERNAME";

	public static final String XMPP_PASSWORD = "XMPP_PASSWORD";

	public static final String XMPP_DRIVER_ID = "XMPP_DRIVER_ID";
	public static final String XMPP_USER_ID = "XMPP_USER_ID";

	public static final String XMPP_MESSAGE = "messageinfo";

	// 消息接收到的广播
	public static final String MESSAGE_RECEIVE = "receive";

	// 当前会话
	public static final String CURRENT_MESSAGE_RECEIVE = "current_message";

	// public static final String USER_KEY = "USER_KEY";

	public static final String DEVICE_ID = "DEVICE_ID";

	public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";

	public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

	public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";

	public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

	public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

	public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

	// NOTIFICATION FIELDS

	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

	public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

	public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

	public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

	public static final String NOTIFICATION_URI = "NOTIFICATION_URI";

	// INTENT ACTIONS

	public static final String ACTION_SHOW_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";

	public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";

	public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";

    public static final String ACTION_NOTIFICATION_ORDER_CONFIRM = "ACTION_NOTIFICATION_ORDER_CONFIRM";//订单确认
	// add by ybxiang begin
	// /** 服务器地址 **/
	// // public static final String DRIVER_SERVER_URL =
	// // "http://www.1yunbao.com/service/driver";
	// public static final String DRIVER_SERVER_URL =
	// "http://www.1yunbao.com/service/driver";
	//
	// /** 公用接口地址 **/
	// public static final String COMMON_SERVER_URL =
	// "http://www.1yunbao.com/service/common";
	//
	// /** 文件上传地址 **/
	// public static final String UPLOAD_FILE_URL =
	// "http://www.1yunbao.com/service/upload/";
	//
	// /** 帮助页面地址 **/
	// public static final String HELP_SERVER_URL =
	// "http://www.1yunbao.com/service/help.html";
	/** 服务器地址 **/
	// public static final String DRIVER_SERVER_URL =
	// "http://www.1yunbao.com/service/driver";
	//public static final String BASE_URL = "http://www.1yunbao.com/service";
    public static String BASE_URL = "http://1yunbao.com:8083/service";
	//public static final String BASE_URL = "http://192.168.1.110:8080/service";
	//public static String BASE_URL = "http://192.168.2.189:8080/service";
    /**
     1yunbao.com:8083/admin
     帐号：admin
     密码：111111

     测试环境:货主 16888668866  123456      司机 16899669966  123456

     线上:通用 15184432705 123456
     */

	/** 服务器地址 **/
	public static String DRIVER_SERVER_URL = BASE_URL + "/driver";
	
	/** 货主端注册地址**/
	public static String SHIPPER_SERVER_URL = BASE_URL + "/user";

	/** 公用接口地址 **/
	public static String COMMON_SERVER_URL = BASE_URL + "/common";

	/** 文件上传地址 **/
	public static String UPLOAD_FILE_URL = BASE_URL + "/upload/";

	/** 帮助页面地址 **/
	public static String HELP_SERVER_URL = BASE_URL + "/help.html";

	/** 司机端注册获取验证码 **/
	public static final String DRIVER_REG_GETCODE = "driver_reg_getcode";
	
	/** 货主 注册获取验证码 **/
	public static final String SHIPPER_REG_GETCODE = "user_reg_getcode";

	/** 司机端注册 **/
	public static final String DRIVER_REG = "driver_reg";

	/** 司机端完善资料 **/
	public static final String DRIVER_REG_OPTIONAL = "driver_reg_optional";

	/** 货主注册 **/
	public static final String SHIPPER_REG = "user_reg";

    /** 货主找回密码 **/
    public static final String SHIPPER_FORGET_PASSWORD = "forgetPWD";

    /** 货主端完善资料 2 **/
	public static final String SHIPPER_REG_OPTIONAL2 = "user_reg_optional";

	/** 诚信认证 **/
	public static final String DRIVER_REG_RENZHENG = "driver_authentication";

	/** 司机端登录 **/
	public static final String DRIVER_LOGIN = "driverlogin";

    /** 货主端登录 **/
    public static final String USER_LOGIN = "userlogin";

	// /** 货源详情 **/
	public static final String GET_SOURCE_ORDER_DETAIL = "get_source_order_detail";

	/** 货源详情 2 **/
	public static final String GET_SOURCE_ORDER_DETAIL2 = "get_source_order_detail_2";

	/** 搜索新货源 **/
	public static final String SEARCH_SOURCE_ORDER = "search_source_order";
	
	/** 关注此路线  **/
	public static final String FOCUS_LINE = "add_Attention_Line";

	/** 新货源关注 **/
	public static final String ATTENTION_SOURCE_ORDER = "attention_source_order";

	/** 新货源列表 **/
	public static final String QUERY_SOURCE_ORDER = "query_source_order";

	/** 货源总数 **/
	public static final String QUERY_SOURCE_ORDER_COUNT = "query_source_order_count";

	/** 好友組列表 **/
	public static final String QUERY_FRIENDS_GROUP = "query_friends_group";
	
	/** 司机端:已关注的所有货主好友列表 **/
	public static final String QUERY_FRIENDS = "query_attentionUserList";

	/** 已完成订单 **/
	public static final String DRIVER_QUERY_FINISHED_ORDER = "driver_query_finished_order";

	/** 在途货源取消抢单 **/
	public static final String CANCEL_PLACE_SOURCE_ORDER = "cancel_place_source_order";

	/** 获取在途货源 **/
	public static final String QUERY_PENDING_SOURCE_ORDER_IN_SHIPPING = "query_pending_source_order_in_shipping";

	/** 更新在途状态 **/
	public static final String SHIPPING_ORDER_UPDATE_STATUS = "shipping_order_update_status";

	/** 司机端:更新位置 **/
	public static final String DRIVER_UPDATE_LOCATION = "driver_reg_optional";

	/** 回单密码 **/
	public static final String VALIDATE_RECEIPT_PASSWORD = "validate_receipt_password";

	/** 获取司机信息 **/
	public static final String GET_DRIVER_INFO = "get_driver_info";

	/** 获取字典数据 **/
	public static final String COMMON_GET_DICT_LIST = "common_get_dict_list";

	/** 意见反馈 **/
	public static final String POST_FEEDBACK = "post_feedback";

	/** 改变密码 **/
	public static final String CHANGE_PASSWORD = "change_password";

    /** 修改支付密码 **/
	public static final String CHANGE_PAY_PASSWORD = "change_pay_password";

    /** 推送货源 */
    public static final String PUSH_ORDER = "push_order";

    /** 装车失败确认完结货单 */
    public static final String LOADING_FAIL_CONTRACT_BALANCE = "loading_fail_contract_balance";

	/** 账户余额 **/
	public static final String GET_ACCOUNT_GOLD = "get_account_gold";

	/** 信息中心 **/
	public static final String QUERY_MESSAGE = "query_message";

    /** 货主端:获取货主信息 */
	public static final String GET_USER_INFO = "get_user_info";

	/** 司机评价列表 **/
	public static final String GET_DRIVER_REPLY = "get_driver_reply";

	/** 货主评价列表 **/
	public static final String GET_USER_REPLY = "get_user_reply";

	/** 忘记密码获取验证码 **/
	public static final String GET_PASSWORD_VCODE = "get_password_vcode";

	/** 找回密码 **/
	public static final String GET_PASSWORD = "get_password";

	/** 28充值请求接口 */
	public static final String COMMON_REQUEST_PAY = "common_request_pay";

	/** 31充值记录 */
	public static final String DRIVER_PAY_HISTORY = "driver_pay_history";

	/** 卡密充值 */
	public static final String DRIVER_COUPON = "driver_coupon";

    /** 代充值 */
    public static final String PROXY_PAY_MONEY = "proxy_pay_momeny";

    /** 转账 */
    public static final String TRANSFER_ACCOUNTS = "transfer_accounts";

    /** 提现 */
    public static final String WITHDRAW_DEPOSIT = "withdraw_deposit";

	/** 添加商户(物流园区) */
	public static final String ADD_VENDER = "add_vender";

	/** 附近商户列表(物流园区) **/
	public static final String QUERY_VENDER = "query_vender";

    /** 添加商户 */
	public static final String ADD_VENDER_OLD = "add_memberPrivilege";

	/** 附近商户列表 **/
	public static final String QUERY_VENDER_OLD = "query_memberPrivilege";

	/** 获取商户评价 **/
	public static final String QUERY_VENDER_REPLY = "query_vender_reply";

	/** 账户记录 */
	public static final String COMMON_GET_BUSINESS = "common_get_business";

	/** 修改主营路线 */
	public static final String DRIVER_UPDATE_LINE = "driver_update_line";

	/** 商户评价 */
	public static final String ADD_VENDER_REPLY = "add_vender_reply";

	/** 分享页面提示 */
	public static final String GET_SHARE_INFO = "get_share_info";

	/** 设置 搜索货源次数 */
	public static final String ADD_SEARCH_COUNT = "add_search_count";

	/** 周边范围 2000 米 */
	public static final int DISTANCE = 2000;

	/** 百度key */
    /**
     * 百度帐号：llanchong
     * 密码：1yunbao56dp
     */
	public static final String strKey = "E109ADE1D56A36882E4C245A18B532DEC2E48DAE";
	public static final String BAIDU_APP_Key = "fGahmvRg6YiiqNTY72uoWE9W";
	public static final String BAIDU_CLOUD_SEARCH_Key = "IrNE1ZluMh9OAThzil9CeXQY";

    /** 百度云检索tableID */
    public static final int BAIDU_LBS_TABLE_ID = 76593;

	/** 易宝 */
	public final static String CUSTOMER_NUMBER = "10012061548";

	public final static String KEY = "og779h1A30w7V637JG8SAF4gQ1g17y7d9ytu849aR6A1493Hj6V8d85H3A15";

    /** 微信支付:统一下单*/
    public final static String WEIXIN_PLACE_ORDER = "weixin_place_order";

    /** 微信支付:后台查询实际支付结果*/
    public final static String WEIXIN_QUERY_PAY_RESULT = "weixin_query_pay_result";

	/** 是否已经完善了资料 */
	public static final String IS_REG_OPTIONAL = "is_reg_optional";

	/** 是否已经通过了 诚信认证 */
	public static final String IS_THROUGH_REZHENG = "is_through_rezheng";

	/** 新货源推送，是否响铃 */
	public static final String IS_RING_NEW_SOURCE = "is_ring_new_source";

    /** 获取点赞数 */
    public static final String GET_PRAISE_COUNT = "get_praise_count";

	/* 验证支付密码 */
	public static final String VALIDATION_PAY_PASSWORD = "validation_pay_password";

    /** 货主端:发布货源 */
    public static final String PUBLISH_SOURCE = "publish_order";

    /** 司机端:关注货主 */
    public static final String ATTENTION_SOURCE_USER = "attentionSourceUser";

    /** 司机端:取消关注货主 */
    public static final String CANCEL_ATTENTION_SOURCE_USER = "cancelSourceUser";

    /** 司机端:好友货源 */
    public static final String FRIEND_ORDER_LIST = "getFriendOrderList";

    /** 司机端:关注货源 */
    public static final String QUERY_MAIN_LINE_ORDER = "query_main_line_order";

    /** 司机端:发布车源 */
    public static final String PUBLISH_DRIVER_CAR_INFO = "pulish_driverCarInfo";

    /** 司机端:已发布车源列表 */
    public static final String QUERY_DRIVER_CAR_INFO_LIST = "getPublishOptionsInfoListByDriver";

    /** 司机端:查询多有关注线路 */
    public static final String QUERY_ALL_FOCUS_LINE = "query_Attention_Line";

    /** 司机端:更新关注线路 */
    public static final String UPDATE_FOCUS_LINE = "update_Attention_Line";

    /** 司机端:删除关注线路 */
    public static final String DELETE_ALL_FOCUS_LINE = "delete_Attention_Line";

    /** 司机端:摇一摇 */
    public static final String SHAKE_ONE_SHAKE = "shake_one_shake";

    /** 司机端:报价 */
    public static final String DRIVER_QUOTE = "driver_quote";

    /** 司机端:抢单 */
    public static final String PLACE_SOURCE_ORDER = "place_source_order";

    /** 司机端:给货主点赞 */
    public static final String DRIVER_TO_USER_PRAISE = "driver_toUser_praise";

    /** 司机端:给货主评价 */
    public static final String RATING_TO_USER = "rating_to_user";

    /** 司机端:货单列表 */
    public static final String QUERY_PENDING_SOURCE_ORDER = "query_pending_source_order";

    /** 司机端：待定货单(删除) */
    public static final String DELETE_PENDING_ORDER = "remove_driver_panding_source_order";

    /** 司机端：待定货单(已定) */
    public static final String DONE_PENDING_ORDER = "has_been_driver_panding_source_order";

    /** 司机端：货单列表数量查询*/
    public static final String QUERY_PENDING_SOURCE_COUNT = "query_pending_source_order_count";

    /** 司机端：装车不成功*/
    public static final String TRUCK_LOADING_FAIL = "truck_loading_fail";

    /** 司机端：已装车*/
    public static final String TRUCK_LOADING_FINISH = "truck_loading_finish";

    /** 司机端：订单确认*/
    public static final String USER_CONFIM_CONTRACT = "user_confim_Contract";

    /** 司机端：司机端接受签约邀请 */
    public static final String ACCEPT_CONTRACT_INVITE = "accept_contract_invite";

    /** 司机端：司机电话反馈 */
    public static final String DRIVER_PHONE_FEEDBACK = "driver_phone_feedback";

    /** 司机端：司机电话反馈 */
    public static final String GET_CONTRACT_LIST = "getContract_list";

    /* 司机端：司机回复评论*/
    public static final String DRIVER_RETURN_REPLY = "driver_return_reply";

    /* 货主端; 货主回复评论*/
    public static final String USER_RETURN_REPLY = "user_return_reply";

    /* 货主端：查看装车不成功原因*/
    public static final String GET_TRUCK_LOADING_FAIL_INFO = "get_truck_loading_fail_info";

    /** 货主端：货单列表数量查询*/
    public static final String QUERY_ORDER_COUNT = "query_order_count";

    /** 货主端:获取承运方列表 */
    public static final String GET_CARRIER_LIST = "getCarrier_list";

    /** 货主端:导入合同数据 */
    public static final String CONTRACT_IMPORT_PREVIEW = "contract_import_preview";

    /** 货主端:撤销货单 */
    public static final String CANCEL_ORDER = "cancel_order";

    /** 货主端:接受抢单 */
    public static final String ACCEPT_ORDER = "accept_order";

    /** 货主端:货单列表 */
    public static final String QUERY_ORDER = "query_order";

    /** 货主端:给司机点赞 */
    public static final String USER_TO_DRIVER_PRAISE = "user_toDriver_praise";

    /** 货主端:给司机评价 */
    public static final String RATING_TO_DRIVER = "rating_to_driver";

    /** 货主端:车队添加车辆 */
    public static final String ADD_MY_FLEET = "add_my_fleet";

    /** 货主端:车队添加车辆 */
    public static final String ADD_MY_FLEET_BY_ACCOUNT = "add_my_fleet_by_account";

    /** 货主端:我的车队查询 */
    public static final String QUERY_MY_FLEET_ALL = "query_my_fleet";

    /** 货主端:我的车队查询 */
    public static final String QUERY_MY_FLEET = "query_my_fleet_new";

    /** 货主端:我的车队详情 */
    public static final String GET_MY_FLEET_DETAIL = "get_my_fleet_detail";

    /** 货主端:删除车队车辆详情 */
    public static final String DELETE_MY_FLEET = "delete_my_fleet";

    /** 货主端:编辑车队车辆详情 */
    public static final String EDIT_MY_FLEET = "edit_my_fleet";

    /** 货主端:验证身份证 */
    public static final String CHECK_CARD = "verifyIdCard";

    /** 货主端:验证记录 */
    public static final String CHECK_CARD_LIST = "verifyRecordList";

    /** 货主端:收费定位 */
    public static final String PHONE_LOCATION = "phoneLoction";

    /** 货主端:免费定位 */
    public static final String FREE_LOCATION = "freeLocation";

    /** 货主端:已发布货源 */
    public static final String QUERY_MY_PUBLIC_ORDER = "query_My_Pulish_order";

    /** 货主端:重新发布货源 */
    public static final String REPUBLISH_ORDER = "rePulish_order";

    /** 货主端:删除货源 */
    public static final String DELETE_PUBLISH_ORDER = "delete_pulish_order";

    /** 货主端:编辑已发布货源 */
    public static final String UPDATE_PUBLISH_ORDER = "update_pulish_order";

    /** 货主端:添加太平洋保险 */
    public static final String SAVE_CPIC = "save_cpic";

    /** 货主端:太平洋保险获取货物名称1 */
    public static final String GET_SAFE_SEA_CARGO_1 = "get_insurance_cargo_types";

    /** 货主端:太平洋保险获取货物名称2 */
    public static final String GET_SAFE_SEA_CARGO_2 = "get_insurance_cargo_type_second";

    /** 货主端:添加太平洋保险 */
    public static final String SAVE_PINGAN = "save_pingan";

    /** 货主端:平安保险获取省 */
    public static final String GET_SAFE_PINAN_PROVINCE = "pinganStartArea";

    /** 货主端:平安保险获取市 */
    public static final String GET_SAFE_PINAN_CITY = "pinganStartCity";

    /** 货主端:平安保险获取货物类型 */
    public static final String GET_SAFE_PINAN_PACK_TYPE = "get_pingan_pack_type";

    /** 货主端:保险记录 */
    public static final String GET_INSURANCE_LIST = "getInsuranceList";

    /** 货主端:查找车源 */
    public static final String QUERY_CAR_SOURCE = "query_driverInfo_by_user";

    /** 货主端:查询三方物流 */
    public static final String QUERY_THREE_PARTY = "three_party_logistics";

    /** 货主端:查询工厂货主 */
    public static final String QUERY_FACTORY_USER = "factory_user";

    /** 货主端:查询零担专线 */
    public static final String QUERY_SPECIAL_LINE = "LTL_maxicabs";

     /** 广告列表 */
    public static final String QUERY_ADVERT_LIST = "get_advert_list";

    public static final String COMMON_KEY = "common_key";
    public static final String COMMON_KEY_1 = "common_key_1";
    public static final String COMMON_KEY_2 = "common_key_2";
    public static final String PAY_RESULT = "PAY_RESULT";
    public static final String IS_MY_REPUTATION = "IS_MY_REPUTATION";
    public static final String IS_CAR_REPUTATION = "IS_CAR_REPUTATION";
    public static final String AGREEMENT_TYPE = "AGREEMENT_TYPE";

    public static final String COMMON_BOOLEAN_KEY = "common_boolean_key";
    public static final String COMMON_ACTION_KEY = "common_action_key";
    public static final String COMMON_OBJECT_KEY = "common_object_key";
    public static final String SEARCH_SOURCE = "search_source";
    public static final String SEARCH_SOURCE_DEFAULT = "search_source_default";
    public static final String QUERY_CAR_INFO_FROM_MAP = "QUERY_CAR_INFO_FROM_MAP";
    public static final String CAR_EDIT_TYPE = "CAR_EDIT_TYPE";
    public static final String MY_CARS_SEARCH = "MY_CARS_SEARCH";
    public static final String CLOUD_CARS_SEARCH = "CLOUD_CARS_SEARCH";
    public static final int CAR_SEARCH_TYPE = 0;
    public static final int SOURCE_SEARCH_TYPE = 1;
    public static final int SOURCE_SEARCH_TYPE_SPECIAL = 2;
    public static final int ADD_CAR = 0;
    public static final int EDIT_CAR = 1;
    // 传递订单编号
    public static final String ORDER_ID = "order_id";

    public static String MAP_TYPE = "MAP_TYPE";
    public static String MAP_TYPE_DEFAULT = "default";
    public static String MAP_TYPE_SHOP = "shop";
    public static String MAP_TYPE_HOTEL = "旅馆"; //住宿
    public static String MAP_TYPE_GAS = "加油站";//加油站
    public static String MAP_TYPE_BANK = "银行";//银行
    public static String MAP_TYPE_WAY_LINE = "里程理算";//里程计算

    public static final int ANONYMOUS_REQUEST_CODE = 1000;
    public static final int ANONYMOUS_RESULT_CODE = 1001;
    public static final int USER_DRIVER = 1; //司机
    public static final int USER_SHIPPER = 0;//货主

    public static final int SAFE_CPIC = 0;//太平洋保险
    public static final int SAFE_PINGAN = 1;//平安保险

    public static final int CAR_WAY_WHOLE = 57;//整车
    public static final int CAR_WAY_PART = 58;//零担

    public static final int REQUEST_CODE = 9000;
    public static final int RESULT_CODE = 9001;

	/**
	 * 通过位置获取车型车型
	 */
    public static int[] carTypeValues = new int[]{43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
	public static int getCarTypeValues(int position){
        if(position > carTypeValues.length) {
            return 43;
        }
		return carTypeValues[position];
	}
    /**
     * 通过位置获取货物类型
     */
    public static int[] sourceTypeValues = new int[]{34,36,37,38,39,40,41,42};
    public static int getSourceTypeValues(int position){
        if(position > sourceTypeValues.length) {
            return 34;
        }
        return sourceTypeValues[position];
    }

    /**
     * 报价,载重单位
     */
    public static int[] unitTypeValues = new int[]{83,84,85};
    public static int getUnitTypeValues(int position){
        if(position > unitTypeValues.length) {
            return 83;
        }
        return unitTypeValues[position];
    }

    /**
     * 运输类型
     */
    public static int[] shipTypeValues = new int[]{57, 58};
    public static int getShipTypeValues(int position){
        if(position > unitTypeValues.length) {
            return 57;
        }
        return shipTypeValues[position];
    }

    /**
     * 太平洋保险险种
     */
    public static int[] seaSafeTypeValues = new int[]{1,2,3,4,5,6};
    public static int getSeaSafeTypeValues(int position){
        if(position > seaSafeTypeValues.length) {
            return 1;
        }
        return seaSafeTypeValues[position];
    }

    /**
     * 太平洋保险包装代码
     */
    public static String[] seaSafeBZDMTypeValues = new String[]{"62","63","64","65","66","67","68","69"};
    public static String getSeaSafeBZDMTypeValues(int position){
        if(position > seaSafeBZDMTypeValues.length) {
            return "62";
        }
        return seaSafeBZDMTypeValues[position];
    }

    /**
     * 太平洋保险货物类型1
     */
    public static int[] seaSafeSourceTypeValues = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14};
    public static int getSeaSafeSourceTypeValues(int position){
        if(position > seaSafeSourceTypeValues.length) {
            return 1;
        }
        return seaSafeSourceTypeValues[position];
    }

    /**
     * 太平洋保险货物类型2
     */
    public static Integer[] seaSafeSourceType2Values = new Integer[]{};
    public static int getSeaSafeSourceType2Values(int position){
        if(position > seaSafeSourceType2Values.length) {
            return 1;
        }
        return seaSafeSourceType2Values[position];
    }

    /**
     * 平安保险货物类型
     */
    public static String[] pinanSourceType2Values = new String[]{};
    public static String getPinanSafeSourceType2Values(int position){
        if(position > pinanSourceType2Values.length) {
            return "";
        }
        return pinanSourceType2Values[position];
    }

    /**
     * 平安保险省(起运地)
     */
    public static String[] pinanStartProvinceType2Values = new String[]{};
    public static String getPinanStartProvinceType2Values(int position){
        if(position > pinanStartProvinceType2Values.length) {
            return "";
        }
        return pinanStartProvinceType2Values[position];
    }

    /**
     * 平安保险省(目的地)
     */
    public static String[] pinanEndProvinceType2Values = new String[]{};
    public static String getPinanEndProvinceType2Values(int position){
        if(position > pinanEndProvinceType2Values.length) {
            return "";
        }
        return pinanEndProvinceType2Values[position];
    }

    /**
     * 平安保险市(起运地)
     */
    public static String[] pinanStartCityType2Values = new String[]{};
    public static String getPinanStartCityType2Values(int position){
        if(position > pinanStartCityType2Values.length) {
            return "";
        }
        return pinanStartCityType2Values[position];
    }

    /**
     * 平安保险市(目的地)
     */
    public static String[] pinanEndCityType2Values = new String[]{};
    public static String getPinanEndCityType2Values(int position){
        if(position > pinanEndCityType2Values.length) {
            return "";
        }
        return pinanEndCityType2Values[position];
    }
}
