/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianEntityRenderer
extends GuardianEntityRenderer {
    public static final Identifier TEXTURE = new Identifier("textures/entity/guardian_elder.png");

    public ElderGuardianEntityRenderer(class_5617.class_5618 arg) {
        super(arg, 1.2f, EntityModelLayers.ELDER_GUARDIAN);
    }

    @Override
    protected void scale(GuardianEntity guardianEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(ElderGuardianEntity.SCALE, ElderGuardianEntity.SCALE, ElderGuardianEntity.SCALE);
    }

    @Override
    public Identifier getTexture(GuardianEntity guardianEntity) {
        return TEXTURE;
    }
}

