/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LeashKnotEntityRenderer
extends EntityRenderer<LeadKnotEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/lead_knot.png");
    private final LeashEntityModel<LeadKnotEntity> model = new LeashEntityModel();

    public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4035(LeadKnotEntity leadKnotEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
        arg.method_22903();
        float i = 0.0625f;
        arg.method_22905(-1.0f, -1.0f, 1.0f);
        int j = leadKnotEntity.getLightmapCoordinates();
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SKIN));
        class_4608.method_23211(lv);
        this.model.setAngles(leadKnotEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        this.model.method_22957(arg, lv, j);
        lv.method_22923();
        arg.method_22909();
        super.render(leadKnotEntity, d, e, f, g, h, arg, arg2);
    }

    public Identifier method_4036(LeadKnotEntity leadKnotEntity) {
        return SKIN;
    }
}

