package net.micode.notes.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import net.micode.notes.R;
import net.micode.notes.model.WorkingNote;

import java.util.ArrayList;
import java.util.List;

public class NewNoteEditActivity extends FragmentActivity {

    WorkingNote workingNote;
    RenderFragment renderFragment;
    PlaintFragment plaintFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_edit);
        initTab();
    }

    public void initTab(){
        long noteId = getIntent().getLongExtra(Intent.EXTRA_UID, 0);
        workingNote = WorkingNote.load(this, noteId);
        final ViewPager viewPager = findViewById(R.id.view_pager);
        final SegmentTabLayout tabLayout = findViewById(R.id.tableLayout);
        String[] titles = {"编辑", "预览"};
        tabLayout.setTabData(titles);
        List<Fragment> list = new ArrayList<>();
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
