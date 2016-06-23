package com.DFM.StormFront.Util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Mick on 4/22/2016.
 */
public class LogUtil {

    public static void log(String msg){
        System.out.println(msg);
    }

    public static void log(String msg, String file){
        try(  PrintWriter out = new PrintWriter( file )  ){
            out.println( msg );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
