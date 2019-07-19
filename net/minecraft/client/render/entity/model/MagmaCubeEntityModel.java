/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity>
extends EntityModel<T> {
    private final ModelPart[] field_3427 = new ModelPart[8];
    private final ModelPart field_3428;

    public MagmaCubeEntityModel() {
        for (int i = 0; i < this.field_3427.length; ++i) {
            int j = 0;
            int k = i;
            if (i == 2) {
                j = 24;
                k = 10;
            } else if (i == 3) {
                j = 24;
                k = 19;
            }
            this.field_3427[i] = new ModelPart(this, j, k);
            this.field_3427[i].addCuboid(-4.0f, 16 + i, -4.0f, 8, 1, 8);
        }
        this.field_3428 = new ModelPart(this, 0, 16);
        this.field_3428.addCuboid(-2.0f, 18.0f, -2.0f, 4, 4, 4);
    }

    @Override
    public void animateModel(T slimeEntity, float f, float g, float h) {
        float i = MathHelper.lerp(h, ((SlimeEntity)slimeEntity).lastStretch, ((SlimeEntity)slimeEntity).stretch);
        if (i < 0.0f) {
            i = 0.0f;
        }
        for (int j = 0; j < this.field_3427.length; ++j) {
            this.field_3427[j].pivotY = (float)(-(4 - j)) * i * 1.7f;
        }
    }

    @Override
    public void render(T slimeEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(slimeEntity, f, g, h, i, j, k);
        this.field_3428.render(k);
        for (ModelPart modelPart : this.field_3427) {
            modelPart.render(k);
        }
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.render((T)((SlimeEntity)entity), f, g, h, i, j, k);
    }
}

