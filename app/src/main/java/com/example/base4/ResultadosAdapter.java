package com.example.base4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.base4.modelo.Resultados;
import java.text.DecimalFormat;
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
        holder.tratamientoTextView.setText(resultado.getTratamiento()); // Mostrar el tratamiento
        holder.dosisTextView.setText(resultado.getDosis()); // Mostrar la dosis

        // Cargar la imagen de manera asincrónica
        new LoadImageTask(holder.imagenImageView).execute(resultado.getImagen());
    }

    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        public TextView textIdResultado;
        public TextView enfermedadTextView;
        public TextView accuracyTextView;
        public TextView fechaHoraTextView;
        public TextView tratamientoTextView; // Nuevo TextView para el tratamiento
        public TextView dosisTextView; // Nuevo TextView para la dosis
        public ImageView imagenImageView;

        public ResultadoViewHolder(View view) {
            super(view);
            textIdResultado = view.findViewById(R.id.textIdResultado);
            enfermedadTextView = view.findViewById(R.id.textEnfermedadResultado);
            accuracyTextView = view.findViewById(R.id.textAccuracyResultado);
            fechaHoraTextView = view.findViewById(R.id.textFechaHoraResultado);
            tratamientoTextView = view.findViewById(R.id.textTratamientoResultado);
            dosisTextView = view.findViewById(R.id.textDosisResultado); // Referencia al nuevo TextView
            imagenImageView = view.findViewById(R.id.imagenResultado);
        }
    }

    // Tarea asincrónica para cargar la imagen
    private static class LoadImageTask extends AsyncTask<byte[], Void, Bitmap> {
        private final ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            byte[] imagenBytes = params[0];
            return BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
