/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends AbstractSignEditScreen {
    public static final float BACKGROUND_SCALE = 62.500004f;
    public static final float TEXT_SCALE_MULTIPLIER = 0.9765628f;
    private static final Vec3f TEXT_SCALE = new Vec3f(0.9765628f, 0.9765628f, 0.9765628f);
    @Nullable
    private SignBlockEntityRenderer.SignModel model;

    public SignEditScreen(SignBlockEntity sign, boolean filtered) {
        super(sign, filtered);
    }

    @Override
    protected void init() {
        super.init();
        this.model = SignBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType);
    }

    @Override
    protected void translateForRender(MatrixStack matrices, BlockState state) {
        super.translateForRender(matrices, state);
        boolean bl = state.getBlock() instanceof SignBlock;
        if (!bl) {
            matrices.translate(0.0, 35.0, 0.0);
        }
    }

    @Override
    protected void renderSignBackground(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, BlockState state) {
        if (this.model == null) {
            return;
        }
        boolean bl = state.getBlock() instanceof SignBlock;
        matrices.translate(0.0, 31.0, 0.0);
        matrices.scale(62.500004f, 62.500004f, -62.500004f);
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(this.signType);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, this.model::getLayer);
        this.model.stick.visible = bl;
        this.model.root.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected Vec3f getTextScale() {
        return TEXT_SCALE;
    }
}

