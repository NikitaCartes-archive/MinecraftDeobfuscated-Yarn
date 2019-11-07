/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.VillagerNamer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class class_4703
implements DebugRenderer.Renderer {
    private final MinecraftClient field_21532;
    private final Map<BlockPos, class_4705> field_21533 = Maps.newHashMap();
    private final Map<UUID, class_4704> field_21534 = Maps.newHashMap();
    private UUID field_21535;

    public class_4703(MinecraftClient minecraftClient) {
        this.field_21532 = minecraftClient;
    }

    @Override
    public void clear() {
        this.field_21533.clear();
        this.field_21534.clear();
        this.field_21535 = null;
    }

    public void method_23807(class_4705 arg) {
        this.field_21533.put(arg.field_21543, arg);
    }

    public void method_23805(class_4704 arg) {
        this.field_21534.put(arg.field_21536, arg);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.method_23819();
        this.method_23823();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.field_21532.player.isSpectator()) {
            this.method_23832();
        }
    }

    private void method_23819() {
        long l = this.field_21532.world.getTime() - 20L;
        this.field_21533.entrySet().removeIf(entry -> ((class_4705)entry.getValue()).field_21546 < l);
    }

    private void method_23823() {
        BlockPos blockPos = this.method_23828().getBlockPos();
        this.field_21534.values().forEach(arg -> {
            if (this.method_23829((class_4704)arg)) {
                this.method_23824((class_4704)arg);
            }
        });
        this.method_23826();
        for (BlockPos blockPos22 : this.field_21533.keySet()) {
            if (!blockPos.isWithinDistance(blockPos22, 30.0)) continue;
            class_4703.method_23808(blockPos22);
        }
        this.field_21533.values().forEach(arg -> {
            if (blockPos.isWithinDistance(arg.field_21543, 30.0)) {
                this.method_23821((class_4705)arg);
            }
        });
        this.method_23830().forEach((blockPos2, list) -> {
            if (blockPos.isWithinDistance((Vec3i)blockPos2, 30.0)) {
                this.method_23813((BlockPos)blockPos2, (List<String>)list);
            }
        });
    }

    private void method_23826() {
        HashMap map = Maps.newHashMap();
        this.field_21534.values().stream().filter(class_4704::method_23836).forEach(arg -> {
            HashSet<UUID> set = (HashSet<UUID>)map.get(arg.field_21541);
            if (set == null) {
                set = Sets.newHashSet();
                map.put(arg.field_21541, set);
            }
            set.add(arg.method_23833());
        });
        map.entrySet().forEach(entry -> {
            BlockPos blockPos = (BlockPos)entry.getKey();
            Set set = (Set)entry.getValue();
            Set set2 = set.stream().map(VillagerNamer::name).collect(Collectors.toSet());
            class_4703.method_23816(set2.toString(), blockPos, 1, -256);
            class_4703.method_23816("Flower", blockPos, 2, -1);
            float f = 0.05f;
            class_4703.method_23809(blockPos, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static void method_23808(BlockPos blockPos) {
        float f = 0.05f;
        class_4703.method_23809(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void method_23813(BlockPos blockPos, List<String> list) {
        float f = 0.05f;
        class_4703.method_23809(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        class_4703.method_23816("" + list, blockPos, 0, -256);
        class_4703.method_23816("Ghost Hive", blockPos, 1, -65536);
    }

    private static void method_23809(BlockPos blockPos, float f, float g, float h, float i, float j) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.drawBox(blockPos, f, g, h, i, j);
    }

    private void method_23821(class_4705 arg) {
        int i = 0;
        if (this.method_23825(arg).isEmpty()) {
            class_4703.method_23815("Out: -", arg, i, -3355444);
        } else if (this.method_23825(arg).size() < 4) {
            class_4703.method_23815("Out: " + this.method_23825(arg), arg, i, -3355444);
        } else {
            class_4703.method_23815("Out: " + this.method_23825(arg).size() + " bees", arg, i, -3355444);
        }
        ++i;
        if (arg.field_21545 == 0) {
            class_4703.method_23815("In: -", arg, i, -256);
        } else if (arg.field_21545 == 1) {
            class_4703.method_23815("In: 1 bee", arg, i, -256);
        } else {
            class_4703.method_23815("In: " + arg.field_21545 + " bees", arg, i, -256);
        }
        class_4703.method_23815(arg.field_21544, arg, ++i, -1);
    }

    private void method_23820(class_4704 arg) {
        if (arg.field_21539 != null) {
            PathfindingDebugRenderer.drawPath(arg.field_21539, 0.5f, false, false, this.method_23828().getPos().getX(), this.method_23828().getPos().getY(), this.method_23828().getPos().getZ());
        }
    }

    private void method_23824(class_4704 arg) {
        boolean bl = this.method_23827(arg);
        int i = 0;
        class_4703.method_23814(arg.field_21538, i, arg.toString(), -1, 0.03f);
        ++i;
        if (arg.field_21540.equals(BlockPos.ORIGIN)) {
            class_4703.method_23814(arg.field_21538, i, "Homeless :(", -98404, 0.02f);
        } else {
            class_4703.method_23814(arg.field_21538, i, "Hive: " + this.method_23806(arg, arg.field_21540), -256, 0.02f);
        }
        ++i;
        if (arg.field_21541.equals(BlockPos.ORIGIN)) {
            class_4703.method_23814(arg.field_21538, i, "No flower :(", -98404, 0.02f);
        } else {
            class_4703.method_23814(arg.field_21538, i, "Flower: " + this.method_23806(arg, arg.field_21541), -256, 0.02f);
        }
        ++i;
        for (String string : arg.field_21542) {
            class_4703.method_23814(arg.field_21538, i, string, -16711936, 0.02f);
            ++i;
        }
        if (bl) {
            this.method_23820(arg);
        }
    }

    private static void method_23815(String string, class_4705 arg, int i, int j) {
        BlockPos blockPos = arg.field_21543;
        class_4703.method_23816(string, blockPos, i, j);
    }

    private static void method_23816(String string, BlockPos blockPos, int i, int j) {
        double d = 1.3;
        double e = 0.2;
        double f = (double)blockPos.getX() + 0.5;
        double g = (double)blockPos.getY() + 1.3 + (double)i * 0.2;
        double h = (double)blockPos.getZ() + 0.5;
        DebugRenderer.drawString(string, f, g, h, j, 0.02f, true, 0.0f, true);
    }

    private static void method_23814(Position position, int i, String string, int j, float f) {
        double d = 2.4;
        double e = 0.25;
        BlockPos blockPos = new BlockPos(position);
        double g = (double)blockPos.getX() + 0.5;
        double h = position.getY() + 2.4 + (double)i * 0.25;
        double k = (double)blockPos.getZ() + 0.5;
        float l = 0.5f;
        DebugRenderer.drawString(string, g, h, k, j, f, false, 0.5f, true);
    }

    private Camera method_23828() {
        return this.field_21532.gameRenderer.getCamera();
    }

    private Set<String> method_23825(class_4705 arg) {
        return this.method_23822(arg.field_21543).stream().map(VillagerNamer::name).collect(Collectors.toSet());
    }

    private String method_23806(class_4704 arg, BlockPos blockPos) {
        float f = MathHelper.sqrt(blockPos.getSquaredDistance(arg.field_21538.getX(), arg.field_21538.getY(), arg.field_21538.getZ(), true));
        double d = (double)Math.round(f * 10.0f) / 10.0;
        return blockPos.method_23854() + " (dist " + d + ")";
    }

    private boolean method_23827(class_4704 arg) {
        return Objects.equals(this.field_21535, arg.field_21536);
    }

    private boolean method_23829(class_4704 arg) {
        ClientPlayerEntity playerEntity = this.field_21532.player;
        BlockPos blockPos = new BlockPos(playerEntity.getX(), arg.field_21538.getY(), playerEntity.getZ());
        BlockPos blockPos2 = new BlockPos(arg.field_21538);
        return blockPos.isWithinDistance(blockPos2, 30.0);
    }

    private Collection<UUID> method_23822(BlockPos blockPos) {
        return this.field_21534.values().stream().filter(arg -> arg.method_23834(blockPos)).map(class_4704::method_23833).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> method_23830() {
        HashMap<BlockPos, List<String>> map = Maps.newHashMap();
        for (class_4704 lv : this.field_21534.values()) {
            if (lv.field_21540 == null || this.field_21533.containsKey(lv.field_21540)) continue;
            ArrayList<String> list = (ArrayList<String>)map.get(lv.field_21540);
            if (list == null) {
                list = Lists.newArrayList();
                map.put(lv.field_21540, list);
            }
            list.add(lv.method_23835());
        }
        return map;
    }

    private void method_23832() {
        DebugRenderer.getTargettedEntity(this.field_21532.getCameraEntity(), 8).ifPresent(entity -> {
            this.field_21535 = entity.getUuid();
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4704 {
        public final UUID field_21536;
        public final int field_21537;
        public final Position field_21538;
        @Nullable
        public final Path field_21539;
        public final BlockPos field_21540;
        public final BlockPos field_21541;
        public final List<String> field_21542 = Lists.newArrayList();

        public class_4704(UUID uUID, int i, Position position, Path path, BlockPos blockPos, BlockPos blockPos2) {
            this.field_21536 = uUID;
            this.field_21537 = i;
            this.field_21538 = position;
            this.field_21539 = path;
            this.field_21540 = blockPos;
            this.field_21541 = blockPos2;
        }

        public boolean method_23834(BlockPos blockPos) {
            return this.field_21540 != null && this.field_21540.equals(blockPos);
        }

        public UUID method_23833() {
            return this.field_21536;
        }

        public String method_23835() {
            return VillagerNamer.name(this.field_21536);
        }

        public String toString() {
            return this.method_23835();
        }

        public boolean method_23836() {
            return this.field_21541 != BlockPos.ORIGIN;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4705 {
        public final BlockPos field_21543;
        public final String field_21544;
        public final int field_21545;
        public final long field_21546;

        public class_4705(BlockPos blockPos, String string, int i, long l) {
            this.field_21543 = blockPos;
            this.field_21544 = string;
            this.field_21545 = i;
            this.field_21546 = l;
        }
    }
}

