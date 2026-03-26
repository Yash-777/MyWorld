package com.github.yash777.basic;

import java.util.HashMap;
import java.util.Map;

public class NtreuExample {

    public static void main(String[] args) {

        Map<String, Double> rollbackVolumes = new HashMap<>();

        // Sample values
        rollbackVolumes.put("NTREU_POSITIVE", 5.0);
        rollbackVolumes.put("NTREU_NEGATIVE", -2.0);
        rollbackVolumes.put("NTREU_ZERO", 0.0);
        // NTREU_NULL not added to map

        testValue(0d, rollbackVolumes.get("NTREU_POSITIVE"));
        testValue(1d, rollbackVolumes.get("NTREU_POSITIVE"));
        testValue(0d, rollbackVolumes.get("NTREU_NEGATIVE"));
        testValue(2d, rollbackVolumes.get("NTREU_NEGATIVE"));
        testValue(3d, rollbackVolumes.get("NTREU_NEGATIVE"));
        testValue(0d, rollbackVolumes.get("NTREU_ZERO"));
        testValue(0d, rollbackVolumes.get("NTREU_NULL"));
    }

    private static void testValue(Double nteu, Double value) {

        // Default to 0 if null
        double ntreu = value != null ? value : 0d;

        // Always positive
        ntreu = Math.abs(ntreu);

        // Example logic
        Integer count = (int) (nteu - ntreu);

        System.out.println("Final NTEU:"+nteu+", NTREU:" + ntreu + ", count: " + count);

        if (count.intValue() > 0) {
            System.out.println("→ calculateCumBonus called\n");
        } else {
            System.out.println("→ calculateCumBonus NOT called\n");
        }
    }
}
