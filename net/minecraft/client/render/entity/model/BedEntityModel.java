/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class BedEntityModel
extends Model {
    private final ModelPart field_3316;
    private final ModelPart field_3318;
    private final ModelPart[] field_3317 = new ModelPart[4];

    public BedEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3316 = new ModelPart(this, 0, 0);
        this.field_3316.addCuboid(0.0f, 0.0f, 0.0f, 16, 16, 6, 0.0f);
        this.field_3318 = new ModelPart(this, 0, 22);
        this.field_3318.addCuboid(0.0f, 0.0f, 0.0f, 16, 16, 6, 0.0f);
        this.field_3317[0] = new ModelPart(this, 50, 0);
        this.field_3317[1] = new ModelPart(this, 50, 6);
        this.field_3317[2] = new ModelPart(this, 50, 12);
        this.field_3317[3] = new ModelPart(this, 50, 18);
        this.field_3317[0].addCuboid(0.0f, 6.0f, -16.0f, 3, 3, 3);
        this.field_3317[1].addCuboid(0.0f, 6.0f, 0.0f, 3, 3, 3);
        this.field_3317[2].addCuboid(-16.0f, 6.0f, -16.0f, 3, 3, 3);
        this.field_3317[3].addCuboid(-16.0f, 6.0f, 0.0f, 3, 3, 3);
        this.field_3317[0].pitch = 1.5707964f;
        this.field_3317[1].pitch = 1.5707964f;
        this.field_3317[2].pitch = 1.5707964f;
        this.field_3317[3].pitch = 1.5707964f;
        this.field_3317[0].roll = 0.0f;
        this.field_3317[1].roll = 1.5707964f;
        this.field_3317[2].roll = 4.712389f;
        this.field_3317[3].roll = (float)Math.PI;
    }

    public void render() {
        this.field_3316.render(0.0625f);
        this.field_3318.render(0.0625f);
        this.field_3317[0].render(0.0625f);
        this.field_3317[1].render(0.0625f);
        this.field_3317[2].render(0.0625f);
        this.field_3317[3].render(0.0625f);
    }

    public void setVisible(boolean bl) {
        this.field_3316.visible = bl;
        this.field_3318.visible = !bl;
        this.field_3317[0].visible = !bl;
        this.field_3317[1].visible = bl;
        this.field_3317[2].visible = !bl;
        this.field_3317[3].visible = bl;
    }
}

