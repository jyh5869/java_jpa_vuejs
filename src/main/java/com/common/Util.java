package com.common;

import java.text.SimpleDateFormat;

public class Util {
    

    public static String remakeDate (Long targetDate, int type){
        
        SimpleDateFormat SDF;
        String toDayFormat;

        if(type == 1){
            SDF = new SimpleDateFormat("yyyy-MM-dd");
            toDayFormat = SDF.format(targetDate);
            return toDayFormat;
        }
        return "Error";
        
    }
}
