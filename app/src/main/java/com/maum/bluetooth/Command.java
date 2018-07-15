package com.maum.bluetooth;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masum Rahman on 5/27/2018.
 */
public class Command {

    static  int rotateCCW = 0 ;
    static int rotateCW = 1;

    private OutputStream outStream = null;
    public static List<StraightLine> lineList = new ArrayList<>();

    private Context context = null;
    //assuming initial vector is from 0,0 to 0,0.5;


    public Command(Context context,OutputStream outputStream) {
        this.context = context;
        this.outStream = outputStream;
        Toast.makeText(context, context.getApplicationContext().toString(), Toast.LENGTH_LONG).show();
    }

    void sendCommand()
    {
        new SenderThread().execute();
    }


    public class SenderThread extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context.getApplicationContext(), "Start sending", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            Toast.makeText(context.getApplicationContext(), "Done with sending", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(context.getApplicationContext(), ""+values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            for(int i=1;i<lineList.size();i++)
            {
                double tmp = Math.toDegrees(lineList.get(i-1).calcAngleWith(lineList.get(i)));  // angle with previous line

                int rotate = tmp<0 ? rotateCW : rotateCCW;  // rotate from previous direction
                double rotateAngle = Math.abs(tmp);  // rotate by this angle
                // move in current direction by  (unitTimes * unit_distance) units
                int unitTimes = (int) Math.round(lineList.get(i).magnitude /0.5 );

                // send Command To MicroController
                char move = (char) unitTimes;
                if(rotate == rotateCCW)
                {
                    move &= ~(1);
                }
                else{
                    move |= 1;
                }
                msg += move;
                //sendData(move);
                publishProgress("Move is " +unitTimes);
                char angle = (char) ((int)rotateAngle );
                msg+=angle;
                //sendData(angle);
                String dir = (rotate==rotateCCW) ? "Anti-Colckwise" : "Clock-Wise";
                publishProgress("Angle is "+(int)angle + " in "+dir);
            }
            char ch;
            for(int i=0;i<5;i++)
            {
                ch = 0b01111111;
                msg+=ch;
            }
            sendData(msg);
            return "Done with sending";
        }

        private void sendData(String message) {
            byte[] msgBuffer = message.getBytes();
            try {
                //attempt to place data on the outstream to the BT device
                outStream.write(msgBuffer);
            } catch (IOException e) {
                //if the sending fails this is most likely because device is no longer there

            }
        }


    }
}
