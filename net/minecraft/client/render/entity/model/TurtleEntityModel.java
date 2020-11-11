/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity>
extends QuadrupedEntityModel<T> {
    /**
     * The belly side of the turtle's shell.
     */
    private final ModelPart plastron;

    public TurtleEntityModel(ModelPart modelPart) {
        super(modelPart, true, 120.0f, 0.0f, 9.0f, 6.0f, 120);
        this.plastron = modelPart.getChild("egg_belly");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(3, 0).cuboid(-3.0f, -1.0f, -3.0f, 6.0f, 5.0f, 6.0f), ModelTransform.pivot(0.0f, 19.0f, -10.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(7, 37).cuboid("shell", -9.5f, 3.0f, -10.0f, 19.0f, 20.0f, 6.0f).uv(31, 1).cuboid("belly", -5.5f, 3.0f, -13.0f, 11.0f, 18.0f, 3.0f), ModelTransform.of(0.0f, 11.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("egg_belly", ModelPartBuilder.create().uv(70, 33).cuboid(-4.5f, 3.0f, -14.0f, 9.0f, 18.0f, 1.0f), ModelTransform.of(0.0f, 11.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        boolean i = true;
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(1, 23).cuboid(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f), ModelTransform.pivot(-3.5f, 22.0f, 11.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(1, 12).cuboid(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f), ModelTransform.pivot(3.5f, 22.0f, 11.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(27, 30).cuboid(-13.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f), ModelTransform.pivot(-5.0f, 21.0f, -4.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(27, 24).cuboid(0.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f), ModelTransform.pivot(5.0f, 21.0f, -4.0f));
        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.plastron));
    }

    @Override
    public void setAngles(T turtleEntity, float f, float g, float h, float i, float j) {
        super.setAngles(turtleEntity, f, g, h, i, j);
        this.field_27476.pitch = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.field_27477.pitch = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.field_27478.roll = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.field_27479.roll = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.field_27478.pitch = 0.0f;
        this.field_27479.pitch = 0.0f;
        this.field_27478.yaw = 0.0f;
        this.field_27479.yaw = 0.0f;
        this.field_27476.yaw = 0.0f;
        this.field_27477.yaw = 0.0f;
        if (!((Entity)turtleEntity).isTouchingWater() && ((Entity)turtleEntity).isOnGround()) {
            float k = ((TurtleEntity)turtleEntity).isDiggingSand() ? 4.0f : 1.0f;
            float l = ((TurtleEntity)turtleEntity).isDiggingSand() ? 2.0f : 1.0f;
            float m = 5.0f;
            this.field_27478.yaw = MathHelper.cos(k * f * 5.0f + (float)Math.PI) * 8.0f * g * l;
            this.field_27478.roll = 0.0f;
            this.field_27479.yaw = MathHelper.cos(k * f * 5.0f) * 8.0f * g * l;
            this.field_27479.roll = 0.0f;
            this.field_27476.yaw = MathHelper.cos(f * 5.0f + (float)Math.PI) * 3.0f * g;
            this.field_27476.pitch = 0.0f;
            this.field_27477.yaw = MathHelper.cos(f * 5.0f) * 3.0f * g;
            this.field_27477.pitch = 0.0f;
        }
        this.plastron.visible = !this.child && ((TurtleEntity)turtleEntity).hasEgg();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        boolean bl = this.plastron.visible;
        if (bl) {
            matrices.push();
            matrices.translate(0.0, -0.08f, 0.0);
        }
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        if (bl) {
            matrices.pop();
        }
    }
}

