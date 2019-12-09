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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(value=EnvType.CLIENT)
public class LevelLoadingScreen
extends Screen {
    private final WorldGenerationProgressTracker progressProvider;
    private long field_19101 = -1L;
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
        NarratorManager.INSTANCE.narrate(I18n.translate("narrator.loading.done", new Object[0]));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        String string = MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
        long l = Util.getMeasuringTimeMs();
        if (l - this.field_19101 > 2000L) {
            this.field_19101 = l;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.loading", string).getString());
        }
        int i = this.width / 2;
        int j = this.height / 2;
        int k = 30;
        LevelLoadingScreen.drawChunkMap(this.progressProvider, i, j + 30, 2, 0);
        this.drawCenteredString(this.font, string, i, j - this.font.fontHeight / 2 - 30, 0xFFFFFF);
    }

    public static void drawChunkMap(WorldGenerationProgressTracker progressProvider, int centerX, int centerY, int chunkSize, int i) {
        int j = chunkSize + i;
        int k = progressProvider.getCenterSize();
        int l = k * j - i;
        int m = progressProvider.getSize();
        int n = m * j - i;
        int o = centerX - n / 2;
        int p = centerY - n / 2;
        int q = l / 2 + 1;
        int r = -16772609;
        if (i != 0) {
            LevelLoadingScreen.fill(centerX - q, centerY - q, centerX - q + 1, centerY + q, -16772609);
            LevelLoadingScreen.fill(centerX + q - 1, centerY - q, centerX + q, centerY + q, -16772609);
            LevelLoadingScreen.fill(centerX - q, centerY - q, centerX + q, centerY - q + 1, -16772609);
            LevelLoadingScreen.fill(centerX - q, centerY + q - 1, centerX + q, centerY + q, -16772609);
        }
        for (int s = 0; s < m; ++s) {
            for (int t = 0; t < m; ++t) {
                ChunkStatus chunkStatus = progressProvider.getChunkStatus(s, t);
                int u = o + s * j;
                int v = p + t * j;
                LevelLoadingScreen.fill(u, v, u + chunkSize, v + chunkSize, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
            }
        }
    }
}

