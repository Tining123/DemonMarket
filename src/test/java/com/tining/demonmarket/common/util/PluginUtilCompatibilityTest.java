package com.tining.demonmarket.common.util;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class PluginUtilCompatibilityTest {

    @Test
    public void mapsLegacyFluidNamesToDisplayItems() throws Exception {
        assertEquals("WATER_BUCKET", normalizeMaterialName("WATER"));
        assertEquals("WATER_BUCKET", normalizeMaterialName("STATIONARYWATER"));
        assertEquals("LAVA_BUCKET", normalizeMaterialName("LAVA"));
        assertEquals("LAVA_BUCKET", normalizeMaterialName("STATIONARYLAVA"));
    }

    @Test
    public void leavesModernItemNamesUntouched() throws Exception {
        assertEquals("DIAMOND", normalizeMaterialName("DIAMOND"));
    }

    private String normalizeMaterialName(String name) throws Exception {
        Method method = PluginUtil.class.getDeclaredMethod("normalizeMaterialName", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, name);
    }
}
