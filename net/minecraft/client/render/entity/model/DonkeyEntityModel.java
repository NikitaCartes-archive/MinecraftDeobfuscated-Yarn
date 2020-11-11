/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel<T extends AbstractDonkeyEntity>
extends HorseEntityModel<T> {
    private final ModelPart field_27399;
    private final ModelPart field_27400;

    public DonkeyEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.field_27399 = this.torso.getChild("left_chest");
        this.field_27400 = this.torso.getChild("right_chest");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = HorseEntityModel.getModelData(Dilation.NONE);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.getChild("body");
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        modelPartData2.addChild("left_chest", modelPartBuilder, ModelTransform.of(6.0f, -8.0f, 0.0f, 0.0f, -1.5707964f, 0.0f));
        modelPartData2.addChild("right_chest", modelPartBuilder, ModelTransform.of(-6.0f, -8.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.getChild("head_parts").getChild("head");
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 12).cuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPartData3.addChild("left_ear", modelPartBuilder2, ModelTransform.of(1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, 0.2617994f));
        modelPartData3.addChild("right_ear", modelPartBuilder2, ModelTransform.of(-1.25f, -10.0f, 4.0f, 0.2617994f, 0.0f, -0.2617994f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        super.setAngles(abstractDonkeyEntity, f, g, h, i, j);
        if (((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest()) {
            this.field_27399.visible = true;
            this.field_27400.visible = true;
        } else {
            this.field_27399.visible = false;
            this.field_27400.visible = false;
        }
    }
}

