package com.mcnewz.tda.rd.cardreader.sample.android_cardreader_tda.sample

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mcnewz.tda.rd.cardreader.sample.android_cardreader_tda.R
import kotlinx.android.synthetic.main.activity_sample_all.*
import rd.nalib.NA
import rd.nalib.ResponseListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class SampleAll : AppCompatActivity(), ResponseListener, View.OnClickListener {
    var na: NA? = null
    var mNIDReader = "/" + "ibuddy_econsent"
    var LICFileName = "/" + "rdnidlib.dls"
    var LicFile = Environment.getExternalStorageDirectory().toString() + mNIDReader + LICFileName
    private val RED = -0x77010000
    private val GREEN = -0x77e690c3
    private val WHITE = -0x1
    private var checkReaderList: ArrayList<String>? = null
    private var checkSelectedCardReader: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_all)
        setupView()
        na = NA(this)
        na?.setListenerNA(this)

        cardReaderFunction("openlib")
        cardReaderFunction("write")
        setResultText("setListenerNA")
    }

    private fun setupView() {
        bt_scanreader.setOnClickListener(this)
        bt_selectreader.setOnClickListener(this)
        bt_connectcard.setOnClickListener(this)
        bt_readnumber.setOnClickListener(this)
        bt_readdata.setOnClickListener(this)
        bt_readphoto.setOnClickListener(this)
        bt_deselectreader.setOnClickListener(this)
        bt_disconnectcard.setOnClickListener(this)
        bt_cardinfo.setOnClickListener(this)
        bt_getrid.setOnClickListener(this)
        bt_updatelicensefile.setOnClickListener(this)
        bt_getlicenseinfo.setOnClickListener(this)
        bt_getsoftwareinfo.setOnClickListener(this)
        bt_exit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bt_scanreader.id -> {
                cardReaderFunction("getreaderlist")
            }
            bt_selectreader.id -> {
                cardReaderFunction("selectreader")
            }
            bt_connectcard.id -> {
                cardReaderFunction("connectcard")
            }
            bt_readnumber.id -> {
                cardReaderFunction("readnumber")
            }
            bt_readdata.id -> {
                cardReaderFunction("readdata")
            }
            bt_readphoto.id -> {
                cardReaderFunction("readphoto")
            }
            bt_deselectreader.id -> {
                cardReaderFunction("deselectreader")
            }
            bt_disconnectcard.id -> {
                cardReaderFunction("disconnectcard")
            }
            bt_cardinfo.id -> {
                cardReaderFunction("cardinfo")
            }
            bt_getrid.id -> {
                cardReaderFunction("getrid")
            }
            bt_updatelicensefile.id -> {
                cardReaderFunction("updatelicensefile")
            }
            bt_getlicenseinfo.id -> {
                cardReaderFunction("getlicenseinfo")
            }
            bt_getsoftwareinfo.id -> {
                cardReaderFunction("getsoftwareinfo")
            }
            bt_exit.id -> {
                cardReaderFunction("exit")
            }
        }
    }

    private fun cardReaderFunction(s: String) {
        when (s) {
            "write" -> {
                mLog("write")
                writeFile(LicFile, "rdnidlib.dls")
            }
            "openlib" -> {
                na?.openLibNA(LicFile)
            }
            "getreaderlist" -> {
                val option = bt_listoption.text.toString().substring(bt_listoption.length() - 2, bt_listoption.length()).toLowerCase()
                mLog(option)
                val listOption = Integer.parseInt(option, 16)
                na?.getReaderListNA(listOption)
            }
            "selectreader" -> {
                var nameDevice = ""
//                if (spinner.childCount != 0) {
//                    nameDevice = spinner.selectedItem.toString()
//                }
                if (checkReaderList != null && checkReaderList?.size != 0)
                    nameDevice = checkReaderList?.get(0).toString()

                else{

                }
                na?.selectReaderNA(nameDevice)
            }
            "deselectreader" -> {
                val Result = na?.deselectReaderNA()
                if (Result == 0) {
                    tv_connected.text = ""
                    ll_connect.setBackgroundColor(RED)
                    tv_connected.setTextColor(WHITE)
                }
                var Output = "deselectReaderNA\n   Return > $Result"
                Output = "$Output\n------------------------------"
                setResultText(Output)
            }
            "connectcard" -> {
                val Result = na?.connectCardNA()
                if (Result == -16) {
                    mToast("ไม่พบบัตร")
                } else if (Result == 0) {
                    var Output = "connectCardNA\n   Return > $Result"
                    Output = "$Output\n------------------------------"
                    setResultText(Output)
                }
            }
            "disconnectcard" -> {
                val Result = na?.disconnectCardNA()
                var Output = "disconnectCardNA\n   Return > $Result"
                Output = "$Output\n------------------------------"
                setResultText(Output)
            }
            "readnumber" -> {
                na?.getNIDNumberNA()
            }
            "readdata" -> {
                na?.getNIDTextNA()
            }
            "readphoto" -> {
                iv_faceimage.setImageResource(R.mipmap.ic_launcher)
                na?.getNIDPhotoNA()
            }
            "getsoftwareinfo" -> {
                val data = arrayOfNulls<String>(1)
                val Result = na?.getSoftwareInfoNA(data)
                var Output = "getSoftwareInfoNA\n   Return > $Result"
                if (Result == 0) {
                    Output = Output + "\n" + "   Result > " + data[0]
                }
                Output = "$Output\n------------------------------"
                setResultText(Output)
            }
            "getlicenseinfo" -> {
                val data = arrayOfNulls<String>(1)
                val Result = na?.getLicenseInfoNA(data)
                var Output = "getLicenseInfoNA\n   Return > $Result"
                if (Result == 0) {
                    Output = Output + "\n" + "   Result > " + data[0]
                }
                Output = "$Output\n------------------------------"
                setResultText(Output)
            }
            "cardinfo" -> {
                val Result = na?.getCardStatusNA()
                var Output = "getCardStatusNA\n   Return > $Result"
                Output = "$Output\n------------------------------"
                setResultText(Output)
            }
            "getrid" -> {
                val Rid = ByteArray(256)
                val result = na?.getRidNA(Rid)
                val hexString = StringBuilder()
                for (aRid in Rid) {
                    var h = Integer.toHexString(0xFF and aRid.toInt())
                    while (h.length < 2)
                        h = "0$h"
                    hexString.append("$h ")
                }
                var Output = ""
                if (result != null) {
                    if (result > 0) {
                        hexString.setLength(result * 3 - 1)
                        Output = "getRidNA\n   Return > $result"
                        Output = Output + "\n" + "   Result > " + hexString.toString().toUpperCase()
                    } else {
                        Output = "getRidNA\n   Return > $result"
                    }
                }
                Output = "$Output\n------------------------------"
                setResultText(Output)
            }
            "updatelicensefile" -> {
                na?.updateLicenseFileNA()
            }
            "exit" -> {
                val Result = na?.closeLibNA()
                var Output = "closeLibNA\n   Return > $Result"
                Output = "$Output\n------------------------------"
                setResultText(Output)
                System.exit(0)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setResultText(text: String) {
        tv_output.text = "${tv_output.text} \n $text"
        scroll_view.post { scroll_view.fullScroll(View.FOCUS_DOWN) }
    }

    override fun onGetNIDPhotoNA(cardData: ByteArray?, Result: Int) {
        // Read Photo
        var Output = "getNIDPhotoNA\n   Return > $Result"
        if (resultCodeValidate(Result)) {
            val bMap = BitmapFactory.decodeByteArray(cardData, 0, cardData!!.size)
            iv_faceimage.setImageBitmap(bMap)
        }
        Output = "$Output\n------------------------------"
        setResultText(Output)
    }

    override fun onGetNIDNumberNA(cardData: String?, Result: Int) {
        var Output = "getNIDNumberNA\n   Return > $Result"
        if (resultCodeValidate(Result)) {
            Output = "$Output\n   Result > $cardData"
        }
        Output = "$Output\n------------------------------"
        setResultText(Output)
    }

    override fun onGetReaderListNA(readerList: ArrayList<String>?, result: Int) {
        // Select Card Reader
        resultCodeValidate(result)
        if (result >= 0) {
            mLog("result >= 0 ")
            checkReaderList = readerList
            val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, readerList)
            spinner.adapter = adapter
            var text = "getReaderListNA (" + bt_listoption.text.toString().substring(bt_listoption.length() - 2, bt_listoption.length()) + ")" + "\n" + "   Return > " + readerList?.size.toString()
            if (readerList != null) {
                for (d in readerList) {
                    text = "$text\n   Result > $d"
                }
            }
            text = "$text\n------------------------------"
            setResultText(text)
        } else {
            mLog("result < 0 ")
            checkReaderList = null
            val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
            spinner.adapter = adapter
            var text = "getReaderListNA (" + bt_listoption.text.toString().substring(bt_listoption.length() - 2, bt_listoption.length()) + ")" + "\n" + "   Return > " + result
            text = "$text\n------------------------------"
            setResultText(text)
        }
    }

    override fun onGetNIDTextNA(cardData: String?, Result: Int) {
        var Output = "getNIDTextNA\n   Return > $Result"
        if (resultCodeValidate(Result)) {
            Output = "$Output\n   Result > $cardData"
            val textInfo = cardData!!.split("#")
            val info = textInfo.filter {
                it != ""
            }.map {
                it
            }

            mToast("$info")
        }
        Output = "$Output\n------------------------------"
        setResultText(Output)
    }

    override fun onSelectReaderNA(Result: Int) {
        resultCodeValidate(Result)
        checkSelectedCardReader = Result

        if (Result == 0) {
            tv_connected.setText(" : Selected")
            ll_connect.setBackgroundColor(GREEN)
            tv_connected.setTextColor(WHITE)
        } else {
            tv_connected.text = ""
            ll_connect.setBackgroundColor(RED)
            tv_connected.setTextColor(WHITE)
        }
        var Output = "selectReaderNA ()\n   Return > $Result"
        Output = "$Output\n------------------------------"
        setResultText(Output)
    }

    override fun onOpenLibNA(result: Int) {
        resultCodeValidate(result)
        mToast(result.toString())
        var Output = "openLibNA\n   Result > $result"
        Output = "$Output\n------------------------------"
        setResultText(Output)
    }

    override fun onUpdateLicenseFileNA(Result: Int) {
        var Output = "updateLicenseFileNA\n   Return > $Result"
        Output = "$Output\n------------------------------"
        setResultText(Output)
    }

    private fun mLog(s: String, tag: String = "MainActivityTest") {
        Log.d(tag, s)
    }

    private fun mToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
        Log.d("mToast", s)
    }

    private fun writeFile(Path: String, Filename: String) {
        val assetManager = assets
        try {
            val `is` = assetManager.open(Filename)
            val out = File(Path)
            if (out.exists())
                return
            val parent = File(Environment.getExternalStorageDirectory().toString() + mNIDReader)
            parent.mkdirs()
            val buffer = ByteArray(1024)
            val fos = FileOutputStream(out)
            val read: Int = `is`.read(buffer, 0, 1024)
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

    private fun resultCodeValidate(code: Int): Boolean {
        var status = false
        mLog("resultCodeValidate: " + code.toString())
        when (code) {
            0 -> {
                status = true
            }
            -1 -> {
                mToast("เกิดข้อผดิพลาดภายในระบบ")
            }
            -2 -> {
                mToast("เครื่องอ่านนี้ใช้ไม่ได้เพราะไม่มีทะเบียนใน ใบอนญุาต")
            }
            -3 -> {
                mToast("ไม่พบเครื่องอ่านบัตร")
            }
            -4 -> {
                mToast("ไม่สามารถติดต่อกับบัตรได้")
            }
            -5 -> {
                mToast("ไม่สามารถอ่านรูปภาพถ่ายใบหนา้ได้")
            }
            -6 -> {
                mToast("ไม่สามารถอ่านข้อมูลตัวอักษรได้")
            }
            -7 -> {
                mToast("บัตรที่อ่านไม่ใช่บัตรประชาชน")
            }
            -8 -> {
                mToast("ไม่รองรับการใชง้านกับบัตรประชาชนรุ่นนี้")
            }
            -9 -> {
                mToast("ไม่สามารถยกเลิกการเชื่อมต่อกับเครื่องอ่าน บัตรได้")
            }
            -10 -> {
                mToast("กระบวนการตั้งค่าเริ่มตน้ทำางานผดิพลาด หรอืยังไม่ได้เรียกใช้งาน openLibNA")
            }
            -11 -> {
                mToast("ไม่รองรับการใชง้านกับเครื่องอ่านนี้ หรือไม่ พบเครอื่งอ่านบัตร")
            }
            -12 -> {
                mToast("ไม่พบแฟ้้มใบอนุญาตหรอืแฟ้้มใบอนุญาต เสียหาย")
            }
            -13 -> {
                mToast("พารามิเตอร์หรือตวัแปรผิดพลาด")
            }
            -14 -> {
                mToast("พารามิเตอร์หรือตวัแปรผิดพลาด")
            }
            -15 -> {
                mToast("ไม่สามารถติดต่ออินเทอร์เน็ตได้")
            }
            -16 -> {
                mToast("ไม่พบบัตรในเครื่องอ่าน")
            }
            -17 -> {
                mToast("ไม่ได้เปิดบลูทูธ ")
            }
            -18 -> {
                mToast("อัปเดตแฟ้้มใบอนุญาตไม่สำาเร็จ")
            }
        }
        return status
    }
}
