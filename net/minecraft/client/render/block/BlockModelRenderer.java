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
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
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

    public boolean render(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay) {
        boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();
        Vec3d vec3d = state.getOffsetPos(world, pos);
        matrix.translate(vec3d.x, vec3d.y, vec3d.z);
        try {
            if (bl) {
                return this.renderSmooth(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay);
            }
            return this.renderFlat(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, pos, state);
            crashReportSection.add("Using AO", bl);
            throw new CrashException(crashReport);
        }
    }

    public boolean renderSmooth(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay) {
        boolean bl = false;
        float[] fs = new float[Direction.values().length * 2];
        BitSet bitSet = new BitSet(3);
        AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
        for (Direction direction : Direction.values()) {
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, direction, random);
            if (list.isEmpty() || cull && !Block.shouldDrawSide(state, world, pos, direction)) continue;
            this.renderQuadsSmooth(world, state, pos, buffer, vertexConsumer, list, fs, bitSet, ambientOcclusionCalculator, overlay);
            bl = true;
        }
        random.setSeed(seed);
        List<BakedQuad> list2 = model.getQuads(state, null, random);
        if (!list2.isEmpty()) {
            this.renderQuadsSmooth(world, state, pos, buffer, vertexConsumer, list2, fs, bitSet, ambientOcclusionCalculator, overlay);
            bl = true;
        }
        return bl;
    }

    public boolean renderFlat(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long l, int i) {
        boolean bl = false;
        BitSet bitSet = new BitSet(3);
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = model.getQuads(state, direction, random);
            if (list.isEmpty() || cull && !Block.shouldDrawSide(state, world, pos, direction)) continue;
            int j = WorldRenderer.getLightmapCoordinates(world, state, pos.offset(direction));
            this.renderQuadsFlat(world, state, pos, j, i, false, buffer, vertexConsumer, list, bitSet);
            bl = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = model.getQuads(state, null, random);
        if (!list2.isEmpty()) {
            this.renderQuadsFlat(world, state, pos, -1, i, true, buffer, vertexConsumer, list2, bitSet);
            bl = true;
        }
        return bl;
    }

    private void renderQuadsSmooth(BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, List<BakedQuad> quads, float[] box, BitSet flags, AmbientOcclusionCalculator ambientOcclusionCalculator, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            this.getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), box, flags);
            ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.getFace(), box, flags, bakedQuad.method_24874());
            this.renderQuad(world, state, pos, vertexConsumer, matrix.peek(), bakedQuad, ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[3], ambientOcclusionCalculator.light[0], ambientOcclusionCalculator.light[1], ambientOcclusionCalculator.light[2], ambientOcclusionCalculator.light[3], overlay);
        }
    }

    private void renderQuad(BlockRenderView world, BlockState state, BlockPos pos, VertexConsumer vertexConsumer, MatrixStack.Entry matrixEntry, BakedQuad quad, float brightness0, float brightness1, float brightness2, float brightness3, int light0, int light1, int light2, int light3, int overlay) {
        float h;
        float g;
        float f;
        if (quad.hasColor()) {
            int i = this.colorMap.getColor(state, world, pos, quad.getColorIndex());
            f = (float)(i >> 16 & 0xFF) / 255.0f;
            g = (float)(i >> 8 & 0xFF) / 255.0f;
            h = (float)(i & 0xFF) / 255.0f;
        } else {
            f = 1.0f;
            g = 1.0f;
            h = 1.0f;
        }
        vertexConsumer.quad(matrixEntry, quad, new float[]{brightness0, brightness1, brightness2, brightness3}, f, g, h, new int[]{light0, light1, light2, light3}, overlay, true);
    }

    private void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, @Nullable float[] box, BitSet flags) {
        float m;
        int l;
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (l = 0; l < 4; ++l) {
            m = Float.intBitsToFloat(vertexData[l * 8]);
            float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
            float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }
        if (box != null) {
            box[Direction.WEST.getId()] = f;
            box[Direction.EAST.getId()] = i;
            box[Direction.DOWN.getId()] = g;
            box[Direction.UP.getId()] = j;
            box[Direction.NORTH.getId()] = h;
            box[Direction.SOUTH.getId()] = k;
            l = Direction.values().length;
            box[Direction.WEST.getId() + l] = 1.0f - f;
            box[Direction.EAST.getId() + l] = 1.0f - i;
            box[Direction.DOWN.getId() + l] = 1.0f - g;
            box[Direction.UP.getId() + l] = 1.0f - j;
            box[Direction.NORTH.getId() + l] = 1.0f - h;
            box[Direction.SOUTH.getId() + l] = 1.0f - k;
        }
        float p = 1.0E-4f;
        m = 0.9999f;
        switch (face) {
            case DOWN: {
                flags.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                flags.set(0, g == j && (g < 1.0E-4f || state.isFullCube(world, pos)));
                break;
            }
            case UP: {
                flags.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                flags.set(0, g == j && (j > 0.9999f || state.isFullCube(world, pos)));
                break;
            }
            case NORTH: {
                flags.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                flags.set(0, h == k && (h < 1.0E-4f || state.isFullCube(world, pos)));
                break;
            }
            case SOUTH: {
                flags.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                flags.set(0, h == k && (k > 0.9999f || state.isFullCube(world, pos)));
                break;
            }
            case WEST: {
                flags.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                flags.set(0, f == i && (f < 1.0E-4f || state.isFullCube(world, pos)));
                break;
            }
            case EAST: {
                flags.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                flags.set(0, f == i && (i > 0.9999f || state.isFullCube(world, pos)));
            }
        }
    }

    private void renderQuadsFlat(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, int light, int overlay, boolean useWorldLight, MatrixStack matrixStack, VertexConsumer vertexConsumer, List<BakedQuad> quads, BitSet flags) {
        for (BakedQuad bakedQuad : quads) {
            if (useWorldLight) {
                this.getQuadDimensions(blockRenderView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, flags);
                BlockPos blockPos2 = flags.get(0) ? blockPos.offset(bakedQuad.getFace()) : blockPos;
                light = WorldRenderer.getLightmapCoordinates(blockRenderView, blockState, blockPos2);
            }
            float f = blockRenderView.method_24852(bakedQuad.getFace(), bakedQuad.method_24874());
            this.renderQuad(blockRenderView, blockState, blockPos, vertexConsumer, matrixStack.peek(), bakedQuad, f, f, f, f, light, light, light, light, overlay);
        }
    }

    public void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, int i, int j) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            BlockModelRenderer.renderQuad(entry, vertexConsumer, f, g, h, bakedModel.getQuads(blockState, direction, random), i, j);
        }
        random.setSeed(42L);
        BlockModelRenderer.renderQuad(entry, vertexConsumer, f, g, h, bakedModel.getQuads(blockState, null, random), i, j);
    }

    private static void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> list, int i, int j) {
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
            vertexConsumer.quad(entry, bakedQuad, k, l, m, i, j);
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
            field_4190 = Util.make(new NeighborData[6], neighborDatas -> {
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
        private final float[] brightness = new float[4];
        private final int[] light = new int[4];

        public void apply(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, float[] box, BitSet flags, boolean bl) {
            float aa;
            float z;
            float y;
            float x;
            int u;
            float t;
            int s;
            float r;
            int q;
            float p;
            int o;
            float n;
            BlockState blockState5;
            boolean bl5;
            BlockPos blockPos = flags.get(0) ? pos.offset(direction) : pos;
            NeighborData neighborData = NeighborData.getData(direction);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            BrightnessCache brightnessCache = (BrightnessCache)brightnessCache.get();
            mutable.set(blockPos).setOffset(neighborData.faces[0]);
            BlockState blockState = world.getBlockState(mutable);
            int i = brightnessCache.getInt(blockState, world, mutable);
            float f = brightnessCache.getFloat(blockState, world, mutable);
            mutable.set(blockPos).setOffset(neighborData.faces[1]);
            BlockState blockState2 = world.getBlockState(mutable);
            int j = brightnessCache.getInt(blockState2, world, mutable);
            float g = brightnessCache.getFloat(blockState2, world, mutable);
            mutable.set(blockPos).setOffset(neighborData.faces[2]);
            BlockState blockState3 = world.getBlockState(mutable);
            int k = brightnessCache.getInt(blockState3, world, mutable);
            float h = brightnessCache.getFloat(blockState3, world, mutable);
            mutable.set(blockPos).setOffset(neighborData.faces[3]);
            BlockState blockState4 = world.getBlockState(mutable);
            int l = brightnessCache.getInt(blockState4, world, mutable);
            float m = brightnessCache.getFloat(blockState4, world, mutable);
            mutable.set(blockPos).setOffset(neighborData.faces[0]).setOffset(direction);
            boolean bl2 = world.getBlockState(mutable).getOpacity(world, mutable) == 0;
            mutable.set(blockPos).setOffset(neighborData.faces[1]).setOffset(direction);
            boolean bl3 = world.getBlockState(mutable).getOpacity(world, mutable) == 0;
            mutable.set(blockPos).setOffset(neighborData.faces[2]).setOffset(direction);
            boolean bl4 = world.getBlockState(mutable).getOpacity(world, mutable) == 0;
            mutable.set(blockPos).setOffset(neighborData.faces[3]).setOffset(direction);
            boolean bl6 = bl5 = world.getBlockState(mutable).getOpacity(world, mutable) == 0;
            if (bl4 || bl2) {
                mutable.set(blockPos).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
                blockState5 = world.getBlockState(mutable);
                n = brightnessCache.getFloat(blockState5, world, mutable);
                o = brightnessCache.getInt(blockState5, world, mutable);
            } else {
                n = f;
                o = i;
            }
            if (bl5 || bl2) {
                mutable.set(blockPos).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
                blockState5 = world.getBlockState(mutable);
                p = brightnessCache.getFloat(blockState5, world, mutable);
                q = brightnessCache.getInt(blockState5, world, mutable);
            } else {
                p = f;
                q = i;
            }
            if (bl4 || bl3) {
                mutable.set(blockPos).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
                blockState5 = world.getBlockState(mutable);
                r = brightnessCache.getFloat(blockState5, world, mutable);
                s = brightnessCache.getInt(blockState5, world, mutable);
            } else {
                r = f;
                s = i;
            }
            if (bl5 || bl3) {
                mutable.set(blockPos).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
                blockState5 = world.getBlockState(mutable);
                t = brightnessCache.getFloat(blockState5, world, mutable);
                u = brightnessCache.getInt(blockState5, world, mutable);
            } else {
                t = f;
                u = i;
            }
            int v = brightnessCache.getInt(state, world, pos);
            mutable.set(pos).setOffset(direction);
            BlockState blockState6 = world.getBlockState(mutable);
            if (flags.get(0) || !blockState6.isFullOpaque(world, mutable)) {
                v = brightnessCache.getInt(blockState6, world, mutable);
            }
            float w = flags.get(0) ? brightnessCache.getFloat(world.getBlockState(blockPos), world, blockPos) : brightnessCache.getFloat(world.getBlockState(pos), world, pos);
            Translation translation = Translation.getTranslations(direction);
            if (!flags.get(1) || !neighborData.nonCubicWeight) {
                x = (m + f + p + w) * 0.25f;
                y = (h + f + n + w) * 0.25f;
                z = (h + g + r + w) * 0.25f;
                aa = (m + g + t + w) * 0.25f;
                this.light[((Translation)translation).firstCorner] = this.getAmbientOcclusionBrightness(l, i, q, v);
                this.light[((Translation)translation).secondCorner] = this.getAmbientOcclusionBrightness(k, i, o, v);
                this.light[((Translation)translation).thirdCorner] = this.getAmbientOcclusionBrightness(k, j, s, v);
                this.light[((Translation)translation).fourthCorner] = this.getAmbientOcclusionBrightness(l, j, u, v);
                this.brightness[((Translation)translation).firstCorner] = x;
                this.brightness[((Translation)translation).secondCorner] = y;
                this.brightness[((Translation)translation).thirdCorner] = z;
                this.brightness[((Translation)translation).fourthCorner] = aa;
            } else {
                x = (m + f + p + w) * 0.25f;
                y = (h + f + n + w) * 0.25f;
                z = (h + g + r + w) * 0.25f;
                aa = (m + g + t + w) * 0.25f;
                float ab = box[neighborData.field_4192[0].shape] * box[neighborData.field_4192[1].shape];
                float ac = box[neighborData.field_4192[2].shape] * box[neighborData.field_4192[3].shape];
                float ad = box[neighborData.field_4192[4].shape] * box[neighborData.field_4192[5].shape];
                float ae = box[neighborData.field_4192[6].shape] * box[neighborData.field_4192[7].shape];
                float af = box[neighborData.field_4185[0].shape] * box[neighborData.field_4185[1].shape];
                float ag = box[neighborData.field_4185[2].shape] * box[neighborData.field_4185[3].shape];
                float ah = box[neighborData.field_4185[4].shape] * box[neighborData.field_4185[5].shape];
                float ai = box[neighborData.field_4185[6].shape] * box[neighborData.field_4185[7].shape];
                float aj = box[neighborData.field_4180[0].shape] * box[neighborData.field_4180[1].shape];
                float ak = box[neighborData.field_4180[2].shape] * box[neighborData.field_4180[3].shape];
                float al = box[neighborData.field_4180[4].shape] * box[neighborData.field_4180[5].shape];
                float am = box[neighborData.field_4180[6].shape] * box[neighborData.field_4180[7].shape];
                float an = box[neighborData.field_4188[0].shape] * box[neighborData.field_4188[1].shape];
                float ao = box[neighborData.field_4188[2].shape] * box[neighborData.field_4188[3].shape];
                float ap = box[neighborData.field_4188[4].shape] * box[neighborData.field_4188[5].shape];
                float aq = box[neighborData.field_4188[6].shape] * box[neighborData.field_4188[7].shape];
                this.brightness[((Translation)translation).firstCorner] = x * ab + y * ac + z * ad + aa * ae;
                this.brightness[((Translation)translation).secondCorner] = x * af + y * ag + z * ah + aa * ai;
                this.brightness[((Translation)translation).thirdCorner] = x * aj + y * ak + z * al + aa * am;
                this.brightness[((Translation)translation).fourthCorner] = x * an + y * ao + z * ap + aa * aq;
                int ar = this.getAmbientOcclusionBrightness(l, i, q, v);
                int as = this.getAmbientOcclusionBrightness(k, i, o, v);
                int at = this.getAmbientOcclusionBrightness(k, j, s, v);
                int au = this.getAmbientOcclusionBrightness(l, j, u, v);
                this.light[((Translation)translation).firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
                this.light[((Translation)translation).secondCorner] = this.getBrightness(ar, as, at, au, af, ag, ah, ai);
                this.light[((Translation)translation).thirdCorner] = this.getBrightness(ar, as, at, au, aj, ak, al, am);
                this.light[((Translation)translation).fourthCorner] = this.getBrightness(ar, as, at, au, an, ao, ap, aq);
            }
            x = world.method_24852(direction, bl);
            int av = 0;
            while (av < this.brightness.length) {
                int n2 = av++;
                this.brightness[n2] = this.brightness[n2] * x;
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
        private final Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

                @Override
                protected void rehash(int i) {
                }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
        });
        private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
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

        public int getInt(BlockState blockState, BlockRenderView blockRenderView, BlockPos pos) {
            int i;
            long l = pos.asLong();
            if (this.enabled && (i = this.intCache.get(l)) != Integer.MAX_VALUE) {
                return i;
            }
            i = WorldRenderer.getLightmapCoordinates(blockRenderView, blockState, pos);
            if (this.enabled) {
                if (this.intCache.size() == 100) {
                    this.intCache.removeFirstInt();
                }
                this.intCache.put(l, i);
            }
            return i;
        }

        public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
            float f;
            long l = pos.asLong();
            if (this.enabled && !Float.isNaN(f = this.floatCache.get(l))) {
                return f;
            }
            f = state.getAmbientOcclusionLightLevel(blockView, pos);
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
            VALUES = Util.make(new Translation[6], translations -> {
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

