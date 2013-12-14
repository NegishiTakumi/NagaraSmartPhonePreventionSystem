package com.grad.nagara;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by CST on 13/12/05.
 */
public class Debugger {
    public static final String TAG = "__Debugger";

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static void Print(String str,int index){
        String dstDir = getMount_sd();
        //String dstDir = Environment.getExternalStorageDirectory().getPath();
        File file = new File(dstDir+"/nagara/Seisilog"+index+".csv");
        file.getParentFile().mkdir();

        try{
            FileOutputStream fos = new FileOutputStream(file,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            PrintWriter pw = new PrintWriter(osw);

            pw.append(str);
            pw.close();
            osw.close();
            fos.close();
            Log.d(TAG,"Write OK-" +dstDir);
        } catch (FileNotFoundException e) { }
        catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        Log.d("MyApp",""+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
    }


    @TargetApi(9)
    private static String getMount_sd() {
        List<String> mountList = new ArrayList<String>();
        String mount_sdcard = null;

        Scanner scanner = null;
        try {
            // システム設定ファイルにアクセス
            File vold_fstab = new File("/system/etc/vold.fstab");
            scanner = new Scanner(new FileInputStream(vold_fstab));
            // 一行ずつ読み込む
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // dev_mountまたはfuse_mountで始まる行の
                if (line.startsWith("dev_mount") || line.startsWith("fuse_mount")) {
                    // 半角スペースではなくタブで区切られている機種もあるらしいので修正して
                    // 半角スペース区切り３つめ（path）を取得
                    String path = line.replaceAll("\t", " ").split(" ")[2];
                    // 取得したpathを重複しないようにリストに登録
                    if (!mountList.contains(path)){
                        mountList.add(path);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        // Environment.isExternalStorageRemovable()はGINGERBREAD以降しか使えない
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            // getExternalStorageDirectory()が罠であれば、そのpathをリストから除外
            if (!Environment.isExternalStorageRemovable()) {   // 注1
                mountList.remove(Environment.getExternalStorageDirectory().getPath());
            }
        }

        // マウントされていないpathは除外
        for (int i = 0; i < mountList.size(); i++) {
            if (!isMounted(mountList.get(i))){
                mountList.remove(i--);
            }
        }

        // 除外されずに残ったものがSDカードのマウント先
        if(mountList.size() > 0){
            mount_sdcard = mountList.get(0);
        }

        // マウント先をreturn（全て除外された場合はnullをreturn）
        return mount_sdcard;
    }

    // 引数に渡したpathがマウントされているかどうかチェックするメソッド
    public static boolean isMounted(String path) {
        boolean isMounted = false;

        Scanner scanner = null;
        try {
            // マウントポイントを取得する
            File mounts = new File("/proc/mounts");   // 注2
            scanner = new Scanner(new FileInputStream(mounts));
            // マウントポイントに該当するパスがあるかチェックする
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().contains(path)) {
                    // 該当するパスがあればマウントされているってこと
                    isMounted = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        // マウント状態をreturn
        return isMounted;
    }

}
