package com.westyorks.chargepoint.admin;

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

public class AdminChargepointAdapter extends RecyclerView.Adapter<AdminChargepointAdapter.ChargepointViewHolder> {
    private List<Chargepoint> chargepoints = new ArrayList<>();
    private OnItemClickListener listener;

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
    }

    @Override
    public int getItemCount() {
        return chargepoints.size();
    }

    public void setChargepoints(List<Chargepoint> chargepoints) {
        this.chargepoints = chargepoints;
        notifyDataSetChanged();
    }

    public Chargepoint getChargepointAt(int position) {
        return chargepoints.get(position);
    }

    class ChargepointViewHolder extends RecyclerView.ViewHolder {
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

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(chargepoints.get(position));
                }
            });
        }

        void bind(Chargepoint chargepoint) {
            nameTextView.setText(chargepoint.getName());
            locationTextView.setText(String.format("%s, %s, %s", 
                chargepoint.getTown(), 
                chargepoint.getCounty(), 
                chargepoint.getPostcode()));
            statusTextView.setText(chargepoint.getChargerStatus());
            typeTextView.setText(chargepoint.getChargerType());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Chargepoint chargepoint);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}