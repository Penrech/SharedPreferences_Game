package com.pauenrech.regalonavidadpauenrech.tools

import android.os.AsyncTask
import android.util.Log
import java.lang.Exception

class WaitSecondsAsyncTask(val taskCompleteListener: TaskCompleted ): AsyncTask<Int,Void,Boolean>() {

    interface TaskCompleted{
        fun asyncTaskCompleted()
    }

    override fun doInBackground(vararg params: Int?): Boolean {
        val miliseconds = (params[0]!! * 1000).toLong()
        try {
            Thread.sleep(miliseconds)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return true
    }

    override fun onPostExecute(result: Boolean?) {
        taskCompleteListener.asyncTaskCompleted()
        super.onPostExecute(result)
    }
}