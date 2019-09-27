/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CaveSpiderEntityRenderer
extends SpiderEntityRenderer<CaveSpiderEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/spider/cave_spider.png");

    public CaveSpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 *= 0.7f;
    }

    protected void method_3886(CaveSpiderEntity caveSpiderEntity, class_4587 arg, float f) {
        arg.method_22905(0.7f, 0.7f, 0.7f);
    }

    public Identifier method_3885(CaveSpiderEntity caveSpiderEntity) {
        return SKIN;
    }
}

