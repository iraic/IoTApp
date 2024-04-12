package mx.tecnm.cdhidalgo.iotapp

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity3 : AppCompatActivity() {
    private lateinit var tvNewId: TextView
    private lateinit var etNewName: EditText
    private lateinit var etNewType: EditText
    private lateinit var etNewValue: EditText
    private lateinit var btnNewCancel: Button
    private lateinit var btnNewSave: Button

    private lateinit var sesion: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        tvNewId = findViewById(R.id.tvNewId)
        etNewName = findViewById(R.id.etNewName)
        etNewType = findViewById(R.id.etNewType)
        etNewValue = findViewById(R.id.etNewValue)
        btnNewCancel = findViewById(R.id.btnNewCancel)
        btnNewSave = findViewById(R.id.btnNewSave)

        sesion = getSharedPreferences("sesion", 0)

        //al presionar el boton de cancelar se cierra la actividad y regresa a la anterior
        btnNewCancel.setOnClickListener { finish() }

        //si se reciben parametros se cargan en los campos de texto y se habilita el boton de guardar para actualizar
        if(intent.extras != null) {
            tvNewId.text = intent.extras?.getString("id")
            etNewName.setText(intent.extras?.getString("name"))
            etNewType.setText(intent.extras?.getString("type"))
            etNewValue.setText(intent.extras?.getString("value"))
            btnNewSave.setOnClickListener { saveChanges() }
        }else{
            //si no se reciben parametros se habilita el boton de guardar para agregar un nuevo sensor
            btnNewSave.setOnClickListener { saveNew() }
        }
    }

    //funcion para actualizar un sensor
    private fun saveChanges() {
        //se obtienen los valores de los campos de texto
        val name = etNewName.text.toString()
        val type = etNewType.text.toString()
        val value = etNewValue.text.toString()
        //si algun campo esta vacio no se hace nada
        if (name.isEmpty() || type.isEmpty() || value.isEmpty()) {
            return
        }
        //se crea un objeto JSON con los valores de los campos de texto que se deben enviar al API REST
        val body = JSONObject()
        body.put("name", name)
        body.put("type", type)
        body.put("value", value)

        //se crea la URL para hacer la peticion PUT al API REST agregando el id del sensor a actualizar tvNewId
        val url = Uri.parse(Config.URL + "sensors/" + tvNewId.text.toString())
            .buildUpon()
            .build().toString()
        //se crea la peticion con JsonObjectRequest para realizar el PUT con los datos de JSONObject body a enviar
        //el API REST regresa un JSON con los datos del senson actualizado, por lo que se puede usar JsonObjectRequest o StringRequest
        //sin embargo la forma de enviar los datos cambia en el StringRequest, ver el ejemplo del login
        val peticion = object: JsonObjectRequest(Request.Method.PUT, url, body, {
            response ->
                Toast.makeText(this, "Guardado:"+response.toString(), Toast.LENGTH_LONG).show()
                finish()
        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    //funcion para agregar un nuevo sensor
    private fun saveNew() {
        val name = etNewName.text.toString()
        val type = etNewType.text.toString()
        val value = etNewValue.text.toString()

        if (name.isEmpty() || type.isEmpty() || value.isEmpty()) {
            return
        }

        val body = JSONObject()
        body.put("name", name)
        body.put("type", type)
        body.put("value", value)
        //no lleva id dado que se creara un nuevo registro y el id se genera automaticamente
        val url = Uri.parse(Config.URL + "sensors")
            .buildUpon()
            .build().toString()
        //se crear la peticion con POST para indicar al API que es un registro nuevo
        val peticion = object: JsonObjectRequest(Request.Method.POST, url, body, {
                response ->
                    Toast.makeText(this, "Guardado:"+response.toString(), Toast.LENGTH_LONG).show()
                    finish()
        }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }
}