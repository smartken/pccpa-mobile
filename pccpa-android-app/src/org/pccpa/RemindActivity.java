package org.pccpa;

import org.pccpa.frage.HelpFragment;
import org.pccpa.frage.RemindListFragment;

import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class RemindActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		RemindListFragment list=new RemindListFragment();
		this.getSupportFragmentManager().beginTransaction().add(android.R.id.content,list).commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("����")
		.setIcon(R.drawable.ic_search)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

    menu.add("ˢ��")
        .setIcon( R.drawable.ic_refresh_inverse)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        //This uses the imported MenuItem from ActionBarSherlock
	    if("ˢ��".equals(item.getTitle())){
	    	RemindListFragment list=new RemindListFragment();
			this.getSupportFragmentManager().beginTransaction().replace(android.R.id.content,list).commit();
			
	    }else if("����".equals(item.getTitle())){
	    	HelpFragment list=new HelpFragment();
	    	list.context="�������";
			list.show(getSupportFragmentManager(), list.context);
	    }
	    return true;
	}
	
	
	

}
