package com.bibibiradio.commoncache;

/*
 * 实现该接口，可注册元素删除时用户自定义操作
 */
public interface CacheDispose {
	
	/*
	 * @key:键值
	 * @rawData:数据
	 * @timestamp:数据插入时间
	 * @timeLimit:Cache设置的超时时间
	 * @type:类型 1:超时导致dispose,2:用户显示删除导致dispose,3:用户插入覆盖该item导致dispose
	 */
	public void dispose(Object key,Object rawData,long timestamp,long timeLimit,int type);
}
