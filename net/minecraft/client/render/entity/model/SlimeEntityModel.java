/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity>
extends class_4595<T> {
    private final ModelPart field_3571;
    private final ModelPart field_3573;
    private final ModelPart field_3572;
    private final ModelPart field_3570;

    public SlimeEntityModel(int i) {
        this.field_3571 = new ModelPart(this, 0, i);
        this.field_3573 = new ModelPart(this, 32, 0);
        this.field_3572 = new ModelPart(this, 32, 4);
        this.field_3570 = new ModelPart(this, 32, 8);
        if (i > 0) {
            this.field_3571.addCuboid(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f);
            this.field_3573.addCuboid(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.field_3572.addCuboid(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.field_3570.addCuboid(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f);
        } else {
            this.field_3571.addCuboid(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
    }

    @Override
    public Iterable<ModelPart> method_22960() {
        return ImmutableList.of(this.field_3571, this.field_3573, this.field_3572, this.field_3570);
    }
}

