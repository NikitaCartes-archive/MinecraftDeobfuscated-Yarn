/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ExtendedBlockView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockModelRenderer {
    private final BlockColors colorMap;
    private static final ThreadLocal<BrightnessCache> brightnessCache = ThreadLocal.withInitial(() -> new BrightnessCache());

    public BlockModelRenderer(BlockColors blockColors) {
        this.colorMap = blockColors;
    }

    public boolean tesselate(ExtendedBlockView extendedBlockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, Random random, long l) {
        boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();
        try {
            if (bl2) {
                return this.tesselateSmooth(extendedBlockView, bakedModel, blockState, blockPos, bufferBuilder, bl, random, l);
            }
            return this.tesselateFlat(extendedBlockView, bakedModel, blockState, blockPos, bufferBuilder, bl, random, l);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
            crashReportSection.add("Using AO", bl2);
            throw new CrashException(crashReport);
        }
    }

    public boolean tesselateSmooth(ExtendedBlockView extendedBlockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, Random random, long l) {
        boolean bl2 = false;
        float[] fs = new float[Direction.values().length * 2];
        BitSet bitSet = new BitSet(3);
        AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
            if (list.isEmpty() || bl && !Block.shouldDrawSide(blockState, extendedBlockView, blockPos, direction)) continue;
            this.tesselateQuadsSmooth(extendedBlockView, blockState, blockPos, bufferBuilder, list, fs, bitSet, ambientOcclusionCalculator);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = bakedModel.getQuads(blockState, null, random);
        if (!list2.isEmpty()) {
            this.tesselateQuadsSmooth(extendedBlockView, blockState, blockPos, bufferBuilder, list2, fs, bitSet, ambientOcclusionCalculator);
            bl2 = true;
        }
        return bl2;
    }

    public boolean tesselateFlat(ExtendedBlockView extendedBlockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, Random random, long l) {
        boolean bl2 = false;
        BitSet bitSet = new BitSet(3);
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
            if (list.isEmpty() || bl && !Block.shouldDrawSide(blockState, extendedBlockView, blockPos, direction)) continue;
            int i = blockState.getBlockBrightness(extendedBlockView, blockPos.offset(direction));
            this.tesselateQuadsFlat(extendedBlockView, blockState, blockPos, i, false, bufferBuilder, list, bitSet);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = bakedModel.getQuads(blockState, null, random);
        if (!list2.isEmpty()) {
            this.tesselateQuadsFlat(extendedBlockView, blockState, blockPos, -1, true, bufferBuilder, list2, bitSet);
            bl2 = true;
        }
        return bl2;
    }

    private void tesselateQuadsSmooth(ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, List<BakedQuad> list, float[] fs, BitSet bitSet, AmbientOcclusionCalculator ambientOcclusionCalculator) {
        Vec3d vec3d = blockState.getOffsetPos(extendedBlockView, blockPos);
        double d = (double)blockPos.getX() + vec3d.x;
        double e = (double)blockPos.getY() + vec3d.y;
        double f = (double)blockPos.getZ() + vec3d.z;
        int j = list.size();
        for (int i = 0; i < j; ++i) {
            BakedQuad bakedQuad = list.get(i);
            this.updateShape(extendedBlockView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), fs, bitSet);
            ambientOcclusionCalculator.apply(extendedBlockView, blockState, blockPos, bakedQuad.getFace(), fs, bitSet);
            bufferBuilder.putVertexData(bakedQuad.getVertexData());
            bufferBuilder.brightness(ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[3]);
            if (bakedQuad.hasColor()) {
                int k = this.colorMap.getColorMultiplier(blockState, extendedBlockView, blockPos, bakedQuad.getColorIndex());
                float g = (float)(k >> 16 & 0xFF) / 255.0f;
                float h = (float)(k >> 8 & 0xFF) / 255.0f;
                float l = (float)(k & 0xFF) / 255.0f;
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[0] * g, ambientOcclusionCalculator.colorMultiplier[0] * h, ambientOcclusionCalculator.colorMultiplier[0] * l, 4);
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[1] * g, ambientOcclusionCalculator.colorMultiplier[1] * h, ambientOcclusionCalculator.colorMultiplier[1] * l, 3);
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[2] * g, ambientOcclusionCalculator.colorMultiplier[2] * h, ambientOcclusionCalculator.colorMultiplier[2] * l, 2);
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[3] * g, ambientOcclusionCalculator.colorMultiplier[3] * h, ambientOcclusionCalculator.colorMultiplier[3] * l, 1);
            } else {
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], 4);
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], 3);
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], 2);
                bufferBuilder.multiplyColor(ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], 1);
            }
            bufferBuilder.postPosition(d, e, f);
        }
    }

    private void updateShape(ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet) {
        float m;
        int l;
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (l = 0; l < 4; ++l) {
            m = Float.intBitsToFloat(is[l * 7]);
            float n = Float.intBitsToFloat(is[l * 7 + 1]);
            float o = Float.intBitsToFloat(is[l * 7 + 2]);
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }
        if (fs != null) {
            fs[Direction.WEST.getId()] = f;
            fs[Direction.EAST.getId()] = i;
            fs[Direction.DOWN.getId()] = g;
            fs[Direction.UP.getId()] = j;
            fs[Direction.NORTH.getId()] = h;
            fs[Direction.SOUTH.getId()] = k;
            l = Direction.values().length;
            fs[Direction.WEST.getId() + l] = 1.0f - f;
            fs[Direction.EAST.getId() + l] = 1.0f - i;
            fs[Direction.DOWN.getId() + l] = 1.0f - g;
            fs[Direction.UP.getId() + l] = 1.0f - j;
            fs[Direction.NORTH.getId() + l] = 1.0f - h;
            fs[Direction.SOUTH.getId() + l] = 1.0f - k;
        }
        float p = 1.0E-4f;
        m = 0.9999f;
        switch (direction) {
            case DOWN: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, (g < 1.0E-4f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, blockPos))) && g == j);
                break;
            }
            case UP: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, (j > 0.9999f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, blockPos))) && g == j);
                break;
            }
            case NORTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, (h < 1.0E-4f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, blockPos))) && h == k);
                break;
            }
            case SOUTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, (k > 0.9999f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, blockPos))) && h == k);
                break;
            }
            case WEST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, (f < 1.0E-4f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, blockPos))) && f == i);
                break;
            }
            case EAST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, (i > 0.9999f || Block.isShapeFullCube(blockState.getCollisionShape(extendedBlockView, blockPos))) && f == i);
            }
        }
    }

    private void tesselateQuadsFlat(ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, int i, boolean bl, BufferBuilder bufferBuilder, List<BakedQuad> list, BitSet bitSet) {
        Vec3d vec3d = blockState.getOffsetPos(extendedBlockView, blockPos);
        double d = (double)blockPos.getX() + vec3d.x;
        double e = (double)blockPos.getY() + vec3d.y;
        double f = (double)blockPos.getZ() + vec3d.z;
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            BakedQuad bakedQuad = list.get(j);
            if (bl) {
                this.updateShape(extendedBlockView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
                BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(bakedQuad.getFace()) : blockPos;
                i = blockState.getBlockBrightness(extendedBlockView, blockPos2);
            }
            bufferBuilder.putVertexData(bakedQuad.getVertexData());
            bufferBuilder.brightness(i, i, i, i);
            if (bakedQuad.hasColor()) {
                int l = this.colorMap.getColorMultiplier(blockState, extendedBlockView, blockPos, bakedQuad.getColorIndex());
                float g = (float)(l >> 16 & 0xFF) / 255.0f;
                float h = (float)(l >> 8 & 0xFF) / 255.0f;
                float m = (float)(l & 0xFF) / 255.0f;
                bufferBuilder.multiplyColor(g, h, m, 4);
                bufferBuilder.multiplyColor(g, h, m, 3);
                bufferBuilder.multiplyColor(g, h, m, 2);
                bufferBuilder.multiplyColor(g, h, m, 1);
            }
            bufferBuilder.postPosition(d, e, f);
        }
    }

    public void render(BakedModel bakedModel, float f, float g, float h, float i) {
        this.render(null, bakedModel, f, g, h, i);
    }

    public void render(@Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, float i) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderQuad(f, g, h, i, bakedModel.getQuads(blockState, direction, random));
        }
        random.setSeed(42L);
        this.renderQuad(f, g, h, i, bakedModel.getQuads(blockState, null, random));
    }

    public void render(BakedModel bakedModel, BlockState blockState, float f, boolean bl) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        int i = this.colorMap.getColorMultiplier(blockState, null, null, 0);
        float g = (float)(i >> 16 & 0xFF) / 255.0f;
        float h = (float)(i >> 8 & 0xFF) / 255.0f;
        float j = (float)(i & 0xFF) / 255.0f;
        if (!bl) {
            GlStateManager.color4f(f, f, f, 1.0f);
        }
        this.render(blockState, bakedModel, f, g, h, j);
    }

    private void renderQuad(float f, float g, float h, float i, List<BakedQuad> list) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            BakedQuad bakedQuad = list.get(j);
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
            bufferBuilder.putVertexData(bakedQuad.getVertexData());
            if (bakedQuad.hasColor()) {
                bufferBuilder.setQuadColor(g * f, h * f, i * f);
            } else {
                bufferBuilder.setQuadColor(f, f, f);
            }
            Vec3i vec3i = bakedQuad.getFace().getVector();
            bufferBuilder.postNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            tessellator.draw();
        }
    }

    public static void enableBrightnessCache() {
        brightnessCache.get().enable();
    }

    public static void disableBrightnessCache() {
        brightnessCache.get().disable();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum NeighborData {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH});

        private final Direction[] faces;
        private final boolean nonCubicWeight;
        private final NeighborOrientation[] field_4192;
        private final NeighborOrientation[] field_4185;
        private final NeighborOrientation[] field_4180;
        private final NeighborOrientation[] field_4188;
        private static final NeighborData[] field_4190;

        private NeighborData(Direction[] directions, float f, boolean bl, NeighborOrientation[] neighborOrientations, NeighborOrientation[] neighborOrientations2, NeighborOrientation[] neighborOrientations3, NeighborOrientation[] neighborOrientations4) {
            this.faces = directions;
            this.nonCubicWeight = bl;
            this.field_4192 = neighborOrientations;
            this.field_4185 = neighborOrientations2;
            this.field_4180 = neighborOrientations3;
            this.field_4188 = neighborOrientations4;
        }

        public static NeighborData getData(Direction direction) {
            return field_4190[direction.getId()];
        }

        static {
            field_4190 = SystemUtil.consume(new NeighborData[6], neighborDatas -> {
                neighborDatas[Direction.DOWN.getId()] = DOWN;
                neighborDatas[Direction.UP.getId()] = UP;
                neighborDatas[Direction.NORTH.getId()] = NORTH;
                neighborDatas[Direction.SOUTH.getId()] = SOUTH;
                neighborDatas[Direction.WEST.getId()] = WEST;
                neighborDatas[Direction.EAST.getId()] = EAST;
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum NeighborOrientation {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        private final int shape;

        private NeighborOrientation(Direction direction, boolean bl) {
            this.shape = direction.getId() + (bl ? Direction.values().length : 0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class AmbientOcclusionCalculator {
        private final float[] colorMultiplier = new float[4];
        private final int[] brightness = new int[4];

        public void apply(ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
            int u;
            float t;
            int s;
            float r;
            int q;
            float p;
            int o;
            float n;
            BlockState blockState6;
            boolean bl4;
            BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
            NeighborData neighborData = NeighborData.getData(direction);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            BrightnessCache brightnessCache = (BrightnessCache)brightnessCache.get();
            mutable.set(blockPos2).setOffset(neighborData.faces[0]);
            BlockState blockState2 = extendedBlockView.getBlockState(mutable);
            int i = brightnessCache.getInt(blockState2, extendedBlockView, mutable);
            float f = brightnessCache.getFloat(blockState2, extendedBlockView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[1]);
            BlockState blockState3 = extendedBlockView.getBlockState(mutable);
            int j = brightnessCache.getInt(blockState3, extendedBlockView, mutable);
            float g = brightnessCache.getFloat(blockState3, extendedBlockView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[2]);
            BlockState blockState4 = extendedBlockView.getBlockState(mutable);
            int k = brightnessCache.getInt(blockState4, extendedBlockView, mutable);
            float h = brightnessCache.getFloat(blockState4, extendedBlockView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[3]);
            BlockState blockState5 = extendedBlockView.getBlockState(mutable);
            int l = brightnessCache.getInt(blockState5, extendedBlockView, mutable);
            float m = brightnessCache.getFloat(blockState5, extendedBlockView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(direction);
            boolean bl = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(direction);
            boolean bl2 = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[2]).setOffset(direction);
            boolean bl3 = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[3]).setOffset(direction);
            boolean bl5 = bl4 = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
            if (bl3 || bl) {
                mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
                blockState6 = extendedBlockView.getBlockState(mutable);
                n = brightnessCache.getFloat(blockState6, extendedBlockView, mutable);
                o = brightnessCache.getInt(blockState6, extendedBlockView, mutable);
            } else {
                n = f;
                o = i;
            }
            if (bl4 || bl) {
                mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
                blockState6 = extendedBlockView.getBlockState(mutable);
                p = brightnessCache.getFloat(blockState6, extendedBlockView, mutable);
                q = brightnessCache.getInt(blockState6, extendedBlockView, mutable);
            } else {
                p = f;
                q = i;
            }
            if (bl3 || bl2) {
                mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
                blockState6 = extendedBlockView.getBlockState(mutable);
                r = brightnessCache.getFloat(blockState6, extendedBlockView, mutable);
                s = brightnessCache.getInt(blockState6, extendedBlockView, mutable);
            } else {
                r = f;
                s = i;
            }
            if (bl4 || bl2) {
                mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
                blockState6 = extendedBlockView.getBlockState(mutable);
                t = brightnessCache.getFloat(blockState6, extendedBlockView, mutable);
                u = brightnessCache.getInt(blockState6, extendedBlockView, mutable);
            } else {
                t = f;
                u = i;
            }
            int v = brightnessCache.getInt(blockState, extendedBlockView, blockPos);
            mutable.set(blockPos).setOffset(direction);
            BlockState blockState7 = extendedBlockView.getBlockState(mutable);
            if (bitSet.get(0) || !blockState7.isFullOpaque(extendedBlockView, mutable)) {
                v = brightnessCache.getInt(blockState7, extendedBlockView, mutable);
            }
            float w = bitSet.get(0) ? brightnessCache.getFloat(extendedBlockView.getBlockState(blockPos2), extendedBlockView, blockPos2) : brightnessCache.getFloat(extendedBlockView.getBlockState(blockPos), extendedBlockView, blockPos);
            Translation translation = Translation.getTranslations(direction);
            if (!bitSet.get(1) || !neighborData.nonCubicWeight) {
                float x = (m + f + p + w) * 0.25f;
                float y = (h + f + n + w) * 0.25f;
                float z = (h + g + r + w) * 0.25f;
                float aa = (m + g + t + w) * 0.25f;
                this.brightness[((Translation)translation).firstCorner] = this.getAmbientOcclusionBrightness(l, i, q, v);
                this.brightness[((Translation)translation).secondCorner] = this.getAmbientOcclusionBrightness(k, i, o, v);
                this.brightness[((Translation)translation).thirdCorner] = this.getAmbientOcclusionBrightness(k, j, s, v);
                this.brightness[((Translation)translation).fourthCorner] = this.getAmbientOcclusionBrightness(l, j, u, v);
                this.colorMultiplier[((Translation)translation).firstCorner] = x;
                this.colorMultiplier[((Translation)translation).secondCorner] = y;
                this.colorMultiplier[((Translation)translation).thirdCorner] = z;
                this.colorMultiplier[((Translation)translation).fourthCorner] = aa;
            } else {
                float x = (m + f + p + w) * 0.25f;
                float y = (h + f + n + w) * 0.25f;
                float z = (h + g + r + w) * 0.25f;
                float aa = (m + g + t + w) * 0.25f;
                float ab = fs[neighborData.field_4192[0].shape] * fs[neighborData.field_4192[1].shape];
                float ac = fs[neighborData.field_4192[2].shape] * fs[neighborData.field_4192[3].shape];
                float ad = fs[neighborData.field_4192[4].shape] * fs[neighborData.field_4192[5].shape];
                float ae = fs[neighborData.field_4192[6].shape] * fs[neighborData.field_4192[7].shape];
                float af = fs[neighborData.field_4185[0].shape] * fs[neighborData.field_4185[1].shape];
                float ag = fs[neighborData.field_4185[2].shape] * fs[neighborData.field_4185[3].shape];
                float ah = fs[neighborData.field_4185[4].shape] * fs[neighborData.field_4185[5].shape];
                float ai = fs[neighborData.field_4185[6].shape] * fs[neighborData.field_4185[7].shape];
                float aj = fs[neighborData.field_4180[0].shape] * fs[neighborData.field_4180[1].shape];
                float ak = fs[neighborData.field_4180[2].shape] * fs[neighborData.field_4180[3].shape];
                float al = fs[neighborData.field_4180[4].shape] * fs[neighborData.field_4180[5].shape];
                float am = fs[neighborData.field_4180[6].shape] * fs[neighborData.field_4180[7].shape];
                float an = fs[neighborData.field_4188[0].shape] * fs[neighborData.field_4188[1].shape];
                float ao = fs[neighborData.field_4188[2].shape] * fs[neighborData.field_4188[3].shape];
                float ap = fs[neighborData.field_4188[4].shape] * fs[neighborData.field_4188[5].shape];
                float aq = fs[neighborData.field_4188[6].shape] * fs[neighborData.field_4188[7].shape];
                this.colorMultiplier[((Translation)translation).firstCorner] = x * ab + y * ac + z * ad + aa * ae;
                this.colorMultiplier[((Translation)translation).secondCorner] = x * af + y * ag + z * ah + aa * ai;
                this.colorMultiplier[((Translation)translation).thirdCorner] = x * aj + y * ak + z * al + aa * am;
                this.colorMultiplier[((Translation)translation).fourthCorner] = x * an + y * ao + z * ap + aa * aq;
                int ar = this.getAmbientOcclusionBrightness(l, i, q, v);
                int as = this.getAmbientOcclusionBrightness(k, i, o, v);
                int at = this.getAmbientOcclusionBrightness(k, j, s, v);
                int au = this.getAmbientOcclusionBrightness(l, j, u, v);
                this.brightness[((Translation)translation).firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
                this.brightness[((Translation)translation).secondCorner] = this.getBrightness(ar, as, at, au, af, ag, ah, ai);
                this.brightness[((Translation)translation).thirdCorner] = this.getBrightness(ar, as, at, au, aj, ak, al, am);
                this.brightness[((Translation)translation).fourthCorner] = this.getBrightness(ar, as, at, au, an, ao, ap, aq);
            }
        }

        private int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
            if (i == 0) {
                i = l;
            }
            if (j == 0) {
                j = l;
            }
            if (k == 0) {
                k = l;
            }
            return i + j + k + l >> 2 & 0xFF00FF;
        }

        private int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
            int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
            int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
            return n << 16 | o;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BrightnessCache {
        private boolean enabled;
        private final Object2IntLinkedOpenHashMap<BlockPos> intCache = SystemUtil.get(() -> {
            Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap = new Object2IntLinkedOpenHashMap<BlockPos>(50, 0.25f){

                @Override
                protected void rehash(int i) {
                }
            };
            object2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return object2IntLinkedOpenHashMap;
        });
        private final Object2FloatLinkedOpenHashMap<BlockPos> floatCache = SystemUtil.get(() -> {
            Object2FloatLinkedOpenHashMap<BlockPos> object2FloatLinkedOpenHashMap = new Object2FloatLinkedOpenHashMap<BlockPos>(50, 0.25f){

                @Override
                protected void rehash(int i) {
                }
            };
            object2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return object2FloatLinkedOpenHashMap;
        });

        private BrightnessCache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.intCache.clear();
            this.floatCache.clear();
        }

        public int getInt(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
            int i;
            if (this.enabled && (i = this.intCache.getInt(blockPos)) != Integer.MAX_VALUE) {
                return i;
            }
            i = blockState.getBlockBrightness(extendedBlockView, blockPos);
            if (this.enabled) {
                if (this.intCache.size() == 50) {
                    this.intCache.removeFirstInt();
                }
                this.intCache.put(blockPos.toImmutable(), i);
            }
            return i;
        }

        public float getFloat(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
            float f;
            if (this.enabled && !Float.isNaN(f = this.floatCache.getFloat(blockPos))) {
                return f;
            }
            f = blockState.getAmbientOcclusionLightLevel(extendedBlockView, blockPos);
            if (this.enabled) {
                if (this.floatCache.size() == 50) {
                    this.floatCache.removeFirstFloat();
                }
                this.floatCache.put(blockPos.toImmutable(), f);
            }
            return f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Translation {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int firstCorner;
        private final int secondCorner;
        private final int thirdCorner;
        private final int fourthCorner;
        private static final Translation[] VALUES;

        private Translation(int j, int k, int l, int m) {
            this.firstCorner = j;
            this.secondCorner = k;
            this.thirdCorner = l;
            this.fourthCorner = m;
        }

        public static Translation getTranslations(Direction direction) {
            return VALUES[direction.getId()];
        }

        static {
            VALUES = SystemUtil.consume(new Translation[6], translations -> {
                translations[Direction.DOWN.getId()] = DOWN;
                translations[Direction.UP.getId()] = UP;
                translations[Direction.NORTH.getId()] = NORTH;
                translations[Direction.SOUTH.getId()] = SOUTH;
                translations[Direction.WEST.getId()] = WEST;
                translations[Direction.EAST.getId()] = EAST;
            });
        }
    }
}

