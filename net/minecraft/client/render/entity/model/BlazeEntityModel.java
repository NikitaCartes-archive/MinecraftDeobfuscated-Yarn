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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart[] rods;
    private final ModelPart head = new ModelPart(this, 0, 0);
    private final ImmutableList<ModelPart> parts;

    public BlazeEntityModel() {
        this.head.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.rods = new ModelPart[12];
        for (int i = 0; i < this.rods.length; ++i) {
            this.rods[i] = new ModelPart(this, 0, 16);
            this.rods[i].addCuboid(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(this.head);
        builder.addAll(Arrays.asList(this.rods));
        this.parts = builder.build();
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return this.parts;
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        int l;
        float k = h * (float)Math.PI * -0.1f;
        for (l = 0; l < 4; ++l) {
            this.rods[l].pivotY = -2.0f + MathHelper.cos(((float)(l * 2) + h) * 0.25f);
            this.rods[l].pivotX = MathHelper.cos(k) * 9.0f;
            this.rods[l].pivotZ = MathHelper.sin(k) * 9.0f;
            k += 1.5707964f;
        }
        k = 0.7853982f + h * (float)Math.PI * 0.03f;
        for (l = 4; l < 8; ++l) {
            this.rods[l].pivotY = 2.0f + MathHelper.cos(((float)(l * 2) + h) * 0.25f);
            this.rods[l].pivotX = MathHelper.cos(k) * 7.0f;
            this.rods[l].pivotZ = MathHelper.sin(k) * 7.0f;
            k += 1.5707964f;
        }
        k = 0.47123894f + h * (float)Math.PI * -0.05f;
        for (l = 8; l < 12; ++l) {
            this.rods[l].pivotY = 11.0f + MathHelper.cos(((float)l * 1.5f + h) * 0.5f);
            this.rods[l].pivotX = MathHelper.cos(k) * 5.0f;
            this.rods[l].pivotZ = MathHelper.sin(k) * 5.0f;
            k += 1.5707964f;
        }
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
    }
}

