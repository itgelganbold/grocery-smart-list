package com.example.john.smartlist;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

/**
 * Created by mshehab on 3/28/15.
 */
public class RegionItem {
    int count = 0;
    Region region;
    double distance = -1;

    public RegionItem(Region region, Beacon beacon){
        this.region = region;
        int count = 5;
        distance = Utils.computeAccuracy(beacon);
    }

    public void updateDistance(double distance){
        this.distance = distance;
    }

    public void resetCount(){
        count = 5;
    }

    public void decrementCount(){
        count = count -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionItem that = (RegionItem) o;
        if (!region.getIdentifier().equals(that.region.getIdentifier())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return region.getIdentifier().hashCode();
    }

    @Override
    public String toString() {
        return "RegionItem{" +
                "region=" + region.getIdentifier() +
                '}';
    }
}
