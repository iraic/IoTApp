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

        btnNewCancel.setOnClickListener { finish() }

        if(intent.extras != null) {
            tvNewId.text = intent.extras?.getString("id")
            etNewName.setText(intent.extras?.getString("name"))
            etNewType.setText(intent.extras?.getString("type"))
            etNewValue.setText(intent.extras?.getString("value"))
            btnNewSave.setOnClickListener { saveChanges() }
        }else{
            btnNewSave.setOnClickListener { saveNew() }
        }
    }

    private fun saveChanges() {
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

        val url = Uri.parse(Config.URL + "sensors/" + tvNewId.text.toString())
            .buildUpon()
            .build().toString()

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

        val url = Uri.parse(Config.URL + "sensors")
            .buildUpon()
            .build().toString()

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