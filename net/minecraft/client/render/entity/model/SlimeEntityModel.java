/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart innerCube;
    private final ModelPart rightEye;
    private final ModelPart leftEye;
    private final ModelPart mouth;

    public SlimeEntityModel(int i) {
        this.innerCube = new ModelPart(this, 0, i);
        this.rightEye = new ModelPart(this, 32, 0);
        this.leftEye = new ModelPart(this, 32, 4);
        this.mouth = new ModelPart(this, 32, 8);
        if (i > 0) {
            this.innerCube.addCuboid(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f);
            this.rightEye.addCuboid(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.leftEye.addCuboid(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.mouth.addCuboid(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f);
        } else {
            this.innerCube.addCuboid(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.innerCube, this.rightEye, this.leftEye, this.mouth);
    }
}

