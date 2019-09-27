/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity>
extends EntityModel<T> {
    private final ModelPart field_20935;
    private final ModelPart field_20936;
    private final ModelPart field_20937;
    private final ModelPart field_20938;
    private final ModelPart field_20939;
    private final ModelPart field_20940;
    private final ModelPart field_3430;
    private final ModelPart field_3429;

    public LlamaEntityModel(float f) {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.field_20935 = new ModelPart(this, 0, 0);
        this.field_20935.addCuboid(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, f);
        this.field_20935.setRotationPoint(0.0f, 7.0f, -6.0f);
        this.field_20935.setTextureOffset(0, 14).addCuboid(-4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, f);
        this.field_20935.setTextureOffset(17, 0).addCuboid(-4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, f);
        this.field_20935.setTextureOffset(17, 0).addCuboid(1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, f);
        this.field_20936 = new ModelPart(this, 29, 0);
        this.field_20936.addCuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, f);
        this.field_20936.setRotationPoint(0.0f, 5.0f, 2.0f);
        this.field_3430 = new ModelPart(this, 45, 28);
        this.field_3430.addCuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, f);
        this.field_3430.setRotationPoint(-8.5f, 3.0f, 3.0f);
        this.field_3430.yaw = 1.5707964f;
        this.field_3429 = new ModelPart(this, 45, 41);
        this.field_3429.addCuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, f);
        this.field_3429.setRotationPoint(5.5f, 3.0f, 3.0f);
        this.field_3429.yaw = 1.5707964f;
        int i = 4;
        int j = 14;
        this.field_20937 = new ModelPart(this, 29, 29);
        this.field_20937.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.field_20937.setRotationPoint(-2.5f, 10.0f, 6.0f);
        this.field_20938 = new ModelPart(this, 29, 29);
        this.field_20938.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.field_20938.setRotationPoint(2.5f, 10.0f, 6.0f);
        this.field_20939 = new ModelPart(this, 29, 29);
        this.field_20939.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.field_20939.setRotationPoint(-2.5f, 10.0f, -4.0f);
        this.field_20940 = new ModelPart(this, 29, 29);
        this.field_20940.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.field_20940.setRotationPoint(2.5f, 10.0f, -4.0f);
        this.field_20937.rotationPointX -= 1.0f;
        this.field_20938.rotationPointX += 1.0f;
        this.field_20937.rotationPointZ += 0.0f;
        this.field_20938.rotationPointZ += 0.0f;
        this.field_20939.rotationPointX -= 1.0f;
        this.field_20940.rotationPointX += 1.0f;
        this.field_20939.rotationPointZ -= 1.0f;
        this.field_20940.rotationPointZ -= 1.0f;
    }

    public void method_22962(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
        boolean bl;
        this.field_20935.pitch = j * ((float)Math.PI / 180);
        this.field_20935.yaw = i * ((float)Math.PI / 180);
        this.field_20936.pitch = 1.5707964f;
        this.field_20937.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_20938.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_20939.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_20940.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_3430.visible = bl = !((PassiveEntity)abstractDonkeyEntity).isBaby() && ((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest();
        this.field_3429.visible = bl;
    }

    @Override
    public void method_17116(class_4587 arg, class_4588 arg2, int i, float f, float g, float h) {
        if (this.isChild) {
            float j = 2.0f;
            arg.method_22903();
            float k = 0.7f;
            arg.method_22905(0.71428573f, 0.64935064f, 0.7936508f);
            arg.method_22904(0.0, 1.3125, 0.22f);
            this.field_20935.method_22699(arg, arg2, 0.0625f, i, null, f, g, h);
            arg.method_22909();
            arg.method_22903();
            float l = 1.1f;
            arg.method_22905(0.625f, 0.45454544f, 0.45454544f);
            arg.method_22904(0.0, 2.0625, 0.0);
            this.field_20936.method_22699(arg, arg2, 0.0625f, i, null, f, g, h);
            arg.method_22909();
            arg.method_22903();
            arg.method_22905(0.45454544f, 0.41322312f, 0.45454544f);
            arg.method_22904(0.0, 2.0625, 0.0);
            ImmutableList.of(this.field_20937, this.field_20938, this.field_20939, this.field_20940, this.field_3430, this.field_3429).forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625f, i, null, f, g, h));
            arg.method_22909();
        } else {
            ImmutableList.of(this.field_20935, this.field_20936, this.field_20937, this.field_20938, this.field_20939, this.field_20940, this.field_3430, this.field_3429).forEach(modelPart -> modelPart.method_22699(arg, arg2, 0.0625f, i, null, f, g, h));
        }
    }
}

