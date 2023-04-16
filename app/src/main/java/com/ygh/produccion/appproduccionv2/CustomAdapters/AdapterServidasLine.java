package com.ygh.produccion.appproduccionv2.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ygh.produccion.appproduccionv2.R;
import com.ygh.produccion.appproduccionv2.pojos.RmsServidaLine;

import java.util.ArrayList;

public class AdapterServidasLine extends ArrayAdapter<RmsServidaLine> {
    private TextView txtIdServidas;
    private TextView txtQty;
    private TextView txtLote;

    private Context currentContext;
    private ArrayList<RmsServidaLine> items;

    public AdapterServidasLine(Context context, int textViewResourceId, ArrayList<RmsServidaLine> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.currentContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_servida_lines, null);
        }

        RmsServidaLine s = items.get(position);
        if(s != null) {
            txtIdServidas = (TextView)v.findViewById(R.id.txtLineID);
            txtQty = (TextView)v.findViewById(R.id.txtQty);
            txtLote = (TextView)v.findViewById(R.id.txtLote);

            txtIdServidas.setText(s.getId() + "");
            txtQty.setText(s.getQtyProgramada() + "");
            txtLote.setText(s.getLotRanchoTxt());
        }
        return v;
    }
}
