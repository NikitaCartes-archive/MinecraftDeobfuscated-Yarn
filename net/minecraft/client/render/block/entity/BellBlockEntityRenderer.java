/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.render.block.entity.BellModel;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockEntityRenderer
extends BlockEntityRenderer<BellBlockEntity> {
    private static final Identifier BELL_BODY_TEXTURE = new Identifier("textures/entity/bell/bell_body.png");
    private final BellModel model = new BellModel();

    @Override
    public void render(BellBlockEntity bellBlockEntity, double d, double e, double f, float g, int i) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        this.bindTexture(BELL_BODY_TEXTURE);
        GlStateManager.translatef((float)d, (float)e, (float)f);
        float h = (float)bellBlockEntity.ringTicks + g;
        float j = 0.0f;
        float k = 0.0f;
        if (bellBlockEntity.isRinging) {
            float l = MathHelper.sin(h / (float)Math.PI) / (4.0f + h / 3.0f);
            if (bellBlockEntity.lastSideHit == Direction.NORTH) {
                j = -l;
            } else if (bellBlockEntity.lastSideHit == Direction.SOUTH) {
                j = l;
            } else if (bellBlockEntity.lastSideHit == Direction.EAST) {
                k = -l;
            } else if (bellBlockEntity.lastSideHit == Direction.WEST) {
                k = l;
            }
        }
        this.model.method_17070(j, k, 0.0625f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}

