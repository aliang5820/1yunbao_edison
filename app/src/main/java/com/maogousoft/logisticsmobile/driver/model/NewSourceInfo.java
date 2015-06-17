package com.maogousoft.logisticsmobile.driver.model;

import java.io.Serializable;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/****
 * 新货源详情实体类
 * 
 * @author lenovo
 */
public class NewSourceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8191048774573241387L;

	// 订单号
	private Integer id = -1;

	// 出发地省份
	private Integer start_province = -1;

	// 出发地城市
	private Integer start_city = -1;

	// 出发地区县
	private Integer start_district = -1;

	// 目的地省份
	private Integer end_province = -1;

	// 目的地城市
	private Integer end_city = -1;

	// 目的地区县
	private Integer end_district = -1;

	// 出发省份
	private String start_province_str = "";

	// 出发城市
	private String start_city_str = "";

	// 出发区县
	private String start_district_str = "";

	// 目的地省份
	private String end_province_str = "";

	// 目的地城市
	private String end_city_str = "";

	// 目的地区县
	private String end_district_str = "";

	// 货物名称
	private String cargo_desc = "";

	// 货物类型
	private Integer cargo_type = -1;

	// 货物类型名称
	private String cargo_type_str = "";

	// 货物数量
	private Integer cargo_number = 0;

	// 货物数量单位,1车2吨3方
	private Integer cargo_unit = 0;

	// 货物数量单位字符串
	private String cargo_unit_name = "";

	// 货物单价
	private Double unit_price = 0.0;

	// 货物价格
	private String price = "";

	// 运输方式
	private Integer ship_type = -1;

	private String ship_type_str = "";

	// 货车类型
	private Integer car_type = -1;

	private String car_type_str = "";

	// 货物照片1
	private String cargo_photo1 = "";

	// 货物照片2
	private String cargo_photo2 = "";

	// 货物照片3
	private String cargo_photo3 = "";

	// 装车时间
	private long loading_time = 0l;

	// 补充说明
	private String cargo_remark = "";

	// 有效截止日期
	private long validate_time = 0l;

	// 货主id
	private Integer user_id = 0;
	// 押金
	private Integer user_bond = 0;

	// 综合评分
	private Float score = 0f;

	// 货源发布时间
	private long create_time = 0;

	// 0未读1已读
	private Integer read_status = 0;

	// 0未关注,1已关注
	private Integer favorite_status = 0;

	// 履约诚信度
	private Integer score1 = 0;

	// 信息真实度
	private Integer score2 = 0;

	// 服务态度
	private Integer score3 = 0;

	// // 货主手机号码
	private String user_phone = "";

	// // 货主姓名
	private String user_name = "";

	// 要求车长
	private Double car_length = 0d;

	private String cargo_user_name = "";// 发货时，填写的 联系人

	private String cargo_user_phone = "";// 发货时，填写的 联系电话

    private String cargo_tip = ""; //常用短语
    //货单新增:
    private int push_drvier_count; //已推送司机数量
    private int vie_driver_count; //已抢单司机数量
    private int order_vie_count; //报价人数
    private int order_place_count; //电话反馈人数
	private String contract_status;//如果合同状态为2 显示红点，取签约失败原因
	private String signing_failed_reason;//如果已经签约且失败，查看签约失败信息
    private String is_able_confim_contract;//是否可以订单确认
    private String is_has_invite;//是否有邀约
    private String is_truck_loading_success;//是否装车不成功 “Y“装车不成功“N”装车成功
    private List<CarrierInfo> carrierInfoList;//抢单报价列表
	private int contract_type;//司机用(合同类型)

	public int getContract_type() {
		return contract_type;
	}

	public void setContract_type(int contract_type) {
		this.contract_type = contract_type;
	}

	public List<CarrierInfo> getCarrierInfoList() {
        return carrierInfoList;
    }

    public void setCarrierInfoList(List<CarrierInfo> carrierInfoList) {
        this.carrierInfoList = carrierInfoList;
    }

    public String getIs_truck_loading_success() {
        return is_truck_loading_success;
    }

    public void setIs_truck_loading_success(String is_truck_loading_success) {
        this.is_truck_loading_success = is_truck_loading_success;
    }

    public String getContract_status() {
		return contract_status;
	}

	public void setContract_status(String contract_status) {
		this.contract_status = contract_status;
	}

	public String getSigning_failed_reason() {
		return signing_failed_reason;
	}

	public void setSigning_failed_reason(String signing_failed_reason) {
		this.signing_failed_reason = signing_failed_reason;
	}

	public String getIs_has_invite() {
        return is_has_invite;
    }

    public void setIs_has_invite(String is_has_invite) {
        this.is_has_invite = is_has_invite;
    }

    public String getIs_able_confim_contract() {
        return is_able_confim_contract;
    }

    public void setIs_able_confim_contract(String is_able_confim_contract) {
        this.is_able_confim_contract = is_able_confim_contract;
    }

	public Double getCar_length() {
		return car_length;
	}

	public void setCar_length(Double car_length) {
		this.car_length = car_length;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStart_province() {
		return start_province;
	}

	public void setStart_province(Integer start_province) {
		this.start_province = start_province;
	}

	public Integer getStart_city() {
		return start_city;
	}

	public void setStart_city(Integer start_city) {
		this.start_city = start_city;
	}

	public Integer getStart_district() {
		return start_district;
	}

	public void setStart_district(Integer start_district) {
		this.start_district = start_district;
	}

	public Integer getEnd_province() {
		return end_province;
	}

	public void setEnd_province(Integer end_province) {
		this.end_province = end_province;
	}

	public Integer getEnd_city() {
		return end_city;
	}

	public void setEnd_city(Integer end_city) {
		this.end_city = end_city;
	}

	public Integer getEnd_district() {
		return end_district;
	}

	public void setEnd_district(Integer end_district) {
		this.end_district = end_district;
	}

	public String getStart_province_str() {
		return start_province_str;
	}

	public void setStart_province_str(String start_province_str) {
		this.start_province_str = start_province_str;
	}

	public String getStart_city_str() {
		return start_city_str;
	}

	public void setStart_city_str(String start_city_str) {
		this.start_city_str = start_city_str;
	}

	public String getStart_district_str() {
		return start_district_str;
	}

	public void setStart_district_str(String start_district_str) {
		this.start_district_str = start_district_str;
	}

	public String getEnd_province_str() {
		return end_province_str;
	}

	public void setEnd_province_str(String end_province_str) {
		this.end_province_str = end_province_str;
	}

	public String getEnd_city_str() {
		return end_city_str;
	}

	public void setEnd_city_str(String end_city_str) {
		this.end_city_str = end_city_str;
	}

	public String getEnd_district_str() {
		return end_district_str;
	}

	public void setEnd_district_str(String end_district_str) {
		this.end_district_str = end_district_str;
	}

	public String getCargo_desc() {
		return cargo_desc;
	}

	public void setCargo_desc(String cargo_desc) {
		this.cargo_desc = cargo_desc;
	}

	public Integer getCargo_type() {
		return cargo_type;
	}

	public void setCargo_type(Integer cargo_type) {
		this.cargo_type = cargo_type;
	}

	public String getCargo_type_str() {
		return cargo_type_str;
	}

	public void setCargo_type_str(String cargo_type_str) {
		this.cargo_type_str = cargo_type_str;
	}

	public Integer getCargo_number() {
		return cargo_number;
	}

	public void setCargo_number(Integer cargo_number) {
		this.cargo_number = cargo_number;
	}

	public Integer getCargo_unit() {
		return cargo_unit;
	}

	public void setCargo_unit(Integer cargo_unit) {
		this.cargo_unit = cargo_unit;
	}

	public String getCargo_unit_name() {
		return cargo_unit_name;
	}

	public void setCargo_unit_name(String cargo_unit_name) {
		this.cargo_unit_name = cargo_unit_name;
	}

	public Double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(Double unit_price) {
		this.unit_price = unit_price;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Integer getShip_type() {
		return ship_type;
	}

	public void setShip_type(Integer ship_type) {
		this.ship_type = ship_type;
	}

	public String getShip_type_str() {
		return ship_type_str;
	}

	public void setShip_type_str(String ship_type_str) {
		this.ship_type_str = ship_type_str;
	}

	public Integer getCar_type() {
		return car_type;
	}

	public void setCar_type(Integer car_type) {
		this.car_type = car_type;
	}

	public String getCar_type_str() {
		return car_type_str;
	}

	public void setCar_type_str(String car_type_str) {
		this.car_type_str = car_type_str;
	}

	public String getCargo_photo1() {
		return cargo_photo1;
	}

	public void setCargo_photo1(String cargo_photo1) {
		this.cargo_photo1 = cargo_photo1;
	}

	public String getCargo_photo2() {
		return cargo_photo2;
	}

	public void setCargo_photo2(String cargo_photo2) {
		this.cargo_photo2 = cargo_photo2;
	}

	public String getCargo_photo3() {
		return cargo_photo3;
	}

	public void setCargo_photo3(String cargo_photo3) {
		this.cargo_photo3 = cargo_photo3;
	}

	public long getLoading_time() {
		return loading_time;
	}

	public void setLoading_time(long loading_time) {
		this.loading_time = loading_time;
	}

	public String getCargo_remark() {
		return cargo_remark;
	}

	public void setCargo_remark(String cargo_remark) {
		this.cargo_remark = cargo_remark;
	}

	public long getValidate_time() {
		return validate_time;
	}

	public void setValidate_time(long validate_time) {
		this.validate_time = validate_time;
	}

	public Integer getUser_bond() {
		return user_bond;
	}

	public void setUser_bond(Integer user_bond) {
		this.user_bond = user_bond;
	}

	public Integer getPush_drvier_count() {
		return push_drvier_count;
	}

	public void setPush_drvier_count(Integer push_drvier_count) {
		this.push_drvier_count = push_drvier_count;
	}

	public Integer getVie_driver_count() {
		return vie_driver_count;
	}

	public void setVie_driver_count(Integer vie_driver_count) {
		this.vie_driver_count = vie_driver_count;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public Integer getRead_status() {
		return read_status;
	}

	public void setRead_status(Integer read_status) {
		this.read_status = read_status;
	}

	public Integer getFavorite_status() {
		return favorite_status;
	}

	public void setFavorite_status(Integer favorite_status) {
		this.favorite_status = favorite_status;
	}

	public Integer getScore1() {
		return score1;
	}

	public void setScore1(Integer score1) {
		this.score1 = score1;
	}

	public Integer getScore2() {
		return score2;
	}

	public void setScore2(Integer score2) {
		this.score2 = score2;
	}

	public Integer getScore3() {
		return score3;
	}

	public void setScore3(Integer score3) {
		this.score3 = score3;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getCargo_user_name() {
		return cargo_user_name;
	}

	public void setCargo_user_name(String cargo_user_name) {
		this.cargo_user_name = cargo_user_name;
	}

	public String getCargo_user_phone() {
		return cargo_user_phone;
	}

	public void setCargo_user_phone(String cargo_user_phone) {
		this.cargo_user_phone = cargo_user_phone;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

    public String getCargo_tip() {
        return cargo_tip;
    }

    public void setCargo_tip(String cargo_tip) {
        this.cargo_tip = cargo_tip;
    }

    public void setPush_drvier_count(int push_drvier_count) {
        this.push_drvier_count = push_drvier_count;
    }

    public void setVie_driver_count(int vie_driver_count) {
        this.vie_driver_count = vie_driver_count;
    }

    public int getOrder_vie_count() {
        return order_vie_count;
    }

    public void setOrder_vie_count(int order_vie_count) {
        this.order_vie_count = order_vie_count;
    }

    public int getOrder_place_count() {
        return order_place_count;
    }

    public void setOrder_place_count(int order_place_count) {
        this.order_place_count = order_place_count;
    }
}