package org.pccpa.api;

import greendroid.util.GDUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.pccpa.DB;
import org.pccpa.R;
import org.pccpa.R.string;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StrictMode;


import com.google.gson.Gson;
import com.kull.android.*;;

public class Client {


	private final static Gson GSON=new Gson();
	final static int BUFFER_SIZE = 4096;  
	
	public static String URL_APK="http://ext.pccpa.cn:90/android.html";
	
	static{
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build()); 
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		
	}
	

	
    private static String path_base="http://oanet.pccpa.cn",
    path_remind_pattern=path_base+"/SYS/D_Menu/reminds/{0}"
    ,path_grid_pattern=path_base+"/{0}/{1}/grid",
    url_home_dologin=path_base+"/SYS/Home/doLogin"
    ,url_em_photo_pattern=path_base+"/FS/D_Employee/photo/{0}"
    ;

    private String eid;
    public static Client CURR_CLIENT;
    
    private Contact contact;
    
    
    
    
    public Contact getContact() {
		return contact;
	}

	private Client(String eid){
    
    	this.eid=eid;
        
    }
    
    public static String urlEmployeePhoto(String eid){
    	return MessageFormat.format(url_em_photo_pattern, eid);
    }
    
    
    public List<RemindItem> getReminds() throws Exception{
    	
    	String url=MessageFormat.format(path_remind_pattern,eid);
    	String context=NetworkHelper.doGet(url, null,null);
    	
    	RemindsAdapter remindsAdapter= GSON.fromJson(context, RemindsAdapter.class);
    	List<RemindItem> reminds=new ArrayList<RemindItem>();
    	reminds.addAll(remindsAdapter.applyList);
    	reminds.addAll(remindsAdapter.remindList);
    	return reminds;
    }
    
    public EMGrid getEms(int start,int limit) throws Exception{
    	String url=MessageFormat.format(path_grid_pattern,"FS","V_Employee");
    	url+=MessageFormat.format("?start={0}&limit={1}",start+"",limit+"");
    	String context=NetworkHelper.doGet(url, null,null);
    	EMGrid grid=GSON.fromJson(context, EMGrid.class);
        return grid;
    }
    
    private static <G extends Grid> G getGrid(Class<G> cls, String namespace,String controler,String query,int start,int limit) throws Exception{
    	String url=MessageFormat.format(path_grid_pattern,namespace,controler);
    	url+=MessageFormat.format("?"+query+"&start={0}&limit={1}",start+"",limit+"");
    	String context=NetworkHelper.doGet(url, null,null);
    	G grid=GSON.fromJson(context, cls);
        return grid;
    }
    
    public static ContactGrid  getContacts(String query,int start,int limit) throws Exception{
    	return getGrid(ContactGrid.class, "FS", "V_Contacts",query,start, limit);
    }
    
    public static Drawable getContactPhoto(Context context,String eid,boolean overwrite) throws Exception{
    	
    	String path=Environment.getExternalStorageDirectory().toString();//+"/pccpa";
    	path+="/contact_"+eid+".photo";
    	File filePhoto=new File(path);
    	if(!filePhoto.exists()||overwrite){
    		URL url=ResourceUtils.getURL(urlEmployeePhoto(eid));
    		InputStream is=NetworkHelper.doGetStream(url.toString(), null, null);
    		filePhoto.createNewFile();
    		FileCopyUtils.copy(is, new FileOutputStream(filePhoto));
    		is.close();
    	}
    	//InputStream is=new FileInputStream(filePhoto);
    	Drawable drawable= BitmapDrawable.createFromPath(path);
    	//is.close();
    	return drawable;
    }
    
    public static Result doLogin(String ELoginID,String EPassword,Context context) throws Exception{
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("ELoginID", ELoginID);
    	param.put("EPassword", EPassword);
    	//HttpParams param=new BasicHttpParams();
    	//param.setParameter("ELoginID", ELoginID);
    	//param.setParameter("EPassword", EPassword);
    	String url=MessageFormat.format(url_home_dologin, ELoginID,EPassword);
    	
    	String content=NetworkHelper.doPost(url, param, null, null);
    	Result result=GSON.fromJson(content, Result.class);
    	if(result.code==0){
    		CURR_CLIENT=new Client(result.action);
    		SQLiteOrmHelper sqLiteOrmHelper=DB.local.createSqLiteOrmHelper(context);
    		try{
    		  CURR_CLIENT.contact=sqLiteOrmHelper.load(Contact.class, result.action);
    		}catch(Exception ex){
    		  CURR_CLIENT.contact=getContacts("q_eq_EID="+result.action, 0, 1).getRows().get(0);
    		}
    	}
    	return result;
    }
    
    public class Result {
    	private String msg,action,icon;
    	private int code;
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
    	
    }
    
    public class ContactGrid extends Grid<Contact>{}
    public class EMGrid extends Grid<EmployeeItem>{}
    
    private class Grid<T>{
    	
    	private int total;
    	private List<T> rows;
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public List<T> getRows() {
			return rows;
		}
		public void setRows(List<T> rows) {
			this.rows = rows;
		}
    	
    	
    }
    
    
    private class RemindsAdapter{
    	private List<RemindItem> remindList,applyList;

		public List<RemindItem> getRemindList() {
			return remindList;
		}

		public void setRemindList(List<RemindItem> remindList) {
			this.remindList = remindList;
		}

		public List<RemindItem> getApplyList() {
			return applyList;
		}

		public void setApplyList(List<RemindItem> applyList) {
			this.applyList = applyList;
		}
    	
    	
    }
}
