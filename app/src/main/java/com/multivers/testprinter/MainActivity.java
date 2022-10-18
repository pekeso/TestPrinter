package com.multivers.testprinter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.multivers.testprinter.utils.SunmiPrintHelper;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;

public class MainActivity extends AppCompatActivity {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    public int sunmiPrinter = CheckSunmiPrinter;

    private SunmiPrinterService sunmiPrinterService;

    private TextView mBtnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSunmiPrinterService(this);
        mBtnPrint = findViewById(R.id.print);
        mBtnPrint.setOnClickListener(
                (view) -> {
                    onClick();
                }
        );
    }


    InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            sunmiPrinterService = service;
        }

        @Override
        protected void onDisconnected() {
            sunmiPrinterService = null;
        }
    };

    /**
     * init sunmi print service
     */
    public void initSunmiPrinterService(Context context){
        try {
            boolean ret =  InnerPrinterManager.getInstance().bindService(context,
                    innerPrinterCallback);
            if(!ret){
                sunmiPrinter = NoSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    public void onClick() {
        print3Line();
        printText("Hello World!\n");
        printText("This is a test\n");
        printText("+++++===++++++\n");
        print3Line();
        printQr("Data to be encoded", 8, 3);
        print3Line();
    }

    public void printText(String content) {
        try {
            sunmiPrinterService.printText(content, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void print3Line(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.lineWrap(3, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void printQr(String data, int modulesize, int errorlevel) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printQRCode(data, modulesize, errorlevel, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}