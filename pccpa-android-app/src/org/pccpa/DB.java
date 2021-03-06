package org.pccpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.pccpa.api.Client;
import org.pccpa.api.Contact;
import org.pccpa.api.InnerMsg;
import org.pccpa.api.LoginLog;
import org.pccpa.api.SiteSynRunnable;

import android.content.Context;

import com.kull.android.SQLiteOrmHelper;

public enum DB {

	
	backup,local; 
	
	public SQLiteOrmHelper createSqLiteOrmHelper(Context context){
		return new SQLiteOrmHelper(context,this.name());
	}
	
	
	public boolean initDB(Context context) {
		SQLiteOrmHelper sqLiteOrmHelper=createSqLiteOrmHelper(context);
		try{
			sqLiteOrmHelper.count(Contact.class);
			sqLiteOrmHelper.count(LoginLog.class);
			sqLiteOrmHelper.count(InnerMsg.class);
		}catch(Exception ex){
			sqLiteOrmHelper.replaceTable(Contact.class,LoginLog.class,InnerMsg.class);
			//SiteSynRunnable siteSynRunnable=new SiteSynRunnable(context);
			//new Thread(siteSynRunnable).start();
			return true;
		}
		return false;
		
		
	}
	
	
	public List<Contact> selectAreaContacts(Context context,String areaid) throws Exception{
		SQLiteOrmHelper sqLiteOrmHelper=createSqLiteOrmHelper(context);
		 //sqLiteOrmHelper.createTable(Contact.class);
		List<Contact> contacts=new ArrayList<Contact>();
		
			contacts=sqLiteOrmHelper.select(Contact.class,
					   new String[]{"*"}
			           ,"areaid = ?"
			           ,new String[]{areaid}
			         ,""
	                 ,""
	                 ,"dorderno asc,eorderno asc"
					   );
		
		return contacts;
	}
	
	public List<Contact> selectAreaRankContacts(Context context,String areaid,String rankid) throws Exception{
		SQLiteOrmHelper sqLiteOrmHelper=createSqLiteOrmHelper(context);
		 //sqLiteOrmHelper.createTable(Contact.class);
		List<Contact> contacts=new ArrayList<Contact>();
		
			contacts=sqLiteOrmHelper.select(Contact.class,
					   new String[]{"*"}
			           ,"areaid = ? and rankid=?"
			           ,new String[]{areaid,rankid}
			         ,""
	                 ,""
	                 ,"dorderno asc,eorderno asc"
					   );
		
		return contacts;
	}
	
	public List<Contact> selectDeptContacts(Context context,String deptid) throws Exception{
		SQLiteOrmHelper sqLiteOrmHelper=createSqLiteOrmHelper(context);
		 //sqLiteOrmHelper.createTable(Contact.class);
		List<Contact> contacts=new ArrayList<Contact>();
		   
			contacts=sqLiteOrmHelper.select(Contact.class,
					   new String[]{"*"}
			           ,"departid = ?"
			           ,new String[]{deptid}
			           ,""
			           ,""
			           ,"eorderno asc"
					   );
		
		return contacts;
	}
	
	public SortedMap<String,Area> selectArea(Context context) throws Exception{
		SQLiteOrmHelper sqLiteOrmHelper=createSqLiteOrmHelper(context);
		SortedMap<String,Area> areas=new TreeMap<String,DB.Area>();
		List<Contact> depts=sqLiteOrmHelper.select(Contact.class, 
				new String[]{"AreaId","AreaName","DepartId","DepartName"}
		        ,""
		        ,new String[]{}
		        ,"departid"
		        ,""
		        ,"aorderno asc,dorderno asc"
				);
		for(Contact dept :depts){
			if(!areas.containsKey(dept.getAreaID())){
				Area area=new Area();
				area.areaId=dept.getAreaID();
				area.areaName=dept.getAreaName();
				areas.put(area.areaId, area);
			}
			Area area=areas.get(dept.getAreaID());
			Dept d=new Dept();
			d.deptId=dept.getDepartID();
			d.deptName=dept.getDepartName();
			d.areaName=dept.getAreaName();
			area.depts.put(d.deptId, d);
		}
		return areas;
	}
	
	public class Area{
		private Area(){}
		private String areaId,areaName;
		private SortedMap<String,Dept> depts=new TreeMap<String, DB.Dept>();
		public String getAreaId() {
			return areaId;
		}
		public String getAreaName() {
			return areaName;
		}
		public SortedMap<String, Dept> getDepts() {
			return depts;
		}
		
		
	}
	
	public class Dept{
		private Dept(){}
		private String deptId,deptName,areaName;

		public String getDeptId() {
			return deptId;
		}

		public String getDeptName() {
			return deptName;
		}

		public String getAreaName() {
			return areaName;
		}
		
		
	}
}
