package com.kull.android;



import java.util.Stack;
import java.util.concurrent.ExecutorService;

import com.kull.android.app.KULLApplication;
import com.kull.android.image.ImageCache;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;



public class AppManager {
	private static Stack<Activity> activityStack;
	private static AppManager instance;
	
	private AppManager(){}
	/**
	 * ��һʵ��
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			instance=new AppManager();
		}
		return instance;
	}
	/**
	 * ���Activity����ջ
	 */
	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	/**
	 * ��ȡ��ǰActivity����ջ�����һ��ѹ��ģ�
	 */
	public Activity currentActivity(){
		Activity activity=activityStack.lastElement();
		return activity;
	}
	/**
	 * ������ǰActivity����ջ�����һ��ѹ��ģ�
	 */
	public void finishActivity(){
		Activity activity=activityStack.lastElement();
		finishActivity(activity);
	}
	/**
	 * ����ָ����Activity
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * ����ָ��������Activity
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	/**
	 * ��������Activity
	 */
	public void finishAllActivity(){
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	activityStack.get(i).finish();
            }
	    }
		activityStack.clear();
	}
	/**
	 * �˳�Ӧ�ó���
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			//activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}
	}
	
	
	


	    /**
	     * Return the current {@link GDApplication}
	     * 
	     * @param context The calling context
	     * @return The {@link GDApplication} the given context is linked to.
	     */
	    public static KULLApplication getApplication(Context context) {
	        return (KULLApplication) context.getApplicationContext();
	    }

	    /**
	     * Return the {@link GDApplication} image cache
	     * 
	     * @param context The calling context
	     * @return The image cache of the current {@link GDApplication}
	     */
	    public static ImageCache getImageCache(Context context) {
	        return getApplication(context).getImageCache();
	    }

	    /**
	     * Return the {@link GDApplication} executors pool.
	     * 
	     * @param context The calling context
	     * @return The executors pool of the current {@link GDApplication}
	     */
	    public static ExecutorService getExecutor(Context context) {
	        return getApplication(context).getExecutor();
	    }
}
