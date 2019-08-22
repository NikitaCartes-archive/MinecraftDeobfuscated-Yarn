/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BeeEntityRenderer
extends MobEntityRenderer<BeeEntity, BeeEntityModel<BeeEntity>> {
    private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/bee/bee_angry.png");
    private static final Identifier ANGRY_NECTAR_SKIN = new Identifier("textures/entity/bee/bee_angry_nectar.png");
    private static final Identifier PASSIVE_SKIN = new Identifier("textures/entity/bee/bee.png");
    private static final Identifier NECTAR_SKIN = new Identifier("textures/entity/bee/bee_nectar.png");

    public BeeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BeeEntityModel(), 0.4f);
    }

    @Nullable
    protected Identifier method_22129(BeeEntity beeEntity) {
        if (beeEntity.isAngry()) {
            if (beeEntity.hasNectar()) {
                return ANGRY_NECTAR_SKIN;
            }
            return ANGRY_SKIN;
        }
        if (beeEntity.hasNectar()) {
            return NECTAR_SKIN;
        }
        return PASSIVE_SKIN;
    }
}

