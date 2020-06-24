/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.snooper.SnooperListener;

public class Snooper {
    private final Map<String, Object> initialInfo = Maps.newHashMap();
    private final Map<String, Object> info = Maps.newHashMap();
    private final String token = UUID.randomUUID().toString();
    private final URL snooperUrl;
    private final SnooperListener listener;
    private final Timer timer = new Timer("Snooper Timer", true);
    private final Object syncObject = new Object();
    private final long startTime;
    private boolean active;

    public Snooper(String urlPath, SnooperListener listener, long startTime) {
        try {
            this.snooperUrl = new URL("http://snoop.minecraft.net/" + urlPath + "?version=" + 2);
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

    public boolean isActive() {
        return this.active;
    }

    public void cancel() {
        this.timer.cancel();
    }

    @Environment(value=EnvType.CLIENT)
    public String getToken() {
        return this.token;
    }

    public long getStartTime() {
        return this.startTime;
    }
}

