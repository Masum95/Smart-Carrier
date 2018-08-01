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

    static int rotateCCW = 0;
    static int rotateCW = 1;
    static double UnitDistance = 0.5; //2 cm
    private OutputStream outStream = null;
    public static List<StraightLine> lineList = new ArrayList<>();

    private Context context = null;
    //assuming initial vector is from 0,0 to 0,0.5;


    public Command(Context context, OutputStream outputStream) {
        this.context = context;
        this.outStream = outputStream;

        Toast.makeText(context, context.getApplicationContext().toString(), Toast.LENGTH_LONG).show();
    }

    void sendCommand() {

        new SenderThread().execute();
    }


    public class SenderThread extends AsyncTask<String, String, String> {

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
            Toast.makeText(context.getApplicationContext(), "" + values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            char msg;
            publishProgress("Size is " + lineList.size());
            int i;
            for (i = 1; i < Math.min(lineList.size(),81); i++) {
                double tmp = Math.toDegrees(lineList.get(i - 1).calcAngleWith(lineList.get(i)));  // angle with previous line

                int rotate = tmp < 0 ? rotateCW : rotateCCW;  // rotate from previous direction
                double rotateAngle = Math.abs(tmp);  // rotate by this angle
                // move in current direction by  (unitTimes * unit_distance) units
                int unitTimes = (int) Math.round(lineList.get(i).magnitude / UnitDistance);

                // send Command To MicroController
                char move = (char) unitTimes;

                try {
                    sendData(move);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int angleInt =  (int) rotateAngle;
                //msg += angle;
                String dir = (rotate == rotateCCW) ? "Anti-Colckwise" : "Clock-Wise";
                //publishProgress(i+ "Angle is " + angle + " in " + dir);
                publishProgress(i+ "Angle is " + angleInt + " Move is " + unitTimes);
                angleInt = (angleInt<<1);

                char angle = (char) angleInt;
                if (rotate == rotateCCW) {
                    angle |= 1;

                } else {
                    angle &= ~(1);
                }
                try {
                    sendData(angle);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(lineList.size()<81){
                char ch = 255;

                try {
                    sendData(ch);
                } catch (Exception e) {
                }
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    sendData(ch);
                } catch (Exception e) {
                }
            }

            return "Done with sending";
        }


    }

    public void sendData(char message) {

        try {
            outStream.write((byte) message);
        } catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "Dhora khaise", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            //attempt to place data on the outstream to the BT device
            outStream.write(message.getBytes());
        } catch (IOException e) {
            //if the sending fails this is most likely because device is no longer there

        }
    }
}
