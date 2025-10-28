package com.example.final_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.data.Message;
import com.example.final_project.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> data = new ArrayList<>();

    public void submit(Message m) {
        data.add(m);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == Message.TYPE_OUT) {
            view = inflater.inflate(R.layout.item_message_out, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_message_in, parent, false);
        }
        return new MsgVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MsgVH) holder).bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MsgVH extends RecyclerView.ViewHolder {
        TextView tv;
        MsgVH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvMsg);
        }
        void bind(Message m) {
            tv.setText(m.getText());
        }
    }
}
