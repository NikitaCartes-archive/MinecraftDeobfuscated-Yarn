/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends Model {
    protected final ModelPart skull;

    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }

    public SkullEntityModel(int i, int j, int k, int l) {
        this.textureWidth = k;
        this.textureHeight = l;
        this.skull = new ModelPart(this, i, j);
        this.skull.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.0f);
        this.skull.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    public void render(class_4587 arg, class_4588 arg2, float f, float g, float h, float i, int j) {
        this.skull.yaw = g * ((float)Math.PI / 180);
        this.skull.pitch = h * ((float)Math.PI / 180);
        this.skull.method_22698(arg, arg2, i, j, null);
    }
}

