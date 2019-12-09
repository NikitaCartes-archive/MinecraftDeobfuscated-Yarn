/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity>
extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightChest;
    private final ModelPart leftChest;

    public LlamaEntityModel(float f) {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, f);
        this.head.setPivot(0.0f, 7.0f, -6.0f);
        this.head.setTextureOffset(0, 14).addCuboid(-4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, f);
        this.head.setTextureOffset(17, 0).addCuboid(-4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, f);
        this.head.setTextureOffset(17, 0).addCuboid(1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, f);
        this.torso = new ModelPart(this, 29, 0);
        this.torso.addCuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, f);
        this.torso.setPivot(0.0f, 5.0f, 2.0f);
        this.rightChest = new ModelPart(this, 45, 28);
        this.rightChest.addCuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, f);
        this.rightChest.setPivot(-8.5f, 3.0f, 3.0f);
        this.rightChest.yaw = 1.5707964f;
        this.leftChest = new ModelPart(this, 45, 41);
        this.leftChest.addCuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, f);
        this.leftChest.setPivot(5.5f, 3.0f, 3.0f);
        this.leftChest.yaw = 1.5707964f;
        int i = 4;
        int j = 14;
        this.rightBackLeg = new ModelPart(this, 29, 29);
        this.rightBackLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.rightBackLeg.setPivot(-2.5f, 10.0f, 6.0f);
        this.leftBackLeg = new ModelPart(this, 29, 29);
        this.leftBackLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.leftBackLeg.setPivot(2.5f, 10.0f, 6.0f);
        this.rightFrontLeg = new ModelPart(this, 29, 29);
        this.rightFrontLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.rightFrontLeg.setPivot(-2.5f, 10.0f, -4.0f);
        this.leftFrontLeg = new ModelPart(this, 29, 29);
        this.leftFrontLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, f);
        this.leftFrontLeg.setPivot(2.5f, 10.0f, -4.0f);
        this.rightBackLeg.pivotX -= 1.0f;
        this.leftBackLeg.pivotX += 1.0f;
        this.rightBackLeg.pivotZ += 0.0f;
        this.leftBackLeg.pivotZ += 0.0f;
        this.rightFrontLeg.pivotX -= 1.0f;
        this.leftFrontLeg.pivotX += 1.0f;
        this.rightFrontLeg.pivotZ -= 1.0f;
        this.leftFrontLeg.pivotZ -= 1.0f;
    }

    @Override
    public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        boolean bl;
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.rightChest.visible = bl = !((PassiveEntity)abstractDonkeyEntity).isBaby() && ((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest();
        this.leftChest.visible = bl;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.child) {
            float f = 2.0f;
            matrices.push();
            float g = 0.7f;
            matrices.scale(0.71428573f, 0.64935064f, 0.7936508f);
            matrices.translate(0.0, 1.3125, 0.22f);
            this.head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.pop();
            matrices.push();
            float h = 1.1f;
            matrices.scale(0.625f, 0.45454544f, 0.45454544f);
            matrices.translate(0.0, 2.0625, 0.0);
            this.torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            matrices.pop();
            matrices.push();
            matrices.scale(0.45454544f, 0.41322312f, 0.45454544f);
            matrices.translate(0.0, 2.0625, 0.0);
            ImmutableList.of(this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach(modelPart -> modelPart.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha));
            matrices.pop();
        } else {
            ImmutableList.of(this.head, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach(modelPart -> modelPart.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha));
        }
    }
}

