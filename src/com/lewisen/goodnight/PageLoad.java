package com.lewisen.goodnight;

import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

public class PageLoad {
	public static final int GET_RESOURCE_ERR = -1;
	int lastPage = 0;

	/**
	 * 页面更新控制
	 * 
	 * @param currentMaxId
	 *            当前最大ID
	 * @param page
	 *            当前显示的页面
	 * @param netState
	 *            网络状态标志
	 * @param mHandler
	 *            消息处理
	 * @param stopThread
	 *            线程停止标志
	 * @param operateDB
	 *            数据库操作对象
	 * @param requestName
	 *            Servlet请求对象名称
	 * @param loadedPage
	 *            已经加载到的页面
	 */
	public void pageUpdateControl(int currentMaxId, int page, boolean netState,
			Handler mHandler, boolean stopThread, OperateDB operateDB,
			String requestName, int loadedPage) {
		int pageID = currentMaxId + 1 - page;
		// 显示内容的页面编号是从1到10
		if ((page != 0) && (page < 11)) {
			// Log.d("DEBUG", "PageID" + pageID + " lastPage:" + lastPage
			// + " page:" + page + " loadedPage:" + loadedPage
			// + " currentMaxId" + currentMaxId);

			// 满足以下条件时联网更新：当前页面没有加载，，页面是在往左翻页，网络可用，
			if ((page > loadedPage)
					&& ((pageID == (lastPage - 1)) || (pageID == currentMaxId))
					&& netState) {
				// Log.d("DEBUG", "加载网络");
				this.loadFromNet(mHandler, page, requestName, pageID,
						stopThread, operateDB);
			} else {// 否则直接加载数据库内容 if ((pageID >= lastPage) || (!netState))
			// Log.d("DEBUG", "加载数据库");
				this.loadFromDB(mHandler, page, pageID, stopThread, operateDB);
			}
			lastPage = pageID;// 保存本次页面id
		}
	}

	/**
	 * 从网络加载数据,并且保存数据到数据库
	 * 
	 * @param mHandler
	 *            消息发送handler
	 * @param page
	 *            显示页面的id 从1--10 对应十个界面
	 * @param requestName
	 *            请求的servlet的名称
	 * @param requestID
	 *            请求servlet的对应的内容的编号
	 * @param stopThread
	 *            停止发送handler标识
	 * @param saveToDbImpl
	 *            若不为空，则调用父类中的saveToDb方法
	 */
	public void loadFromNet(final Handler mHandler, final int page,
			final String requestName, final int requestID,
			final boolean stopThread, final OperateDB operateDB) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				// System.out.println("开始加载网络资源");
				JSONObject jsonObject = GetJsonFromNet.getJsonObj(requestName,
						requestID, 1);

				// 如果当前的线程加载的界面不再显示，则不能再发送message，否则造成handler处理message时空指针异常
				// 目前解决方案是:设置了一个当前界面的标志位，stopThread，每次发送message前判断，并且每次处理handler前判断。
				// 尚未想到其他好的方法，或许可以采用整个程序共用一个handler
				if (!stopThread) {
					if ((jsonObject != null) && (operateDB != null)) {
						Object obj = operateDB.saveToDb(jsonObject, page);
						mHandler.obtainMessage(page, obj).sendToTarget();
					} else {
						mHandler.obtainMessage(GET_RESOURCE_ERR).sendToTarget();
					}
				}
			}
		}).start();
	}

	/**
	 * 加载本地数据库内容
	 * 
	 * @param mHandler
	 *            发送消息的对象
	 * @param page
	 *            显示的页面 1--10
	 * @param pageID
	 *            请求数据库的页面id
	 * @param stopThread
	 * @param operateDB
	 */
	private void loadFromDB(final Handler mHandler, final int page,
			final int pageID, final boolean stopThread,
			final OperateDB operateDB) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!stopThread) {
					Object obj = operateDB.getFromDb(pageID);
					mHandler.obtainMessage(page, obj).sendToTarget();
				}
			}
		}).start();
	}

}
