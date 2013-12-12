package com.grad.nagara;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Object;

/**
 * Created by CST on 13/12/05.
 */
public class Debugger {
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static void Print(String str){
        String dstDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        File file = new File(dstDir+"/memo.txt");
        file.getParentFile().mkdir();

        try{
            FileOutputStream fos = new FileOutputStream(file,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            PrintWriter pw = new PrintWriter(osw);

            pw.append(str);
            pw.close();
            osw.close();
            fos.close();
        } catch (FileNotFoundException e) { }
        catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        Log.d("MyApp",""+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
    }
}
