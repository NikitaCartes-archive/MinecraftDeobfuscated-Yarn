/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityModel
extends Model {
    private final ModelPart banner;
    private final ModelPart verticalStick;
    private final ModelPart horizontalStick;

    public BannerBlockEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.banner = new ModelPart(this, 0, 0);
        this.banner.addCuboid(-10.0f, 0.0f, -2.0f, 20, 40, 1, 0.0f);
        this.verticalStick = new ModelPart(this, 44, 0);
        this.verticalStick.addCuboid(-1.0f, -30.0f, -1.0f, 2, 42, 2, 0.0f);
        this.horizontalStick = new ModelPart(this, 0, 42);
        this.horizontalStick.addCuboid(-10.0f, -32.0f, -1.0f, 20, 2, 2, 0.0f);
    }

    public void render() {
        this.banner.rotationPointY = -32.0f;
        this.banner.render(0.0625f);
        this.verticalStick.render(0.0625f);
        this.horizontalStick.render(0.0625f);
    }

    public ModelPart getVerticalStick() {
        return this.verticalStick;
    }

    public ModelPart getBanner() {
        return this.banner;
    }
}

