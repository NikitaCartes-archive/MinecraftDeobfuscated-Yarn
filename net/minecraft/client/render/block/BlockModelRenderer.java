/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockModelRenderer {
    private final BlockColors colorMap;
    private static final ThreadLocal<BrightnessCache> brightnessCache = ThreadLocal.withInitial(() -> new BrightnessCache());

    public BlockModelRenderer(BlockColors blockColors) {
        this.colorMap = blockColors;
    }

    public boolean tesselate(BlockRenderView blockRenderView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random, long l, int i) {
        boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();
        Vec3d vec3d = blockState.getOffsetPos(blockRenderView, blockPos);
        matrixStack.translate(vec3d.x, vec3d.y, vec3d.z);
        try {
            if (bl2) {
                return this.tesselateSmooth(blockRenderView, bakedModel, blockState, blockPos, matrixStack, vertexConsumer, bl, random, l, i);
            }
            return this.tesselateFlat(blockRenderView, bakedModel, blockState, blockPos, matrixStack, vertexConsumer, bl, random, l, i);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
            crashReportSection.add("Using AO", bl2);
            throw new CrashException(crashReport);
        }
    }

    public boolean tesselateSmooth(BlockRenderView blockRenderView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random, long l, int i) {
        boolean bl2 = false;
        float[] fs = new float[Direction.values().length * 2];
        BitSet bitSet = new BitSet(3);
        AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
            if (list.isEmpty() || bl && !Block.shouldDrawSide(blockState, blockRenderView, blockPos, direction)) continue;
            this.tesselateQuadsSmooth(blockRenderView, blockState, blockPos, matrixStack, vertexConsumer, list, fs, bitSet, ambientOcclusionCalculator, i);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = bakedModel.getQuads(blockState, null, random);
        if (!list2.isEmpty()) {
            this.tesselateQuadsSmooth(blockRenderView, blockState, blockPos, matrixStack, vertexConsumer, list2, fs, bitSet, ambientOcclusionCalculator, i);
            bl2 = true;
        }
        return bl2;
    }

    public boolean tesselateFlat(BlockRenderView blockRenderView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random, long l, int i) {
        boolean bl2 = false;
        BitSet bitSet = new BitSet(3);
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
            if (list.isEmpty() || bl && !Block.shouldDrawSide(blockState, blockRenderView, blockPos, direction)) continue;
            int j = blockRenderView.getLightmapCoordinates(blockState, blockPos.offset(direction));
            this.tesselateQuadsFlat(blockRenderView, blockState, blockPos, j, i, false, matrixStack, vertexConsumer, list, bitSet);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = bakedModel.getQuads(blockState, null, random);
        if (!list2.isEmpty()) {
            this.tesselateQuadsFlat(blockRenderView, blockState, blockPos, -1, i, true, matrixStack, vertexConsumer, list2, bitSet);
            bl2 = true;
        }
        return bl2;
    }

    private void tesselateQuadsSmooth(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, MatrixStack matrixStack, VertexConsumer vertexConsumer, List<BakedQuad> list, float[] fs, BitSet bitSet, AmbientOcclusionCalculator ambientOcclusionCalculator, int i) {
        for (BakedQuad bakedQuad : list) {
            this.updateShape(blockRenderView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), fs, bitSet);
            ambientOcclusionCalculator.apply(blockRenderView, blockState, blockPos, bakedQuad.getFace(), fs, bitSet);
            this.method_23073(blockRenderView, blockState, blockPos, vertexConsumer, matrixStack.peek(), matrixStack.peekNormal(), bakedQuad, ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[3], i);
        }
    }

    private void method_23073(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, BakedQuad bakedQuad, float f, float g, float h, float i, int j, int k, int l, int m, int n) {
        float r;
        float q;
        float p;
        if (bakedQuad.hasColor()) {
            int o = this.colorMap.getColorMultiplier(blockState, blockRenderView, blockPos, bakedQuad.getColorIndex());
            p = (float)(o >> 16 & 0xFF) / 255.0f;
            q = (float)(o >> 8 & 0xFF) / 255.0f;
            r = (float)(o & 0xFF) / 255.0f;
        } else {
            p = 1.0f;
            q = 1.0f;
            r = 1.0f;
        }
        vertexConsumer.quad(matrix4f, matrix3f, bakedQuad, new float[]{f, g, h, i}, p, q, r, new int[]{j, k, l, m}, n, true);
    }

    private void updateShape(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet) {
        float m;
        int l;
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (l = 0; l < 4; ++l) {
            m = Float.intBitsToFloat(is[l * 8]);
            float n = Float.intBitsToFloat(is[l * 8 + 1]);
            float o = Float.intBitsToFloat(is[l * 8 + 2]);
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
                bitSet.set(0, g == j && (g < 1.0E-4f || blockState.method_21743(blockRenderView, blockPos)));
                break;
            }
            case UP: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, g == j && (j > 0.9999f || blockState.method_21743(blockRenderView, blockPos)));
                break;
            }
            case NORTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, h == k && (h < 1.0E-4f || blockState.method_21743(blockRenderView, blockPos)));
                break;
            }
            case SOUTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, h == k && (k > 0.9999f || blockState.method_21743(blockRenderView, blockPos)));
                break;
            }
            case WEST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, f == i && (f < 1.0E-4f || blockState.method_21743(blockRenderView, blockPos)));
                break;
            }
            case EAST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, f == i && (i > 0.9999f || blockState.method_21743(blockRenderView, blockPos)));
            }
        }
    }

    private void tesselateQuadsFlat(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, int i, int j, boolean bl, MatrixStack matrixStack, VertexConsumer vertexConsumer, List<BakedQuad> list, BitSet bitSet) {
        for (BakedQuad bakedQuad : list) {
            if (bl) {
                this.updateShape(blockRenderView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
                BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(bakedQuad.getFace()) : blockPos;
                i = blockRenderView.getLightmapCoordinates(blockState, blockPos2);
            }
            this.method_23073(blockRenderView, blockState, blockPos, vertexConsumer, matrixStack.peek(), matrixStack.peekNormal(), bakedQuad, 1.0f, 1.0f, 1.0f, 1.0f, i, i, i, i, j);
        }
    }

    public void render(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, int i, int j) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            BlockModelRenderer.renderQuad(matrix4f, matrix3f, vertexConsumer, f, g, h, bakedModel.getQuads(blockState, direction, random), i, j);
        }
        random.setSeed(42L);
        BlockModelRenderer.renderQuad(matrix4f, matrix3f, vertexConsumer, f, g, h, bakedModel.getQuads(blockState, null, random), i, j);
    }

    private static void renderQuad(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> list, int i, int j) {
        for (BakedQuad bakedQuad : list) {
            float m;
            float l;
            float k;
            if (bakedQuad.hasColor()) {
                k = MathHelper.clamp(f, 0.0f, 1.0f);
                l = MathHelper.clamp(g, 0.0f, 1.0f);
                m = MathHelper.clamp(h, 0.0f, 1.0f);
            } else {
                k = 1.0f;
                l = 1.0f;
                m = 1.0f;
            }
            vertexConsumer.quad(matrix4f, matrix3f, bakedQuad, k, l, m, i, j);
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

        public void apply(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
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
            BlockState blockState2 = blockRenderView.getBlockState(mutable);
            int i = brightnessCache.getInt(blockState2, blockRenderView, mutable);
            float f = brightnessCache.getFloat(blockState2, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[1]);
            BlockState blockState3 = blockRenderView.getBlockState(mutable);
            int j = brightnessCache.getInt(blockState3, blockRenderView, mutable);
            float g = brightnessCache.getFloat(blockState3, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[2]);
            BlockState blockState4 = blockRenderView.getBlockState(mutable);
            int k = brightnessCache.getInt(blockState4, blockRenderView, mutable);
            float h = brightnessCache.getFloat(blockState4, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[3]);
            BlockState blockState5 = blockRenderView.getBlockState(mutable);
            int l = brightnessCache.getInt(blockState5, blockRenderView, mutable);
            float m = brightnessCache.getFloat(blockState5, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(direction);
            boolean bl = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(direction);
            boolean bl2 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[2]).setOffset(direction);
            boolean bl3 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[3]).setOffset(direction);
            boolean bl5 = bl4 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            if (bl3 || bl) {
                mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
                blockState6 = blockRenderView.getBlockState(mutable);
                n = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                o = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                n = f;
                o = i;
            }
            if (bl4 || bl) {
                mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
                blockState6 = blockRenderView.getBlockState(mutable);
                p = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                q = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                p = f;
                q = i;
            }
            if (bl3 || bl2) {
                mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
                blockState6 = blockRenderView.getBlockState(mutable);
                r = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                s = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                r = f;
                s = i;
            }
            if (bl4 || bl2) {
                mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
                blockState6 = blockRenderView.getBlockState(mutable);
                t = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                u = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                t = f;
                u = i;
            }
            int v = brightnessCache.getInt(blockState, blockRenderView, blockPos);
            mutable.set(blockPos).setOffset(direction);
            BlockState blockState7 = blockRenderView.getBlockState(mutable);
            if (bitSet.get(0) || !blockState7.isFullOpaque(blockRenderView, mutable)) {
                v = brightnessCache.getInt(blockState7, blockRenderView, mutable);
            }
            float w = bitSet.get(0) ? brightnessCache.getFloat(blockRenderView.getBlockState(blockPos2), blockRenderView, blockPos2) : brightnessCache.getFloat(blockRenderView.getBlockState(blockPos), blockRenderView, blockPos);
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
        private final Long2IntLinkedOpenHashMap intCache = SystemUtil.get(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

                @Override
                protected void rehash(int i) {
                }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
        });
        private final Long2FloatLinkedOpenHashMap floatCache = SystemUtil.get(() -> {
            Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                @Override
                protected void rehash(int i) {
                }
            };
            long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap;
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

        public int getInt(BlockState blockState, BlockRenderView blockRenderView, BlockPos blockPos) {
            int i;
            long l = blockPos.asLong();
            if (this.enabled && (i = this.intCache.get(l)) != Integer.MAX_VALUE) {
                return i;
            }
            i = blockRenderView.getLightmapCoordinates(blockState, blockPos);
            if (this.enabled) {
                if (this.intCache.size() == 100) {
                    this.intCache.removeFirstInt();
                }
                this.intCache.put(l, i);
            }
            return i;
        }

        public float getFloat(BlockState blockState, BlockRenderView blockRenderView, BlockPos blockPos) {
            float f;
            long l = blockPos.asLong();
            if (this.enabled && !Float.isNaN(f = this.floatCache.get(l))) {
                return f;
            }
            f = blockState.getAmbientOcclusionLightLevel(blockRenderView, blockPos);
            if (this.enabled) {
                if (this.floatCache.size() == 100) {
                    this.floatCache.removeFirstFloat();
                }
                this.floatCache.put(l, f);
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

