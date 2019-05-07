/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;

@Environment(value=EnvType.CLIENT)
public class EditSignScreen
extends Screen {
    private final SignBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;

    public EditSignScreen(SignBlockEntity signBlockEntity) {
        super(new TranslatableComponent("sign.edit", new Object[0]));
        this.sign = signBlockEntity;
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.finishEditing()));
        this.sign.setEditable(false);
        this.selectionManager = new SelectionManager(this.minecraft, () -> this.sign.getTextOnRow(this.currentRow).getString(), string -> this.sign.setTextOnRow(this.currentRow, new TextComponent((String)string)), 90);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.sign.getTextOnRow(0), this.sign.getTextOnRow(1), this.sign.getTextOnRow(2), this.sign.getTextOnRow(3)));
        }
        this.sign.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().supports(this.sign.getCachedState().getBlock())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.sign.markDirty();
        this.minecraft.openScreen(null);
    }

    @Override
    public boolean charTyped(char c, int i) {
        this.selectionManager.insert(c);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (i == 264 || i == 257 || i == 335) {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (this.selectionManager.handleSpecialKey(i)) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 0xFFFFFF);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.width / 2, 0.0f, 50.0f);
        float g = 93.75f;
        GlStateManager.scalef(-93.75f, -93.75f, -93.75f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        BlockState blockState = this.sign.getCachedState();
        float h = blockState.getBlock() instanceof SignBlock ? (float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0f : blockState.get(WallSignBlock.FACING).asRotation();
        GlStateManager.rotatef(h, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.0625f, 0.0f);
        this.sign.setSelectionState(this.currentRow, this.selectionManager.getSelectionStart(), this.selectionManager.getSelectionEnd(), this.ticksSinceOpened / 6 % 2 == 0);
        BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.resetSelectionState();
        GlStateManager.popMatrix();
        super.render(i, j, f);
    }
}

