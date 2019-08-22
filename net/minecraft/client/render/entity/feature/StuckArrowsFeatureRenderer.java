/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4507;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends class_4507<T, M> {
    private final EntityRenderDispatcher field_17153;
    private ArrowEntity field_20528;

    public StuckArrowsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
        this.field_17153 = livingEntityRenderer.getRenderManager();
    }

    @Override
    protected void method_22131(T livingEntity) {
        GuiLighting.disable();
        this.field_20528 = new ArrowEntity(((LivingEntity)livingEntity).world, ((LivingEntity)livingEntity).x, ((LivingEntity)livingEntity).y, ((LivingEntity)livingEntity).z);
    }

    @Override
    protected int method_22134(T livingEntity) {
        return ((LivingEntity)livingEntity).getStuckArrows();
    }

    @Override
    protected void method_22130(Entity entity, float f, float g, float h, float i) {
        float j = MathHelper.sqrt(f * f + h * h);
        this.field_20528.yaw = (float)(Math.atan2(f, h) * 57.2957763671875);
        this.field_20528.pitch = (float)(Math.atan2(g, j) * 57.2957763671875);
        this.field_20528.prevYaw = this.field_20528.yaw;
        this.field_20528.prevPitch = this.field_20528.pitch;
        this.field_17153.render(this.field_20528, 0.0, 0.0, 0.0, 0.0f, i, false);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

