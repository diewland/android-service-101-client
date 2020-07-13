package com.diewland.serviceclient101

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    val TAG = "OTA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // (1) service

        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val btnStatus = findViewById<Button>(R.id.btn_status)

        val myServiceIntent = Intent()
        val clzName1 = "com.diewland.service101.MyService"
        myServiceIntent.component = ComponentName("com.diewland.service101", clzName1)

        btnStart.setOnClickListener {
            // TODO handle duplicate start
            // Intent(this, MyService::class.java).also { startService(it) }
            startService(myServiceIntent)
        }
        btnStop.setOnClickListener {
            // Intent(this, MyService::class.java).also { stopService(it) }
            stopService(myServiceIntent)
        }
        btnStatus.setOnClickListener {
            val status = isMyServiceRunning(clzName1)
            Log.d(TAG, status.toString())
        }

        // (2) intent service

        val myIntentServiceIntent = Intent()
        val clzName2 = "com.diewland.service101.MyIntentService"
        myIntentServiceIntent.component = ComponentName("com.diewland.service101", clzName2)

        val btnIsStart = findViewById<Button>(R.id.btn_is_start)
        val btnIsStop = findViewById<Button>(R.id.btn_is_stop)
        val btnIsStatus = findViewById<Button>(R.id.btn_is_status)

        btnIsStart.setOnClickListener {
            // TODO prevent duplicate start
            startService(myIntentServiceIntent)
        }
        btnIsStop.setOnClickListener {
            stopService(myIntentServiceIntent)
        }
        btnIsStatus.setOnClickListener {
            val status = isMyServiceRunning(clzName2)
            Log.d(TAG, status.toString())
        }

        // (3) bound service

        val msgServiceIntent = Intent()
        val clzName3 = "com.diewland.service101.MessengerService"
        msgServiceIntent.component = ComponentName("com.diewland.service101", clzName3)

        val MSG_SAY_HELLO = 1

        /** Messenger for communicating with the service.  */
        var mService: Messenger? = null

        /** Flag indicating whether we have called bind on the service.  */
        var bound: Boolean = false

        /**
         * Class for interacting with the main interface of the service.
         */
        val mConnection = object : ServiceConnection {

            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                // This is called when the connection with the service has been
                // established, giving us the object we can use to
                // interact with the service.  We are communicating with the
                // service using a Messenger, so here we get a client-side
                // representation of that from the raw IBinder object.
                mService = Messenger(service)
                bound = true
            }

            override fun onServiceDisconnected(className: ComponentName) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                mService = null
                bound = false
            }
        }

        fun sayHello() {
            if (!bound) return
            // Create and send a message to the service, using a supported 'what' value
            val msg: Message = Message.obtain(null, MSG_SAY_HELLO, 0, 0)
            try {
                mService?.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        val btnBStart = findViewById<Button>(R.id.btn_b_start)
        val btnBStop = findViewById<Button>(R.id.btn_b_stop)
        val btnBHi = findViewById<Button>(R.id.btn_b_hi)
        val btnBStatus = findViewById<Button>(R.id.btn_b_status)

        btnBStart.setOnClickListener {
            // Bind to the service
            bindService(msgServiceIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
        btnBStop.setOnClickListener {
            if (bound) {
                unbindService(mConnection)
                bound = false
            }
        }
        btnBHi.setOnClickListener {
            sayHello()
        }
        btnBStatus.setOnClickListener {
            val status = isMyServiceRunning(clzName3)
            Log.d(TAG, status.toString())
        }

    }

    @Suppress("DEPRECATION")
    private fun isMyServiceRunning(clzName: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (clzName == service.service.className) {
                return true
            }
        }
        return false
    }

}
