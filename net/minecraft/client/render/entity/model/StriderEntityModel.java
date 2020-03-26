/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StriderEntityModel<T extends StriderEntity>
extends CompositeEntityModel<T> {
    private final ModelPart field_23353;
    private final ModelPart field_23354;
    private final ModelPart field_23355;
    private final ModelPart field_23356;
    private final ModelPart field_23357;
    private final ModelPart field_23358;
    private final ModelPart field_23359;
    private final ModelPart field_23360;
    private final ModelPart field_23361;

    public StriderEntityModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.field_23353 = new ModelPart(this, 0, 32);
        this.field_23353.setPivot(-4.0f, 8.0f, 0.0f);
        this.field_23353.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f);
        this.field_23354 = new ModelPart(this, 0, 32);
        this.field_23354.setPivot(4.0f, 8.0f, 0.0f);
        this.field_23354.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f);
        this.field_23355 = new ModelPart(this, 0, 0);
        this.field_23355.setPivot(0.0f, 1.0f, 0.0f);
        this.field_23355.addCuboid(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f, 0.0f);
        this.field_23356 = new ModelPart(this, 16, 65);
        this.field_23356.setPivot(-8.0f, 4.0f, -8.0f);
        this.field_23356.addCuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.method_26415(this.field_23356, 0.0f, 0.0f, -1.2217305f);
        this.field_23357 = new ModelPart(this, 16, 49);
        this.field_23357.setPivot(-8.0f, -1.0f, -8.0f);
        this.field_23357.addCuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.method_26415(this.field_23357, 0.0f, 0.0f, -1.134464f);
        this.field_23358 = new ModelPart(this, 16, 33);
        this.field_23358.setPivot(-8.0f, -5.0f, -8.0f);
        this.field_23358.addCuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.method_26415(this.field_23358, 0.0f, 0.0f, -0.87266463f);
        this.field_23359 = new ModelPart(this, 16, 33);
        this.field_23359.setPivot(8.0f, -6.0f, -8.0f);
        this.field_23359.addCuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.method_26415(this.field_23359, 0.0f, 0.0f, 0.87266463f);
        this.field_23360 = new ModelPart(this, 16, 49);
        this.field_23360.setPivot(8.0f, -2.0f, -8.0f);
        this.field_23360.addCuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.method_26415(this.field_23360, 0.0f, 0.0f, 1.134464f);
        this.field_23361 = new ModelPart(this, 16, 65);
        this.field_23361.setPivot(8.0f, 3.0f, -8.0f);
        this.field_23361.addCuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.method_26415(this.field_23361, 0.0f, 0.0f, 1.2217305f);
        this.field_23355.addChild(this.field_23356);
        this.field_23355.addChild(this.field_23357);
        this.field_23355.addChild(this.field_23358);
        this.field_23355.addChild(this.field_23359);
        this.field_23355.addChild(this.field_23360);
        this.field_23355.addChild(this.field_23361);
    }

    @Override
    public void setAngles(StriderEntity striderEntity, float f, float g, float h, float i, float j) {
        g = Math.min(0.25f, g);
        if (striderEntity.getPassengerList().size() <= 0) {
            this.field_23355.pitch = j * ((float)Math.PI / 180);
            this.field_23355.yaw = i * ((float)Math.PI / 180);
        } else {
            this.field_23355.pitch = 0.0f;
            this.field_23355.yaw = 0.0f;
        }
        float k = 1.5f;
        this.field_23355.roll = 0.1f * MathHelper.sin(f * 1.5f) * 4.0f * g;
        this.field_23355.pivotY = 2.0f;
        this.field_23355.pivotY -= 2.0f * MathHelper.cos(f * 1.5f) * 2.0f * g;
        this.field_23354.pitch = MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.field_23353.pitch = MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.field_23354.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f) * g;
        this.field_23353.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f + (float)Math.PI) * g;
        this.field_23354.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.field_23353.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.field_23356.roll = -1.2217305f;
        this.field_23357.roll = -1.134464f;
        this.field_23358.roll = -0.87266463f;
        this.field_23359.roll = 0.87266463f;
        this.field_23360.roll = 1.134464f;
        this.field_23361.roll = 1.2217305f;
        float l = MathHelper.cos(f * 1.5f + (float)Math.PI) * g;
        this.field_23356.roll += l * 1.3f;
        this.field_23357.roll += l * 1.2f;
        this.field_23358.roll += l * 0.6f;
        this.field_23359.roll += l * 0.6f;
        this.field_23360.roll += l * 1.2f;
        this.field_23361.roll += l * 1.3f;
        float m = 1.0f;
        float n = 1.0f;
        this.field_23356.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
        this.field_23357.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.field_23358.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.field_23359.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.field_23360.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.field_23361.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
    }

    public void method_26415(ModelPart modelPart, float f, float g, float h) {
        modelPart.pitch = f;
        modelPart.yaw = g;
        modelPart.roll = h;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_23355, this.field_23354, this.field_23353);
    }
}

