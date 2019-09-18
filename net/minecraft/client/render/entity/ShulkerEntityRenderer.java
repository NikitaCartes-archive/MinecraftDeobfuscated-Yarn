/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ShulkerSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderer
extends MobEntityRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
    public static final Identifier SKIN = new Identifier("textures/" + ModelLoader.field_20845.getPath() + ".png");
    public static final Identifier[] SKIN_COLOR = (Identifier[])ModelLoader.field_20846.stream().map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png")).toArray(Identifier[]::new);

    public ShulkerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ShulkerEntityModel(), 0.0f);
        this.addFeature(new ShulkerSomethingFeatureRenderer(this));
    }

    public void method_4113(ShulkerEntity shulkerEntity, double d, double e, double f, float g, float h) {
        int i = shulkerEntity.method_7113();
        if (i > 0 && shulkerEntity.method_7117()) {
            BlockPos blockPos = shulkerEntity.getAttachedBlock();
            BlockPos blockPos2 = shulkerEntity.method_7120();
            double j = (double)((float)i - h) / 6.0;
            j *= j;
            double k = (double)(blockPos.getX() - blockPos2.getX()) * j;
            double l = (double)(blockPos.getY() - blockPos2.getY()) * j;
            double m = (double)(blockPos.getZ() - blockPos2.getZ()) * j;
            super.method_4072(shulkerEntity, d - k, e - l, f - m, g, h);
        } else {
            super.method_4072(shulkerEntity, d, e, f, g, h);
        }
    }

    public boolean method_4112(ShulkerEntity shulkerEntity, VisibleRegion visibleRegion, double d, double e, double f) {
        if (super.method_4068(shulkerEntity, visibleRegion, d, e, f)) {
            return true;
        }
        if (shulkerEntity.method_7113() > 0 && shulkerEntity.method_7117()) {
            BlockPos blockPos = shulkerEntity.method_7120();
            BlockPos blockPos2 = shulkerEntity.getAttachedBlock();
            Vec3d vec3d = new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            Vec3d vec3d2 = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (visibleRegion.intersects(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
                return true;
            }
        }
        return false;
    }

    protected Identifier method_4111(ShulkerEntity shulkerEntity) {
        if (shulkerEntity.getColor() == null) {
            return SKIN;
        }
        return SKIN_COLOR[shulkerEntity.getColor().getId()];
    }

    protected void method_4114(ShulkerEntity shulkerEntity, float f, float g, float h) {
        super.setupTransforms(shulkerEntity, f, g, h);
        switch (shulkerEntity.getAttachedFace()) {
            case DOWN: {
                break;
            }
            case EAST: {
                RenderSystem.translatef(0.5f, 0.5f, 0.0f);
                RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                RenderSystem.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case WEST: {
                RenderSystem.translatef(-0.5f, 0.5f, 0.0f);
                RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                RenderSystem.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case NORTH: {
                RenderSystem.translatef(0.0f, 0.5f, -0.5f);
                RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                break;
            }
            case SOUTH: {
                RenderSystem.translatef(0.0f, 0.5f, 0.5f);
                RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case UP: {
                RenderSystem.translatef(0.0f, 1.0f, 0.0f);
                RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
        }
    }

    protected void method_4109(ShulkerEntity shulkerEntity, float f) {
        float g = 0.999f;
        RenderSystem.scalef(0.999f, 0.999f, 0.999f);
    }
}

