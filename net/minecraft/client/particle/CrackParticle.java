/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class CrackParticle
extends SpriteBillboardParticle {
    private final float sampleU;
    private final float sampleV;

    private CrackParticle(World world, double x, double y, double z, double d, double e, double f, ItemStack itemStack) {
        this(world, x, y, z, itemStack);
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += d;
        this.velocityY += e;
        this.velocityZ += f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.TERRAIN_SHEET;
    }

    protected CrackParticle(World world, double d, double e, double f, ItemStack itemStack) {
        super(world, d, e, f, 0.0, 0.0, 0.0);
        this.setSprite(MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(itemStack, world, null).getSprite());
        this.gravityStrength = 1.0f;
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f * 16.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f * 16.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SnowballFactory
    implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(world, d, e, f, new ItemStack(Items.SNOWBALL));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SlimeballFactory
    implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(world, d, e, f, new ItemStack(Items.SLIME_BALL));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ItemFactory
    implements ParticleFactory<ItemStackParticleEffect> {
        @Override
        public Particle createParticle(ItemStackParticleEffect itemStackParticleEffect, World world, double d, double e, double f, double g, double h, double i) {
            return new CrackParticle(world, d, e, f, g, h, i, itemStackParticleEffect.getItemStack());
        }
    }
}

