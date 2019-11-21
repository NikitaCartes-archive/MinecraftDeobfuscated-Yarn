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
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity>
extends CompositeEntityModel<T> {
    private final ModelPart[] field_3427 = new ModelPart[8];
    private final ModelPart innerCube;
    private final ImmutableList<ModelPart> field_20934;

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
            this.field_3427[i].addCuboid(-4.0f, 16 + i, -4.0f, 8.0f, 1.0f, 8.0f);
        }
        this.innerCube = new ModelPart(this, 0, 16);
        this.innerCube.addCuboid(-2.0f, 18.0f, -2.0f, 4.0f, 4.0f, 4.0f);
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(this.innerCube);
        builder.addAll(Arrays.asList(this.field_3427));
        this.field_20934 = builder.build();
    }

    @Override
    public void setAngles(T slimeEntity, float f, float g, float h, float i, float j) {
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

    public ImmutableList<ModelPart> getParts() {
        return this.field_20934;
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

