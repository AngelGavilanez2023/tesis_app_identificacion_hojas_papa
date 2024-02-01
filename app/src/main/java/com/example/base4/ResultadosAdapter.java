package com.example.base4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.base4.modelo.Resultados;

import java.text.DecimalFormat;
import java.util.List;
import com.example.base4.ResultadosAdapter;

import java.util.List;

public class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ResultadoViewHolder> {

    private List<Resultados> listaResultados;

    public ResultadosAdapter(List<Resultados> listaResultados) {
        this.listaResultados = listaResultados;
    }

    @Override
    public ResultadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resultado_item, parent, false);
        return new ResultadoViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return listaResultados.size();
    }

    @Override
    public void onBindViewHolder(ResultadoViewHolder holder, int position) {
        Resultados resultado = listaResultados.get(position);

        // Formatear la precisión a dos decimales
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedPrecision = decimalFormat.format(Float.parseFloat(resultado.getAccuracy()));

        holder.textIdResultado.setText(String.valueOf(resultado.getId()));
        holder.enfermedadTextView.setText(resultado.getEnfermedad());
        holder.accuracyTextView.setText(formattedPrecision + "%"); // Usar la precisión formateada
        holder.fechaHoraTextView.setText(resultado.getFecha_hora());
    }



    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        public TextView textIdResultado;
        public TextView enfermedadTextView;
        public TextView accuracyTextView;
        public TextView fechaHoraTextView;

        public ResultadoViewHolder(View view) {
            super(view);
            //colocar id tambien
            textIdResultado = view.findViewById(R.id.textIdResultado);
            enfermedadTextView = view.findViewById(R.id.textEnfermedadResultado);
            accuracyTextView = view.findViewById(R.id.textAccuracyResultado);
            fechaHoraTextView = view.findViewById(R.id.textFechaHoraResultado);
        }
    }
}
