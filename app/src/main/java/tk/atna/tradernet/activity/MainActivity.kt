package tk.atna.tradernet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tk.atna.tradernet.R
import tk.atna.tradernet.databinding.ActivityMainBinding
import tk.atna.tradernet.fragment.QuotesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // R.layout.activity_main


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        //
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, QuotesFragment())
                    .commitNow()
        }
    }
}