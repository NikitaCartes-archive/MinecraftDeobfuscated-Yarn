/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
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
    public void buildGeometry(class_4588 arg, Camera camera, float f, float g, float h, float i, float j, float k) {
        if (this.guardian == null) {
            return;
        }
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        float l = 1.0f / ElderGuardianEntity.field_17492;
        float m = ((float)this.age + f) / (float)this.maxAge;
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
        float n = 240.0f;
        RenderSystem.glMultiTexCoord2f(33986, 240.0f, 240.0f);
        RenderSystem.pushMatrix();
        float o = 0.05f + 0.5f * MathHelper.sin(m * (float)Math.PI);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, o);
        RenderSystem.translatef(0.0f, 1.8f, 0.0f);
        RenderSystem.rotatef(180.0f - camera.getYaw(), 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(60.0f - 150.0f * m - camera.getPitch(), 1.0f, 0.0f, 0.0f);
        RenderSystem.translatef(0.0f, -0.4f, -1.5f);
        RenderSystem.scalef(l, l, l);
        this.guardian.yaw = 0.0f;
        this.guardian.headYaw = 0.0f;
        this.guardian.prevYaw = 0.0f;
        this.guardian.prevHeadYaw = 0.0f;
        entityRenderDispatcher.render(this.guardian, f);
        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        public Particle method_3042(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new ElderGuardianAppearanceParticle(world, d, e, f);
        }
    }
}

