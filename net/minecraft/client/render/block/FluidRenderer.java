/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
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
    private final Sprite[] lavaSprites = new Sprite[2];
    private final Sprite[] waterSprites = new Sprite[2];
    private Sprite waterOverlaySprite;

    protected void onResourceReload() {
        this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
        this.lavaSprites[1] = ModelLoader.LAVA_FLOW.method_24148();
        this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite();
        this.waterSprites[1] = ModelLoader.WATER_FLOW.method_24148();
        this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.method_24148();
    }

    private static boolean isSameFluid(BlockView blockView, BlockPos blockPos, Direction direction, FluidState fluidState) {
        BlockPos blockPos2 = blockPos.offset(direction);
        FluidState fluidState2 = blockView.getFluidState(blockPos2);
        return fluidState2.getFluid().matchesType(fluidState.getFluid());
    }

    private static boolean isSideCovered(BlockView blockView, BlockPos blockPos, Direction direction, float f) {
        BlockPos blockPos2 = blockPos.offset(direction);
        BlockState blockState = blockView.getBlockState(blockPos2);
        if (blockState.isOpaque()) {
            VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, f, 1.0);
            VoxelShape voxelShape2 = blockState.getCullingShape(blockView, blockPos2);
            return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
        }
        return false;
    }

    public boolean render(BlockRenderView blockRenderView, BlockPos blockPos, VertexConsumer vertexConsumer, FluidState fluidState) {
        float aj;
        float ai;
        float ah;
        float aa;
        float z;
        float y;
        float x;
        float w;
        float v;
        float t;
        boolean bl7;
        boolean bl = fluidState.matches(FluidTags.LAVA);
        Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
        int i = bl ? 0xFFFFFF : BiomeColors.getWaterColor(blockRenderView, blockPos);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
        boolean bl2 = !FluidRenderer.isSameFluid(blockRenderView, blockPos, Direction.UP, fluidState);
        boolean bl3 = !FluidRenderer.isSameFluid(blockRenderView, blockPos, Direction.DOWN, fluidState) && !FluidRenderer.isSideCovered(blockRenderView, blockPos, Direction.DOWN, 0.8888889f);
        boolean bl4 = !FluidRenderer.isSameFluid(blockRenderView, blockPos, Direction.NORTH, fluidState);
        boolean bl5 = !FluidRenderer.isSameFluid(blockRenderView, blockPos, Direction.SOUTH, fluidState);
        boolean bl6 = !FluidRenderer.isSameFluid(blockRenderView, blockPos, Direction.WEST, fluidState);
        boolean bl8 = bl7 = !FluidRenderer.isSameFluid(blockRenderView, blockPos, Direction.EAST, fluidState);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return false;
        }
        boolean bl82 = false;
        float j = 0.5f;
        float k = 1.0f;
        float l = 0.8f;
        float m = 0.6f;
        float n = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos, fluidState.getFluid());
        float o = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.south(), fluidState.getFluid());
        float p = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.east().south(), fluidState.getFluid());
        float q = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.east(), fluidState.getFluid());
        double d = blockPos.getX() & 0xF;
        double e = blockPos.getY() & 0xF;
        double r = blockPos.getZ() & 0xF;
        float s = 0.001f;
        if (bl2 && !FluidRenderer.isSideCovered(blockRenderView, blockPos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
            float ae;
            float ad;
            float ac;
            float ab;
            float u;
            Sprite sprite;
            bl82 = true;
            n -= 0.001f;
            o -= 0.001f;
            p -= 0.001f;
            q -= 0.001f;
            Vec3d vec3d = fluidState.getVelocity(blockRenderView, blockPos);
            if (vec3d.x == 0.0 && vec3d.z == 0.0) {
                sprite = sprites[0];
                t = sprite.getFrameU(0.0);
                u = sprite.getFrameV(0.0);
                v = t;
                w = sprite.getFrameV(16.0);
                x = sprite.getFrameU(16.0);
                y = w;
                z = x;
                aa = u;
            } else {
                sprite = sprites[1];
                ab = (float)MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707964f;
                ac = MathHelper.sin(ab) * 0.25f;
                ad = MathHelper.cos(ab) * 0.25f;
                ae = 8.0f;
                t = sprite.getFrameU(8.0f + (-ad - ac) * 16.0f);
                u = sprite.getFrameV(8.0f + (-ad + ac) * 16.0f);
                v = sprite.getFrameU(8.0f + (-ad + ac) * 16.0f);
                w = sprite.getFrameV(8.0f + (ad + ac) * 16.0f);
                x = sprite.getFrameU(8.0f + (ad + ac) * 16.0f);
                y = sprite.getFrameV(8.0f + (ad - ac) * 16.0f);
                z = sprite.getFrameU(8.0f + (ad - ac) * 16.0f);
                aa = sprite.getFrameV(8.0f + (-ad - ac) * 16.0f);
            }
            float af = (t + v + x + z) / 4.0f;
            ab = (u + w + y + aa) / 4.0f;
            ac = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
            ad = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
            ae = 4.0f / Math.max(ad, ac);
            t = MathHelper.lerp(ae, t, af);
            v = MathHelper.lerp(ae, v, af);
            x = MathHelper.lerp(ae, x, af);
            z = MathHelper.lerp(ae, z, af);
            u = MathHelper.lerp(ae, u, ab);
            w = MathHelper.lerp(ae, w, ab);
            y = MathHelper.lerp(ae, y, ab);
            aa = MathHelper.lerp(ae, aa, ab);
            int ag = this.getLight(blockRenderView, blockPos);
            ah = 1.0f * f;
            ai = 1.0f * g;
            aj = 1.0f * h;
            this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ah, ai, aj, t, u, ag);
            this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ah, ai, aj, v, w, ag);
            this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ah, ai, aj, x, y, ag);
            this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ah, ai, aj, z, aa, ag);
            if (fluidState.method_15756(blockRenderView, blockPos.up())) {
                this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ah, ai, aj, t, u, ag);
                this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ah, ai, aj, z, aa, ag);
                this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ah, ai, aj, x, y, ag);
                this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ah, ai, aj, v, w, ag);
            }
        }
        if (bl3) {
            t = sprites[0].getMinU();
            v = sprites[0].getMaxU();
            x = sprites[0].getMinV();
            z = sprites[0].getMaxV();
            int ak = this.getLight(blockRenderView, blockPos.down());
            w = 0.5f * f;
            y = 0.5f * g;
            aa = 0.5f * h;
            this.vertex(vertexConsumer, d, e + (double)0.001f, r + 1.0, w, y, aa, t, z, ak);
            this.vertex(vertexConsumer, d, e + (double)0.001f, r, w, y, aa, t, x, ak);
            this.vertex(vertexConsumer, d + 1.0, e + (double)0.001f, r, w, y, aa, v, x, ak);
            this.vertex(vertexConsumer, d + 1.0, e + (double)0.001f, r + 1.0, w, y, aa, v, z, ak);
            bl82 = true;
        }
        for (int al = 0; al < 4; ++al) {
            Block block;
            boolean bl9;
            Direction direction;
            double ap;
            double ao;
            double an;
            double am;
            if (al == 0) {
                v = n;
                x = q;
                am = d;
                an = d + 1.0;
                ao = r + (double)0.001f;
                ap = r + (double)0.001f;
                direction = Direction.NORTH;
                bl9 = bl4;
            } else if (al == 1) {
                v = p;
                x = o;
                am = d + 1.0;
                an = d;
                ao = r + 1.0 - (double)0.001f;
                ap = r + 1.0 - (double)0.001f;
                direction = Direction.SOUTH;
                bl9 = bl5;
            } else if (al == 2) {
                v = o;
                x = n;
                am = d + (double)0.001f;
                an = d + (double)0.001f;
                ao = r + 1.0;
                ap = r;
                direction = Direction.WEST;
                bl9 = bl6;
            } else {
                v = q;
                x = p;
                am = d + 1.0 - (double)0.001f;
                an = d + 1.0 - (double)0.001f;
                ao = r;
                ap = r + 1.0;
                direction = Direction.EAST;
                bl9 = bl7;
            }
            if (!bl9 || FluidRenderer.isSideCovered(blockRenderView, blockPos, direction, Math.max(v, x))) continue;
            bl82 = true;
            BlockPos blockPos2 = blockPos.offset(direction);
            Sprite sprite2 = sprites[1];
            if (!bl && ((block = blockRenderView.getBlockState(blockPos2).getBlock()) == Blocks.GLASS || block instanceof StainedGlassBlock)) {
                sprite2 = this.waterOverlaySprite;
            }
            ah = sprite2.getFrameU(0.0);
            ai = sprite2.getFrameU(8.0);
            aj = sprite2.getFrameV((1.0f - v) * 16.0f * 0.5f);
            float aq = sprite2.getFrameV((1.0f - x) * 16.0f * 0.5f);
            float ar = sprite2.getFrameV(8.0);
            int as = this.getLight(blockRenderView, blockPos2);
            float at = al < 2 ? 0.8f : 0.6f;
            float au = 1.0f * at * f;
            float av = 1.0f * at * g;
            float aw = 1.0f * at * h;
            this.vertex(vertexConsumer, am, e + (double)v, ao, au, av, aw, ah, aj, as);
            this.vertex(vertexConsumer, an, e + (double)x, ap, au, av, aw, ai, aq, as);
            this.vertex(vertexConsumer, an, e + (double)0.001f, ap, au, av, aw, ai, ar, as);
            this.vertex(vertexConsumer, am, e + (double)0.001f, ao, au, av, aw, ah, ar, as);
            if (sprite2 == this.waterOverlaySprite) continue;
            this.vertex(vertexConsumer, am, e + (double)0.001f, ao, au, av, aw, ah, ar, as);
            this.vertex(vertexConsumer, an, e + (double)0.001f, ap, au, av, aw, ai, ar, as);
            this.vertex(vertexConsumer, an, e + (double)x, ap, au, av, aw, ai, aq, as);
            this.vertex(vertexConsumer, am, e + (double)v, ao, au, av, aw, ah, aj, as);
        }
        return bl82;
    }

    private void vertex(VertexConsumer vertexConsumer, double d, double e, double f, float g, float h, float i, float j, float k, int l) {
        vertexConsumer.vertex(d, e, f).color(g, h, i, 1.0f).texture(j, k).light(l).normal(0.0f, 1.0f, 0.0f).next();
    }

    private int getLight(BlockRenderView blockRenderView, BlockPos blockPos) {
        int i = WorldRenderer.method_23794(blockRenderView, blockPos);
        int j = WorldRenderer.method_23794(blockRenderView, blockPos.up());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int m = i >> 16 & 0xFF;
        int n = j >> 16 & 0xFF;
        return (k > l ? k : l) | (m > n ? m : n) << 16;
    }

    private float getNorthWestCornerFluidHeight(BlockView blockView, BlockPos blockPos, Fluid fluid) {
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < 4; ++j) {
            BlockPos blockPos2 = blockPos.add(-(j & 1), 0, -(j >> 1 & 1));
            if (blockView.getFluidState(blockPos2.up()).getFluid().matchesType(fluid)) {
                return 1.0f;
            }
            FluidState fluidState = blockView.getFluidState(blockPos2);
            if (fluidState.getFluid().matchesType(fluid)) {
                float g = fluidState.getHeight(blockView, blockPos2);
                if (g >= 0.8f) {
                    f += g * 10.0f;
                    i += 10;
                    continue;
                }
                f += g;
                ++i;
                continue;
            }
            if (blockView.getBlockState(blockPos2).getMaterial().isSolid()) continue;
            ++i;
        }
        return f / (float)i;
    }
}

