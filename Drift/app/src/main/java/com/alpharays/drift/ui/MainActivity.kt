package com.alpharays.drift.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.alpharays.drift.R
import com.alpharays.drift.data.Messages
import com.alpharays.drift.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var builder: AlertDialog.Builder
    private lateinit var databaseReference: DatabaseReference
    private var contactsDuplicateCheckMap = mutableMapOf<String, Int>()

    // permissions
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    //    private var isRecordPermissionGranted = false
    private var isReadContactsPermissionGranted = false
    private var isWriteContactsPermissionGranted = false
    private var isReadPermissionGranted = false
    private val permissionList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestingPermissions()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitProcess(0)
            }
        })

        init()
        phoneNumbers()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        Log.i("authNotNull", auth.currentUser?.phoneNumber.toString())
        drawerFunction()
    }

    private fun drawerFunction() {
        val drawerLayout = binding.drawerLayout
        binding.settingsProfile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            val navView = binding.navView
            toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.isDrawerSlideAnimationEnabled = true
            toggle.syncState()
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.Log_out -> {
                        confirmingLogOut()
                    }
                }
                true
            }
        }
    }

    private fun confirmingLogOut() {
        builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert))
            .setMessage("Do you wish to Log Out?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                logout()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        // set the new task and clear flags
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun phoneNumbers() {
        val phones: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) + 0)
            var phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) + 0)
            if (phoneNumber.length >= 10 && name != "" || name != null) {
                phoneNumber = reorderingPhoneNumber(phoneNumber)
                val ok = checkValidPhoneNumber(phoneNumber)
                if (!contactsDuplicateCheckMap.containsKey(phoneNumber) && ok && phoneNumber != auth.currentUser?.phoneNumber) {
                    contactsDuplicateCheckMap[phoneNumber] = 1
                    checkContact(phoneNumber, name)
                }
            }
        }
        phones.close()
    }

    private fun checkValidPhoneNumber(phoneNumber: String): Boolean {
        for (i in phoneNumber.indices) {
            if (phoneNumber[i].isDigit() || phoneNumber[i] == '+') {
                continue
            } else {
                return false
            }
        }
        return true
    }

    private fun reorderingPhoneNumber(phoneNumber: String?): String {
        var s = ""
        if (phoneNumber != null) {
            for (i in phoneNumber.indices) {
                if (phoneNumber[i] != ' ') {
                    s += phoneNumber[i]
                }
            }
        }
        return s.reversed().substring(0, 10).reversed()
    }

    private fun checkContact(numberCheck: String, name: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(numberCheck).get().addOnSuccessListener {
            if (it.exists()) {
                Log.i("contactsInFirebase", "$numberCheck && $name")
                readChats(numberCheck, name)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Try again later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readChats(receiverNumber: String, name: String) {
        val senderNum = auth.currentUser?.phoneNumber
        val receiverRoom = senderNum + receiverNumber
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child("ALL_MESSAGES")
                .child(receiverRoom)

        databaseReference.get().addOnSuccessListener {
            if (it.exists()) {
                // here lastMsgText will be the last message of ChatsRecyclerView
                // ************************ USING DB TO RETRIEVE THE LAST MESSAGE ************************
                var msg = ""
                var num = ""
                var msgCount = 0
                for (m in it.child("messages").children) {
                    msg = m.getValue(Messages::class.java)?.message.toString()
                    num = m.getValue(Messages::class.java)?.senderNumber.toString()
                    msgCount += 1
                    Log.i("Messages", "$msg : $num : $msgCount")
                }
                // ************************ USING DB TO RETRIEVE THE LAST MESSAGE ************************
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Try again later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendChat() {
//        val senderNum = auth.currentUser?.phoneNumber
//        databaseReference =
//            FirebaseDatabase.getInstance().getReference("Users").child("ALL_MESSAGES")
    }


    // PERMISSIONS
    private fun requestingPermissions() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isReadPermissionGranted =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: isReadPermissionGranted
                isWriteContactsPermissionGranted =
                    permissions[android.Manifest.permission.WRITE_CONTACTS]
                        ?: isWriteContactsPermissionGranted
                isReadContactsPermissionGranted =
                    permissions[android.Manifest.permission.READ_CONTACTS]
                        ?: isReadContactsPermissionGranted
            }
        requestPermission()
    }

    private fun requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isReadContactsPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        isWriteContactsPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED


        if (!isReadPermissionGranted) {
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isReadContactsPermissionGranted) {
            permissionList.add(android.Manifest.permission.READ_CONTACTS)
        }
        if (!isWriteContactsPermissionGranted) {
            permissionList.add(android.Manifest.permission.WRITE_CONTACTS)
        }

        if (permissionList.isNotEmpty()) {
            permissionLauncher.launch(permissionList.toTypedArray())
        }

    }
}