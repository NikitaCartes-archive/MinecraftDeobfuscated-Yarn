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
    private final ModelPart[] tentacles = new ModelPart[8];
    private final ImmutableList<ModelPart> parts;

    public SquidEntityModel() {
        int i = -16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f);
        this.head.pivotY += 8.0f;
        for (int j = 0; j < this.tentacles.length; ++j) {
            this.tentacles[j] = new ModelPart(this, 48, 0);
            double d = (double)j * Math.PI * 2.0 / (double)this.tentacles.length;
            float f = (float)Math.cos(d) * 5.0f;
            float g = (float)Math.sin(d) * 5.0f;
            this.tentacles[j].addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
            this.tentacles[j].pivotX = f;
            this.tentacles[j].pivotZ = g;
            this.tentacles[j].pivotY = 15.0f;
            d = (double)j * Math.PI * -2.0 / (double)this.tentacles.length + 1.5707963267948966;
            this.tentacles[j].yaw = (float)d;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(this.head);
        builder.addAll(Arrays.asList(this.tentacles));
        this.parts = builder.build();
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for (ModelPart modelPart : this.tentacles) {
            modelPart.pitch = animationProgress;
        }
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return this.parts;
    }
}

