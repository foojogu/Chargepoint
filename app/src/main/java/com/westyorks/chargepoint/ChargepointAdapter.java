package com.westyorks.chargepoint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.westyorks.chargepoint.model.Chargepoint;
import java.util.ArrayList;
import java.util.List;

public class ChargepointAdapter extends RecyclerView.Adapter<ChargepointAdapter.ChargepointViewHolder> {
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
        holder.tvName.setText(current.getName());
        // Combine town, county, and postcode for the address
        String address = String.format("%s, %s, %s", 
            current.getTown(), 
            current.getCounty(), 
            current.getPostcode());
        holder.tvAddress.setText(address);
        holder.tvChargerType.setText(current.getChargerType());
    }

    @Override
    public int getItemCount() {
        return chargepoints.size();
    }

    public void setChargepoints(List<Chargepoint> chargepoints) {
        this.chargepoints = chargepoints;
        notifyDataSetChanged();
    }

    class ChargepointViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvAddress;
        private TextView tvChargerType;

        ChargepointViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvChargerType = itemView.findViewById(R.id.tvChargerType);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(chargepoints.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Chargepoint chargepoint);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
} 