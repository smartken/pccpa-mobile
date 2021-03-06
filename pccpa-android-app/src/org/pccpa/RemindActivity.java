package org.pccpa;

import org.pccpa.ContactActivity.TabsAdapter;
import org.pccpa.api.Client;
import org.pccpa.api.Client.RemindsAdapter;
import org.pccpa.api.RemindItem;
import org.pccpa.frage.HelpFragment;
import org.pccpa.frage.RemindListFragment;
import org.pccpa.frage.ApplyRemindListFragment;
import org.pccpa.frage.CheckRemindListFragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class RemindActivity extends BaseFragmentActivity {

	TabsAdapter mTabsAdapter;
	TabHost mTabHost;
	 ViewPager  mViewPager;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_tabs_pager);
		
		//this.getSupportFragmentManager().beginTransaction().add(android.R.id.content,list).commit();
		 mTabHost = (TabHost)findViewById(android.R.id.tabhost);
	        mTabHost.setup();

	        mViewPager = (ViewPager)findViewById(R.id.pager);

	        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager); 
			 try {
				
				
				Bundle b0=new Bundle(),b1=new Bundle();
				b0.putCharSequence("rtype", "check");
				b1.putCharSequence("rtype", "apply");
				mTabsAdapter.addTab(mTabHost.newTabSpec("������").setIndicator("������"),
		                CheckRemindListFragment.class, null);
				mTabsAdapter.addTab(mTabHost.newTabSpec("������").setIndicator("������"),
		                ApplyRemindListFragment.class, null);
				 //mTabsAdapter.addTab(mTabHost.newTabSpec("������").setIndicator("������"),
			      //          ContactListFragment.class, null);
			    //    mTabsAdapter.addTab(mTabHost.newTabSpec("������").setIndicator("������"),
			    //    		ContactListFragment.class, null);
			    // //   mTabsAdapter.addTab(mTabHost.newTabSpec("��ƴ��").setIndicator("��ƴ��"),
			    //    		ContactListFragment.class, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				contextHelper.alert(e, Toast.LENGTH_LONG);
			}
			 if (arg0 != null) {
		            mTabHost.setCurrentTabByTag(arg0.getString("tab"));
		        }
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
	    	//RemindListFragment list=new RemindListFragment();
			//this.getSupportFragmentManager().beginTransaction().replace(android.R.id.content,list).commit();
		   RemindListFragment remindListFragment=(RemindListFragment)mTabsAdapter.getItem(mTabHost.getCurrentTab());
	       remindListFragment.loadList(this);
	    }else if("����".equals(item.getTitle())){
	    	HelpFragment list=new HelpFragment();
	    	list.context="�������";
			list.show(getSupportFragmentManager(), list.context);
	    }
	    return true;
	}
	
	
	

}
