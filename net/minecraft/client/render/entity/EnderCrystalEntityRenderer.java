/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnderCrystalEntityRenderer
extends EntityRenderer<EnderCrystalEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/end_crystal/end_crystal.png");
    private final EntityModel<EnderCrystalEntity> field_4662 = new EndCrystalEntityModel<EnderCrystalEntity>(0.0f, true);
    private final EntityModel<EnderCrystalEntity> field_4664 = new EndCrystalEntityModel<EnderCrystalEntity>(0.0f, false);

    public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
    }

    public void method_3908(EnderCrystalEntity enderCrystalEntity, double d, double e, double f, float g, float h) {
        float i = (float)enderCrystalEntity.field_7034 + h;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d, (float)e, (float)f);
        this.bindTexture(SKIN);
        float j = MathHelper.sin(i * 0.2f) / 2.0f + 0.5f;
        j = j * j + j;
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(enderCrystalEntity));
        }
        if (enderCrystalEntity.getShowBottom()) {
            this.field_4662.render(enderCrystalEntity, 0.0f, i * 3.0f, j * 0.2f, 0.0f, 0.0f, 0.0625f);
        } else {
            this.field_4664.render(enderCrystalEntity, 0.0f, i * 3.0f, j * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        BlockPos blockPos = enderCrystalEntity.getBeamTarget();
        if (blockPos != null) {
            this.bindTexture(EnderDragonEntityRenderer.CRYSTAL_BEAM);
            float k = (float)blockPos.getX() + 0.5f;
            float l = (float)blockPos.getY() + 0.5f;
            float m = (float)blockPos.getZ() + 0.5f;
            double n = (double)k - enderCrystalEntity.x;
            double o = (double)l - enderCrystalEntity.y;
            double p = (double)m - enderCrystalEntity.z;
            EnderDragonEntityRenderer.renderCrystalBeam(d + n, e - 0.3 + (double)(j * 0.4f) + o, f + p, h, k, l, m, enderCrystalEntity.field_7034, enderCrystalEntity.x, enderCrystalEntity.y, enderCrystalEntity.z);
        }
        super.render(enderCrystalEntity, d, e, f, g, h);
    }

    protected Identifier method_3909(EnderCrystalEntity enderCrystalEntity) {
        return SKIN;
    }

    public boolean method_3907(EnderCrystalEntity enderCrystalEntity, VisibleRegion visibleRegion, double d, double e, double f) {
        return super.isVisible(enderCrystalEntity, visibleRegion, d, e, f) || enderCrystalEntity.getBeamTarget() != null;
    }
}

