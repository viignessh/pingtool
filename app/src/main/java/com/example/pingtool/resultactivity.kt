package com.example.pingtool
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val resultsTextView:TextView=findViewById(R.id.resultsTextView)
        val minTextView:TextView=findViewById(R.id.minTextView)
        val maxTextView:TextView=findViewById(R.id.maxTextView)
        val averageTextView:TextView=findViewById(R.id.averageTextView)
        val backToHomeButton:Button=findViewById(R.id.backToHomeButton)
        val pingResults=intent.getStringArrayListExtra("PING_RESULTS")
        val durations=intent.getLongArrayExtra("PING_DURATIONS")
        val pingStatistics=intent.getSerializableExtra("PING_STATISTICS") as? MainActivity.PingStatistics
        resultsTextView.text=pingResults?.joinToString("\n") ?: "No ping results available"
        if (pingResults!=null&&durations!=null&&pingStatistics!=null) {
            minTextView.text="Min Duration: ${if (pingStatistics.min!=-1L) pingStatistics.min.toString() else "N/A"} ms"
            maxTextView.text="Max Duration: ${if (pingStatistics.max!=-1L) pingStatistics.max.toString() else "N/A"} ms"
            averageTextView.text="Average Duration: ${if (pingStatistics.average!=-1.0) "%.2f".format(pingStatistics.average) else "N/A"} ms"
        } else {
            minTextView.text="Min Duration: N/A"
            maxTextView.text="Max Duration: N/A"
            averageTextView.text="Average Duration: N/A"
        }
        backToHomeButton.setOnClickListener {
            val intent=Intent(this@ResultActivity,HomePage::class.java)
            startActivity(intent)
            finish()
        }

    }
}
