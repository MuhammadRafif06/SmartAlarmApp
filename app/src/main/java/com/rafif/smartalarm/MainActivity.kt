package com.rafif.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafif.smartalarm.adapter.AlarmAdapter
import com.rafif.smartalarm.data.Alarm
import com.rafif.smartalarm.data.local.AlarmDB
import com.rafif.smartalarm.databinding.ActivityMainBinding
import com.rafif.smartalarm.databinding.ActivityRepeatingAlarmBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmAdapter: AlarmAdapter? = null

    private var alarmService: AlarmReceiver? = null

    private val db by lazy { AlarmDB(this) }

    override fun onResume() {
        super.onResume()

        db.alarmDao().getAlarm().observe(this){
            alarmAdapter?.setData(it)
            Log.i("GetAlarm", "setUpRecyclerView: with this data $it")
        }
        /*CoroutineScope(Dispatchers.IO).launch {
            val alarm = db.alarmDao().getAlarm()
            alarmAdapter?.setData(alarm)
            Log.i("GetAlarm", "SetupRecyclerView: with this date $alarm")
            withContext(Dispatchers.Main){
                alarmAdapter?.setData(alarm)
            }
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alarmService = AlarmReceiver()
        initView()
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        binding.apply {
            alarmAdapter = AlarmAdapter()
            rvReminderAlarm.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = alarmAdapter
            }
            swipeToDelete(rvReminderAlarm)
        }
    }

    private fun initView() {
        binding.apply {
            cvSetOneTimeAlarm.setOnClickListener{
                startActivity(Intent(this@MainActivity, OneTimeAlarmActivity::class.java))
            }

            cvSetRepeatingAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java ))
            }
        }
    }
    
    private fun swipeToDelete(recyclerView: RecyclerView){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = alarmAdapter?.ListAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                    deletedItem?.let { db.alarmDao().deleteAlarm(it) }
                    Log.i("DeleteAlarm", "OnSwiped: succes deleted alarm with $deletedItem")
                }

                deletedItem?.let { alarmService?.cancelAlarm(applicationContext, it.type) }

//                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }
            
        }).attachToRecyclerView(recyclerView)
    }
}
