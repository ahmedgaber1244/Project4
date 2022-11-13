package com.udacity.project4.utils

import android.content.Context

class SharedPref {

    fun getState(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("userState", Context.MODE_PRIVATE)
        return sharedPref?.getBoolean("state", false)!!
    }

    fun setState(context:Context,state:Boolean){
        val sharedPref = context.getSharedPreferences("userState", Context.MODE_PRIVATE)?.edit()
        sharedPref?.putBoolean("state", state)
        sharedPref?.commit()
    }
}