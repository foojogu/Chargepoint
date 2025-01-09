package com.westyorks.chargepoint.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.westyorks.chargepoint.R;
import com.westyorks.chargepoint.model.Chargepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChargepointAdapter extends RecyclerView.Adapter<ChargepointAdapter.ChargepointViewHolder> {
    private List<Chargepoint> chargepoints = new ArrayList<>();
    private List<Chargepoint> allChargepoints = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private boolean isReadOnly = false;

    @NonNull
    @Override
    public ChargepointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chargepoint, parent, false);
        return new ChargepointViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargepointViewHolder holder, int position) {
        Chargepoint current = chargepoints.get(position);
        holder.bind(current);

        if (!isReadOnly) {
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(current);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(current);
                    return true;
                }
                return false;
            });
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return chargepoints.size();
    }

    public void setChargepoints(List<Chargepoint> chargepoints) {
        this.allChargepoints = new ArrayList<>(chargepoints);
        this.chargepoints = new ArrayList<>(chargepoints);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        chargepoints.clear();
        if (query.isEmpty()) {
            chargepoints.addAll(allChargepoints);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (Chargepoint chargepoint : allChargepoints) {
                if (matches(chargepoint, lowerCaseQuery)) {
                    chargepoints.add(chargepoint);
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean matches(Chargepoint chargepoint, String query) {
        return chargepoint.getName().toLowerCase(Locale.getDefault()).contains(query) ||
               chargepoint.getTown().toLowerCase(Locale.getDefault()).contains(query) ||
               chargepoint.getCounty().toLowerCase(Locale.getDefault()).contains(query) ||
               chargepoint.getChargerType().toLowerCase(Locale.getDefault()).contains(query);
    }

    static class ChargepointViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView locationTextView;
        private TextView statusTextView;
        private TextView typeTextView;

        ChargepointViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
        }

        void bind(Chargepoint chargepoint) {
            nameTextView.setText(chargepoint.getName());
            locationTextView.setText(String.format("%s, %s", chargepoint.getTown(), chargepoint.getCounty()));
            statusTextView.setText(chargepoint.getChargerStatus());
            typeTextView.setText(chargepoint.getChargerType());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Chargepoint chargepoint);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Chargepoint chargepoint);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
    }
}
