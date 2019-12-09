/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientGameSession;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.MetricsData;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MinecraftClientGame
implements RunningGame {
    private final MinecraftClient client;
    @Nullable
    private final Launcher launcher;
    private SessionEventListener listener = SessionEventListener.NONE;

    public MinecraftClientGame(MinecraftClient client) {
        this.client = client;
        this.launcher = Bridge.getLauncher();
        if (this.launcher != null) {
            this.launcher.registerGame(this);
        }
    }

    @Override
    public GameVersion getVersion() {
        return SharedConstants.getGameVersion();
    }

    @Override
    public Language getSelectedLanguage() {
        return this.client.getLanguageManager().getLanguage();
    }

    @Override
    @Nullable
    public GameSession getCurrentSession() {
        ClientWorld clientWorld = this.client.world;
        return clientWorld == null ? null : new ClientGameSession(clientWorld, this.client.player, this.client.player.networkHandler);
    }

    @Override
    public PerformanceMetrics getPerformanceMetrics() {
        MetricsData metricsData = this.client.getMetricsData();
        long l = Integer.MAX_VALUE;
        long m = Integer.MIN_VALUE;
        long n = 0L;
        for (long o : metricsData.getSamples()) {
            l = Math.min(l, o);
            m = Math.max(m, o);
            n += o;
        }
        return new PerformanceMetricsImpl((int)l, (int)m, (int)(n / (long)metricsData.getSamples().length), metricsData.getSamples().length);
    }

    @Override
    public void setSessionEventListener(SessionEventListener listener) {
        this.listener = listener;
    }

    public void onStartGameSession() {
        this.listener.onStartGameSession(this.getCurrentSession());
    }

    public void onLeaveGameSession() {
        this.listener.onLeaveGameSession(this.getCurrentSession());
    }

    @Environment(value=EnvType.CLIENT)
    static class PerformanceMetricsImpl
    implements PerformanceMetrics {
        private final int minTime;
        private final int maxTime;
        private final int averageTime;
        private final int sampleCount;

        public PerformanceMetricsImpl(int minTime, int maxTime, int averageTime, int sampleCount) {
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.averageTime = averageTime;
            this.sampleCount = sampleCount;
        }

        @Override
        public int getMinTime() {
            return this.minTime;
        }

        @Override
        public int getMaxTime() {
            return this.maxTime;
        }

        @Override
        public int getAverageTime() {
            return this.averageTime;
        }

        @Override
        public int getSampleCount() {
            return this.sampleCount;
        }
    }
}

