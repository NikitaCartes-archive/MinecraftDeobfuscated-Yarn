/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityModel
extends Model {
    private final ModelPart field_3562 = new ModelPart(this, 0, 0);
    private final ModelPart signpost;

    public SignBlockEntityModel() {
        this.field_3562.addCuboid(-12.0f, -14.0f, -1.0f, 24, 12, 2, 0.0f);
        this.signpost = new ModelPart(this, 0, 14);
        this.signpost.addCuboid(-1.0f, -2.0f, -1.0f, 2, 14, 2, 0.0f);
    }

    public void render() {
        this.field_3562.render(0.0625f);
        this.signpost.render(0.0625f);
    }

    public ModelPart getSignpostModel() {
        return this.signpost;
    }
}

