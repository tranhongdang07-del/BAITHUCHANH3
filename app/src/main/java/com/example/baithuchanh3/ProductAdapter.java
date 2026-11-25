package com.example.baithuchanh3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private int resource;
    private List<Product> products;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.products = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvItemName);
            holder.tvPrice = convertView.findViewById(R.id.tvItemPrice);
            holder.tvCategory = convertView.findViewById(R.id.tvItemCategory);
            holder.tvQuantity = convertView.findViewById(R.id.tvItemQuantity);
            holder.tvWarningTag = convertView.findViewById(R.id.tvItemWarningTag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);
        if (product != null) {
            holder.tvName.setText(product.getName());
            holder.tvPrice.setText(formatter.format(product.getPrice()));
            holder.tvCategory.setText(product.getCategory());
            holder.tvQuantity.setText(String.valueOf(product.getQuantity()));

            if (product.isLowStock()) {
                holder.tvWarningTag.setVisibility(View.VISIBLE);
                holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.red_warning));
            } else {
                holder.tvWarningTag.setVisibility(View.GONE);
                holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvCategory;
        TextView tvQuantity;
        TextView tvWarningTag;
    }
}
