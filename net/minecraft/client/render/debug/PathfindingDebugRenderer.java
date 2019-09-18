/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.SystemUtil;

@Environment(value=EnvType.CLIENT)
public class PathfindingDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private final Map<Integer, Path> paths = Maps.newHashMap();
    private final Map<Integer, Float> field_4617 = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();

    public PathfindingDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void addPath(int i, Path path, float f) {
        this.paths.put(i, path);
        this.pathTimes.put(i, SystemUtil.getMeasuringTimeMs());
        this.field_4617.put(i, Float.valueOf(f));
    }
}

