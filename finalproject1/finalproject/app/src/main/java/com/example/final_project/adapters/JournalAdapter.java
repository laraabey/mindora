package com.example.final_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.ViewJournalActivity;
import com.example.final_project.data.Journal;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private Context context;
    private List<Journal> journalList;

    public JournalAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    public void updateData(List<Journal> newJournalList) {
        this.journalList = newJournalList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        Journal journal = journalList.get(position);

        holder.tvTitle.setText(journal.getTitle());

        String preview = journal.getContent();
        if (preview != null && preview.length() > 100) {
            preview = preview.substring(0, 100) + "...";
        }
        holder.tvPreview.setText(preview);

        holder.tvDate.setText(journal.getDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewJournalActivity.class);
            intent.putExtra("journalId", journal.getId());
            intent.putExtra("title", journal.getTitle());
            intent.putExtra("content", journal.getContent());
            intent.putExtra("date", journal.getDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return journalList != null ? journalList.size() : 0;
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPreview, tvDate;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJournalTitle);
            tvPreview = itemView.findViewById(R.id.tvJournalPreview);
            tvDate = itemView.findViewById(R.id.tvJournalDate);
        }
    }
}