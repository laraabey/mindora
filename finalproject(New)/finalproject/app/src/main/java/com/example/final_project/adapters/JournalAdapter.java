package com.example.final_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.JournalDetailActivity;
import com.example.final_project.R;
import com.example.final_project.data.Journal;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journals;

    public JournalAdapter(Context context, List<Journal> journals) {
        this.context = context;
        this.journals = journals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_journal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal = journals.get(position);
        holder.tvItemDate.setText(journal.getDate());
        holder.tvItemTitle.setText(journal.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JournalDetailActivity.class);
            intent.putExtra("journalId", journal.getId());
            intent.putExtra("title", journal.getTitle());
            intent.putExtra("content", journal.getContent()); // changed here
            intent.putExtra("date", journal.getDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    public void updateData(List<Journal> newJournals) {
        this.journals = newJournals;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemDate, tvItemTitle;

        ViewHolder(View itemView) {
            super(itemView);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
        }
    }
}
