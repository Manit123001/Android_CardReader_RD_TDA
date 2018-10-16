//package com.mcnewz.tda.rd.cardreader.sample.android_cardreader_tda
//
//import android.app.AlertDialog
//import android.content.DialogInterface
//import android.content.pm.PackageInfo
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.os.Handler
//import android.widget.Toast
//import kotlinx.android.synthetic.main.activity_main.*
//import rd.TDA.TDA
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        btn_read.setOnClickListener {
//            sampleConnect()
//        }
//    }
//
//    fun sampleConnect() {
//        val tda = TDA(this)
//        tda.serviceTA("1")
//        var pInfo: PackageInfo? = null
//        try {
//            pInfo = packageManager.getPackageInfo(packageName, 0)    //get Info of Package
//            val version = pInfo!!.versionName                         //get Version from Info
//            Toast.makeText(this, "$version", Toast.LENGTH_SHORT).show()
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
////        if (!tda.isPackageInstalled(this)) {
////            AlertDialog.Builder(this)
////                    .setMessage("ยังไม่ได้ติดตั้งโปรแกรมบริการ TDAService")
////                    .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
////                    .show()
////        }
//
//        val handler = Handler()                        //Create Object Handler for draw screen in thread
//        val thread = Thread(object : Runnable {
//            var photo: ByteArray? = null
//            var bPhoto: Bitmap? = null
//            var data: String? = null
//            override fun run() {
//                //Read Text from NID card
//                data = tda.nidTextTA("0")                      //ReadText
//                if (data!!.compareTo("-2") == 0) {                      //Check if un-registered reader
//                    tda.serviceTA("2")                         //Update license file
//                    data = tda.nidTextTA("0")                  //Read Text Again
//                }
//                handler.post {
//                    tv_show.text = data                   //Set Data Text on Screen
//                }
//                //Read Photo from NID card
//                photo = tda.nidPhotoTA("0")                     //Read Photo
//                bPhoto = BitmapFactory.decodeByteArray(photo, 0, photo!!.size)     // Decode Byte Array to Bitmap
//                handler.post {
//                    val info = tda.nidNumberTA("1")
//                    iv_pic.setImageBitmap(bPhoto)       //set Bitmap on Screen
//                }
//            }
//        })
//        thread.start()
//    }
//}
