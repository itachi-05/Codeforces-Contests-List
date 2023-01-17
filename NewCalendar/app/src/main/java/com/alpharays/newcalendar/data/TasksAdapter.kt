package com.alpharays.newcalendar.data

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alpharays.newcalendar.R
import com.alpharays.newcalendar.models.crudviewmodel.CreateTaskViewModel
import com.alpharays.newcalendar.models.crudviewmodel.ReadTaskViewModel
import com.alpharays.newcalendar.models.crudviewmodel.UpdateTaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.Observer


class TasksAdapter(private val context: Context, private var userTaskList: ArrayList<UserTask>) :
    RecyclerView.Adapter<TasksAdapter.MyViewHolder>() {

    private val updateViewModel: UpdateTaskViewModel =
        ViewModelProvider(context as ViewModelStoreOwner)[UpdateTaskViewModel::class.java]
    private var aStr = "Task Name can not be empty"
    private var bStr = "Wrong Start Time entered"
    private var cStr = "Wrong End Time entered"
    private var dStr = "Task Venue can not be empty"
    private var updateDialog: Dialog? = Dialog(context)
    private lateinit var builderDelete: AlertDialog.Builder
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance()
        .getReference("Users")
        .child(auth.currentUser?.uid.toString())
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tasks_menu_item, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "CutPasteId")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTask = userTaskList[position]
        val black = ForegroundColorSpan(Color.BLACK)
        // task name
        val eName = "Event Name:" + "\n${currentTask.taskName}"
        val spannableString1 = SpannableString(eName)
        spannableString1.setSpan(black, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.myTaskName.text = spannableString1
        // task name
        // start time
        val eStart = "Time: " + "${currentTask.taskStartTime} -"
        val spannableString2 = SpannableString(eStart)
        spannableString2.setSpan(black, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.myTaskStartTime.text = spannableString2
        // start time
        // end time
        holder.myTaskEndTime.text = currentTask.taskEndTime
        // end time
        // venue
        val venue = "Location:\n${currentTask.taskVenue}"
        val spannableString3 = SpannableString(venue)
        spannableString3.setSpan(black, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.myTaskVenue.text = spannableString3
        // venue
        holder.myTaskDelete.setOnClickListener {
            builderDelete = AlertDialog.Builder(context)
            builderDelete.setTitle("Alert")
                .setMessage("Want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->   // dialogInterface, it
                    // delete task
                    userTaskList.removeAt(position)
                    databaseReference.child(currentTask.taskDate.toString())
                        .child(currentTask.taskKey.toString()).removeValue()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, userTaskList.size)
                    notifyDataSetChanged()
                }
                .setNegativeButton("No") { dialogInterface, _ ->     // dialogInterface, it
                    dialogInterface.cancel()
                }
                .show()
        }
        holder.myTaskUpdate.setOnClickListener {
            updateDialog = Dialog(context)
            updateDialog?.setContentView(R.layout.popup_window)
            updateDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            updateDialog?.findViewById<ImageView>(R.id.dialogClose)?.setOnClickListener {
                updateDialog?.dismiss()
            }
            // ViewModel Work
            updateViewModel.updateDataInFirebase(
                currentTask.taskDate.toString(),
                context,
                currentTask.taskKey.toString(),
                currentTask
            )
            var userTaskKey = ""
            updateViewModel.getData().observe(context as LifecycleOwner, Observer { userDataValue ->
                Log.i("data found", userDataValue.toString())
                userTaskKey = userDataValue.taskKey.toString()
                updateDialog?.findViewById<TextView>(R.id.event_task_name)?.text =
                    userDataValue?.taskName
                updateDialog?.findViewById<TextView>(R.id.event_date)?.text =
                    userDataValue?.taskDate
                updateDialog?.findViewById<TextView>(R.id.event_task_start_time)?.text =
                    userDataValue?.taskStartTime
                updateDialog?.findViewById<TextView>(R.id.event_task_end_time)?.text =
                    userDataValue?.taskEndTime
                updateDialog?.findViewById<TextView>(R.id.event_task_location)?.text =
                    userDataValue?.taskVenue
            })

//                        // ##########################################  collecting data from the dialog box   ##########################################
            val myCalendar = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val format = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(format, Locale.UK)
                updateDialog?.findViewById<TextView>(R.id.event_date)?.text =
                    sdf.format(myCalendar.time)
            }
            val taskDatePicker = updateDialog?.findViewById<Button>(R.id.task_datePicker_btn)
            taskDatePicker?.setOnClickListener {
                DatePickerDialog(
                    context,
                    datePicker,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            val taskSubmitBtn = updateDialog?.findViewById<Button>(R.id.task_submit_btn)
            taskSubmitBtn?.setOnClickListener {
                var taskName =
                    updateDialog?.findViewById<EditText>(R.id.event_task_name)?.text.toString()
                var taskStartTime =
                    updateDialog?.findViewById<EditText>(R.id.event_task_start_time)?.text.toString()
                var taskEndTime =
                    updateDialog?.findViewById<EditText>(R.id.event_task_end_time)?.text.toString()
                var taskVenue =
                    updateDialog?.findViewById<EditText>(R.id.event_task_location)?.text.toString()
                val checkingDatePicked =
                    updateDialog?.findViewById<TextView>(R.id.event_date)?.text

                if (checkingDatePicked == "No Date Picked") Toast.makeText(
                    context,
                    "Please Select the Date...",
                    Toast.LENGTH_SHORT
                ).show()
                else if (!checkStrings(taskName)) Toast.makeText(
                    context,
                    aStr,
                    Toast.LENGTH_SHORT
                ).show()
                else if (!checkStrings(taskStartTime)) Toast.makeText(
                    context,
                    bStr,
                    Toast.LENGTH_SHORT
                )
                    .show()
                else if (!checkStrings(taskEndTime)) Toast.makeText(
                    context,
                    cStr,
                    Toast.LENGTH_SHORT
                )
                    .show()
                else if (!checkStrings(taskVenue)) Toast.makeText(
                    context,
                    dStr,
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    taskName = reorderStrings(taskName)
                    taskStartTime = reorderStrings(taskStartTime)
                    taskEndTime = reorderStrings(taskEndTime)
                    taskVenue = reorderStrings(taskVenue)
                    updateDialog?.findViewById<EditText>(R.id.event_task_name)
                        ?.setText("")
                    updateDialog?.findViewById<EditText>(R.id.event_task_start_time)
                        ?.setText("")
                    updateDialog?.findViewById<EditText>(R.id.event_task_end_time)
                        ?.setText("")
                    updateDialog?.findViewById<EditText>(R.id.event_task_location)
                        ?.setText("")
//                                // ****************************************   Adding to database ****************************************
                    val finalUserDataTask = UserTask(
                        taskKey = userTaskKey,
                        taskDate = checkingDatePicked.toString(),
                        taskName = taskName,
                        taskStartTime = taskStartTime,
                        taskEndTime = taskEndTime,
                        taskVenue = taskVenue
                    )
                    updateViewModel.updateDataInFirebase(
                        checkingDatePicked.toString(),
                        context,
                        userTaskKey,
                        finalUserDataTask
                    )
                    updateDialog?.dismiss()
                    // ****************************************   Adding to database ****************************************
                }
            }
//                        // ##########################################  collecting data from the dialog box   ##########################################
            updateDialog?.setCancelable(false)
            updateDialog?.setCanceledOnTouchOutside(false)
            updateDialog?.show()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return userTaskList.size
    }

    private fun reorderStrings(task: String): String {
        val ls = task.split(" ")
        var s = ""
        for (each in ls) {
            s += "$each "
        }
        return s
    }

    private fun checkStrings(taskName: String): Boolean {
        if (taskName.isEmpty()) return false
        for (i in taskName.indices) {
            if (taskName[i] != ' ') {
                Log.i("OK char", i.toString())
                return true
            } else {
                Log.i("NULL char", i.toString())
            }
        }
        return false
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myTaskName: TextView = itemView.findViewById(R.id.myTaskName)
        val myTaskStartTime: TextView = itemView.findViewById(R.id.myTaskStartTime)
        val myTaskEndTime: TextView = itemView.findViewById(R.id.myTaskEndTime)
        val myTaskVenue: TextView = itemView.findViewById(R.id.myTaskVenue)
        val myTaskDelete: ImageButton = itemView.findViewById(R.id.deleteTask)
        val myTaskUpdate: ImageButton = itemView.findViewById(R.id.updateTask)
    }
}
