/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(value=EnvType.CLIENT)
public class LevelLoadingScreen
extends Screen {
    private static final long NARRATION_DELAY = 2000L;
    private final WorldGenerationProgressTracker progressProvider;
    private long lastNarrationTime = -1L;
    private boolean done;
    private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR = Util.make(new Object2IntOpenHashMap(), map -> {
        map.defaultReturnValue(0);
        map.put(ChunkStatus.EMPTY, 0x545454);
        map.put(ChunkStatus.STRUCTURE_STARTS, 0x999999);
        map.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        map.put(ChunkStatus.BIOMES, 8434258);
        map.put(ChunkStatus.NOISE, 0xD1D1D1);
        map.put(ChunkStatus.SURFACE, 7497737);
        map.put(ChunkStatus.CARVERS, 7169628);
        map.put(ChunkStatus.LIQUID_CARVERS, 3159410);
        map.put(ChunkStatus.FEATURES, 2213376);
        map.put(ChunkStatus.LIGHT, 0xCCCCCC);
        map.put(ChunkStatus.SPAWN, 15884384);
        map.put(ChunkStatus.HEIGHTMAPS, 0xEEEEEE);
        map.put(ChunkStatus.FULL, 0xFFFFFF);
    });

    public LevelLoadingScreen(WorldGenerationProgressTracker progressProvider) {
        super(NarratorManager.EMPTY);
        this.progressProvider = progressProvider;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void removed() {
        this.done = true;
        this.narrateScreenIfNarrationEnabled(true);
    }

    @Override
    protected void addElementNarrations(NarrationMessageBuilder builder) {
        if (this.done) {
            builder.put(NarrationPart.TITLE, (Text)Text.translatable("narrator.loading.done"));
        } else {
            String string = this.getPercentage();
            builder.put(NarrationPart.TITLE, string);
        }
    }

    private String getPercentage() {
        return MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastNarrationTime > 2000L) {
            this.lastNarrationTime = l;
            this.narrateScreenIfNarrationEnabled(true);
        }
        int i = this.width / 2;
        int j = this.height / 2;
        int k = 30;
        LevelLoadingScreen.drawChunkMap(matrices, this.progressProvider, i, j + 30, 2, 0);
        LevelLoadingScreen.drawCenteredText(matrices, this.textRenderer, this.getPercentage(), i, j - this.textRenderer.fontHeight / 2 - 30, 0xFFFFFF);
    }

    public static void drawChunkMap(MatrixStack matrices, WorldGenerationProgressTracker progressProvider, int centerX, int centerY, int pixelSize, int pixelMargin) {
        int i = pixelSize + pixelMargin;
        int j = progressProvider.getCenterSize();
        int k = j * i - pixelMargin;
        int l = progressProvider.getSize();
        int m = l * i - pixelMargin;
        int n = centerX - m / 2;
        int o = centerY - m / 2;
        int p = k / 2 + 1;
        int q = -16772609;
        if (pixelMargin != 0) {
            LevelLoadingScreen.fill(matrices, centerX - p, centerY - p, centerX - p + 1, centerY + p, -16772609);
            LevelLoadingScreen.fill(matrices, centerX + p - 1, centerY - p, centerX + p, centerY + p, -16772609);
            LevelLoadingScreen.fill(matrices, centerX - p, centerY - p, centerX + p, centerY - p + 1, -16772609);
            LevelLoadingScreen.fill(matrices, centerX - p, centerY + p - 1, centerX + p, centerY + p, -16772609);
        }
        for (int r = 0; r < l; ++r) {
            for (int s = 0; s < l; ++s) {
                ChunkStatus chunkStatus = progressProvider.getChunkStatus(r, s);
                int t = n + r * i;
                int u = o + s * i;
                LevelLoadingScreen.fill(matrices, t, u, t + pixelSize, u + pixelSize, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
            }
        }
    }
}

