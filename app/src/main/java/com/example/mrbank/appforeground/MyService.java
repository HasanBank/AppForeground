package com.example.mrbank.appforeground;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import android.os.Handler;

import com.example.mrbank.appforeground.DataBase.ObjectProgramUsing;
import com.example.mrbank.appforeground.DataBase.TableControllerPrograms;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MyService extends Service {

    Handler mHandler=new Handler(); // to post from background

    public static long UPDATE_INTERVAL = 1*5*1000;

    private static Timer timer = new Timer();

    private String foreGroundApp;
    private ActivityManager.RunningAppProcessInfo info;
    public TableControllerPrograms controller ;
      public static String kategori;

    public String getCategory() {
        return kategori;
    }

    public void setCategory(String str) {
        kategori = str;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

  @Override
    public void onCreate() {
        super.onCreate();
       controller = new TableControllerPrograms(getBaseContext());
       // _startService();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(getBaseContext(), "Service Started", Toast.LENGTH_LONG).show();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                doServiceWork();
            }
        }, 1000, UPDATE_INTERVAL);


        return START_STICKY;
    }


    /*
    public void _startService(Intent intent) {
        Toast.makeText(this,"was started",Toast.LENGTH_SHORT).show();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doServiceWork();
            }
        }, 1000, UPDATE_INTERVAL);
    }*/

    private void doServiceWork() {
        mHandler.post(new Runnable() {

            public void run () {




                ActivityManager am = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);


                // The first in the list of RunningTasks is always the foreground task.

                ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);




                String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
                PackageManager pm = getBaseContext().getPackageManager();
                PackageInfo foregroundAppPackageInfo = null;
                try {
                    foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
                // foregroundTaskPackageName is for packagename of foregroundApp

                /*
                ActivityManager    mActivityManager = (ActivityManager)getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
                Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
                while(i.hasNext()){
                    info = i.next();
                    if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            ){
                        foreGroundApp=info.toString();
                        break;
                    }
                }
                */



                // Toast.makeText(getBaseContext(), ""+foregroundTaskAppName, Toast.LENGTH_SHORT).show();



                // if program hasnt added database
                // CREATE

                //if yes
                // update record


              // if it was first appeared , it should write to database
               if( !(controller.readSingleRecord(foregroundTaskAppName)) ) {
                    Toast.makeText( getBaseContext(),foregroundTaskPackageName,Toast.LENGTH_SHORT).show();

                    setCategory("empty");
                   new findCategory().execute(foregroundTaskPackageName);

                    ObjectProgramUsing objectProgram ;           // object of Program Info
                        objectProgram= new ObjectProgramUsing();
                       objectProgram.programName = foregroundTaskAppName;


                   try {
                       Thread.sleep(3000);
                   } catch (InterruptedException e) {
                       // Nothing here // Just Waiting response of async task
                   }

                    objectProgram.category = getCategory();

                        Toast.makeText(getBaseContext(),getCategory(),Toast.LENGTH_SHORT).show();


                       boolean result = controller.create(objectProgram);
                       if (result) {
                           Toast.makeText(getBaseContext(), "Student information was saved.", Toast.LENGTH_SHORT).show();
                       } else {
                            Toast.makeText(getBaseContext(), "Unable to save student information.", Toast.LENGTH_SHORT).show();
                       }




               }
               else
               {
                   controller.incrementofUsing(foregroundTaskAppName);
               }


                }
            });
    }

    private void _shutdownService()
    {
        if (timer != null) timer.cancel();

    }


    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    } */

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        _shutdownService();

    }




    //AsyncTask'a ihiyaç var.Web işlemi main thread'de gerçekleştirelimiyor.
    // App'in kategorisini bulma
    private class findCategory extends AsyncTask<String,Void,Void> {




        @Override
        protected  Void doInBackground(String... param) {

            StringBuilder stringBuilder;
            setCategory("null");

            try {


                // learn category of program
                stringBuilder = new StringBuilder();
                stringBuilder.append("https://play.google.com/store/apps/details?id=");

            String foregroundTaskPackageName = param[0];
            stringBuilder.append(foregroundTaskPackageName);
            String url = stringBuilder.toString();



                //Connect to web-site
                Document document = Jsoup.connect(url).get();

                Elements category = document.select("span[itemprop=genre]");
                setCategory(category.text());

                //kategori = category.text();



            }
            catch (CancellationException e) {
                e.printStackTrace();
            }



            catch (IOException e) {
                e.printStackTrace();
            }

            finally {


                return null;
            }


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Toast.makeText(getBaseContext(), kategori, Toast.LENGTH_SHORT).show();
        }
    }

}
