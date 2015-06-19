package com.bibibiradio.commoncache;

public class CacheTestDisposer implements CacheDispose {

	@Override
	public void dispose(Object key, Object rawData, long timestamp,
			long timeLimit) {
		// TODO Auto-generated method stub
		System.out.println("[dispose] "+timestamp+" "+(String)rawData);
	}

}
