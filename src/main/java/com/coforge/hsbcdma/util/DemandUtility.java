package com.coforge.hsbcdma.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Utitlity class for static methods.
 * Created By : pratish.b
 */
public class DemandUtility {

    private static final Logger logger = LoggerFactory.getLogger(DemandUtility.class);

    public static int getCurrentDemandSequence(Optional<String> currentDemandId){
        int currentNumber = 0;
        if(currentDemandId.isPresent()){
            String[] strArray = currentDemandId.get().split("-");
            currentNumber = Integer.parseInt(strArray[1].trim());
        }
        return currentNumber;
    }
   }
