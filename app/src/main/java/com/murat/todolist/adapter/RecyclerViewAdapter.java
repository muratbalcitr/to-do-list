package com.murat.todolist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.murat.todolist.R;
import com.murat.todolist.interfaces.ClickListener;
import com.murat.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<ToDo> todoList;
    private List<ToDo> filterList;
    private ClickListener clickListener;
    Context mContext;
    private int layoutId;

    public RecyclerViewAdapter(Context context, List<ToDo> list) {
        todoList = list;
        filterList = list;
        this.mContext = context;
    }

    private int getLayoutIdForPosition(int position) {
        return layoutId;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        ToDo todo = filterList.get(position);
        if (todo.getCategory().equals(mContext.getResources().getString(R.string.important)))
            holder.txtCategory.setTextColor(Color.RED);
        else if (todo.getCategory().equals(mContext.getResources().getString(R.string.Normal)))
            holder.txtCategory.setTextColor(mContext.getResources().getColor(R.color.normal));
        else if (todo.getCategory().equals(mContext.getResources().getString(R.string.Personality)))
            holder.txtCategory.setTextColor(Color.GREEN);
        else if (todo.getCategory().equals(mContext.getResources().getString(R.string.Target)))
            holder.txtCategory.setTextColor(Color.BLUE);
        else
            holder.txtCategory.setTextColor(mContext.getResources().getColor(R.color.normal));


        holder.txtName.setText(todo.name);
        holder.txtNo.setText("#" + todo.deadline);
        holder.txtDesc.setText(todo.desc);
        holder.txtCategory.setText(todo.category);


    }

    @Override
    public int getItemCount() {
        return filterList == null ? 0 : filterList.size();
    }


    public void updateTodoList(List<ToDo> data) {
        todoList.clear();
        todoList.addAll(data);
        notifyDataSetChanged();
    }

    public void addRow(ToDo data) {
        todoList.add(data);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                try {

                    String searcString = constraint.toString();
                    Log.d("search", searcString);
                    if (searcString.isEmpty())
                        filterList = todoList;
                    else {
                        ArrayList<ToDo> tempFilterList = new ArrayList<>();
                        for (ToDo model : todoList) {
                            if (model.getName().contains(searcString) || model.getDesc().contains(searcString)) {
                                tempFilterList.add(model);
                            }
                        }
                        filterList = tempFilterList;
                    }
                } catch (IndexOutOfBoundsException index) {
                    index.getMessage();
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;


            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (List<ToDo>) filterResults.values;
                RecyclerViewAdapter.this.notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtNo;
        public TextView txtDesc;
        public TextView txtCategory;
        public CardView cardView;
        public CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);

            txtNo = view.findViewById(R.id.txtNo);
            txtName = view.findViewById(R.id.txtName);
            txtDesc = view.findViewById(R.id.txtDesc);
            txtCategory = view.findViewById(R.id.txtCategory);
            cardView = view.findViewById(R.id.cardView);
            checkBox = view.findViewById(R.id.checkBox);
        }


    }
}
