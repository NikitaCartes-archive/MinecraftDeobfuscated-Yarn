/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class ItemBillboardParticle
extends SpriteBillboardParticle {
    ItemBillboardParticle(ClientWorld clientWorld, double d, double e, double f, ItemConvertible itemConvertible) {
        super(clientWorld, d, e, f);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getModels().getModelParticleSprite(itemConvertible));
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
    public static class LightFactory
    implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new ItemBillboardParticle(clientWorld, d, e, f, Items.LIGHT);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class BarrierFactory
    implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new ItemBillboardParticle(clientWorld, d, e, f, Blocks.BARRIER.asItem());
        }
    }
}

