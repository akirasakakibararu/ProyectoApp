import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.pojos.Albaran
import java.time.LocalDate

class AlbaranesAdapter(
    private val albaranes: List<Albaran>,
    private val multiSelectMode: Boolean = false,  // Si es true, selección múltiple
    private val onItemClick: ((Albaran) -> Unit)? = null,  // Para modo simple
    private val onSelectionChanged: ((List<Albaran>) -> Unit)? = null  // Para modo múltiple
) : RecyclerView.Adapter<AlbaranesAdapter.AlbaranViewHolder>() {

    private val selectedItems = mutableSetOf<Albaran>()

    inner class AlbaranViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewId = itemView.findViewById<TextView>(R.id.textViewAlbaranId)
        val textViewFecha = itemView.findViewById<TextView>(R.id.textViewFechaAlbaran)
        val textViewEstado = itemView.findViewById<TextView>(R.id.textViewEstadoAlbaran)
        val textViewNif = itemView.findViewById<TextView>(R.id.textViewNifAlbaran)
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkboxSeleccion)  // Nuevo checkbox para múltiple selección
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbaranViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_albaran, parent, false)
        return AlbaranViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbaranViewHolder, position: Int) {
        val albaran = albaranes[position]

        holder.textViewId.text = albaran.idAlbaran.toString()
        val fecha = LocalDate.parse(albaran.fechaAlbaran.substring(0, 10))
        holder.textViewFecha.text = fecha.toString()
        holder.textViewEstado.text = albaran.estado
        holder.textViewNif.text = albaran.nif

        val backgroundColor = if (position % 2 == 0)
            holder.itemView.context.getColor(R.color.row_even)
        else
            holder.itemView.context.getColor(R.color.row_odd)
        holder.itemView.setBackgroundColor(backgroundColor)

        if (multiSelectMode) {
            // Mostrar checkbox y actualizar su estado
            holder.checkbox.visibility = View.VISIBLE
            holder.checkbox.isChecked = selectedItems.contains(albaran)

            // Click en item o checkbox alterna selección
            holder.itemView.setOnClickListener {
                toggleSelection(albaran)
                notifyItemChanged(position)
                onSelectionChanged?.invoke(selectedItems.toList())
            }

            holder.checkbox.setOnClickListener {
                toggleSelection(albaran)
                notifyItemChanged(position)
                onSelectionChanged?.invoke(selectedItems.toList())
            }
        } else {
            // Ocultar checkbox para modo simple
            holder.checkbox.visibility = View.GONE
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(albaran)
            }
        }
    }

    private fun toggleSelection(albaran: Albaran) {
        if (selectedItems.contains(albaran)) {
            selectedItems.remove(albaran)
        } else {
            selectedItems.add(albaran)
        }
    }

    override fun getItemCount() = albaranes.size
}
