/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;

@Environment(value=EnvType.CLIENT)
public class BlockMarkerParticle
extends SpriteBillboardParticle {
    BlockMarkerParticle(ClientWorld world, double x, double y, double z, BlockState state) {
        super(world, x, y, z);
        this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
        this.gravityStrength = 0.0f;
        this.maxAge = 80;
        this.collidesWithWorld = false;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    @Override
    public float getSize(float tickDelta) {
        return 0.5f;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        @Override
        public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new BlockMarkerParticle(clientWorld, d, e, f, blockStateParticleEffect.getBlockState());
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleEffect particleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return this.createParticle((BlockStateParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}

