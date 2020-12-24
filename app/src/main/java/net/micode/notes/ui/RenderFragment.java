package net.micode.notes.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.micode.notes.R;
import net.micode.notes.ui.callback.CallBack;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RenderFragment extends Fragment {

    private CallBack callBack;
    private View view;
    private String initContent;

    public RenderFragment() {
        callBack = new CallBack() {
            @Override
            public void emit(String msg) {
                MarkdownView markdownView = view.findViewById(R.id.container);
                markdownView.loadMarkdown(msg);
            }
        };
    }

    public static RenderFragment newInstance(String param1, String param2) {
        RenderFragment fragment = new RenderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_render, container, false);
        MarkdownView markdownView = view.findViewById(R.id.container);
        markdownView.loadMarkdown(initContent);
        markdownView.addStyleSheet(new Github());
        return view;
    }

    public CallBack getCallBack(){
        return callBack;
    }

    public void setText(String text){
        this.initContent = text;
    }
}