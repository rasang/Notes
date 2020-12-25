package net.micode.notes.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.model.WorkingNote;
import net.micode.notes.tool.ResourceParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewNoteEditActivity extends FragmentActivity {

    private WorkingNote workingNote;
    private RenderFragment renderFragment;
    private PlaintFragment plaintFragment;
    private Calendar calendar = Calendar.getInstance();
    private int hour = calendar.get(Calendar.HOUR);
    private int minute = calendar.get(Calendar.MINUTE);
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_edit);
        initTab();
    }

    public void addAlarm(View view){
        context = this;
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewNoteEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                CreateAlarm ccreate = new CreateAlarm();
                ccreate.addAlarm(h, m, context, workingNote.getContent());
            }
        },hour, minute, true);
        timePickerDialog.show();
    }

    public void initTab(){
        final ViewPager viewPager = findViewById(R.id.view_pager);
        final SegmentTabLayout tabLayout = findViewById(R.id.tableLayout);
        String[] titles = {"编辑", "预览"};
        tabLayout.setTabData(titles);
        List<Fragment> list = new ArrayList<>();
        long noteId = getIntent().getLongExtra(Intent.EXTRA_UID, 0);
        if(TextUtils.equals(Intent.ACTION_VIEW, getIntent().getAction())){
            workingNote = WorkingNote.load(this, noteId);
        }
        else{
            long folderId = getIntent().getLongExtra(Notes.INTENT_EXTRA_FOLDER_ID, 0);
            int widgetId = getIntent().getIntExtra(Notes.INTENT_EXTRA_WIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int widgetType = getIntent().getIntExtra(Notes.INTENT_EXTRA_WIDGET_TYPE,
                    Notes.TYPE_WIDGET_INVALIDE);
            int bgResId = getIntent().getIntExtra(Notes.INTENT_EXTRA_BACKGROUND_ID,
                    ResourceParser.getDefaultBgId(this));
            workingNote = WorkingNote.createEmptyNote(this, folderId, widgetId, widgetType,
                    bgResId);
        }
        renderFragment = new RenderFragment();
        plaintFragment = new PlaintFragment(renderFragment.getCallBack());
        plaintFragment.setText(workingNote.getContent());
        renderFragment.setText(workingNote.getContent());
        list.add(plaintFragment);
        list.add(renderFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        workingNote.setWorkingText(plaintFragment.getText());
        workingNote.saveNote();
        super.onBackPressed();
    }
}
