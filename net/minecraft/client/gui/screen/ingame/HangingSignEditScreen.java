/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class HangingSignEditScreen
extends AbstractSignEditScreen {
    public static final float BACKGROUND_SCALE = 4.0f;
    private static final Vector3f TEXT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final int field_40433 = 16;
    private static final int field_40434 = 16;
    private final Identifier texture;

    public HangingSignEditScreen(SignBlockEntity signBlockEntity, boolean bl) {
        super(signBlockEntity, bl, Text.translatable("hanging_sign.edit"));
        this.texture = new Identifier("textures/gui/hanging_signs/" + this.signType.getName() + ".png");
    }

    @Override
    protected void translateForRender(MatrixStack matrices, BlockState state) {
        matrices.translate((float)this.width / 2.0f, 125.0f, 50.0f);
    }

    @Override
    protected void renderSignBackground(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, BlockState state) {
        matrices.translate(0.0f, -13.0f, 0.0f);
        RenderSystem.setShaderTexture(0, this.texture);
        matrices.scale(4.0f, 4.0f, 1.0f);
        HangingSignEditScreen.drawTexture(matrices, -8, -8, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    @Override
    protected Vector3f getTextScale() {
        return TEXT_SCALE;
    }
}

