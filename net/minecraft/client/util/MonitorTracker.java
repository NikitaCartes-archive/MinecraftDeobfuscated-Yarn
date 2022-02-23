/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorFactory;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MonitorTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Long2ObjectMap<Monitor> pointerToMonitorMap = new Long2ObjectOpenHashMap<Monitor>();
    private final MonitorFactory monitorFactory;

    public MonitorTracker(MonitorFactory monitorFactory) {
        RenderSystem.assertInInitPhase();
        this.monitorFactory = monitorFactory;
        GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            for (int i = 0; i < pointerBuffer.limit(); ++i) {
                long l = pointerBuffer.get(i);
                this.pointerToMonitorMap.put(l, monitorFactory.createMonitor(l));
            }
        }
    }

    private void handleMonitorEvent(long monitor, int event) {
        RenderSystem.assertOnRenderThread();
        if (event == 262145) {
            this.pointerToMonitorMap.put(monitor, this.monitorFactory.createMonitor(monitor));
            LOGGER.debug("Monitor {} connected. Current monitors: {}", (Object)monitor, (Object)this.pointerToMonitorMap);
        } else if (event == 262146) {
            this.pointerToMonitorMap.remove(monitor);
            LOGGER.debug("Monitor {} disconnected. Current monitors: {}", (Object)monitor, (Object)this.pointerToMonitorMap);
        }
    }

    @Nullable
    public Monitor getMonitor(long pointer) {
        RenderSystem.assertInInitPhase();
        return (Monitor)this.pointerToMonitorMap.get(pointer);
    }

    @Nullable
    public Monitor getMonitor(Window window) {
        long l = GLFW.glfwGetWindowMonitor(window.getHandle());
        if (l != 0L) {
            return this.getMonitor(l);
        }
        int i = window.getX();
        int j = i + window.getWidth();
        int k = window.getY();
        int m = k + window.getHeight();
        int n = -1;
        Monitor monitor = null;
        long o = GLFW.glfwGetPrimaryMonitor();
        LOGGER.debug("Selecting monitor - primary: {}, current monitors: {}", (Object)o, (Object)this.pointerToMonitorMap);
        for (Monitor monitor2 : this.pointerToMonitorMap.values()) {
            int y;
            int p = monitor2.getViewportX();
            int q = p + monitor2.getCurrentVideoMode().getWidth();
            int r = monitor2.getViewportY();
            int s = r + monitor2.getCurrentVideoMode().getHeight();
            int t = MonitorTracker.clamp(i, p, q);
            int u = MonitorTracker.clamp(j, p, q);
            int v = MonitorTracker.clamp(k, r, s);
            int w = MonitorTracker.clamp(m, r, s);
            int x = Math.max(0, u - t);
            int z = x * (y = Math.max(0, w - v));
            if (z > n) {
                monitor = monitor2;
                n = z;
                continue;
            }
            if (z != n || o != monitor2.getHandle()) continue;
            LOGGER.debug("Primary monitor {} is preferred to monitor {}", (Object)monitor2, (Object)monitor);
            monitor = monitor2;
        }
        LOGGER.debug("Selected monitor: {}", (Object)monitor);
        return monitor;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public void stop() {
        RenderSystem.assertOnRenderThread();
        GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
        if (gLFWMonitorCallback != null) {
            gLFWMonitorCallback.free();
        }
    }
}

