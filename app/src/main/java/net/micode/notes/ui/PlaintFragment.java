package net.micode.notes.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.micode.notes.R;
import net.micode.notes.ui.callback.CallBack;

public class PlaintFragment extends Fragment {
    private CallBack callBack;
    private View view;
    private String initContent;

    public PlaintFragment(CallBack callBack) {
        this.callBack = callBack;
    }

    public static PlaintFragment newInstance(CallBack callBack) {
        PlaintFragment fragment = new PlaintFragment(callBack);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_plaint, container, false);
        // 对编辑器设置编辑文本变更的设定
        final EditText editText = view.findViewById(R.id.editor);
        editText.setText(initContent);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                callBack.emit(editText.getText().toString());
            }
        });
        return view;
    }

    public void setText(String text){
        initContent = text;
    }

    public String getText(){
        EditText editText = view.findViewById(R.id.editor);
        return editText.getText().toString();
    }
}