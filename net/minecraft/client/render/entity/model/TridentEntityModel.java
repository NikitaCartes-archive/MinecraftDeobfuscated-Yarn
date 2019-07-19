/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentEntityModel
extends Model {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
    private final ModelPart field_3593;

    public TridentEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.field_3593 = new ModelPart(this, 0, 0);
        this.field_3593.addCuboid(-0.5f, -4.0f, -0.5f, 1, 31, 1, 0.0f);
        ModelPart modelPart = new ModelPart(this, 4, 0);
        modelPart.addCuboid(-1.5f, 0.0f, -0.5f, 3, 2, 1);
        this.field_3593.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 4, 3);
        modelPart2.addCuboid(-2.5f, -3.0f, -0.5f, 1, 4, 1);
        this.field_3593.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(this, 4, 3);
        modelPart3.mirror = true;
        modelPart3.addCuboid(1.5f, -3.0f, -0.5f, 1, 4, 1);
        this.field_3593.addChild(modelPart3);
    }

    public void renderItem() {
        this.field_3593.render(0.0625f);
    }
}

