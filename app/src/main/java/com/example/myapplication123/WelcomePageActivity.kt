package com.example.myapplication123

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class WelcomePageActivity : AppCompatActivity(), View.OnClickListener {
    private var firstNameET: EditText? = null
    private var middleNameET: EditText? = null
    private var lastNameET: EditText? = null
    private var profilePictureEV: ImageView? = null
    private var profilePicturePath: String? = null

    private var photoButton: Button? = null
    private var submitButton: Button? = null

    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstNameET = findViewById(R.id.firstName)
        middleNameET = findViewById(R.id.middleName)
        lastNameET = findViewById(R.id.lastName)
        profilePictureEV = findViewById(R.id.profileImage)

        photoButton = findViewById(R.id.photoButton)
        submitButton = findViewById(R.id.submitButton)

        photoButton!!.setOnClickListener(this)
        submitButton!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.photoButton -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraActivity.launch(cameraIntent)
                } catch (ex: ActivityNotFoundException) {
                }
            }

            R.id.submitButton -> {
                firstName = firstNameET!!.text.toString()
                middleName = middleNameET!!.text.toString()
                lastName = lastNameET!!.text.toString()

                //Check if the first name and last name is empty
                if (firstName.isNullOrBlank()) {
                    Toast.makeText(this@WelcomePageActivity, "Enter your first name!", Toast.LENGTH_SHORT)
                        .show()
                }
                else if(lastName.isNullOrBlank()) {
                    Toast.makeText(this@WelcomePageActivity, "Enter your last name!", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    //Tell the user that they have filled the profile successfully
                    Toast.makeText(this@WelcomePageActivity, "Success!", Toast.LENGTH_SHORT).show()

                    //Go to Main Page
                    val mainPageIntent = Intent(this, MainPageActivity::class.java)
                    mainPageIntent.putExtra("firstName", firstName)
                    mainPageIntent.putExtra("middleName", middleName)
                    mainPageIntent.putExtra("lastName", lastName)
                    startActivity(mainPageIntent)
                }
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("FN_TEXT",firstName)
        outState.putString("MN_TEXT",middleName)
        outState.putString("LN_TEXT",lastName)
        outState.putString("PICTURE_PATH", profilePicturePath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        firstNameET!!.setText(savedInstanceState.getString("FN_TEXT"))
        middleNameET!!.setText(savedInstanceState.getString("MN_TEXT"))
        lastNameET!!.setText(savedInstanceState.getString("LN_TEXT"))

        // Read saved photo
        var path = savedInstanceState.getString("PICTURE_PATH")
        if (path != null) {
            profilePicturePath = path
            var savedPhoto = BitmapFactory.decodeFile(path)
            if (savedPhoto != null)
                profilePictureEV?.setImageBitmap(savedPhoto)
        }

    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            profilePictureEV = findViewById<View>(R.id.profileImage) as ImageView

            val thumbnailImage: Bitmap?

            if (Build.VERSION.SDK_INT >= 33) {
                thumbnailImage = result.data!!.getParcelableExtra("data", Bitmap::class.java)
                profilePictureEV!!.setImageBitmap(thumbnailImage)
            }
            else{
                thumbnailImage = result.data!!.getParcelableExtra<Bitmap>("data")
                profilePictureEV!!.setImageBitmap(thumbnailImage)
            }

            // Save the image
            profilePicturePath = saveImage(thumbnailImage)
        }
    }
}