/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    private List<ModelPart> parts = Lists.newArrayList();
    public final ModelPart leftArmOverlay;
    public final ModelPart rightArmOverlay;
    public final ModelPart leftLegOverlay;
    public final ModelPart rightLegOverlay;
    public final ModelPart bodyOverlay;
    private final ModelPart cape;
    private final ModelPart ears;
    private final boolean thinArms;

    public PlayerEntityModel(float f, boolean bl) {
        super(RenderLayer::getEntityTranslucent, f, 0.0f, 64, 64);
        this.thinArms = bl;
        this.ears = new ModelPart(this, 24, 0);
        this.ears.addCuboid(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, f);
        this.cape = new ModelPart(this, 0, 0);
        this.cape.setTextureSize(64, 32);
        this.cape.addCuboid(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, f);
        if (bl) {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f);
            this.leftArm.setPivot(5.0f, 2.5f, 0.0f);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addCuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f);
            this.rightArm.setPivot(-5.0f, 2.5f, 0.0f);
            this.leftArmOverlay = new ModelPart(this, 48, 48);
            this.leftArmOverlay.addCuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f + 0.25f);
            this.leftArmOverlay.setPivot(5.0f, 2.5f, 0.0f);
            this.rightArmOverlay = new ModelPart(this, 40, 32);
            this.rightArmOverlay.addCuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f + 0.25f);
            this.rightArmOverlay.setPivot(-5.0f, 2.5f, 10.0f);
        } else {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.leftArmOverlay = new ModelPart(this, 48, 48);
            this.leftArmOverlay.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
            this.leftArmOverlay.setPivot(5.0f, 2.0f, 0.0f);
            this.rightArmOverlay = new ModelPart(this, 40, 32);
            this.rightArmOverlay.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
            this.rightArmOverlay.setPivot(-5.0f, 2.0f, 10.0f);
        }
        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.leftLegOverlay = new ModelPart(this, 0, 48);
        this.leftLegOverlay.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
        this.leftLegOverlay.setPivot(1.9f, 12.0f, 0.0f);
        this.rightLegOverlay = new ModelPart(this, 0, 32);
        this.rightLegOverlay.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
        this.rightLegOverlay.setPivot(-1.9f, 12.0f, 0.0f);
        this.bodyOverlay = new ModelPart(this, 16, 32);
        this.bodyOverlay.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f + 0.25f);
        this.bodyOverlay.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> method_22948() {
        return Iterables.concat(super.method_22948(), ImmutableList.of(this.leftLegOverlay, this.rightLegOverlay, this.leftArmOverlay, this.rightArmOverlay, this.bodyOverlay));
    }

    public void renderEars(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, int i, int j) {
        this.ears.copyRotation(this.head);
        this.ears.pivotX = 0.0f;
        this.ears.pivotY = 0.0f;
        this.ears.render(matrixStack, vertexConsumer, f, i, j, null);
    }

    public void renderCape(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, int i, int j) {
        this.cape.render(matrixStack, vertexConsumer, f, i, j, null);
    }

    @Override
    public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17087(livingEntity, f, g, h, i, j, k);
        this.leftLegOverlay.copyRotation(this.leftLeg);
        this.rightLegOverlay.copyRotation(this.rightLeg);
        this.leftArmOverlay.copyRotation(this.leftArm);
        this.rightArmOverlay.copyRotation(this.rightArm);
        this.bodyOverlay.copyRotation(this.body);
        this.cape.pivotY = ((Entity)livingEntity).isInSneakingPose() ? 2.0f : 0.0f;
    }

    @Override
    public void setVisible(boolean bl) {
        super.setVisible(bl);
        this.leftArmOverlay.visible = bl;
        this.rightArmOverlay.visible = bl;
        this.leftLegOverlay.visible = bl;
        this.rightLegOverlay.visible = bl;
        this.bodyOverlay.visible = bl;
        this.cape.visible = bl;
        this.ears.visible = bl;
    }

    @Override
    public void setArmAngle(float f, Arm arm, MatrixStack matrixStack) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float g = 0.5f * (float)(arm == Arm.RIGHT ? 1 : -1);
            modelPart.pivotX += g;
            modelPart.rotate(matrixStack, f);
            modelPart.pivotX -= g;
        } else {
            modelPart.rotate(matrixStack, f);
        }
    }

    public ModelPart getRandomPart(Random random) {
        return this.parts.get(random.nextInt(this.parts.size()));
    }

    @Override
    public void method_22696(ModelPart modelPart) {
        if (this.parts == null) {
            this.parts = Lists.newArrayList();
        }
        this.parts.add(modelPart);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.method_22696((ModelPart)object);
    }
}

