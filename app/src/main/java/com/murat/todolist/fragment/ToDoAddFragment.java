package com.murat.todolist.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.murat.todolist.R;
import com.murat.todolist.database.ToDoDatabase;
import com.murat.todolist.interfaces.SendContent;
import com.murat.todolist.model.ToDo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ToDoAddFragment extends DialogFragment {
    Spinner spinner;
    EditText inTitle, inDesc;
    Button btnDone, btnDelete;
    TextView tvDate, tvAssignDate;
    ImageView imgDateSelect;
    boolean isNewTodo = false;
    List<String> spinnerCategories = null;
    ToDoDatabase myToDoDatabase;
    ToDo updateTodo;

    SendContent sendContent;

    @Override
    public void onAttach(@NonNull Context context) {
        spinnerCategories = Arrays.asList(getResources().getStringArray(R.array.status));

        super.onAttach(context);
        sendContent = (SendContent) getTargetFragment();
        if (sendContent == null) {
            sendContent = (SendContent) getTargetFragment();
        }
    }

    String title = "", desc = "", category = "", createDate = "", deadline = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme2);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            desc = getArguments().getString("desc");
            category = getArguments().getString("category");
            createDate = getArguments().getString("createDate");
            deadline = getArguments().getString("deadline");

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_note_dialog, container, false);
        spinner = view.findViewById(R.id.addSpinner);
        inTitle = view.findViewById(R.id.inTitle);
        inDesc = view.findViewById(R.id.inDescription);
        btnDone = view.findViewById(R.id.btnDone);
        btnDelete = view.findViewById(R.id.btnDelete);
        tvDate = view.findViewById(R.id.textviewDate);
        tvAssignDate = view.findViewById(R.id.tvAssignDate);
        imgDateSelect = view.findViewById(R.id.imgDateSelect);
        if (!title.matches("") || !desc.matches("") || !category.matches("") || !createDate.matches("") || !deadline.matches("")) {
            spinner.setPrompt(category);
            inTitle.setText(title);
            inDesc.setText(desc);
            tvDate.setText(createDate);
            tvAssignDate.setText(deadline);
        } else {

            tvDate.setText(date());
            imgDateSelect.setOnClickListener(v -> {
                viewCalendar();
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerCategories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
        myToDoDatabase = ToDoDatabase.getInstance(getContext());


        int todo_id = getActivity().getIntent().getIntExtra("id", -100);

        if (todo_id == -100)
            isNewTodo = true;

        if (!isNewTodo) {
            fetchTodoById(todo_id);

        }

        btnDone.setOnClickListener(v -> {
            if (isNewTodo) {
                ToDo todo = new ToDo();
                todo.setName(inTitle.getText().toString());
                todo.setDesc(inDesc.getText().toString());
                todo.setCategory(spinner.getSelectedItem().toString());
                todo.setCreateDate(tvDate.getText().toString());
                todo.setDeadline(tvAssignDate.getText().toString());
                todo.setComplete(0);
                insertRow(todo);
                sendContent.sendCode(1);
                getDialog().dismiss();
            } else {
                updateTodo = new ToDo();
                updateTodo.setName(inTitle.getText().toString());
                updateTodo.setDesc(inDesc.getText().toString());
                updateTodo.setCategory(spinner.getSelectedItem().toString());
                updateTodo.setCreateDate(tvDate.getText().toString());
                updateTodo.setDeadline(tvAssignDate.getText().toString());
                updateTodo.setComplete(0);

                updateRow(updateTodo);
                sendContent.sendCode(2);
                getDialog().dismiss();

            }
        });

        btnDelete.setOnClickListener(v -> {
            deleteRow(updateTodo);
            sendContent.sendCode(3);
            getDialog().dismiss();
        });
        return view;
    }


    int year = 0;
    int month = 0;
    int day = 0;

    private String date() {
        Calendar mcurrentTime = Calendar.getInstance();

        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        final int second = mcurrentTime.get(Calendar.SECOND);

        year = mcurrentTime.get(Calendar.YEAR);
        month = mcurrentTime.get(Calendar.MONTH) + 1;
        day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

        return "" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

    }

    private void viewCalendar() {

        DatePickerDialog dpd = new DatePickerDialog(Objects.requireNonNull(getContext()),
                (view, year1, month1, dayOfMonth) -> {

                    month1 += 1;
                    year = year1;
                    month = month1;
                    day = dayOfMonth;
                    tvAssignDate.setText(year + "-" + month + "-" + day);


                }, 0, 0, 0);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Select", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
        dpd.show();

    }

    @SuppressLint("StaticFieldLeak")
    private void fetchTodoById(final int todo_id) {
        new AsyncTask<Integer, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Integer... params) {

                return myToDoDatabase.daoAccess().fetchTodoListById(params[0]);

            }

            @Override
            protected void onPostExecute(ToDo todo) {
                super.onPostExecute(todo);
                inTitle.setText(todo.name);
                inDesc.setText(todo.desc);
                spinner.setSelection(spinnerCategories.indexOf(todo.category));

                updateTodo = todo;
            }
        }.execute(todo_id);

    }

    @SuppressLint("StaticFieldLeak")
    private void insertRow(ToDo todo) {
        new AsyncTask<ToDo, Void, Long>() {
            @Override
            protected Long doInBackground(ToDo... params) {
                return myToDoDatabase.daoAccess().insertTodo(params[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);

            }
        }.execute(todo);

    }

    @SuppressLint("StaticFieldLeak")
    private void deleteRow(ToDo todo) {
        new AsyncTask<ToDo, Void, Integer>() {
            @Override
            protected Integer doInBackground(ToDo... params) {
                return myToDoDatabase.daoAccess().deleteTodo(params[0].id);
            }

            @Override
            protected void onPostExecute(Integer number) {
                super.onPostExecute(number);


            }
        }.execute(todo);

    }


    @SuppressLint("StaticFieldLeak")
    private void updateRow(ToDo todo) {
        new AsyncTask<ToDo, Void, Integer>() {
            @Override
            protected Integer doInBackground(ToDo... params) {
                return myToDoDatabase.daoAccess().updateTodo(params[0]);
            }

            @Override
            protected void onPostExecute(Integer number) {
                super.onPostExecute(number);


            }
        }.execute(todo);
    }
}