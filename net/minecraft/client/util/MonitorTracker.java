/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
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

@Environment(value=EnvType.CLIENT)
public class MonitorTracker {
    private final Long2ObjectMap<Monitor> pointerToMonitorMap = new Long2ObjectOpenHashMap<Monitor>();
    private final MonitorFactory monitorFactory;

    public MonitorTracker(MonitorFactory monitorFactory) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
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

    private void handleMonitorEvent(long l, int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i == 262145) {
            this.pointerToMonitorMap.put(l, this.monitorFactory.createMonitor(l));
        } else if (i == 262146) {
            this.pointerToMonitorMap.remove(l);
        }
    }

    @Nullable
    public Monitor getMonitor(long l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return (Monitor)this.pointerToMonitorMap.get(l);
    }

    @Nullable
    public Monitor getMonitor(Window window) {
        long l = GLFW.glfwGetWindowMonitor(window.getHandle());
        if (l != 0L) {
            return this.getMonitor(l);
        }
        int i = window.getPositionY();
        int j = i + window.getWidth();
        int k = window.getPositionX();
        int m = k + window.getHeight();
        int n = -1;
        Monitor monitor = null;
        for (Monitor monitor2 : this.pointerToMonitorMap.values()) {
            int x;
            int o = monitor2.getViewportX();
            int p = o + monitor2.getCurrentVideoMode().getWidth();
            int q = monitor2.getViewportY();
            int r = q + monitor2.getCurrentVideoMode().getHeight();
            int s = MonitorTracker.clamp(i, o, p);
            int t = MonitorTracker.clamp(j, o, p);
            int u = MonitorTracker.clamp(k, q, r);
            int v = MonitorTracker.clamp(m, q, r);
            int w = Math.max(0, t - s);
            int y = w * (x = Math.max(0, v - u));
            if (y <= n) continue;
            monitor = monitor2;
            n = y;
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
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
        if (gLFWMonitorCallback != null) {
            gLFWMonitorCallback.free();
        }
    }
}

