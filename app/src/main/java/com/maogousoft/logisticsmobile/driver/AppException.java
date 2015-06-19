package com.maogousoft.logisticsmobile.driver;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.http.HttpException;

import android.content.Context;
import android.widget.Toast;

/**
 * 自定义程序异常
 * 
 * @author maogousoft
 */
public class AppException extends Exception implements UncaughtExceptionHandler {

	private static final long serialVersionUID = 1L;

	/** 定义异常类型 */
	public final static byte TYPE_NETWORK = 0x01;

	public final static byte TYPE_SOCKET = 0x02;

	public final static byte TYPE_HTTP_CODE = 0x03;

	public final static byte TYPE_HTTP_ERROR = 0x04;

	public final static byte TYPE_XML = 0x05;

	public final static byte TYPE_IO = 0x06;

	public final static byte TYPE_RUN = 0x07;

	private byte type;

	private int code;

	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private AppException() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	private AppException(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;
	}

	public static AppException http(int code) {
		return new AppException(TYPE_HTTP_CODE, code, null);
	}

	public static AppException http(Exception excp) {
		return new AppException(TYPE_HTTP_ERROR, 0, excp);
	}

	public static AppException run(Exception e) {
		return new AppException(TYPE_RUN, 0, e);
	}

	public static AppException io(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(TYPE_NETWORK, 0, e);
		} else if (e instanceof IOException) {
			return new AppException(TYPE_IO, 0, e);
		}
		return run(e);
	}

	public static AppException socket(Exception e) {
		return new AppException(TYPE_SOCKET, 0, e);
	}

	public static AppException network(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(TYPE_NETWORK, 0, e);
		} else if (e instanceof HttpException) {
			return http(e);
		} else if (e instanceof SocketException) {
			return socket(e);
		}
		return http(e);
	}

	public int getCode() {
		return this.code;
	}

	public int getType() {
		return this.type;
	}

	/** 提示异常信息 **/
	public String getErrorMessage() {
		String err = "服务器异常,请联系客服!";
		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			err = "网络异常，错误码：" +  this.getCode();
			break;
		case TYPE_HTTP_ERROR:
			err = "网络异常，请求超时，请检查网络设置";
			break;
		case TYPE_SOCKET:
			err = "网络异常，读取数据超时";
			break;
		case TYPE_NETWORK:
			err = "网络连接失败，请检查网络设置";
			break;
		case TYPE_XML:
			err = "数据解析异常";
			break;
		case TYPE_IO:
			err = "文件流异常";
			break;
		case TYPE_RUN:
			err = "应用程序运行时异常";
			break;
		}
		return err;
	}

	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

}
