package mx.tecnm.cdhidalgo.iotapp

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest

//se le asigna la interfaz "ItemListener" a la actividad actual para que pueda manejar los eventos de click en el recycler view
class MainActivity2 : AppCompatActivity(), ItemListener {
    private lateinit var rvList: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var btnRfresh: Button

    private lateinit var sesion: SharedPreferences
    //matriz de datos de string para guardar los datos de los sensores
    private lateinit var lista: Array<Array<String?>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        rvList = findViewById(R.id.rvList)
        btnAdd = findViewById(R.id.btnAdd)
        btnRfresh = findViewById(R.id.btnRefresh)

        sesion = getSharedPreferences("sesion", 0)
        //si el soporte de la barra de accion no es nulo se le asigna un titulo
        // con el nombre de usuario guardado en el archivo de preferencias durante el login
        //revisar res/values/themes/themes.xml que no tenga la propiedad de NoActionBar para que se muestre
        if(supportActionBar != null)
            supportActionBar!!.title = "Sensores - " + sesion.getString("username", "")
        //se asigna un tamaño fijo al recycler view
        rvList.setHasFixedSize(true)
        //se asigna un animador por defecto al recycler view
        rvList.itemAnimator = DefaultItemAnimator()
        //se asigna un layout manager al recycler view para que se muestre como una lista
        rvList.layoutManager = LinearLayoutManager(this)
        //se llena el recycler view con los datos de los sensores
        fill()
        //se asigna un listener al boton de agregar para que al dar click se abra la actividad de agregar sensor
        btnAdd.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }
        //se asigna un listener al boton de refrescar para que al dar click se actualice la lista de sensores
        btnRfresh.setOnClickListener {
            fill()
        }
    }

    //metodo que se ejecuta al reanudar la actividad para que se actualice la lista de sensores
    override fun onResume() {
        super.onResume()
        fill()
    }

    //metodo para llenar la lista de sensores
    private fun fill(){
        //se crea la url para obtener los sensores
        val url = Uri.parse(Config.URL + "sensors")
            .buildUpon()
            .build().toString()
        //se crea una peticion de tipo JsonObjectRequest para obtener los sensores con GET
        val peticion = object: JsonObjectRequest(Request.Method.GET, url, null, {
            //si la peticion es exitosa se obtiene la respuesta del servidor
            response ->
                //los datos de los sensores vienen en un arreglo en la propiedad data de la respuesta ya que estan paginados por API REST
                val data = response.getJSONArray("data")
                //se crea una matriz de datos de string con el tamaño del arreglo de datos obtenidos de la respuesta anterior
                lista = Array(data.length()){arrayOfNulls<String>(5)}
                //se recorre el arreglo de datos obtenidos de la respuesta y se guardan en la matriz de datos de string
                for(i in 0 until data.length()){
                    lista[i][0]  = data.getJSONObject(i).getString("id")
                    lista[i][1]  = data.getJSONObject(i).getString("name")
                    lista[i][2]  = data.getJSONObject(i).getString("type")
                    lista[i][3]  = data.getJSONObject(i).getString("value")
                    lista[i][4]  = data.getJSONObject(i).getString("date")
                }
                //se asigna el adaptador al recycler view con los datos de los sensores y el listener de la actividad actual que implementa la interfaz "ItemListener"
                rvList.adapter = MyAdpater(lista, this)
        }, { error ->
                //si la peticion no es exitosa se muestra un mensaje de error
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            //se envián las cabeceras de la peticion con el token de autenticacion que se guardo en el archivo de preferencias durante el login
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    //los siguientes metodos son los que se implementan de la interfaz "ItemListener" para los eventos de click en el recycler view
    override fun onClick(v: View?, position: Int) {
        //se muestra un mensaje con la posicion y el id del sensor al dar click en el recycler view
        Toast.makeText(this, "Click en posiscion:$position, id:${lista[position][0]}", Toast.LENGTH_SHORT).show()
    }

    override fun onEdit(v: View?, position: Int) {
        //se crea un intento para abrir la actividad de agregar sensor con los datos del sensor seleccionado
        //se envian los datos del sensor seleccionado a la actividad de agregar sensor para que puedan editarse
        val intent = Intent(this, MainActivity3::class.java)
        intent.putExtra("id", lista[position][0])
        intent.putExtra("name", lista[position][1])
        intent.putExtra("type", lista[position][2])
        intent.putExtra("value", lista[position][3])
        startActivity(intent)
    }

    override fun onDel(v: View?, position: Int) {
        //se muestra un dialogo de alerta para confirmar la eliminacion del sensor seleccionado
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Desea eliminar el sensor ${lista[position][1]}")
            .setPositiveButton("Si"){dialog, which -> //si se da click en si se elimina el sensor seleccionado
                //se crea la url para eliminar el sensor seleccionado
                val url = Uri.parse(Config.URL + "sensors/" + lista[position][0])
                    .buildUpon()
                    .build().toString()
                //se crea una peticion de tipo StringRequest para eliminar el sensor seleccionado con DELETE
                //el API REST devuelve los datos del sensor eliminado por lo que se puede usar StringRequest o JsonObjectRequest
                val peticion = object: StringRequest(Request.Method.DELETE, url, {
                    //si la peticion es exitosa se actualiza la lista de sensores
                    response -> fill()
                }, { error ->
                    //si la peticion no es exitosa se muestra un mensaje de error
                    Log.d("ERROR", error.toString())
                    //se actualiza la lista de sensores por si otro cliente ya lo habia eliminado
                    fill()
                }){
                    //se envián las cabeceras de la peticion con el token de autenticacion que se guardo en el archivo de preferencias durante el login
                    override fun getHeaders(): Map<String, String>{
                        val body: MutableMap<String, String> = HashMap()
                        body["Authorization"] = sesion.getString("jwt", "").toString()
                        return body
                    }
                }
                MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
            }
            .setNegativeButton("No", null) //si se da click en no no se hace nada
            .show()
    }
}