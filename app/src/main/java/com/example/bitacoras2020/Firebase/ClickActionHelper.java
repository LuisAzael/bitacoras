package com.example.bitacoras2020.Firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ClickActionHelper {
    public static void startActivity(String className, Context context){
        Class cls = null;
        try {
            cls = Class.forName(className);
        }catch(ClassNotFoundException e){
            //means you made a wrong input in firebase console
        }
        Intent i = new Intent(context, cls);
        context.startActivity(i);
    }
}