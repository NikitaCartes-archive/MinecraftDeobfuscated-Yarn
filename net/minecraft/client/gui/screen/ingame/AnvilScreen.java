/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AnvilScreen
extends ForgingScreen<AnvilScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/anvil.png");
    private static final Text field_26559 = new TranslatableText("container.repair.expensive");
    private TextFieldWidget nameField;

    public AnvilScreen(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
        this.titleX = 60;
    }

    @Override
    protected void setup() {
        this.client.keyboard.enableRepeatEvents(true);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, new TranslatableText("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setHasBorder(false);
        this.nameField.setMaxLength(35);
        this.nameField.setChangedListener(this::onRenamed);
        this.children.add(this.nameField);
        this.setInitialFocus(this.nameField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string);
    }

    @Override
    public void removed() {
        super.removed();
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }
        if (this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onRenamed(String name) {
        if (name.isEmpty()) {
            return;
        }
        String string = name;
        Slot slot = ((AnvilScreenHandler)this.handler).getSlot(0);
        if (slot != null && slot.hasStack() && !slot.getStack().hasCustomName() && string.equals(slot.getStack().getName().getString())) {
            string = "";
        }
        ((AnvilScreenHandler)this.handler).setNewItemName(string);
        this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        super.drawForeground(matrices, mouseX, mouseY);
        int i = ((AnvilScreenHandler)this.handler).getLevelCost();
        if (i > 0) {
            Text text;
            int j = 8453920;
            if (i >= 40 && !this.client.player.abilities.creativeMode) {
                text = field_26559;
                j = 0xFF6060;
            } else if (!((AnvilScreenHandler)this.handler).getSlot(2).hasStack()) {
                text = null;
            } else {
                text = new TranslatableText("container.repair.cost", i);
                if (!((AnvilScreenHandler)this.handler).getSlot(2).canTakeItems(this.playerInventory.player)) {
                    j = 0xFF6060;
                }
            }
            if (text != null) {
                int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
                int l = 69;
                AnvilScreen.fill(matrices, k - 2, 67, this.backgroundWidth - 8, 79, 0x4F000000);
                this.textRenderer.method_30881(matrices, text, k, 69.0f, j);
            }
        }
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseY, int i, float f) {
        this.nameField.render(matrixStack, mouseY, i, f);
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 0) {
            this.nameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
            this.nameField.setEditable(!stack.isEmpty());
            this.setFocused(this.nameField);
        }
    }
}

