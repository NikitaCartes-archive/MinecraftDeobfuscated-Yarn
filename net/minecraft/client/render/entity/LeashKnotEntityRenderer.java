/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LeashKnotEntityRenderer
extends EntityRenderer<LeadKnotEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/lead_knot.png");
    private final LeashEntityModel<LeadKnotEntity> model = new LeashEntityModel();

    public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4035(LeadKnotEntity leadKnotEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translatef((float)d, (float)e, (float)f);
        float i = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.bindEntityTexture(leadKnotEntity);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(leadKnotEntity));
        }
        this.model.render(leadKnotEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(leadKnotEntity, d, e, f, g, h);
    }

    protected Identifier method_4036(LeadKnotEntity leadKnotEntity) {
        return SKIN;
    }
}

