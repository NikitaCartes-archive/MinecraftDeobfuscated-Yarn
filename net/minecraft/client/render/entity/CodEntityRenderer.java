/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CodEntityRenderer
extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/fish/cod.png");

    public CodEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CodEntityModel(), 0.3f);
    }

    @Nullable
    protected Identifier method_3897(CodEntity codEntity) {
        return SKIN;
    }

    protected void method_3896(CodEntity codEntity, float f, float g, float h) {
        super.setupTransforms(codEntity, f, g, h);
        float i = 4.3f * MathHelper.sin(0.6f * f);
        RenderSystem.rotatef(i, 0.0f, 1.0f, 0.0f);
        if (!codEntity.isInsideWater()) {
            RenderSystem.translatef(0.1f, 0.1f, -0.1f);
            RenderSystem.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
}

