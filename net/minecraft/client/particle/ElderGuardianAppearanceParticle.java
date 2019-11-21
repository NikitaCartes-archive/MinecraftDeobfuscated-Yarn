/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianAppearanceParticle
extends Particle {
    private LivingEntity guardian;

    private ElderGuardianAppearanceParticle(World world, double d, double e, double f) {
        super(world, d, e, f);
        this.gravityStrength = 0.0f;
        this.maxAge = 30;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.guardian == null) {
            ElderGuardianEntity elderGuardianEntity = EntityType.ELDER_GUARDIAN.create(this.world);
            elderGuardianEntity.straightenTail();
            this.guardian = elderGuardianEntity;
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
        if (this.guardian == null) {
            return;
        }
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        float g = 1.0f / ElderGuardianEntity.field_17492;
        float h = ((float)this.age + f) / (float)this.maxAge;
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float i = 240.0f;
        RenderSystem.glMultiTexCoord2f(33986, 240.0f, 240.0f);
        RenderSystem.pushMatrix();
        float j = 0.05f + 0.5f * MathHelper.sin(h * (float)Math.PI);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, j);
        RenderSystem.translatef(0.0f, 1.8f, 0.0f);
        RenderSystem.rotatef(180.0f - camera.getYaw(), 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(60.0f - 150.0f * h - camera.getPitch(), 1.0f, 0.0f, 0.0f);
        RenderSystem.translatef(0.0f, -0.4f, -1.5f);
        RenderSystem.scalef(g, g, g);
        this.guardian.yaw = 0.0f;
        this.guardian.headYaw = 0.0f;
        this.guardian.prevYaw = 0.0f;
        this.guardian.prevHeadYaw = 0.0f;
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        entityRenderDispatcher.render(this.guardian, 0.0, 0.0, 0.0, 0.0f, f, new MatrixStack(), immediate, 0xF000F0);
        immediate.draw();
        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new ElderGuardianAppearanceParticle(world, d, e, f);
        }
    }
}

