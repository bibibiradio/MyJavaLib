package com.bibibiradio.commoncache;

/*
 * 实现该接口，可注册元素删除时用户自定义操作
 */
public interface CacheDispose {
	public void dispose(Object key,Object rawData,long timestamp,long timeLimit);
}
