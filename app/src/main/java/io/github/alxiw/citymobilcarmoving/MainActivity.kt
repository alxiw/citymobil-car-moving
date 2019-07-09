package io.github.alxiw.citymobilcarmoving

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val area = DriveLayout(this)
        setContentView(area)
    }

}
