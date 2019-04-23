/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityModelTurtle;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityRenderer
extends MobEntityRenderer<TurtleEntity, EntityModelTurtle<TurtleEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/turtle/big_sea_turtle.png");

    public TurtleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EntityModelTurtle(0.0f), 0.7f);
    }

    public void method_4138(TurtleEntity turtleEntity, double d, double e, double f, float g, float h) {
        if (turtleEntity.isBaby()) {
            this.field_4673 *= 0.5f;
        }
        super.method_4072(turtleEntity, d, e, f, g, h);
    }

    @Nullable
    protected Identifier method_4139(TurtleEntity turtleEntity) {
        return SKIN;
    }
}

