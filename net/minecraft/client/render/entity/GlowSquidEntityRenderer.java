/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class GlowSquidEntityRenderer
extends SquidEntityRenderer<GlowSquidEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/squid/glow_squid.png");

    public GlowSquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel<GlowSquidEntity> squidEntityModel) {
        super(context, squidEntityModel);
    }

    @Override
    public Identifier getTexture(GlowSquidEntity glowSquidEntity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(GlowSquidEntity glowSquidEntity, BlockPos blockPos) {
        return MathHelper.clamp(15 - glowSquidEntity.getDarkTicksRemaining(), 0, 15);
    }
}

