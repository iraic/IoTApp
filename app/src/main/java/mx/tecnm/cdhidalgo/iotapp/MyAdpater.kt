package mx.tecnm.cdhidalgo.iotapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//el constructor de la clase MyAdpater recibe una matriz dataset con los datos de los sensores y un listener de tipo ItemListener con las acciones a realizar
class MyAdpater(private val dataset: Array<Array<String?>>?, private val listener: ItemListener):
    RecyclerView.Adapter<MyAdpater.ViewHolder>() {
        //la clase ViewHolder recibe una vista y un listener de tipo ItemListener
        //el ViewHolder es el encargado de manejar los elementos de la vista
        class ViewHolder(v: View, listener: ItemListener): RecyclerView.ViewHolder(v) {
            var tvItemId: TextView
            var tvItemType: TextView
            var tvItemValue: TextView
            var tvItemName: TextView
            var tvItemDate: TextView
            var btnItemEdit: Button
            var btnItemDelete: Button
            init {
                tvItemId = v.findViewById(R.id.tvItemId)
                tvItemType = v.findViewById(R.id.tvItemType)
                tvItemValue = v.findViewById(R.id.tvItemValue)
                tvItemName = v.findViewById(R.id.tvItemName)
                tvItemDate = v.findViewById(R.id.tvItemDate)
                btnItemEdit = v.findViewById(R.id.btnItemEdit)
                btnItemDelete = v.findViewById(R.id.btnItemDelete)

                v.setOnClickListener { view -> listener.onClick(view, adapterPosition) }
                btnItemEdit.setOnClickListener { view -> listener.onEdit(view, adapterPosition) }
                btnItemDelete.setOnClickListener { view -> listener.onDel(view, adapterPosition) }
            }
        }

    //la funcion onCreateViewHolder crea una nueva vista a partir del layout item_sensor
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sensor, parent, false)
        return ViewHolder(v, listener)
    }

    //la funcion getItemCount regresa el numero de elementos en el dataset
    override fun getItemCount(): Int {
        return dataset!!.size
    }

    //la funcion onBindViewHolder asigna los valores de los elementos del dataset a los elementos de la vista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItemId.text = dataset!![position][0]
        holder.tvItemName.text = dataset[position][1]
        holder.tvItemType.text = dataset[position][2]
        holder.tvItemValue.text = dataset[position][3]
        holder.tvItemDate.text = dataset[position][4]
    }
}




