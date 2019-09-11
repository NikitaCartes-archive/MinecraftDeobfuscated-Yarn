/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BannerBlockEntityModel;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderer
extends BlockEntityRenderer<BannerBlockEntity> {
    private final BannerBlockEntityModel model = new BannerBlockEntityModel();

    public void method_3546(BannerBlockEntity bannerBlockEntity, double d, double e, double f, float g, int i) {
        long l;
        float h = 0.6666667f;
        boolean bl = bannerBlockEntity.getWorld() == null;
        RenderSystem.pushMatrix();
        ModelPart modelPart = this.model.getVerticalStick();
        if (bl) {
            l = 0L;
            RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
            modelPart.visible = true;
        } else {
            l = bannerBlockEntity.getWorld().getTime();
            BlockState blockState = bannerBlockEntity.getCachedState();
            if (blockState.getBlock() instanceof BannerBlock) {
                RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                RenderSystem.rotatef((float)(-blockState.get(BannerBlock.ROTATION).intValue() * 360) / 16.0f, 0.0f, 1.0f, 0.0f);
                modelPart.visible = true;
            } else {
                RenderSystem.translatef((float)d + 0.5f, (float)e - 0.16666667f, (float)f + 0.5f);
                RenderSystem.rotatef(-blockState.get(WallBannerBlock.FACING).asRotation(), 0.0f, 1.0f, 0.0f);
                RenderSystem.translatef(0.0f, -0.3125f, -0.4375f);
                modelPart.visible = false;
            }
        }
        BlockPos blockPos = bannerBlockEntity.getPos();
        float j = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + g) / 100.0f;
        this.model.getBanner().pitch = (-0.0125f + 0.01f * MathHelper.cos((float)Math.PI * 2 * j)) * (float)Math.PI;
        RenderSystem.enableRescaleNormal();
        Identifier identifier = this.getTextureId(bannerBlockEntity);
        if (identifier != null) {
            this.bindTexture(identifier);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.6666667f, -0.6666667f, -0.6666667f);
            this.model.render();
            RenderSystem.popMatrix();
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }

    @Nullable
    private Identifier getTextureId(BannerBlockEntity bannerBlockEntity) {
        return TextureCache.BANNER.get(bannerBlockEntity.getPatternCacheKey(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getPatternColors());
    }
}

