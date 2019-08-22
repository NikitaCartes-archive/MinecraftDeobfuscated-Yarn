/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel();

    public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3962(EvokerFangsEntity evokerFangsEntity, double d, double e, double f, float g, float h) {
        float i = evokerFangsEntity.getAnimationProgress(h);
        if (i == 0.0f) {
            return;
        }
        float j = 2.0f;
        if (i > 0.9f) {
            j = (float)((double)j * ((1.0 - (double)i) / (double)0.1f));
        }
        RenderSystem.pushMatrix();
        RenderSystem.disableCull();
        RenderSystem.enableAlphaTest();
        this.bindEntityTexture(evokerFangsEntity);
        RenderSystem.translatef((float)d, (float)e, (float)f);
        RenderSystem.rotatef(90.0f - evokerFangsEntity.yaw, 0.0f, 1.0f, 0.0f);
        RenderSystem.scalef(-j, -j, j);
        float k = 0.03125f;
        RenderSystem.translatef(0.0f, -0.626f, 0.0f);
        this.model.render(evokerFangsEntity, i, 0.0f, 0.0f, evokerFangsEntity.yaw, evokerFangsEntity.pitch, 0.03125f);
        RenderSystem.popMatrix();
        RenderSystem.enableCull();
        super.render(evokerFangsEntity, d, e, f, g, h);
    }

    protected Identifier method_3963(EvokerFangsEntity evokerFangsEntity) {
        return SKIN;
    }
}

