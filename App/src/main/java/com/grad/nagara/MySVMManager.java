package com.grad.nagara;

import android.os.Environment;
import android.util.Log;

import com.grad.nagara.libsvm.svm;
import com.grad.nagara.libsvm.svm_model;
import com.grad.nagara.libsvm.svm_node;

import java.io.IOException;

/**
 * Created by CST on 13/12/15.
 */
public class MySVMManager {
    public static final String TAG = "__SVMManager";

    svm_node[] input;
    public MySVMManager(float[] data){
        int elemcnt = data.length;
       input = new svm_node[elemcnt];
        InitNode(input);

        for(int i = 0; i<elemcnt; i++){
            input[i].index = i+1;
            input[i].value = (double)data[i];
        }
    }
    private void InitNode(svm_node[] node){
        for(int i = 0; i<node.length; i++){
            node[i] = new svm_node();
        }
    }

    public double getAccuration() {
        svm_model model = null;
        try {
            String srcDir = Environment.getExternalStorageDirectory().getPath()+"/nagaraData";
            model = svm.svm_load_model(srcDir + "/nagarainput.txt.model");

            return svm.svm_predict(model,input);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"Cant load model");
        }
        return -1;
    }


}
