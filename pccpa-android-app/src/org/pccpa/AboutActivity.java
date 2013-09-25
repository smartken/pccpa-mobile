package org.pccpa;

import org.pccpa.widget.MenuActionGrid;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;



import greendroid.app.GDActivity;

public class AboutActivity extends BaseFragmentActivity {

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setActionBarContentView(R.layout.about);
        this.setContentView(R.layout.about);
        final TextView aboutText = (TextView) findViewById(R.id.about);
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        String content="<p>��Ӧ�ô������ֹ�����ҵ����Ȥ�з�����������˾����!</p><p>���ڱ���ˮƽ���ޣ�ż����������������£�����Ƚϼ�ª��Ҳ�벻Ҫ����</p>";
        content+="<p>������Դ���ѿ��������ڳ�����չ�У���ӭѧϰ����</p>";
        content+="<p>github:<a href='https://github.com/smartken/pccpa-mobile'>https://github.com/smartken/pccpa-mobile</a></p>";
        aboutText.setText(Html.fromHtml(content));
        //MenuActionGrid.renderTo(this);
    }
}
