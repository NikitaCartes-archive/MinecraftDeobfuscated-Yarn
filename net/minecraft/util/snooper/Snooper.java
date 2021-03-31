/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.snooper.SnooperListener;

public class Snooper {
    private static final String field_29957 = "http://snoop.minecraft.net/";
    private static final long field_29958 = 900000L;
    private static final int field_29959 = 2;
    private final Map<String, Object> initialInfo = Maps.newHashMap();
    private final Map<String, Object> info = Maps.newHashMap();
    private final String token = UUID.randomUUID().toString();
    private final URL snooperUrl;
    private final SnooperListener listener;
    private final Timer timer = new Timer("Snooper Timer", true);
    private final Object syncObject = new Object();
    private final long startTime;
    private boolean active;
    private int field_29960;

    public Snooper(String urlPath, SnooperListener listener, long startTime) {
        try {
            this.snooperUrl = new URL(field_29957 + urlPath + "?version=" + 2);
        } catch (MalformedURLException malformedURLException) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
        this.startTime = startTime;
    }

    public void method_5482() {
        if (!this.active) {
            // empty if block
        }
    }

    private void method_35030() {
        this.method_35032();
        this.addInfo("snooper_token", this.token);
        this.addInitialInfo("snooper_token", this.token);
        this.addInitialInfo("os_name", System.getProperty("os.name"));
        this.addInitialInfo("os_version", System.getProperty("os.version"));
        this.addInitialInfo("os_architecture", System.getProperty("os.arch"));
        this.addInitialInfo("java_version", System.getProperty("java.version"));
        this.addInfo("version", SharedConstants.getGameVersion().getId());
        this.listener.method_35034(this);
    }

    private void method_35032() {
        int[] is = new int[]{0};
        Util.getJVMFlags().forEach(string -> {
            int n = is[0];
            is[0] = n + 1;
            this.addInfo("jvm_arg[" + n + "]", string);
        });
        this.addInfo("jvm_args", is[0]);
    }

    public void update() {
        this.addInitialInfo("memory_total", Runtime.getRuntime().totalMemory());
        this.addInitialInfo("memory_max", Runtime.getRuntime().maxMemory());
        this.addInitialInfo("memory_free", Runtime.getRuntime().freeMemory());
        this.addInitialInfo("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.listener.addSnooperInfo(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addInfo(String key, Object value) {
        Object object = this.syncObject;
        synchronized (object) {
            this.info.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addInitialInfo(String key, Object value) {
        Object object = this.syncObject;
        synchronized (object) {
            this.initialInfo.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, String> method_35024() {
        LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
        Object object = this.syncObject;
        synchronized (object) {
            this.update();
            for (Map.Entry<String, Object> entry : this.initialInfo.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
            for (Map.Entry<String, Object> entry : this.info.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return map;
    }

    public boolean isActive() {
        return this.active;
    }

    public void cancel() {
        this.timer.cancel();
    }

    public String getToken() {
        return this.token;
    }

    public long getStartTime() {
        return this.startTime;
    }
}

