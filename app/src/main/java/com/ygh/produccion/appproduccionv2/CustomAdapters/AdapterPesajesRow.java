package com.ygh.produccion.appproduccionv2.CustomAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ygh.produccion.appproduccionv2.R;
import com.ygh.produccion.appproduccionv2.pojos.StockMove;

import java.text.NumberFormat;
import java.util.List;

public class AdapterPesajesRow extends ArrayAdapter<StockMove> {
    private final static NumberFormat numberFormat = NumberFormat.getNumberInstance();

    private TextView lblNombreInsumo;
    private TextView lblProgramadoRow;
    private TextView lblRealRow;

    private TextView lblInsumoTitulo;
    private TextView lblProgramadoPesaje;
    private TextView lblRealLabel;

    private LinearLayout mainLayout;

    private Context currentContext;
    private List<StockMove> items;

    public AdapterPesajesRow(Context context, int textViewResourceId, List<StockMove> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.currentContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);

        if(v == null) {
            LayoutInflater vi = (LayoutInflater)currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_insumos_produccion, null);
        }

        StockMove s = items.get(position);
        if(s != null) {
            lblNombreInsumo = (TextView)v.findViewById(R.id.lblNombreInsumo);
            lblProgramadoRow = (TextView)v.findViewById(R.id.lblProgramadoRow);
            lblRealRow = (TextView)v.findViewById(R.id.lblRealRow);

            lblRealLabel = (TextView)v.findViewById(R.id.lblRealLabel);
            lblInsumoTitulo = (TextView)v.findViewById(R.id.lblInsumoTitulo);
            lblProgramadoPesaje = (TextView)v.findViewById(R.id.lblProgramadoPesaje);

            mainLayout = (LinearLayout)v.findViewById(R.id.mainLayoutOrdenes);

            lblNombreInsumo.setText(s.getProductId() + "");
          //  lblProgramadoRow.setText(intFormat.format(s.getQtyProgramada()));
          //  lblRealRow.setText(intFormat.format(s.getQtyPesada()));
            lblProgramadoRow.setText(numberFormat.format(s.getQtyProgramada()));
            lblRealRow.setText(numberFormat.format(s.getQtyPesada()));
            
            if(s.getInsumoActual() != null && s.getInsumoActual()) {
                v.setBackgroundColor(Color.YELLOW);
                lblNombreInsumo.setTypeface(null, Typeface.BOLD);
                lblProgramadoRow.setTypeface(null, Typeface.BOLD);
                lblRealRow.setTypeface(null, Typeface.BOLD);
                lblRealLabel.setTypeface(null, Typeface.BOLD);
                lblInsumoTitulo.setTypeface(null, Typeface.BOLD);
                lblProgramadoPesaje.setTypeface(null, Typeface.BOLD);
            }

            if(s.getQtyPesada() > 0.0) {
                v.setBackgroundColor(Color.GREEN);
            }
        }
        return v;
    }
}
