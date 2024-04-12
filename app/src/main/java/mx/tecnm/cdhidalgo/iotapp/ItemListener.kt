package mx.tecnm.cdhidalgo.iotapp

import android.view.View

//Interfaz para los eventos de los botones de la lista en el RecyclerView
interface ItemListener {
    //Evento de clic en el item
    fun onClick(v: View?, position: Int)
    //Evento de clic en el botón de editar
    fun onEdit(v: View?, position: Int)
    //Evento de clic en el botón de eliminar
    fun onDel(v: View?, position: Int)
}