/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PufferfishEntityRenderer
extends MobEntityRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/fish/pufferfish.png");
    private int field_4765 = 3;
    private final SmallPufferfishEntityModel<PufferfishEntity> smallModel = new SmallPufferfishEntityModel();
    private final MediumPufferfishEntityModel<PufferfishEntity> mediumModel = new MediumPufferfishEntityModel();
    private final LargePufferfishEntityModel<PufferfishEntity> largeModel = new LargePufferfishEntityModel();

    public PufferfishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new LargePufferfishEntityModel(), 0.2f);
    }

    @Override
    @Nullable
    protected Identifier getTexture(PufferfishEntity pufferfishEntity) {
        return SKIN;
    }

    @Override
    public void render(PufferfishEntity pufferfishEntity, double d, double e, double f, float g, float h) {
        int i = pufferfishEntity.getPuffState();
        if (i != this.field_4765) {
            this.model = i == 0 ? this.smallModel : (i == 1 ? this.mediumModel : this.largeModel);
        }
        this.field_4765 = i;
        this.field_4673 = 0.1f + 0.1f * (float)i;
        super.render(pufferfishEntity, d, e, f, g, h);
    }

    @Override
    protected void setupTransforms(PufferfishEntity pufferfishEntity, float f, float g, float h) {
        GlStateManager.translatef(0.0f, MathHelper.cos(f * 0.05f) * 0.08f, 0.0f);
        super.setupTransforms(pufferfishEntity, f, g, h);
    }
}

