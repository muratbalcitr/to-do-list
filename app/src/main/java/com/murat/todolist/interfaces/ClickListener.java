package com.murat.todolist.interfaces;

import android.view.View;

public interface ClickListener {
    void OnItemClickListener(View v, int position);
    void OnLongItemClickListener(View v, int position);

}
