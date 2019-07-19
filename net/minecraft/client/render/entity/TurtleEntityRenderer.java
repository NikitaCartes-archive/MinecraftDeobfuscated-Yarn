/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityRenderer
extends MobEntityRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/turtle/big_sea_turtle.png");

    public TurtleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TurtleEntityModel(0.0f), 0.7f);
    }

    @Override
    public void render(TurtleEntity turtleEntity, double d, double e, double f, float g, float h) {
        if (turtleEntity.isBaby()) {
            this.field_4673 *= 0.5f;
        }
        super.render(turtleEntity, d, e, f, g, h);
    }

    @Override
    @Nullable
    protected Identifier getTexture(TurtleEntity turtleEntity) {
        return SKIN;
    }
}

