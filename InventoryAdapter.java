package com.zybooks.warehouse.ux.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.warehouse.R;
import com.zybooks.warehouse.data.model.InventoryItem;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> itemList;
    private OnModifyClickListener modifyListener;

    public interface OnModifyClickListener {
        void onModify(InventoryItem item);
    }


    public InventoryAdapter(List<InventoryItem> itemList, OnModifyClickListener modifyListener) {
        this.itemList = itemList;
        this.modifyListener = modifyListener;
    }


    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_card, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = itemList.get(position);

        holder.itemName.setText(item.name);
        holder.itemId.setText("SKU: " + item.sku);
        holder.itemQty.setText("Qty: " + item.quantity);
        holder.itemLocation.setText(item.location);

        holder.itemView.setOnClickListener(v -> modifyListener.onModify(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemId, itemQty, itemLocation;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemId = itemView.findViewById(R.id.itemId);
            itemQty = itemView.findViewById(R.id.itemQty);
            itemLocation = itemView.findViewById(R.id.itemLocation);
        }
    }
}
