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
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class SquidEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart head;
    private final ModelPart[] field_3574 = new ModelPart[8];
    private final ImmutableList<ModelPart> field_20942;

    public SquidEntityModel() {
        int i = -16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f);
        this.head.pivotY += 8.0f;
        for (int j = 0; j < this.field_3574.length; ++j) {
            this.field_3574[j] = new ModelPart(this, 48, 0);
            double d = (double)j * Math.PI * 2.0 / (double)this.field_3574.length;
            float f = (float)Math.cos(d) * 5.0f;
            float g = (float)Math.sin(d) * 5.0f;
            this.field_3574[j].addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
            this.field_3574[j].pivotX = f;
            this.field_3574[j].pivotZ = g;
            this.field_3574[j].pivotY = 15.0f;
            d = (double)j * Math.PI * -2.0 / (double)this.field_3574.length + 1.5707963267948966;
            this.field_3574[j].yaw = (float)d;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(this.head);
        builder.addAll(Arrays.asList(this.field_3574));
        this.field_20942 = builder.build();
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for (ModelPart modelPart : this.field_3574) {
            modelPart.pitch = animationProgress;
        }
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return this.field_20942;
    }
}

