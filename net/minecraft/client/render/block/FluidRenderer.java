/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@Environment(value=EnvType.CLIENT)
public class FluidRenderer {
    private static final float field_32781 = 0.8888889f;
    private final Sprite[] lavaSprites = new Sprite[2];
    private final Sprite[] waterSprites = new Sprite[2];
    private Sprite waterOverlaySprite;

    protected void onResourceReload() {
        this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getParticleSprite();
        this.lavaSprites[1] = ModelLoader.LAVA_FLOW.getSprite();
        this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getParticleSprite();
        this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
        this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
    }

    private static boolean isSameFluid(FluidState a, FluidState b) {
        return b.getFluid().matchesType(a.getFluid());
    }

    private static boolean isSideCovered(BlockView world, Direction direction, float height, BlockPos pos, BlockState state) {
        if (state.isOpaque()) {
            VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, height, 1.0);
            VoxelShape voxelShape2 = state.getCullingShape(world, pos);
            return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
        }
        return false;
    }

    private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation, BlockState state) {
        return FluidRenderer.isSideCovered(world, direction, maxDeviation, pos.offset(direction), state);
    }

    private static boolean isOppositeSideCovered(BlockView world, BlockPos pos, BlockState state, Direction direction) {
        return FluidRenderer.isSideCovered(world, direction.getOpposite(), 1.0f, pos, state);
    }

    public static boolean shouldRenderSide(BlockRenderView world, BlockPos pos, FluidState fluidState, BlockState blockState, Direction direction, FluidState neighborFluidState) {
        return !FluidRenderer.isOppositeSideCovered(world, pos, blockState, direction) && !FluidRenderer.isSameFluid(fluidState, neighborFluidState);
    }

    public boolean render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        float ap;
        float ao;
        float ag;
        float af;
        float ae;
        float ad;
        float ac;
        float ab;
        float z;
        float y;
        float r;
        float q;
        float p;
        float o;
        boolean bl = fluidState.isIn(FluidTags.LAVA);
        Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
        int i = bl ? 0xFFFFFF : BiomeColors.getWaterColor(world, pos);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
        BlockState blockState2 = world.getBlockState(pos.offset(Direction.DOWN));
        FluidState fluidState2 = blockState2.getFluidState();
        BlockState blockState3 = world.getBlockState(pos.offset(Direction.UP));
        FluidState fluidState3 = blockState3.getFluidState();
        BlockState blockState4 = world.getBlockState(pos.offset(Direction.NORTH));
        FluidState fluidState4 = blockState4.getFluidState();
        BlockState blockState5 = world.getBlockState(pos.offset(Direction.SOUTH));
        FluidState fluidState5 = blockState5.getFluidState();
        BlockState blockState6 = world.getBlockState(pos.offset(Direction.WEST));
        FluidState fluidState6 = blockState6.getFluidState();
        BlockState blockState7 = world.getBlockState(pos.offset(Direction.EAST));
        FluidState fluidState7 = blockState7.getFluidState();
        boolean bl2 = !FluidRenderer.isSameFluid(fluidState, fluidState3);
        boolean bl3 = FluidRenderer.shouldRenderSide(world, pos, fluidState, blockState, Direction.DOWN, fluidState2) && !FluidRenderer.isSideCovered((BlockView)world, pos, Direction.DOWN, 0.8888889f, blockState2);
        boolean bl4 = FluidRenderer.shouldRenderSide(world, pos, fluidState, blockState, Direction.NORTH, fluidState4);
        boolean bl5 = FluidRenderer.shouldRenderSide(world, pos, fluidState, blockState, Direction.SOUTH, fluidState5);
        boolean bl6 = FluidRenderer.shouldRenderSide(world, pos, fluidState, blockState, Direction.WEST, fluidState6);
        boolean bl7 = FluidRenderer.shouldRenderSide(world, pos, fluidState, blockState, Direction.EAST, fluidState7);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return false;
        }
        boolean bl8 = false;
        float j = world.getBrightness(Direction.DOWN, true);
        float k = world.getBrightness(Direction.UP, true);
        float l = world.getBrightness(Direction.NORTH, true);
        float m = world.getBrightness(Direction.WEST, true);
        Fluid fluid = fluidState.getFluid();
        float n = this.getFluidHeight(world, fluid, pos, blockState, fluidState);
        if (n >= 1.0f) {
            o = 1.0f;
            p = 1.0f;
            q = 1.0f;
            r = 1.0f;
        } else {
            float s = this.getFluidHeight(world, fluid, pos.north(), blockState4, fluidState4);
            float t = this.getFluidHeight(world, fluid, pos.south(), blockState5, fluidState5);
            float u = this.getFluidHeight(world, fluid, pos.east(), blockState7, fluidState7);
            float v = this.getFluidHeight(world, fluid, pos.west(), blockState6, fluidState6);
            o = this.calculateFluidHeight(world, fluid, n, s, u, pos.offset(Direction.NORTH).offset(Direction.EAST));
            p = this.calculateFluidHeight(world, fluid, n, s, v, pos.offset(Direction.NORTH).offset(Direction.WEST));
            q = this.calculateFluidHeight(world, fluid, n, t, u, pos.offset(Direction.SOUTH).offset(Direction.EAST));
            r = this.calculateFluidHeight(world, fluid, n, t, v, pos.offset(Direction.SOUTH).offset(Direction.WEST));
        }
        double d = pos.getX() & 0xF;
        double e = pos.getY() & 0xF;
        double w = pos.getZ() & 0xF;
        float x = 0.001f;
        float f2 = y = bl3 ? 0.001f : 0.0f;
        if (bl2 && !FluidRenderer.isSideCovered((BlockView)world, pos, Direction.UP, Math.min(Math.min(p, r), Math.min(q, o)), blockState3)) {
            float ak;
            float aj;
            float ai;
            float ah;
            float aa;
            bl8 = true;
            p -= 0.001f;
            r -= 0.001f;
            q -= 0.001f;
            o -= 0.001f;
            Vec3d vec3d = fluidState.getVelocity(world, pos);
            if (vec3d.x == 0.0 && vec3d.z == 0.0) {
                sprite = sprites[0];
                z = sprite.getFrameU(0.0);
                aa = sprite.getFrameV(0.0);
                ab = z;
                ac = sprite.getFrameV(16.0);
                ad = sprite.getFrameU(16.0);
                ae = ac;
                af = ad;
                ag = aa;
            } else {
                sprite = sprites[1];
                ah = (float)MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707964f;
                ai = MathHelper.sin(ah) * 0.25f;
                aj = MathHelper.cos(ah) * 0.25f;
                ak = 8.0f;
                z = sprite.getFrameU(8.0f + (-aj - ai) * 16.0f);
                aa = sprite.getFrameV(8.0f + (-aj + ai) * 16.0f);
                ab = sprite.getFrameU(8.0f + (-aj + ai) * 16.0f);
                ac = sprite.getFrameV(8.0f + (aj + ai) * 16.0f);
                ad = sprite.getFrameU(8.0f + (aj + ai) * 16.0f);
                ae = sprite.getFrameV(8.0f + (aj - ai) * 16.0f);
                af = sprite.getFrameU(8.0f + (aj - ai) * 16.0f);
                ag = sprite.getFrameV(8.0f + (-aj - ai) * 16.0f);
            }
            float al = (z + ab + ad + af) / 4.0f;
            ah = (aa + ac + ae + ag) / 4.0f;
            ai = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
            aj = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
            ak = 4.0f / Math.max(aj, ai);
            z = MathHelper.lerp(ak, z, al);
            ab = MathHelper.lerp(ak, ab, al);
            ad = MathHelper.lerp(ak, ad, al);
            af = MathHelper.lerp(ak, af, al);
            aa = MathHelper.lerp(ak, aa, ah);
            ac = MathHelper.lerp(ak, ac, ah);
            ae = MathHelper.lerp(ak, ae, ah);
            ag = MathHelper.lerp(ak, ag, ah);
            int am = this.getLight(world, pos);
            float an = k * f;
            ao = k * g;
            ap = k * h;
            this.vertex(vertexConsumer, d + 0.0, e + (double)p, w + 0.0, an, ao, ap, z, aa, am);
            this.vertex(vertexConsumer, d + 0.0, e + (double)r, w + 1.0, an, ao, ap, ab, ac, am);
            this.vertex(vertexConsumer, d + 1.0, e + (double)q, w + 1.0, an, ao, ap, ad, ae, am);
            this.vertex(vertexConsumer, d + 1.0, e + (double)o, w + 0.0, an, ao, ap, af, ag, am);
            if (fluidState.method_15756(world, pos.up())) {
                this.vertex(vertexConsumer, d + 0.0, e + (double)p, w + 0.0, an, ao, ap, z, aa, am);
                this.vertex(vertexConsumer, d + 1.0, e + (double)o, w + 0.0, an, ao, ap, af, ag, am);
                this.vertex(vertexConsumer, d + 1.0, e + (double)q, w + 1.0, an, ao, ap, ad, ae, am);
                this.vertex(vertexConsumer, d + 0.0, e + (double)r, w + 1.0, an, ao, ap, ab, ac, am);
            }
        }
        if (bl3) {
            z = sprites[0].getMinU();
            ab = sprites[0].getMaxU();
            ad = sprites[0].getMinV();
            af = sprites[0].getMaxV();
            int aq = this.getLight(world, pos.down());
            ac = j * f;
            ae = j * g;
            ag = j * h;
            this.vertex(vertexConsumer, d, e + (double)y, w + 1.0, ac, ae, ag, z, af, aq);
            this.vertex(vertexConsumer, d, e + (double)y, w, ac, ae, ag, z, ad, aq);
            this.vertex(vertexConsumer, d + 1.0, e + (double)y, w, ac, ae, ag, ab, ad, aq);
            this.vertex(vertexConsumer, d + 1.0, e + (double)y, w + 1.0, ac, ae, ag, ab, af, aq);
            bl8 = true;
        }
        int ar = this.getLight(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            Block block;
            double av;
            double au;
            double at;
            double as;
            float aa;
            if (!(switch (direction) {
                case Direction.NORTH -> {
                    af = p;
                    aa = o;
                    as = d;
                    at = d + 1.0;
                    au = w + (double)0.001f;
                    av = w + (double)0.001f;
                    yield bl4;
                }
                case Direction.SOUTH -> {
                    af = q;
                    aa = r;
                    as = d + 1.0;
                    at = d;
                    au = w + 1.0 - (double)0.001f;
                    av = w + 1.0 - (double)0.001f;
                    yield bl5;
                }
                case Direction.WEST -> {
                    af = r;
                    aa = p;
                    as = d + (double)0.001f;
                    at = d + (double)0.001f;
                    au = w + 1.0;
                    av = w;
                    yield bl6;
                }
                default -> {
                    af = o;
                    aa = q;
                    as = d + 1.0 - (double)0.001f;
                    at = d + 1.0 - (double)0.001f;
                    au = w;
                    av = w + 1.0;
                    yield bl7;
                }
            }) || FluidRenderer.isSideCovered((BlockView)world, pos, direction, Math.max(af, aa), world.getBlockState(pos.offset(direction)))) continue;
            bl8 = true;
            BlockPos blockPos = pos.offset(direction);
            Sprite sprite2 = sprites[1];
            if (!bl && ((block = world.getBlockState(blockPos).getBlock()) instanceof TransparentBlock || block instanceof LeavesBlock)) {
                sprite2 = this.waterOverlaySprite;
            }
            ao = sprite2.getFrameU(0.0);
            ap = sprite2.getFrameU(8.0);
            float aw = sprite2.getFrameV((1.0f - af) * 16.0f * 0.5f);
            float ax = sprite2.getFrameV((1.0f - aa) * 16.0f * 0.5f);
            float ay = sprite2.getFrameV(8.0);
            float az = direction.getAxis() == Direction.Axis.Z ? l : m;
            float ba = k * az * f;
            float bb = k * az * g;
            float bc = k * az * h;
            this.vertex(vertexConsumer, as, e + (double)af, au, ba, bb, bc, ao, aw, ar);
            this.vertex(vertexConsumer, at, e + (double)aa, av, ba, bb, bc, ap, ax, ar);
            this.vertex(vertexConsumer, at, e + (double)y, av, ba, bb, bc, ap, ay, ar);
            this.vertex(vertexConsumer, as, e + (double)y, au, ba, bb, bc, ao, ay, ar);
            if (sprite2 == this.waterOverlaySprite) continue;
            this.vertex(vertexConsumer, as, e + (double)y, au, ba, bb, bc, ao, ay, ar);
            this.vertex(vertexConsumer, at, e + (double)y, av, ba, bb, bc, ap, ay, ar);
            this.vertex(vertexConsumer, at, e + (double)aa, av, ba, bb, bc, ap, ax, ar);
            this.vertex(vertexConsumer, as, e + (double)af, au, ba, bb, bc, ao, aw, ar);
        }
        return bl8;
    }

    private float calculateFluidHeight(BlockRenderView world, Fluid fluid, float originHeight, float northSouthHeight, float eastWestHeight, BlockPos pos) {
        if (eastWestHeight >= 1.0f || northSouthHeight >= 1.0f) {
            return 1.0f;
        }
        float[] fs = new float[2];
        if (eastWestHeight > 0.0f || northSouthHeight > 0.0f) {
            float f = this.getFluidHeight(world, fluid, pos);
            if (f >= 1.0f) {
                return 1.0f;
            }
            this.addHeight(fs, f);
        }
        this.addHeight(fs, originHeight);
        this.addHeight(fs, eastWestHeight);
        this.addHeight(fs, northSouthHeight);
        return fs[0] / fs[1];
    }

    private void addHeight(float[] weightedAverageHeight, float height) {
        if (height >= 0.8f) {
            weightedAverageHeight[0] = weightedAverageHeight[0] + height * 10.0f;
            weightedAverageHeight[1] = weightedAverageHeight[1] + 10.0f;
        } else if (height >= 0.0f) {
            weightedAverageHeight[0] = weightedAverageHeight[0] + height;
            weightedAverageHeight[1] = weightedAverageHeight[1] + 1.0f;
        }
    }

    private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return this.getFluidHeight(world, fluid, pos, blockState, blockState.getFluidState());
    }

    private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.matchesType(fluidState.getFluid())) {
            BlockState blockState2 = world.getBlockState(pos.up());
            if (fluid.matchesType(blockState2.getFluidState().getFluid())) {
                return 1.0f;
            }
            return fluidState.getHeight();
        }
        if (!blockState.getMaterial().isSolid()) {
            return 0.0f;
        }
        return -1.0f;
    }

    private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
        vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0f).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f).next();
    }

    private int getLight(BlockRenderView world, BlockPos pos) {
        int i = WorldRenderer.getLightmapCoordinates(world, pos);
        int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
        int k = i & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int l = j & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int m = i >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int n = j >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        return (k > l ? k : l) | (m > n ? m : n) << 16;
    }
}

