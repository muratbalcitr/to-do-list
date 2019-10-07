package com.murat.todolist.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.murat.todolist.R;
import com.murat.todolist.adapter.RecyclerViewAdapter;
import com.murat.todolist.database.ToDoDatabase;
import com.murat.todolist.interfaces.ClickListener;
import com.murat.todolist.interfaces.SendContent;
import com.murat.todolist.model.ToDo;
import com.murat.todolist.util.RecyclerItemListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AllNotesFragment extends Fragment implements AdapterView.OnItemSelectedListener, SendContent {

    ToDoDatabase toDoDatabase;
    RecyclerView recyclerView;
    Spinner spinner;
    RecyclerViewAdapter recyclerViewAdapter;
    ImageView imgDelete, imgComplete,imgShare;
    ImageView imgDate;
    TextInputEditText etSearch;
    ImageView imgFilter;
    LinearLayout llVisibleTodos;
    LinearLayout llBottom;

    ArrayList<ToDo> todoArrayList = new ArrayList<>();
    List<String> spinnerCategories = null;
    public static final int NEW_TODO_REQUEST_CODE = 200;
    public static final int UPDATE_TODO_REQUEST_CODE = 300;
    private boolean isGone = false;
    int pos1 = 0;
    FloatingActionButton fab, fabOrder;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        spinnerCategories = Arrays.asList(getResources().getStringArray(R.array.status));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toDoDatabase = ToDoDatabase.getInstance(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.all_notes, container, false);
        initViews(root);
        loadAllTodos();
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);

        return root;
    }


    private void initViews(View view) {
        fab = view.findViewById(R.id.fab);
        fabOrder = view.findViewById(R.id.fabOrder);
        spinner = view.findViewById(R.id.spinner);
        recyclerView = view.findViewById(R.id.recyclerView);
        imgDate = view.findViewById(R.id.btnDate);
        llVisibleTodos = view.findViewById(R.id.llVisibleTodos);
        llBottom = view.findViewById(R.id.llBottom);
        etSearch = view.findViewById(R.id.etSearch);
        imgFilter = view.findViewById(R.id.imgFilter);
        imgComplete = view.findViewById(R.id.btnCompletedNotes);
        imgDelete = view.findViewById(R.id.btnDeleteNotes);
        imgShare = view.findViewById(R.id.btnShareNotes);
        llBottom.setVisibility(View.GONE);
        imgFilter.setOnClickListener(v -> {
            if (isGone) {
                isGone = false;
                llVisibleTodos.setVisibility(View.VISIBLE);
            } else {
                isGone = true;
                llVisibleTodos.setVisibility(View.GONE);
            }
        });
        fab.setOnClickListener(view1 -> {
            ToDoAddFragment addFragment = new ToDoAddFragment();
            assert getFragmentManager() != null;
            addFragment.setTargetFragment(AllNotesFragment.this, 11);
            addFragment.show(getFragmentManager(), "");
        });
        fabOrder.setOnClickListener(v -> {
            openDialog();

        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        llBottom.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), todoArrayList);
        recyclerView.addOnItemTouchListener(new RecyclerItemListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void OnItemClickListener(View v, int position) {
                llBottom.setVisibility(View.GONE);
                Bundle bundle = new Bundle();

                ToDoAddFragment addFragment = new ToDoAddFragment();
                assert getFragmentManager() != null;
                bundle.putString("title", todoArrayList.get(position).getName());
                bundle.putString("desc", todoArrayList.get(position).getDesc());
                bundle.putString("category", todoArrayList.get(position).getCategory());
                bundle.putString("createDate", todoArrayList.get(position).getCreateDate());
                bundle.putString("deadline", todoArrayList.get(position).getDeadline());

                addFragment.setTargetFragment(AllNotesFragment.this, 11);
                addFragment.setArguments(bundle);
                addFragment.show(getFragmentManager(), "");
            }

            @Override
            public void OnLongItemClickListener(View v, int position) {
                pos1 = position;
                llBottom.setVisibility(View.VISIBLE);
            }
        }));

        recyclerView.setAdapter(recyclerViewAdapter);
        imgDate.setOnClickListener(v -> {
            String date = viewCalendar();
            fetchOrderDate(date);

        });

        imgDelete.setOnClickListener(v -> {
            deleteTodo(pos1);
        });
        imgComplete.setOnClickListener(view12 -> updateToDo(pos1));
        imgShare.setOnClickListener(v->{share(pos1);});

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                recyclerViewAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerViewAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void share(int pos1) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"To Do Name :"+todoArrayList.get(pos1).getName()+"\n Description : "+todoArrayList.get(pos1).getDesc()+" \n deadLine :"+todoArrayList.get(pos1).getDeadline());
         sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
    public void openDialog() {
        Dialog builder = new Dialog(Objects.requireNonNull(getActivity()));
        builder.setTitle("Select Order Type");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_todo, null);
        RadioButton rbCompleted = view.findViewById(R.id.rbCompletedFilter);
        RadioButton rbNotCompleted = view.findViewById(R.id.rbNotCompletedFilter);
        Button btnDesc = view.findViewById(R.id.btnDescFilter);
        Button btnAsc = view.findViewById(R.id.btnIncFilter);
        Button dateSelect = view.findViewById(R.id.btnDateSelectFilter);
        builder.setContentView(view);
        btnAsc.setOnClickListener(v -> {
            loadFilteredTodosByNameASC();
            builder.dismiss();
        });
        btnDesc.setOnClickListener(v -> {
            loadFilteredTodosByNameDESC();
            builder.dismiss();
        });
        rbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loadAllTodosCompleted();

            builder.dismiss();
        });
        rbNotCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loadAllTodos();

            builder.dismiss();
        });
        dateSelect.setOnClickListener(v -> {
            String date = viewCalendar();
            fetchOrderDate(date);
            builder.dismiss();


        });

        builder.show();
    }

    private String date;

    private String viewCalendar() {
        Calendar mcurrentTime = Calendar.getInstance();

        final int year = mcurrentTime.get(Calendar.YEAR);
        final int month = mcurrentTime.get(Calendar.MONTH) + 1;
        final int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Objects.requireNonNull(getContext()),
                (view, year1, month1, dayOfMonth) -> {

                    month1 += 1;

                    date = MessageFormat.format("{0}-{1}-{2}", dayOfMonth, month1, year1);
                }, year, month, day);

        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Select", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
        dpd.show();

        return date;

    }

    private void updateToDo(int pos1) {
        toDoDatabase.daoAccess().Complete(todoArrayList.get(pos1).id);
        loadAllTodos();
        recyclerViewAdapter.notifyDataSetChanged();
        llBottom.setVisibility(View.GONE);
    }

    private void deleteTodo(int pos) {
        toDoDatabase.daoAccess().deleteTodo(todoArrayList.get(pos).id);
        loadAllTodos();
        recyclerViewAdapter.notifyDataSetChanged();
        llBottom.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            loadAllTodos();
        } else {
            String string = parent.getItemAtPosition(position).toString();
            loadFilteredTodos(string);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodos(String category) {
        new AsyncTask<String, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(String... params) {
                return toDoDatabase.daoAccess().fetchTodoListByCategory(params[0]);

            }

            @Override
            protected void onPostExecute(List<ToDo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute(category);

    }

    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodosByNameASC() {
        new AsyncTask<String, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(String... params) {
                return toDoDatabase.daoAccess().fetchOrderNameASC();
            }

            @Override
            protected void onPostExecute(List<ToDo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodosByNameDESC() {
        new AsyncTask<String, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(String... params) {
                return toDoDatabase.daoAccess().fetchOrderNameDESC();
            }

            @Override
            protected void onPostExecute(List<ToDo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    private void fetchOrderDate(String date) {
        new AsyncTask<String, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(String... params) {
                return toDoDatabase.daoAccess().fetchToDoListByDeadline(params[0]);
            }

            @Override
            protected void onPostExecute(List<ToDo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute(date);

    }


    @SuppressLint("StaticFieldLeak")
    private void fetchTodoByIdAndInsert(int id) {
        new AsyncTask<Integer, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Integer... params) {
                return toDoDatabase.daoAccess().fetchTodoListById(params[0]);

            }

            @Override
            protected void onPostExecute(ToDo todoList) {
                recyclerViewAdapter.addRow(todoList);
            }
        }.execute(id);

    }

    @SuppressLint("StaticFieldLeak")
    private void loadAllTodos() {
        new AsyncTask<String, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(String... params) {
                if (toDoDatabase.daoAccess().fetchAllTodos() != null)
                    return toDoDatabase.daoAccess().fetchAllTodos();
                else
                    return null;
            }

            @Override
            protected void onPostExecute(List<ToDo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadAllTodosCompleted() {
        new AsyncTask<String, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(String... params) {
                if (toDoDatabase.daoAccess().fetchAllCompleted() != null)
                    return toDoDatabase.daoAccess().fetchAllCompleted();
                else
                    return null;
            }

            @Override
            protected void onPostExecute(List<ToDo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //reset spinners
            spinner.setSelection(0);

            if (requestCode == NEW_TODO_REQUEST_CODE) {
                long id = data.getLongExtra("id", -1);
                Toast.makeText(getContext(), "Row inserted", Toast.LENGTH_SHORT).show();
                fetchTodoByIdAndInsert((int) id);

            } else if (requestCode == UPDATE_TODO_REQUEST_CODE) {

                boolean isDeleted = data.getBooleanExtra("isDeleted", false);
                int number = data.getIntExtra("number", -1);
                if (isDeleted) {
                    Toast.makeText(getContext(), number + " rows deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), number + " rows updated", Toast.LENGTH_SHORT).show();
                }

                loadAllTodos();

            }


        } else {
            Toast.makeText(getContext(), "No action done by user", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void insertList(List<ToDo> todoList) {
        new AsyncTask<List<ToDo>, Void, Void>() {
            @Override
            protected Void doInBackground(List<ToDo>... params) {
                toDoDatabase.daoAccess().insertTodo(params[0]);

                return null;

            }

            @Override
            protected void onPostExecute(Void voids) {
                super.onPostExecute(voids);
            }
        }.execute(todoList);

    }

    private void checkIfAppLaunchedFirstTime() {
        final String PREFS_NAME = "SharedPrefs";

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("firstTime", true)) {
            settings.edit().putBoolean("firstTime", false).apply();
            //buildDummyTodos();
        }
    }

    @Override
    public void sendCode(int code) {
        int value = code;
        if (value == 1) {
            loadAllTodos();
        } else if (value == 2) {
            loadAllTodos();
        } else if (value == 3) {
            loadAllTodos();
        }
    }
}