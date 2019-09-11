/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockCrackParticle
extends SpriteBillboardParticle {
    private final BlockState blockState;
    private BlockPos blockPos;
    private final float field_17884;
    private final float field_17885;

    public BlockCrackParticle(World world, double d, double e, double f, double g, double h, double i, BlockState blockState) {
        super(world, d, e, f, g, h, i);
        this.blockState = blockState;
        this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(blockState));
        this.gravityStrength = 1.0f;
        this.colorRed = 0.6f;
        this.colorGreen = 0.6f;
        this.colorBlue = 0.6f;
        this.scale /= 2.0f;
        this.field_17884 = this.random.nextFloat() * 3.0f;
        this.field_17885 = this.random.nextFloat() * 3.0f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    public BlockCrackParticle setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
        if (this.blockState.getBlock() == Blocks.GRASS_BLOCK) {
            return this;
        }
        this.updateColor(blockPos);
        return this;
    }

    public BlockCrackParticle setBlockPosFromPosition() {
        this.blockPos = new BlockPos(this.x, this.y, this.z);
        Block block = this.blockState.getBlock();
        if (block == Blocks.GRASS_BLOCK) {
            return this;
        }
        this.updateColor(this.blockPos);
        return this;
    }

    protected void updateColor(@Nullable BlockPos blockPos) {
        int i = MinecraftClient.getInstance().getBlockColorMap().getColorMultiplier(this.blockState, this.world, blockPos, 0);
        this.colorRed *= (float)(i >> 16 & 0xFF) / 255.0f;
        this.colorGreen *= (float)(i >> 8 & 0xFF) / 255.0f;
        this.colorBlue *= (float)(i & 0xFF) / 255.0f;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getU((this.field_17884 + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getU(this.field_17884 / 4.0f * 16.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getV(this.field_17885 / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getV((this.field_17885 + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    public int getColorMultiplier(float f) {
        int i = super.getColorMultiplier(f);
        int j = 0;
        if (this.world.isChunkLoaded(this.blockPos)) {
            j = this.world.getLightmapIndex(this.blockPos);
        }
        return i == 0 ? j : i;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        public Particle method_3109(BlockStateParticleEffect blockStateParticleEffect, World world, double d, double e, double f, double g, double h, double i) {
            BlockState blockState = blockStateParticleEffect.getBlockState();
            if (blockState.isAir() || blockState.getBlock() == Blocks.MOVING_PISTON) {
                return null;
            }
            return new BlockCrackParticle(world, d, e, f, g, h, i, blockState).setBlockPosFromPosition();
        }
    }
}

