/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class GameTestDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos blockPos, int i, String string, int j) {
        this.markers.put(blockPos, new Marker(i, string, SystemUtil.getMeasuringTimeMs() + (long)j));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Environment(value=EnvType.CLIENT)
    static class Marker {
        public int color;
        public String message;
        public long removalTime;

        public Marker(int i, String string, long l) {
            this.color = i;
            this.message = string;
            this.removalTime = l;
        }
    }
}

