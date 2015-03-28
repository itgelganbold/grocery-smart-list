package com.example.john.smartlist;

import com.estimote.sdk.Region;

/**
 * Created by mshehab on 3/28/15.
 */
public class BeaconUtils {

    public static String getUUICombined(Region region){
        return region.getProximityUUID() + "-" + region.getMajor()+ "-" + region.getMinor();
    }

}
