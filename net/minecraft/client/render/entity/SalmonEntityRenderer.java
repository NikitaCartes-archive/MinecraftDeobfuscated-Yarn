/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityRenderer
extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/fish/salmon.png");

    public SalmonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SalmonEntityModel(), 0.4f);
    }

    @Nullable
    protected Identifier method_4101(SalmonEntity salmonEntity) {
        return SKIN;
    }

    protected void method_4100(SalmonEntity salmonEntity, float f, float g, float h) {
        super.setupTransforms(salmonEntity, f, g, h);
        float i = 1.0f;
        float j = 1.0f;
        if (!salmonEntity.isInsideWater()) {
            i = 1.3f;
            j = 1.7f;
        }
        float k = i * 4.3f * MathHelper.sin(j * 0.6f * f);
        RenderSystem.rotatef(k, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(0.0f, 0.0f, -0.4f);
        if (!salmonEntity.isInsideWater()) {
            RenderSystem.translatef(0.2f, 0.1f, 0.0f);
            RenderSystem.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
}

