/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorFactory;
import net.minecraft.client.util.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

@Environment(value=EnvType.CLIENT)
public class MonitorTracker {
    private final Map<Long, Monitor> pointerToMonitorMap = Maps.newHashMap();
    private final Map<Long, Window> pointerToWindowMap = Maps.newHashMap();
    private final Map<Window, Monitor> windowToMonitorMap = Maps.newHashMap();
    private final MonitorFactory monitorFactory;

    public MonitorTracker(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
        GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        for (int i = 0; i < pointerBuffer.limit(); ++i) {
            long l = pointerBuffer.get(i);
            this.pointerToMonitorMap.put(l, monitorFactory.createMonitor(l));
        }
    }

    private void handleMonitorEvent(long l, int i) {
        if (i == 262145) {
            this.pointerToMonitorMap.put(l, this.monitorFactory.createMonitor(l));
        } else if (i == 262146) {
            this.pointerToMonitorMap.remove(l);
        }
    }

    public Monitor getMonitor(long l) {
        return this.pointerToMonitorMap.get(l);
    }

    public Monitor getMonitor(Window window) {
        long l = GLFW.glfwGetWindowMonitor(window.getHandle());
        if (l != 0L) {
            return this.getMonitor(l);
        }
        Monitor monitor = this.pointerToMonitorMap.values().iterator().next();
        int i = -1;
        int j = window.getPositionX();
        int k = j + window.getWidth();
        int m = window.getPositionY();
        int n = m + window.getHeight();
        for (Monitor monitor2 : this.pointerToMonitorMap.values()) {
            int x;
            int o = monitor2.getViewportX();
            int p = o + monitor2.getCurrentVideoMode().getWidth();
            int q = monitor2.getViewportY();
            int r = q + monitor2.getCurrentVideoMode().getHeight();
            int s = MonitorTracker.clamp(j, o, p);
            int t = MonitorTracker.clamp(k, o, p);
            int u = MonitorTracker.clamp(m, q, r);
            int v = MonitorTracker.clamp(n, q, r);
            int w = Math.max(0, t - s);
            int y = w * (x = Math.max(0, v - u));
            if (y <= i) continue;
            monitor = monitor2;
            i = y;
        }
        if (monitor != this.windowToMonitorMap.get(window)) {
            this.windowToMonitorMap.put(window, monitor);
        }
        return monitor;
    }

    public static int clamp(int i, int j, int k) {
        if (i < j) {
            return j;
        }
        if (i > k) {
            return k;
        }
        return i;
    }

    public void stop() {
        GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
        if (gLFWMonitorCallback != null) {
            gLFWMonitorCallback.free();
        }
    }
}

