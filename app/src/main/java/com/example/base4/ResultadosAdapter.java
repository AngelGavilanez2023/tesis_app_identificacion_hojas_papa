package com.example.base4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.base4.DB.DBmanager;
import com.example.base4.modelo.Resultados;
import java.text.DecimalFormat;
import java.util.List;
import android.app.AlertDialog;
import android.widget.Toast;


public class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ResultadoViewHolder> {

    private List<Resultados> listaResultados;
    private Context context;

    public ResultadosAdapter(List<Resultados> listaResultados, Context context) {
        this.listaResultados = listaResultados;
        this.context = context;
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
        holder.aplicarTextView.setText(resultado.getAplicar()); // Mostrar el aplicar
        // Asigna el clic del botón al método eliminarResultado
        holder.btnEliminarResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminarResultado(holder.getAdapterPosition());  // Usa getAdapterPosition()
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

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
        public TextView aplicarTextView; // Nuevo TextView para el aplicar
        public ImageView imagenImageView;
        public Button btnEliminarResultado;

        public ResultadoViewHolder(View view) {
            super(view);
            textIdResultado = view.findViewById(R.id.textIdResultado);
            enfermedadTextView = view.findViewById(R.id.textEnfermedadResultado);
            accuracyTextView = view.findViewById(R.id.textAccuracyResultado);
            fechaHoraTextView = view.findViewById(R.id.textFechaHoraResultado);
            tratamientoTextView = view.findViewById(R.id.textTratamientoResultado);
            dosisTextView = view.findViewById(R.id.textDosisResultado); // Referencia al nuevo TextView
            aplicarTextView = view.findViewById(R.id.textAplicarResultado);
            imagenImageView = view.findViewById(R.id.imagenResultado);
            btnEliminarResultado = view.findViewById(R.id.btnEliminarResultado);

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

    public List<Resultados> getListaResultados() {
        return listaResultados;
    }

    // Nuevo método para eliminar un resultado
// Nuevo método para eliminar un resultado
    private void eliminarResultado(int position) {
        // Obtén el resultado de la posición seleccionada
        Resultados resultado = listaResultados.get(position);

        // Crea un cuadro de diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar este resultado?");

        // Agrega botones al cuadro de diálogo
        builder.setPositiveButton("Sí", (dialog, which) -> {
            try {
                // Lógica para eliminar el resultado de la base de datos
                DBmanager dbManager = new DBmanager(context);
                dbManager.open();
                dbManager.eliminarResultadoDeBD(resultado.getId());
                dbManager.close();

                // Elimina el resultado de la lista y notifica al adaptador del cambio
                listaResultados.remove(position);
                notifyItemRemoved(position);

                // Muestra un Toast indicando que el resultado fue eliminado
                Toast.makeText(context, "Resultado eliminado", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();  // Imprime el stack trace para conocer la causa del error
                // Puedes mostrar un mensaje de error o realizar otras acciones según tus necesidades
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // No hacer nada si se selecciona "No"
        });

        // Muestra el cuadro de diálogo
        builder.show();
    }
}
