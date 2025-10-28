package com.example.final_project.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.final_project.R;

import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> days;
    private int currentDay;
    private LayoutInflater inflater;

    public CalendarAdapter(Context context, List<Integer> days, int currentDay) {
        this.context = context;
        this.days = days;
        this.currentDay = currentDay;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return days.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_day_item, parent, false);
        }

        TextView dayText = convertView.findViewById(R.id.dayText);
        int day = days.get(position);
        dayText.setText(String.valueOf(day));

        if (day == currentDay) {
           convertView.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_orange));
            dayText.setTextColor(Color.WHITE);
        } else {
            convertView.setBackground(null);
            dayText.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
