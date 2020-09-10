/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity>
extends CompositeEntityModel<T> {
    private final ModelPart[] bodySegments;
    private final ModelPart[] heads;
    private final ImmutableList<ModelPart> parts;

    public WitherEntityModel(float scale) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.bodySegments = new ModelPart[3];
        this.bodySegments[0] = new ModelPart(this, 0, 16);
        this.bodySegments[0].addCuboid(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, scale);
        this.bodySegments[1] = new ModelPart(this).setTextureSize(this.textureWidth, this.textureHeight);
        this.bodySegments[1].setPivot(-2.0f, 6.9f, -0.5f);
        this.bodySegments[1].setTextureOffset(0, 22).addCuboid(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, scale);
        this.bodySegments[1].setTextureOffset(24, 22).addCuboid(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, scale);
        this.bodySegments[1].setTextureOffset(24, 22).addCuboid(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, scale);
        this.bodySegments[1].setTextureOffset(24, 22).addCuboid(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, scale);
        this.bodySegments[2] = new ModelPart(this, 12, 22);
        this.bodySegments[2].addCuboid(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, scale);
        this.heads = new ModelPart[3];
        this.heads[0] = new ModelPart(this, 0, 0);
        this.heads[0].addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, scale);
        this.heads[1] = new ModelPart(this, 32, 0);
        this.heads[1].addCuboid(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, scale);
        this.heads[1].pivotX = -8.0f;
        this.heads[1].pivotY = 4.0f;
        this.heads[2] = new ModelPart(this, 32, 0);
        this.heads[2].addCuboid(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, scale);
        this.heads[2].pivotX = 10.0f;
        this.heads[2].pivotY = 4.0f;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(this.heads));
        builder.addAll(Arrays.asList(this.bodySegments));
        this.parts = builder.build();
    }

    public ImmutableList<ModelPart> getParts() {
        return this.parts;
    }

    @Override
    public void setAngles(T witherEntity, float f, float g, float h, float i, float j) {
        float k = MathHelper.cos(h * 0.1f);
        this.bodySegments[1].pitch = (0.065f + 0.05f * k) * (float)Math.PI;
        this.bodySegments[2].setPivot(-2.0f, 6.9f + MathHelper.cos(this.bodySegments[1].pitch) * 10.0f, -0.5f + MathHelper.sin(this.bodySegments[1].pitch) * 10.0f);
        this.bodySegments[2].pitch = (0.265f + 0.1f * k) * (float)Math.PI;
        this.heads[0].yaw = i * ((float)Math.PI / 180);
        this.heads[0].pitch = j * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T witherEntity, float f, float g, float h) {
        for (int i = 1; i < 3; ++i) {
            this.heads[i].yaw = (((WitherEntity)witherEntity).getHeadYaw(i - 1) - ((WitherEntity)witherEntity).bodyYaw) * ((float)Math.PI / 180);
            this.heads[i].pitch = ((WitherEntity)witherEntity).getHeadPitch(i - 1) * ((float)Math.PI / 180);
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

