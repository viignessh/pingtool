package com.example.pingtool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.os.AsyncTask
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import android.widget.EditText
import android.widget.TextView
import android.content.Intent
import java.net.ConnectException
import java.io.Serializable
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ipEditText:EditText=findViewById(R.id.ipEditText)
        val pingButton:Button=findViewById(R.id.pingButton)
        pingButton.setOnClickListener {
            val ipAddress=ipEditText.text.toString()
            PingTask{results,durations,pingStatistics ->
                val resultTextView: TextView=findViewById(R.id.resultTextView)
                val intent = Intent(this@MainActivity,ResultActivity::class.java)
                intent.putStringArrayListExtra("PING_RESULTS",ArrayList(results))
                intent.putExtra("PING_DURATIONS",durations.toLongArray())
                intent.putExtra("PING_STATISTICS",pingStatistics)
                startActivity(intent)
            }.execute(ipAddress)
        }
    }
    class PingTask(private val callback:(List<String>,List<Long>,PingStatistics) -> Unit) :
        AsyncTask<String,Void,Triple<List<String>,List<Long>,PingStatistics>>() {
        override fun doInBackground(vararg params:String?):Triple<List<String>,List<Long>,PingStatistics> {
            val ipAddress=params[0]
            val pingResults=mutableListOf<String>()
            val durations=mutableListOf<Long>()
            try{
                for(i in 1..5) {
                    val startTime=System.currentTimeMillis()
                    val socket=Socket()
                    try{
                        socket.connect(InetSocketAddress(ipAddress,80),5000)
                    } catch(connectException:ConnectException) {
                        pingResults.add("Error: Connection refused")
                        durations.add(-1)
                        continue
                    } catch(ioException:IOException) {
                        pingResults.add("Error: ${ioException.message}")
                        durations.add(-1)
                        continue
                    } finally {
                        socket.close()
                    }
                    val endTime=System.currentTimeMillis()
                    val duration=endTime-startTime
                    pingResults.add("Ping $ipAddress:$duration ms")
                    durations.add(duration)
                }
                val pingStatistics=PingStatistics(
                    durations.minOrNull() ?: -1,
                    durations.maxOrNull() ?: -1,
                    durations.average()
                )
                return Triple(pingResults,durations,pingStatistics)
            } catch (e: Exception) {
                return Triple(
                    listOf("Error: ${e.message}"),
                    emptyList(),
                    PingStatistics(-1, -1, -1.0)
                )
            }
        }
        override fun onPostExecute(result:Triple<List<String>,List<Long>,PingStatistics>) {
            callback(result.first, result.second, result.third)
        }
    }
    data class PingStatistics(val min: Long, val max: Long, val average: Double) : Serializable
}