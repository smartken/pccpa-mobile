package org.pccpa.api;

import java.text.MessageFormat;
import java.util.List;

import org.pccpa.DB;
import org.pccpa.api.Client.ContactGrid;

import com.kull.android.SQLiteOrmHelper;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class SiteSynRunnable implements Runnable {

	private Context _context;
	
	public SiteSynRunnable(Context context){
		_context=context;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
		ToastHandler toastHandler=new ToastHandler(_context);
		  try{
			  //Toast.makeText(_context, "��ʼ��ȡ�ӿ����ݡ�����", 1000).show();
			  Message msg=new Message();
			  msg.obj="��ʼ��ȡ�ӿ����ݡ�����";
			  toastHandler.sendMessage(msg);
			int start=0,limit=100;
		  ContactGrid grid= Client.CURR_CLIENT.getContacts((start++)*limit,limit);
		    
			
			int eff=0;
			//for(int start=0;start*limit<grid.getTotal();start++){
			List<Contact> ems=grid.getRows();
			while(ems.size()<grid.getTotal()){
				grid= Client.CURR_CLIENT.getContacts((start++)*limit,limit);
				ems.addAll(grid.getRows());
			}
			//Toast.makeText(_context, MessageFormat.format("�ؽӿڻ�� {0} ����¼����ʼ���롣����", grid.getTotal()), 3000).show();
			msg.obj="�ؽӿڻ�� {0} ����¼����ʼ���롣����";
			  toastHandler.sendMessage(msg);
			SQLiteOrmHelper ormHelper=DB.local.createSqLiteOrmHelper(_context);
			ormHelper.replaceTable(Contact.class);
			for(Contact em:ems){
				ormHelper.insert(em);
				//progressBar.setProgress(i++);
				eff++;
				//int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * eff;;
	            //setSupportProgress(progress);
			}
		
			//progressBar.setVisibility(View.GONE);
			//Toast.makeText(_context, "�ɹ�ͬ�� "+eff+" ����¼", 3000).show();
			msg.obj="�ɹ�ͬ�� "+eff+" ����¼";
			  toastHandler.sendMessage(msg);
		  }catch(Exception ex){
			  Toast.makeText(_context, "ͬ��ʧ��: "+ex.getLocalizedMessage(), 3000).show();
		  }
		  Looper.loop();
		  }

}
