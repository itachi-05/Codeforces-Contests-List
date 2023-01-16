package com.example.internapp

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.internapp.databinding.ActivityDocumentBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File


class DocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentBinding
    private lateinit var pdfTextView: TextView
    private lateinit var pdfUri: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var btnReport: Button
    private val storage = Firebase.storage
    var storageRef = storage.reference

    private val getImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            val file =
                File(Environment.getExternalStorageDirectory().toString() + "/filepath/" + it?.path)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel")
            startActivity(intent)
//            val myIntent = Intent(Intent.ACTION_VIEW)
//            myIntent.data = Uri.fromFile(it.path)
//            val j = Intent.createChooser(myIntent, "Choose an application to open with:")
//            startActivity(j)
//            storageReference = FirebaseStorage.getInstance().getReference("myDocuments/")
//            if (it != null) {
//                storageReference.putFile(it).addOnSuccessListener {
//                    Toast.makeText(baseContext, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
//                    btnReport.visibility = View.VISIBLE
//                    pdfTextView.visibility = View.INVISIBLE
//                    getProfilePic()
//
//                }.addOnFailureListener{
//                    Toast.makeText(baseContext, "Failed to update profile image", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        pdfTextView = findViewById(R.id.txtView)
        btnReport = binding.btnReport

        pdfTextView.setOnClickListener {
            getImage.launch("application/*")
//            selectPdfFromStorage()

        }
    }

    private fun getProfilePic() {
//        storageReference = FirebaseStorage.getInstance().reference.child("myDocuments/")
//        val s = storageReference.downloadUrl.result
//        val httpsReference = storage.getReferenceFromUrl(
//            "https://console.firebase.google.com/u/0/project/internapp-9645c/storage/internapp-9645c.appspot.com/files/~2F")
//        Log.i("checking",s.toString())


//        val localFile = File.createTempFile("tempImage","")
//        storageReference.getFile(localFile).addOnSuccessListener {
//            Log.i("localFIle",localFile.toString())
//            val intentSharing = Intent(Intent.ACTION_SEND)
//            intentSharing.type = "application/*"
////            intentSharing.putExtra(Intent.EXTRA, localFile)
//            startActivity(Intent.createChooser(intentSharing, "Share file with"));
//            Toast.makeText(this,"Found",Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener{
////             Image can not be loaded when user login's for first time
////            Toast.makeText(baseContext, "Failed...", Toast.LENGTH_SHORT).show()
//            Log.i("GET PROFILE PIC","CAN NOT BE LOADED or IS NOT UPLOADED")
//        }
    }




    private fun selectPdfFromStorage() {
        Toast.makeText(this, "selectPDF", Toast.LENGTH_LONG).show()
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/*"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // For loading PDF
        when (requestCode) {
            12 -> if (resultCode == RESULT_OK) {
                pdfUri = data?.data!!

                val uri: Uri = data.data!!
                val uriString: String = uri.toString()


                val storage = Firebase.storage
                val storageRef = storage.reference
                storageRef.child("img/doc_testing").putFile(uri).addOnSuccessListener {
                    Toast.makeText(this,"DONE",Toast.LENGTH_SHORT).show()
                }


                var pdfName: String? = null
                if (uriString.startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        myCursor = applicationContext!!.contentResolver.query(uri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            pdfTextView.text = pdfName
                        }
                    } finally {
                        myCursor?.close()
                    }
                }
            }
        }
    }
}