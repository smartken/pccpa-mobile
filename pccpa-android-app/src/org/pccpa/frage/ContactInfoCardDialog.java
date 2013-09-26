package org.pccpa.frage;



import java.text.MessageFormat;

import org.pccpa.ContactActivity;
import org.pccpa.DB;
import org.pccpa.R;
import org.pccpa.api.Client;
import org.pccpa.api.Contact;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.kull.StringHelper;
import com.kull.android.ContextHelper;
import com.kull.android.widget.AsyncImageView;

public class ContactInfoCardDialog extends SherlockDialogFragment {

	private Contact _contact;
	
	
	
	
	
	public Contact get_contact() {
		return _contact;
	}





	public void set_contact(Contact _contact) {
		this._contact = _contact;
	}


private ContactListFragment parent;
	
	

	public void setParent(ContactListFragment parent) {
		this.parent = parent;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog= super.onCreateDialog(savedInstanceState);
	    dialog.setTitle("������Ϣ");
		return dialog;
	}





	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final ContextHelper contextHelper=new ContextHelper(getActivity());
		View view=inflater.inflate(R.layout.dialog_contact_info_card2, container,false);
		TextView txvEmName=(TextView)view.findViewById(R.id.txvEmName)
		 ,txvDepName=(TextView)view.findViewById(R.id.txvDepName)
				 ,btnDep=(TextView)view.findViewById(R.id.btnDep)
		 ,txvTel=(TextView)view.findViewById(R.id.txvTelInfor)
		 
	     ,txvRankName=(TextView)view.findViewById(R.id.txvRankName)
		 ;
		TextView imbCallMobile=(TextView)view.findViewById(R.id.btnCallMobile)
				,imbCallMobileShort=(TextView)view.findViewById(R.id.btnCallMobileShort)
			    ,imbSendEmail=(TextView)view.findViewById(R.id.btnSendMail)
			   
		;
		Button btnSaveToPhone=(Button)view.findViewById(R.id.btnSaveToPhone)
			,btnShareBySMS=(Button)view.findViewById(R.id.btnShareBySMS)
			,btnShareByMail=(Button)view.findViewById(R.id.btnShareByMail);
				;
        txvEmName.setText(_contact.getEUserName());
        txvDepName.setText(Html.fromHtml("<u>"+ _contact.getAreaName()+"</u>"));
        txvDepName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    parent.loadAreaContacts(_contact.getAreaName(), _contact.getAreaID());
			    dismiss();
			}
		});
        
        btnDep.setText(Html.fromHtml("<u>"+_contact.getDepartName()+"</u>"));
        btnDep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  parent.loadDeptContacts(_contact.getAreaName(),_contact.getDepartName(), _contact.getDepartID());
				  dismiss();
			}
		});
        //txvMobile.setText(_contact.getEMobile());
        txvTel.setText(_contact.getETelWork());
        //txvMobileShort.setText(_contact.getEMobileShort());
        //txvTelShort.setText(_contact.getETelWorkShort());
        //txvEmail.setText(_contact.getEMail());
        
        txvRankName.setText(Html.fromHtml("<u>"+_contact.getRankName()+"</u>"));
        
        txvRankName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				parent.loadAreaRankContacts(_contact.getAreaName(), _contact.getRankName(),  _contact.getAreaID(),_contact.getRankID());
			    dismiss();	
			}
		});
        
        AsyncImageView imvPhoto=(AsyncImageView)view.findViewById(R.id.imvPhoto);
        try{
          //Bitmap bitmap=Client.getContactPhoto(this.getActivity(),_contact.getEID(),false);
          //Drawable drPhoto=Client.getContactPhoto(this.getActivity(),_contact.getEID(),false);
          imvPhoto.setUrl(Client.urlEmployeePhoto(_contact.getEID()));
          //imvPhoto.setImageDrawable(drPhoto);
        }catch(Exception ex){
        	//imvPhoto.setBackgroundColor(R.color.abs__primary_text_holo_light);
        	ex.printStackTrace();
        }
        
       
        
        Contact curContact=Client.CURR_CLIENT.getContact();
        final String shareContent=MessageFormat.format("��ĺ��ѻ�ͬ��  {0} {1} {2} �����Ƽ�  �콡�ۺϹ���ϵͳ android �ͻ���, ���ص�ַ��{3}",
        		curContact.getAreaName(),
        		curContact.getDepartName(),
        		curContact.getEUserName(),
       		 Client.URL_APK
       		);
        if(StringHelper.isBlank(_contact.getEMobile() ) ){
           imbCallMobile.setVisibility(View.INVISIBLE);
           btnShareBySMS.setVisibility(View.INVISIBLE);
        }else{
        	imbCallMobile.setText(Html.fromHtml("<u>"+_contact.getEMobile()+"</u>"));
        imbCallMobile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			      Intent intent=	contextHelper.toCallTel(_contact.getEMobile());
				  getActivity().startActivity(intent);
			}
			
			
		});
        
        

         btnShareBySMS.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				Intent intent= contextHelper.toSendSms(_contact.getEMobile(), shareContent);
 			    startActivity(intent);
 			}
 		});
         
        }
        if(StringHelper.isBlank(_contact.getEMobileShort() ) ){
            imbCallMobileShort.setVisibility(View.INVISIBLE);
         }else{
            imbCallMobileShort.setText(Html.fromHtml("<u>"+_contact.getEMobileShort()+"</u>"));
           imbCallMobileShort.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    	Intent intent= contextHelper.toCallTel(_contact.getEMobileShort());
			    	getActivity().startActivity(intent);
				
			}
		  });
         }
        
        if(StringHelper.isBlank(_contact.getEMail() ) ){
        	imbSendEmail.setVisibility(View.INVISIBLE);
         }else{
        	 imbSendEmail.setText(Html.fromHtml("<u>"+_contact.getEMail()+"</u>"));
        	 imbSendEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    	//contextHelper.toTel(_contact.getEMobileShort());
				 Intent intent= contextHelper.toSendMail(_contact.getEMail());
				 getActivity().startActivity(intent);
			}
		  });
         }
        
        btnSaveToPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    try {
			    	Intent intent =contextHelper.toSaveContact(_contact.getEUserName(),
							StringHelper.firstNotBlank(_contact.getEMobile(),_contact.getEMobileShort())
							, _contact.getEMail()
							, _contact.getAreaName()+" "+_contact.getDepartName()
							,  _contact.getRankName()
							,""
			    			);
			    	 startActivity(intent);
					 Toast.makeText(getActivity(), 
						      MessageFormat.format("{0} ����Ϣ�ѳɹ����浽�ֻ�", _contact.getEUserName())
						    		, 3000).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(getActivity(), 
						      MessageFormat.format("���ʧ��:{0}", e.getMessage())
						    		, 5000).show();
					e.printStackTrace();
				} 
			   
			}
		});
        
        
        
        
        btnShareByMail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent= contextHelper.toSendMail(_contact.getEMail(),new String[]{},shareContent,"android �ۺ���Ϣ����ϵͳ");
			    startActivity(intent);
			}
		});
        
		return view;
	}

}
