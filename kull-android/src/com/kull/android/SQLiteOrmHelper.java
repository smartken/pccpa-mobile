package com.kull.android;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.kull.LinqHelper;
import com.kull.ObjectHelper;
import com.kull.StringHelper;
import com.kull.bean.JdbcBean.Database;
import com.kull.jdbc.MssqlDialect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteOrmHelper extends SQLiteOpenHelper {

	public final static Map<Integer, Class> JDBCTYPE_REF_CLASS=new HashMap<Integer, Class>();
	public final static Map<Class,String> CLASS_REF_JDBCTYPE=new HashMap<Class, String>();
	
	private static Map<String, String> SQL_CACHE=new HashMap<String, String>();
	
	static{
		JDBCTYPE_REF_CLASS.put(Types.VARCHAR, String.class);
        JDBCTYPE_REF_CLASS.put(Types.INTEGER, Integer.class);
		
		CLASS_REF_JDBCTYPE.put(String.class, "text");
		CLASS_REF_JDBCTYPE.put(Integer.class, "int");
		CLASS_REF_JDBCTYPE.put(Long.class, "long");
		CLASS_REF_JDBCTYPE.put(Double.class, "double");
		CLASS_REF_JDBCTYPE.put(Short.class, "short");
		CLASS_REF_JDBCTYPE.put(Float.class, "float");
		CLASS_REF_JDBCTYPE.put(byte[].class, "blob");
	}
	
	
	
	public SQLiteOrmHelper(Context context, String name) {
		super(context, name, null, 3);
		// TODO Auto-generated constructor stub
		
	}
	
	public SQLiteOrmHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stu
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	
	
	public int createTable(Class... clss) {
		String createSql="";
		SQLiteDatabase database=getWritableDatabase();
		int eff=0;
		for(Class cls :clss){
			OrmTable ormTable=(OrmTable)cls.getAnnotation(OrmTable.class);
			createSql=MessageFormat.format("create table {0} ( ", ormTable.name());
			Field[] fields=cls.getDeclaredFields();
			for(Field field : fields){
				String fname=field.getName();
				Class fcls=field.getType();
				if(!CLASS_REF_JDBCTYPE.containsKey(fcls))continue;
				String type=CLASS_REF_JDBCTYPE.get(fcls);
				createSql+=MessageFormat.format("{0} {1} ,", fname,type);
			}
			createSql=createSql.substring(0, createSql.length()-1);
			createSql+=")";
			
			try{
			   database.execSQL(createSql);
			}catch(Exception ex){continue;}
		    eff++;
		}
		database.close();
		//SQLiteDatabase.releaseMemory();
		return eff;
	}
	
	public Set<String> listTable() throws Exception {
		Set<String> tables=new HashSet<String>();
		List<SqliteMaster> sqliteMasters=select(SqliteMaster.class,new String[]{"*"}, "type=?", new String[]{"table"});
		for(SqliteMaster sqliteMaster :sqliteMasters){
			tables.add(sqliteMaster.getName());
		}
		return tables;
	}
	
	public boolean existTable(Class... clss)  {
		Set<String> tables= new HashSet<String>();
		try {
			tables = listTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                       
		for(Class cls :clss){
			OrmTable ormTable=(OrmTable)cls.getAnnotation(OrmTable.class);
			if(!tables.contains(ormTable.name()))return false;
		}
		
		return true;
	}
	
	public int dropTable(Class... clss) {
		String createSql="";
		SQLiteDatabase database=getWritableDatabase();
		int eff=0;
		for(Class cls :clss){
		OrmTable ormTable=(OrmTable)cls.getAnnotation(OrmTable.class);
		createSql =MessageFormat.format("drop table {0}  ", ormTable.name());
		try{
		database.execSQL(createSql);
		eff++;
		}catch(Exception ex){}
	    
		}
		database.close();
		//SQLiteDatabase.releaseMemory();
		return eff;
	}
	
	public int replaceTable(Class...clss){
		int eff=0;
		eff+=dropTable(clss);
		eff+= createTable(clss);
		return eff;
	}
	
	public int truncateTable(Class... clss) throws SQLException{
		String createSql="";
		int eff=0;
		SQLiteDatabase database=getWritableDatabase();
		for(Class cls :clss){
		OrmTable ormTable=(OrmTable)cls.getAnnotation(OrmTable.class);
		createSql =MessageFormat.format("delete from {0}; vacuum;  ", ormTable.name());
		try{
		database.execSQL(createSql);
		}catch(Exception ex){}
	    eff++;
		}
		database.close();
		//SQLiteDatabase.releaseMemory();
		return eff;
	}
	
	
	public <T> T load(Class<T> cls,String pk) throws Exception{
		 T t=cls.newInstance();
		 return load(t, pk);
	}
	
	public <T> T load(T t,String pk) throws Exception {
		if(t==null)throw new NullPointerException();
		OrmTable table=null;
		//String sql="";
		//Field pkField=null;
		table=t.getClass().getAnnotation(OrmTable.class);
		//pkField=t.getClass().getDeclaredField(table.pk());
	    return load(t, table.pk(),pk);
	}
	
	public <T> T load(Class<T> cls,String column,String pk) throws Exception{
		 T t=cls.newInstance();
		 return load(t,column, pk);
	}
	
	public <T> T load(T t,String column,String pk) throws Exception {
		if(t==null)throw new NullPointerException();
		OrmTable table=null;
		String sql="";
		table=t.getClass().getAnnotation(OrmTable.class);
		sql=MessageFormat.format(" {1}= ?", table.name(),column);
		SQLiteDatabase rdatabase=this.getReadableDatabase();
	    Cursor cursor=rdatabase.query(table.name(), new String[]{"*"}, sql, new String[]{pk},"","","");
	    if(cursor.moveToNext()){
	    t=evalObject(t, cursor);
	    }else{
	    	throw new Exception(MessageFormat.format("{0} not contains {1} = {2}", table.name(),column,pk));
	    }
	    cursor.close();
	    //rdatabase.releaseReference();
	    //SQLiteDatabase.releaseMemory();
	    rdatabase.close();
		return t;
	}
	
public int insert(Object...objs) throws Exception{
		
		int success=0;
		SQLiteDatabase wdatabase=this.getWritableDatabase();
		wdatabase.beginTransaction();
		for(Object obj:objs){
			if(obj==null)continue;
			OrmTable table=null;
			String  sqlPattern="insert into {0} ({1}) values ({2})",sql="",
					sqlCacheKey=obj.getClass().getName()+":insert",cols="",vals="";
			Field[] fields=null;
		    
	    		table=obj.getClass().getAnnotation(OrmTable.class);
				fields=obj.getClass().getDeclaredFields();
		    	if(SQL_CACHE.containsKey(sqlCacheKey)){
		    		sql=SQL_CACHE.get(sqlCacheKey);
		    	}else{

					
					for(Field field:fields){
						if( ObjectHelper.isIn(field.getName(),table.ingoreColumnNames())||
								   (!table.insertPk()&& field.getName().equalsIgnoreCase(table.pk()) )
						)continue;	
						cols+=MessageFormat.format(" `{0}`,",field.getName() );
						vals+=" ?,";
						
					}
					cols=StringHelper.trim(cols, ",");
					vals=StringHelper.trim(vals, ",");
		    	    sql=MessageFormat.format(sqlPattern, table.name(),cols,vals);
		    	    SQL_CACHE.put(sqlCacheKey, sql);
		    	    
		    	}
		    	SQLiteStatement sqLiteStatement= wdatabase.compileStatement(sql);
		        int i=0;
		        //List<Object> ivals=new ArrayList<Object>();
				for(Field field:fields){
					if( ObjectHelper.isIn(field.getName(),table.ingoreColumnNames())||
					   (!table.insertPk()&& field.getName().equalsIgnoreCase(table.pk()) )
					)continue;
					i++;
					//String getterName="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
					Method m=ObjectHelper.getGetter(obj.getClass(), field);
					Object value=m.invoke(obj);
					//ivals.add(value);
					bind(sqLiteStatement, i, value);
				}
				// wdatabase.execSQL(sql, ivals.toArray());
				sqLiteStatement.executeInsert();
				success++;
		   
		       
		    
		}
		wdatabase.setTransactionSuccessful();
		wdatabase.endTransaction();
		//wdatabase.releaseMemory();
		wdatabase.close();
		//SQLiteDatabase.releaseMemory();
		return success;
	}


public <M> int  insertBatch(List<M> list) throws Exception{
	int eff=0;
	if(list==null||list.isEmpty())return eff;
	M m0=list.get(0);
	OrmTable table=m0.getClass().getAnnotation(OrmTable.class);
	Field[] fields=m0.getClass().getDeclaredFields();;
	SQLiteDatabase wdatabase=this.getWritableDatabase();
	wdatabase.beginTransaction();
	String  sqlPattern="insert into {0} ({1}) values ({2})",sql="",
			sqlCacheKey=m0.getClass().getName()+":insert",cols="",vals="";
    	if(SQL_CACHE.containsKey(sqlCacheKey)){
    		sql=SQL_CACHE.get(sqlCacheKey);
    	}else{

			
			for(Field field:fields){
				if( ObjectHelper.isIn(field.getName(),table.ingoreColumnNames())||
						   (!table.insertPk()&& field.getName().equalsIgnoreCase(table.pk()) )
				)continue;	
				cols+=MessageFormat.format(" `{0}`,",field.getName() );
				vals+=" ?,";
				
			}
			cols=StringHelper.trim(cols, ",");
			vals=StringHelper.trim(vals, ",");
    	    sql=MessageFormat.format(sqlPattern, table.name(),cols,vals);
    	    SQL_CACHE.put(sqlCacheKey, sql);
    	    
    	}
    	SQLiteStatement sqLiteStatement= wdatabase.compileStatement(sql);
	for(M obj:list){
		if(obj==null)continue;

	        //List<Object> ivals=new ArrayList<Object>();
	        int i=0;
	        sqLiteStatement.clearBindings();
			for(Field field:fields){				
				if((table.ingoreColumnNames().length>0&& ObjectHelper.isIn(field.getName(),table.ingoreColumnNames()) )||
				   (!table.insertPk()&& field.getName().equalsIgnoreCase(table.pk()) )
				)continue;	
				i++;
				//String getterName="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
				Method m=ObjectHelper.getGetter(obj.getClass(), field);
				Object value=m.invoke(obj);
				bind(sqLiteStatement, i, value);
			}
			// wdatabase.execSQL(sql, ivals.toArray());
			sqLiteStatement.executeInsert();
			eff++;
	   
	       
	    
	}
	wdatabase.setTransactionSuccessful();
	wdatabase.endTransaction();
	//SQLiteDatabase.releaseMemory();
	wdatabase.close();
	return eff;
}




public int update(Object...objs) throws Exception{
	
	int success=0;
	SQLiteDatabase wdatabase=this.getWritableDatabase();
	wdatabase.beginTransaction();
	for(Object obj : objs){
		if(obj==null)continue;
		OrmTable table=null;
		String sqlPattern="update {0} set {1} where {2}=? ",key="",sql=""
				,sqlCacheKey=obj.getClass().getName()+":update";
		Field[] fields=null;
		Field pkField=null;
		
			table=obj.getClass().getAnnotation(OrmTable.class);
			fields=obj.getClass().getDeclaredFields();
			pkField=obj.getClass().getDeclaredField(table.pk());
			if(SQL_CACHE.containsKey(sqlCacheKey)){
					sql=SQL_CACHE.get(sqlCacheKey);  
		}else{

		int i=0;
		for(Field field:fields){
			if(LinqHelper.isIn(field.getName(),table.ingoreColumnNames())||field.getName().equalsIgnoreCase(table.pk())){pkField=field;continue;}
			key+=MessageFormat.format(" `{0}` =? ,",field.getName() );
			i++;
		}
		key=StringHelper.trim(key, ",");
	    sql=MessageFormat.format(sqlPattern, table.name(),key,table.pk());
	    SQL_CACHE.put(sqlCacheKey,sql);
	    }
	    //.fields.preparedStatement=conn.prepareStatement(sql);
		//List<Object> params=new ArrayList<Object>();
	    int i=0;
	    SQLiteStatement sqLiteStatement= wdatabase.compileStatement(sql);
		for(Field field:fields){
			if(LinqHelper.isIn(field.getName(),table.ingoreColumnNames())||field.getName().equalsIgnoreCase(table.pk()))continue;	
			//String getterName="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
			i++;
			Method m=ObjectHelper.getGetter(obj.getClass(), field);
			Object value=m.invoke(obj);
			//params.add(value);
			bind(sqLiteStatement, i, value);
			
		}
		//String getterName="get"+pkField.getName().substring(0,1).toUpperCase()+pkField.getName().substring(1);
		Method m=ObjectHelper.getGetter(obj.getClass(), pkField);
		Object value=m.invoke(obj);
		//preparedStatement.setObject(j+1, value);
		//params.add(value);
		bind(sqLiteStatement, i+1, value);
		sqLiteStatement.executeUpdateDelete();
		//wdatabase.execSQL(sql,params.toArray());
		//success+=preparedStatement.executeUpdate();
		success++;
		
	
	}
	wdatabase.setTransactionSuccessful();
	wdatabase.endTransaction();
	wdatabase.close();
	//SQLiteDatabase.releaseMemory();
	
	return success;
}

public int delete(Object...objs)throws Exception{
	
	int success=0;
    SQLiteDatabase wdatabase=this.getWritableDatabase();
    wdatabase.beginTransaction();
	for(Object obj:objs){
		if(obj==null)continue;
		OrmTable table=null;
		String  sqlPattern="delete from {0} where {1}=?",sql="",
				sqlCacheKey=obj.getClass().getName()+":delete";
		
		  table=obj.getClass().getAnnotation(OrmTable.class);
		  if(SQL_CACHE.containsKey(sqlCacheKey)){
			sql=SQL_CACHE.get(sqlCacheKey);  
		  }else{
		    sql=MessageFormat.format(sqlPattern, table.name(),table.pk());
		    SQL_CACHE.put(sqlCacheKey,sql); 
		  }
		  //System.out.println(sql);
		  String getterName="get"+table.pk().substring(0,1).toUpperCase()+table.pk().substring(1);;
		  Method method=obj.getClass().getDeclaredMethod(getterName);
		  Object value=method.invoke(obj);
		  wdatabase.execSQL(sql,new Object[]{value});
		  success++;
		
	}
	wdatabase.setTransactionSuccessful();
	wdatabase.endTransaction();
	wdatabase.close();
	//SQLiteDatabase.releaseMemory();
	return success;
}



public <T> List<T> select(Class<T> cls) throws Exception{
    return select(cls, new String[]{"*"}, "", new String[]{});
}

public <T> List<T> select(Class<T> cls,int start,int limit) throws Exception{
    return select(cls, new String[]{"*"}, "", new String[]{},start,limit);
}


public <T> List<T> select(Class<T> cls,String[] columns,String selection,String[] selectionArgs) throws Exception{
    return select(cls, columns, selection, selectionArgs,"","","");
}

public <T> List<T> select(Class<T> cls,String[] columns,String selection,String[] selectionArgs,int start,int limit) throws Exception{
      return select(cls, columns, selection, selectionArgs,"","","",start,limit);
}



public <T> List<T> select(Class<T> cls,String[] columns,String selection,String[] selectionArgs,String  groupBy,String  having,String orderBy) throws Exception{
	   return select(cls, columns, selection, selectionArgs, groupBy, having, orderBy, 0,Integer.MAX_VALUE);
}

  public <T> List<T> select(Class<T> cls,String[] columns,String selection,String[] selectionArgs,String  groupBy,String  having,String orderBy,int start,int limit) throws Exception{
	   OrmTable table=cls.getAnnotation(OrmTable.class);
	   List<T> list=select(cls, table.name(), columns, selection, selectionArgs, groupBy, having, orderBy,limit,start);
	   return list;
  }
  
  public <T> List<T> select(Class<T> cls,String table,String[] columns,String selection,String[] selectionArgs,String  groupBy,String  having,String orderBy) throws Exception{
	   return select(cls, table, columns, selection, selectionArgs, groupBy, having, orderBy,0,Integer.MAX_VALUE);
}
  
  public <T> List<T> select(Class<T> cls,String table,String[] columns,String selection,String[] selectionArgs,String  groupBy,String  having,String orderBy,int start,int limit) throws Exception{
	   SQLiteDatabase database= this.getReadableDatabase();
	   Cursor cursor=database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	   List<T> list=evalList(cls, cursor,limit,start);
	   cursor.close();
	   //database.releaseReference();
	   //database.releaseMemory();
	   database.close();
	   //SQLiteDatabase.releaseMemory();
	   return list;
 }
  
  public <T> int count(Class<T> cls) throws Exception{
	  return count(cls,"",new String[]{});
  }
  
  public <T> int count(Class<T> cls,String selection,String[] selectionArgs) throws Exception{
    return count(cls, selection, selectionArgs,"","");
  }
  
  public <T> int count(Class<T> cls,String selection,String[] selectionArgs,String  groupBy,String  having) throws Exception{
	  OrmTable table= cls.getAnnotation(OrmTable.class);
	  return count(table.name(), selection, selectionArgs, groupBy, having);
  }
  
  public int count(String table,String selection,String[] selectionArgs,String  groupBy,String  having) throws Exception{
	   SQLiteDatabase database= this.getReadableDatabase();
	   int c=0;
	   Cursor cursor=database.query(table, new String[]{"count(*)"}, selection, selectionArgs, groupBy, having,"");
	   if(cursor.moveToNext()){
		   c=cursor.getInt(0);
	   }
	   cursor.close();
	   //database.releaseReference();
	   //database.releaseMemory();
	   database.close();
	   //SQLiteDatabase.releaseMemory();
	   return c;
}
       
   public  <T> List<T> evalList (Class<T> cls,Cursor cursor) throws InstantiationException, IllegalAccessException{
	  
	   return evalList(cls, cursor, 0, Integer.MAX_VALUE);
   }
   

   
   public  <T> List<T> evalList (Class<T> cls,Cursor cursor,int start,int limit) throws InstantiationException, IllegalAccessException{
	   List<T> list=new ArrayList<T>();
	   int i=1;
	   if(!cursor.moveToPosition(start)){
		   return list;
	   }
	   do{
		   T t=evalObject(cls, cursor);
			  list.add(t);  
	   }
	   while((i++<limit)&&cursor.moveToNext());
	   return list;
   }
   
   private <T> T evalObject (Class<T> cls,Cursor cursor) throws InstantiationException, IllegalAccessException {
	   T t=cls.newInstance();
	   return evalObject(t, cursor);
   }
   
   private <T> T evalObject (T t,Cursor cursor) {
	   Class cls=t.getClass();
	   for(Field field :ObjectHelper.getAllDeclaredFields(cls)){
		   int i=cursor.getColumnIndex(field.getName());
		   if(i<0)continue;
		   Type ft=field.getType();
		   try{
		   Method setter=ObjectHelper.getSetter(cls, field); 
		   if(String.class.equals(ft)){
			   setter.invoke(t, cursor.getString(i));
		   }else if(Integer.class.equals(ft)){
			   setter.invoke(t, cursor.getInt(i));
		   }else if(Double.class.equals(ft)){
			   setter.invoke(t, cursor.getDouble(i));
		   }else if(Float.class.equals(ft)){
			   setter.invoke(t, cursor.getFloat(i));
		   }else if(Long.class.equals(ft)){
			   setter.invoke(t, cursor.getLong(i));
		   }else if(Short.class.equals(ft)){
			   setter.invoke(t, cursor.getShort(i));
		   }else if(byte[].class.equals(ft)){
			   setter.invoke(t, cursor.getBlob(i));
		   }
		   }catch(Exception ex){continue;}
		   
	   }
	   return t;
   }
   
   private void bind(SQLiteStatement sqLiteStatement,int i,Object value){
	   if(value==null)sqLiteStatement.bindNull(i);
		else if(value instanceof String)sqLiteStatement.bindString(i, (String)value);
		else if(value instanceof Integer )sqLiteStatement.bindLong(i, (Long.parseLong(value.toString())));
		else if(value instanceof Double )sqLiteStatement.bindDouble(i, (Double)value);
		else if( value instanceof Long)sqLiteStatement.bindLong(i, (Long)value);
		else if(value instanceof Float)sqLiteStatement.bindDouble(i, (Double.parseDouble(value.toString())));
		else if(value instanceof byte[])sqLiteStatement.bindBlob(i, (byte[])value);
   }
   
   private ContentValues toContentValues(Object obj){
	   OrmTable table=obj.getClass().getAnnotation(OrmTable.class);
	   Set<String> ingoreColumns=new HashSet<String>();
	   for(String ic : table.ingoreColumnNames()){
		   ingoreColumns.add(ic);
	   }
	   return toContentValues(obj, ingoreColumns);
   }
   
   
   
   private ContentValues toContentValues(Object obj,Set<String> ingoreColumnNames){
	   ContentValues contentValues=null;
	   if(obj==null)return contentValues;
	   contentValues=new ContentValues();
	   Class cls=obj.getClass();
	   
	   for(Field field :ObjectHelper.getAllDeclaredFields(cls)){
		   String fname=field.getName();
		   if(ingoreColumnNames.contains(fname))continue;
		   Type ft=field.getType();
		   try{
		   Method getter=ObjectHelper.getGetter(cls, field); 
		   Object value=getter.invoke(obj);
		   
		   if(value==null){
			   contentValues.putNull(fname);
		   }
		   else if(String.class.equals(ft)){
			   contentValues.put(fname, (String)value);
		   }else if(Integer.class.equals(ft)){
			   contentValues.put(fname, (Integer)value);
		   }else if(Double.class.equals(ft)){
			   contentValues.put(fname, (Double)value);
		   }else if(Float.class.equals(ft)){
			   contentValues.put(fname, (Float)value);
		   }else if(Long.class.equals(ft)){
			   contentValues.put(fname, (Long)value);
		   }else if(Short.class.equals(ft)){
			   contentValues.put(fname, (Short)value);
		   }else if(byte[].class.equals(ft)){
			   contentValues.put(fname, (byte[])value);
		   }
		   }catch(Exception ex){continue;}
	   }
	   return contentValues;
   }

   @OrmTable(name="sqlite_master",pk="name")
   public class SqliteMaster{
	   private String name,type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	   
   }
   
}
