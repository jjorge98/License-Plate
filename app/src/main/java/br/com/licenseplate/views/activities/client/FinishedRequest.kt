package br.com.licenseplate.views.activities.client

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.licenseplate.R
import br.com.licenseplate.views.activities.MainActivity
import kotlinx.android.synthetic.main.activity_finished_request.*

class FinishedRequest : AppCompatActivity() {
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_request)

        val intent = this.intent
        id = intent.getIntExtra("id", 0)

        protocolNumber.text = id.toString()

        buttonFinishedRequest.setOnClickListener { backMain() }
    }

    override fun onBackPressed() {
        //Bloqueia o botão de voltar a tela anterior
    }

    private fun backMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
