package com.ygh.produccion.appproduccionv2.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ygh.produccion.appproduccionv2.R;
import com.ygh.produccion.appproduccionv2.pojos.OrdenesProduccionInfo;

import java.util.List;

public class AdapterOrdenesRow extends ArrayAdapter<OrdenesProduccionInfo> {
    private TextView lblOrdenBachada;
    private TextView lblTipoOrden;
    private TextView lblOrdenFormula;
    private TextView lblOrdenCantidad;
    private TextView lblOrdenNombre;

    private Context currentContext;
    private List<OrdenesProduccionInfo> items;

    public AdapterOrdenesRow(Context context, int textViewResourceId, List<OrdenesProduccionInfo> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.currentContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater) currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_ordenes_produccion, null);
        }

        OrdenesProduccionInfo s = items.get(position);
        if(s != null) {
            lblOrdenBachada = (TextView)v.findViewById(R.id.lblRealLabel);
            lblTipoOrden = (TextView)v.findViewById(R.id.lblTipoOrden);
            lblOrdenFormula = (TextView)v.findViewById(R.id.lblOrdenFormula);
            lblOrdenCantidad = (TextView)v.findViewById(R.id.lblOrdenCantidad);
            lblOrdenNombre = (TextView)v.findViewById(R.id.lblOrdenNombre);

            lblOrdenBachada.setText(s.getBachada() + "");
            if(!s.getOrdenFabricacion().equals("")) {
                lblTipoOrden.setText("Órden de Fabricación");
                lblOrdenNombre.setText(s.getOrdenFabricacion());
            }
            else {
                lblTipoOrden.setText("Órden de Traslado");
                lblOrdenNombre.setText(s.getOrdenTraslado());
            }

            lblOrdenFormula.setText(s.getFormula());
            lblOrdenCantidad.setText(s.getCantidad() + "");
        }
        return v;
    }
}
