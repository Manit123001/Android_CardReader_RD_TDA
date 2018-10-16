package com.mcnewz.tda.rd.cardreader.sample.android_cardreader_tda

// NASample
// Copyright R&D Computer System Co.,Ltd
import android.graphics.BitmapFactory
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_second.*
import rd.nalib.NA
import rd.nalib.ResponseListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ThirdActivity : AppCompatActivity() {
    private var NALibs: NA? = null
    private val mNIDReader = "/" + "NASample"
    private val RootForder = Environment.getExternalStorageDirectory().toString() + mNIDReader
    lateinit var mHandler: Handler
    private var handler = Handler()
    private var ReaderSelect = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        mHandler = MyHandlerClass(Looper.getMainLooper())

        bt_SelectReader!!.setOnClickListener {
            tv_Result!!.text = ""
            iv_Photo!!.setImageResource(R.mipmap.ic_launcher)
            val msg = mHandler!!.obtainMessage()
            msg!!.obj = "getreaderlist"
            mHandler!!.sendMessage(msg)
        }
        bt_Read!!.setOnClickListener {
            tv_Result!!.text = ""
            iv_Photo!!.setImageResource(R.mipmap.ic_launcher)
            val msg = mHandler!!.obtainMessage()
            msg!!.obj = "read"
            mHandler!!.sendMessage(msg)
        }
        bt_UpdateLicense!!.setOnClickListener {
            setEnableButton(false)
            tv_Result!!.text = ""
            iv_Photo!!.setImageResource(R.mipmap.ic_launcher)
            val msg = mHandler!!.obtainMessage()
            msg!!.obj = "updatelicense"
            mHandler!!.sendMessage(msg)
        }
        bt_Exit!!.setOnClickListener {
            NALibs!!.deselectReaderNA()
            NALibs!!.closeLibNA()
            System.exit(0)
        }
        NALibs = NA(this)
        val LICFileName = "/" + "rdnidlib.dls"
        writeFile(RootForder + LICFileName, "rdnidlib.dls") // Write file Licence
        NALibs!!.setListenerNA(responseListener)
        NALibs!!.openLibNA(RootForder + LICFileName)                  // Open Libs

//        val progressDialog = ProgressDialog(this)
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//        progressDialog.setMessage("Scan bluetooth")
    }

    private var responseListener: ResponseListener = object : ResponseListener {
        override fun onOpenLibNA(i: Int) {
            if (i == 0) {
                var data = arrayOfNulls<String>(1)
                NALibs!!.getSoftwareInfoNA(data)
                if (data[0] != null) {
                    val finalData = data
                    setText(tv_SoftwareInfo, "Software Info : " + finalData[0])
                }
                data = arrayOfNulls(1)
                NALibs!!.getLicenseInfoNA(data)
                if (data[0] != null) {
                    val finalData1 = data
                    setText(tv_LicenseInfo, "License Info : " + finalData1[0])
                }
                bt_SelectReader!!.isEnabled = true
                bt_Read!!.isEnabled = false
                bt_UpdateLicense!!.isEnabled = true
                bt_Exit!!.isEnabled = true
            } else {
                setText(tv_Result, "Open Lib failed; Plese restart app")
                bt_SelectReader!!.isEnabled = false
                bt_Read!!.isEnabled = false
                bt_UpdateLicense!!.isEnabled = false
                bt_Exit!!.isEnabled = true
            }
        }

        override fun onGetReaderListNA(arrayList: ArrayList<String>, i: Int) {
            if (i > 0) {
                ReaderSelect = arrayList[0]
                setText(tv_Reader, "Reader Selecting...")
                NALibs!!.selectReaderNA(ReaderSelect)
            } else {
                setText(tv_Result, "Get Reader List failed")
                setText(tv_Reader, "Reader not found.")
                bt_SelectReader!!.isEnabled = true
                bt_Read!!.isEnabled = false
                bt_UpdateLicense!!.isEnabled = true
                bt_Exit!!.isEnabled = true
            }
        }

        override fun onSelectReaderNA(i: Int) {
            if (i == 0) {
                setText(tv_Reader, "Reader : $ReaderSelect")
                setEnableButton(true)
            } else {
                when (i) {
                    -2 -> {
                        setText(tv_Result, "This reader is not licensed.")
                        setText(tv_Reader, "Reader : $ReaderSelect")
                        handler.post {
                            bt_SelectReader!!.isEnabled = true
                            bt_Read!!.isEnabled = false
                            bt_UpdateLicense!!.isEnabled = true
                            bt_Exit!!.isEnabled = true
                        }
                    }
                    -12 -> {
                        setText(tv_Result, "License file error.")
                        setText(tv_Reader, "Reader : $ReaderSelect")
                        handler.post {
                            bt_SelectReader!!.isEnabled = true
                            bt_Read!!.isEnabled = false
                            bt_UpdateLicense!!.isEnabled = true
                            bt_Exit!!.isEnabled = true
                        }
                    }
                    else -> {
                        setText(tv_Result, "Connect failed")
                        setText(tv_Reader, "Reader not found.")
                        handler.post {
                            bt_SelectReader!!.isEnabled = true
                            bt_Read!!.isEnabled = false
                            bt_UpdateLicense!!.isEnabled = true
                            bt_Exit!!.isEnabled = true
                        }
                    }
                }
            }
        }

        override fun onGetNIDNumberNA(s: String, i: Int) {}
        override fun onGetNIDTextNA(s: String, i: Int) {
            if (i == 0) {
                setText(tv_Result, s)
            } else {
                setText(tv_Result, "Read Data failed")
                NALibs!!.disconnectCardNA()
                setEnableButton(true)
                return
            }
            handler.post { iv_Photo!!.setImageResource(R.mipmap.ic_launcher) }
            NALibs!!.nidPhotoNA
        }

        override fun onGetNIDPhotoNA(bytes: ByteArray, i: Int) {
            if (i == 0) {
                val bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                handler.post { iv_Photo!!.setImageBitmap(bMap) }
            } else {
                setText(tv_Result, "Read Photo failed")
            }
            NALibs!!.disconnectCardNA()
            setEnableButton(true)
        }

        override fun onUpdateLicenseFileNA(i: Int) {
            when (i) {
                0 -> {
                    val data = arrayOfNulls<String>(1)
                    NALibs!!.getLicenseInfoNA(data)
                    if (data[0] != null) {
                        setText(tv_LicenseInfo, "License Info : " + data[0])
                    }
                    setText(tv_Result, "License has been successfully updated.")
                }
                1 -> setText(tv_Result, "The latest license has already been installed.")
                -15 -> setText(tv_Result, "Internet error.")
                else -> setText(tv_Result, "License updating failed.")
            }
            setEnableButton(true)
        }
    }

    fun setText(tv: TextView?, message: String) {
        handler.post { tv!!.text = message }
    }

    fun setEnableButton(Enable: Boolean) {
        handler.post {
            bt_SelectReader!!.isEnabled = Enable
            bt_UpdateLicense!!.isEnabled = Enable
            bt_Read!!.isEnabled = Enable
            bt_Exit!!.isEnabled = Enable
        }
    }

    fun writeFile(Path: String, Filename: String) {
        val assetManager = assets
        try {
            val `is` = assetManager.open(Filename)
            val out = File(Path)
            if (out.exists())
                return
            val parent = File(RootForder)
            parent.mkdirs()
            val buffer = ByteArray(1024)
            val fos = FileOutputStream(out)
            var read: Int = `is`.read(buffer, 0, 1024)
            while (read >= 0) {
                fos.write(buffer, 0, read)
            }
            fos.flush()
            fos.close()
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inner class MyHandlerClass(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val message = msg!!.obj as String
            when (message) {
                "getreaderlist" -> {
                    handler.post {
                        setEnableButton(false)
                        tv_Reader!!.text = "Reader scanning..."
                    }
                    NALibs!!.getReaderListNA(0xD3)
                }
                "read" -> {
                    handler.post {
                        //                        setEnableButton(false)
                        tv_Result!!.text = ""
                        iv_Photo!!.setImageResource(R.mipmap.ic_launcher)
                    }
                    val result = NALibs!!.connectCardNA()
                    if (result == -16) {
                        handler.post {
                            tv_Result!!.text = "Card not found."
                            setEnableButton(true)
                        }
                    } else if (result != 0) {
                        handler.post {
                            tv_Result!!.text = "Card connection error."
                            setEnableButton(true)
                        }
                    }
                    NALibs!!.nidTextNA
                }
                "updatelicense" -> {
                    NALibs!!.updateLicenseFileNA()
                }
            }
        }
    }
}
