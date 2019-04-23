/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.DebugNameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PointOfInterestDebugRenderer
implements DebugRenderer.Renderer {
    private static final Logger field_18920 = LogManager.getLogger();
    private final MinecraftClient field_18786;
    private final Map<BlockPos, class_4233> pointsOfInterest = Maps.newHashMap();
    private final Set<ChunkSectionPos> field_18788 = Sets.newHashSet();
    private final Map<UUID, class_4232> field_18921 = Maps.newHashMap();
    private UUID field_18922;

    public PointOfInterestDebugRenderer(MinecraftClient minecraftClient) {
        this.field_18786 = minecraftClient;
    }

    @Override
    public void method_20414() {
        this.pointsOfInterest.clear();
        this.field_18788.clear();
        this.field_18921.clear();
        this.field_18922 = null;
    }

    public void method_19701(class_4233 arg) {
        this.pointsOfInterest.put(arg.field_18931, arg);
    }

    public void removePointOfInterest(BlockPos blockPos) {
        this.pointsOfInterest.remove(blockPos);
    }

    public void method_19702(BlockPos blockPos, int i) {
        class_4233 lv = this.pointsOfInterest.get(blockPos);
        if (lv == null) {
            field_18920.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
            return;
        }
        lv.field_18933 = i;
    }

    public void method_19433(ChunkSectionPos chunkSectionPos) {
        this.field_18788.add(chunkSectionPos);
    }

    public void method_19435(ChunkSectionPos chunkSectionPos) {
        this.field_18788.remove(chunkSectionPos);
    }

    public void addPointOfInterest(class_4232 arg) {
        this.field_18921.put(arg.field_18923, arg);
    }

    @Override
    public void render(long l) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        this.method_19699();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        this.method_19710();
    }

    private void method_19699() {
        BlockPos blockPos = this.getCamera().getBlockPos();
        for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            PointOfInterestDebugRenderer.method_19709(blockPos2);
        }
        for (ChunkSectionPos chunkSectionPos : this.field_18788) {
            if (!blockPos.isWithinDistance(chunkSectionPos.getCenterPos(), 160.0)) continue;
            PointOfInterestDebugRenderer.method_19714(chunkSectionPos);
        }
        for (class_4232 lv : this.field_18921.values()) {
            if (!this.method_19715(lv)) continue;
            this.method_19707(lv);
        }
        for (class_4233 lv2 : this.pointsOfInterest.values()) {
            if (!blockPos.isWithinDistance(lv2.field_18931, 160.0)) continue;
            this.method_19708(lv2);
        }
    }

    private static void method_19714(ChunkSectionPos chunkSectionPos) {
        float f = 1.0f;
        BlockPos blockPos = chunkSectionPos.getCenterPos();
        BlockPos blockPos2 = blockPos.add(-1.0, -1.0, -1.0);
        BlockPos blockPos3 = blockPos.add(1.0, 1.0, 1.0);
        DebugRenderer.method_19697(blockPos2, blockPos3, 0.2f, 1.0f, 0.2f, 0.15f);
    }

    private static void method_19709(BlockPos blockPos) {
        float f = 0.05f;
        DebugRenderer.method_19696(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void method_19708(class_4233 arg) {
        int i = 0;
        PointOfInterestDebugRenderer.method_19705("Ticket holders: " + this.method_19712(arg), arg, i, -256);
        PointOfInterestDebugRenderer.method_19705("Free tickets: " + arg.field_18933, arg, ++i, -256);
        PointOfInterestDebugRenderer.method_19705(arg.field_18932, arg, ++i, -1);
    }

    private void method_19707(class_4232 arg) {
        int i = 0;
        PointOfInterestDebugRenderer.method_19704(arg.field_18926, i, arg.field_18925, -1, 0.03f);
        ++i;
        for (String string : arg.field_18928) {
            PointOfInterestDebugRenderer.method_19704(arg.field_18926, i, string, -16711681, 0.02f);
            ++i;
        }
        for (String string : arg.field_18927) {
            PointOfInterestDebugRenderer.method_19704(arg.field_18926, i, string, -16711936, 0.02f);
            ++i;
        }
        if (this.method_19711(arg)) {
            for (String string : Lists.reverse(arg.field_18929)) {
                PointOfInterestDebugRenderer.method_19704(arg.field_18926, i, string, -3355444, 0.02f);
                ++i;
            }
        }
    }

    private static void method_19705(String string, class_4233 arg, int i, int j) {
        double d = 1.3;
        double e = 0.2;
        double f = (double)arg.field_18931.getX() + 0.5;
        double g = (double)arg.field_18931.getY() + 1.3 + (double)i * 0.2;
        double h = (double)arg.field_18931.getZ() + 0.5;
        DebugRenderer.method_3712(string, f, g, h, j, 0.02f, true, 0.0f, true);
    }

    private static void method_19704(Position position, int i, String string, int j, float f) {
        double d = 2.4;
        double e = 0.25;
        BlockPos blockPos = new BlockPos(position);
        double g = (double)blockPos.getX() + 0.5;
        double h = position.getY() + 2.4 + (double)i * 0.25;
        double k = (double)blockPos.getZ() + 0.5;
        float l = 0.5f;
        DebugRenderer.method_3712(string, g, h, k, j, f, false, 0.5f, true);
    }

    private Camera getCamera() {
        return this.field_18786.gameRenderer.getCamera();
    }

    private Set<String> method_19712(class_4233 arg) {
        return this.method_19713(arg.field_18931).stream().map(DebugNameGenerator::getDebugName).collect(Collectors.toSet());
    }

    private boolean method_19711(class_4232 arg) {
        return Objects.equals(this.field_18922, arg.field_18923);
    }

    private boolean method_19715(class_4232 arg) {
        ClientPlayerEntity playerEntity = this.field_18786.player;
        BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
        BlockPos blockPos2 = new BlockPos(arg.field_18926);
        return blockPos.isWithinDistance(blockPos2, 160.0);
    }

    private Collection<UUID> method_19713(BlockPos blockPos) {
        return this.field_18921.values().stream().filter(arg -> ((class_4232)arg).method_19718(blockPos)).map(class_4232::method_19716).collect(Collectors.toSet());
    }

    private void method_19710() {
        DebugRenderer.method_19694(this.field_18786.getCameraEntity(), 16).ifPresent(entity -> {
            this.field_18922 = entity.getUuid();
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4232 {
        public final UUID field_18923;
        public final int field_18924;
        public final String field_18925;
        public final Position field_18926;
        public final List<String> field_18927 = Lists.newArrayList();
        public final List<String> field_18928 = Lists.newArrayList();
        public final List<String> field_18929 = Lists.newArrayList();
        public final Set<BlockPos> field_18930 = Sets.newHashSet();

        public class_4232(UUID uUID, int i, String string, Position position) {
            this.field_18923 = uUID;
            this.field_18924 = i;
            this.field_18925 = string;
            this.field_18926 = position;
        }

        private boolean method_19718(BlockPos blockPos) {
            return this.field_18930.stream().anyMatch(blockPos::equals);
        }

        public UUID method_19716() {
            return this.field_18923;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4233 {
        public final BlockPos field_18931;
        public String field_18932;
        public int field_18933;

        public class_4233(BlockPos blockPos, String string, int i) {
            this.field_18931 = blockPos;
            this.field_18932 = string;
            this.field_18933 = i;
        }
    }
}

