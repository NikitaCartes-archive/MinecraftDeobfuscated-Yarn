/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class GhastEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart[] tentacles = new ModelPart[9];
    private final ImmutableList<ModelPart> parts;

    public GhastEntityModel() {
        ImmutableList.Builder builder = ImmutableList.builder();
        ModelPart modelPart = new ModelPart(this, 0, 0);
        modelPart.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        modelPart.pivotY = 17.6f;
        builder.add(modelPart);
        Random random = new Random(1660L);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = new ModelPart(this, 0, 0);
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float g = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int j = random.nextInt(7) + 8;
            this.tentacles[i].addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, j, 2.0f);
            this.tentacles[i].pivotX = f;
            this.tentacles[i].pivotZ = g;
            this.tentacles[i].pivotY = 24.6f;
            builder.add(this.tentacles[i]);
        }
        this.parts = builder.build();
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].pitch = 0.2f * MathHelper.sin(animationProgress * 0.3f + (float)i) + 0.4f;
        }
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return this.parts;
    }
}

