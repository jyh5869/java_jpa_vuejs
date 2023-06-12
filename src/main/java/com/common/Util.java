package com.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Util {
    
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    /**
     * @method 날짜 변환
     * @param targetDate 현재날짜
     * @param type 변환할 날짜 타입
     * @return
     */
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

    /**
     * @method 작업시간 측정
     * @param type 타입(시작/종료)
     * @param msg 작업명(로깅 메세지용)
     * @param targetTime 시간
     * @param status 상태(작업 성공여부)
     * @return
     * @throws InterruptedException
     */
    public static long durationTime (String type, String msg, long targetTime, String status ) throws InterruptedException{
        
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss.SS");

        if(type.equals("start")){//START

            long reqTime = System.currentTimeMillis(); 
		    String reqTimeStr = dayTime.format(new Date(reqTime));

            LOG.info("Start - " + msg + " Process : TIME:" + reqTimeStr + ", STATUS : " + status + "DURATION" );

            return reqTime;   
        }
        else{//END
            //Thread.sleep(1);// ms 단위로 Delay
            long reqTime = targetTime; 
            long resTime = System.currentTimeMillis(); 
		    String resTimeStr = dayTime.format(resTime);
            double durationTime = (resTime - reqTime)/1000.000;
            
            LOG.info("End - " + msg + " Process : TIME:" + resTimeStr + ", STATUS : " + status + "DURATION : " + durationTime );
            
            return resTime;
        }	
    }
}
