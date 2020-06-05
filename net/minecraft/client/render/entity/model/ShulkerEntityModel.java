/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity>
extends CompositeEntityModel<T> {
    private final ModelPart bottomShell;
    private final ModelPart topShell = new ModelPart(64, 64, 0, 0);
    private final ModelPart head;

    public ShulkerEntityModel() {
        super(RenderLayer::getEntityCutoutNoCullZOffset);
        this.bottomShell = new ModelPart(64, 64, 0, 28);
        this.head = new ModelPart(64, 64, 0, 52);
        this.topShell.addCuboid(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f);
        this.topShell.setPivot(0.0f, 24.0f, 0.0f);
        this.bottomShell.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f);
        this.bottomShell.setPivot(0.0f, 24.0f, 0.0f);
        this.head.addCuboid(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.head.setPivot(0.0f, 12.0f, 0.0f);
    }

    @Override
    public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)((ShulkerEntity)shulkerEntity).age;
        float l = (0.5f + ((ShulkerEntity)shulkerEntity).getOpenProgress(k)) * (float)Math.PI;
        float m = -1.0f + MathHelper.sin(l);
        float n = 0.0f;
        if (l > (float)Math.PI) {
            n = MathHelper.sin(h * 0.1f) * 0.7f;
        }
        this.topShell.setPivot(0.0f, 16.0f + MathHelper.sin(l) * 8.0f + n, 0.0f);
        this.topShell.yaw = ((ShulkerEntity)shulkerEntity).getOpenProgress(k) > 0.3f ? m * m * m * m * (float)Math.PI * 0.125f : 0.0f;
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = (((ShulkerEntity)shulkerEntity).headYaw - 180.0f - ((ShulkerEntity)shulkerEntity).bodyYaw) * ((float)Math.PI / 180);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.bottomShell, this.topShell);
    }

    public ModelPart getBottomShell() {
        return this.bottomShell;
    }

    public ModelPart getTopShell() {
        return this.topShell;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

