package com.example.initialprojectupload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class KeranjangAdapter
        extends ArrayAdapter<KeranjangItem> {

    Context context;
    ArrayList<KeranjangItem> data;

    public KeranjangAdapter(
            Context context,
            ArrayList<KeranjangItem> data
    ) {
        super(
                context,
                0,
                data
        );

        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        if(convertView == null){

            convertView =
                    LayoutInflater.from(context)
                            .inflate(
                                    R.layout.item_keranjang,
                                    parent,
                                    false
                            );
        }

        TextView tvNamaProduk =
                convertView.findViewById(
                        R.id.tvNamaProduk
                );

        TextView tvQty =
                convertView.findViewById(
                        R.id.tvQty
                );

        Button btnMinus =
                convertView.findViewById(
                        R.id.btnMinus
                );

        Button btnPlus =
                convertView.findViewById(
                        R.id.btnPlus
                );

        KeranjangItem item =
                data.get(position);

        tvNamaProduk.setText(
                item.namaProduk
        );

        tvQty.setText(
                String.valueOf(
                        item.qty
                )
        );

        btnPlus.setOnClickListener(v -> {

            item.qty++;

            notifyDataSetChanged();

        });

        btnMinus.setOnClickListener(v -> {

            if(item.qty > 1){

                item.qty--;

                notifyDataSetChanged();
            }

        });

        return convertView;
    }
}