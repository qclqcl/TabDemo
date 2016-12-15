package com.andyidea.tabdemo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

public class SettingTabActivity extends TabActivity implements OnCheckedChangeListener{
	
	private TabHost settingTabHost;
	private Intent globalAIntent;
	private Intent satsBIntent;
	private Intent mapCIntent;
	private Intent skyDIntent;
	private Intent passEIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settingtabs);
        
        this.globalAIntent = new Intent(this,GlobalSettingActivity.class);
        this.satsBIntent = new Intent(this,SatellitesSettingActivity.class);
        this.mapCIntent = new Intent(this,MapSettingActivity.class);
        this.skyDIntent = new Intent(this,SkySettingActivity.class);
        this.passEIntent = new Intent(this,PassSettingActivity.class);
        
		((RadioButton) findViewById(R.id.setting_button0))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.setting_button1))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.setting_button2))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.setting_button3))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.setting_button4))
		.setOnCheckedChangeListener(this);
        
        setupIntent();
    }


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			switch (buttonView.getId()) {
			case R.id.setting_button0:
				this.settingTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.setting_button1:
				this.settingTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.setting_button2:
				this.settingTabHost.setCurrentTabByTag("C_TAB");
				break;
			case R.id.setting_button3:
				this.settingTabHost.setCurrentTabByTag("D_TAB");
				break;
			case R.id.setting_button4:
				this.settingTabHost.setCurrentTabByTag("MORE_TAB");
				break;
			}
		}
		
	}

    
    
    
	private void setupIntent() {
		this.settingTabHost = getTabHost();
		TabHost localTabHost = this.settingTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_stats,
				R.drawable.icon, this.globalAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_map,
				R.drawable.icon, this.satsBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB",R.string.main_sky,
				R.drawable.icon, this.mapCIntent));

		localTabHost.addTab(buildTabSpec("D_TAB", R.string.main_pass,
				R.drawable.icon, this.skyDIntent));

		localTabHost.addTab(buildTabSpec("MORE_TAB", R.string.main_more,
				R.drawable.icon, this.passEIntent));

	}
	
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.settingTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
}