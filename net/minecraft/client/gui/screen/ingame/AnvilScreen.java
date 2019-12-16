/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AnvilScreen
extends AbstractContainerScreen<AnvilContainer>
implements ContainerListener {
    private static final Identifier BG_TEX = new Identifier("textures/gui/container/anvil.png");
    private TextFieldWidget nameField;

    public AnvilScreen(AnvilContainer container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboard.enableRepeatEvents(true);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.nameField = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, I18n.translate("container.repair", new Object[0]));
        this.nameField.setFocusUnlocked(false);
        this.nameField.changeFocus(true);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setHasBorder(false);
        this.nameField.setMaxLength(35);
        this.nameField.setChangedListener(this::onRenamed);
        this.children.add(this.nameField);
        ((AnvilContainer)this.container).addListener(this);
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
        this.minecraft.keyboard.enableRepeatEvents(false);
        ((AnvilContainer)this.container).removeListener(this);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }
        if (this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        this.font.draw(this.title.asFormattedString(), 60.0f, 6.0f, 0x404040);
        int i = ((AnvilContainer)this.container).getLevelCost();
        if (i > 0) {
            int j = 8453920;
            boolean bl = true;
            String string = I18n.translate("container.repair.cost", i);
            if (i >= 40 && !this.minecraft.player.abilities.creativeMode) {
                string = I18n.translate("container.repair.expensive", new Object[0]);
                j = 0xFF6060;
            } else if (!((AnvilContainer)this.container).getSlot(2).hasStack()) {
                bl = false;
            } else if (!((AnvilContainer)this.container).getSlot(2).canTakeItems(this.playerInventory.player)) {
                j = 0xFF6060;
            }
            if (bl) {
                int k = this.containerWidth - 8 - this.font.getStringWidth(string) - 2;
                int l = 69;
                AnvilScreen.fill(k - 2, 67, this.containerWidth - 8, 79, 0x4F000000);
                this.font.drawWithShadow(string, k, 69.0f, j);
            }
        }
    }

    private void onRenamed(String name) {
        if (name.isEmpty()) {
            return;
        }
        String string = name;
        Slot slot = ((AnvilContainer)this.container).getSlot(0);
        if (slot != null && slot.hasStack() && !slot.getStack().hasCustomName() && string.equals(slot.getStack().getName().getString())) {
            string = "";
        }
        ((AnvilContainer)this.container).setNewItemName(string);
        this.minecraft.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        this.nameField.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BG_TEX);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        this.blit(i + 59, j + 20, 0, this.containerHeight + (((AnvilContainer)this.container).getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((((AnvilContainer)this.container).getSlot(0).hasStack() || ((AnvilContainer)this.container).getSlot(1).hasStack()) && !((AnvilContainer)this.container).getSlot(2).hasStack()) {
            this.blit(i + 99, j + 45, this.containerWidth, 0, 28, 21);
        }
    }

    @Override
    public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
        this.onContainerSlotUpdate(container, 0, container.getSlot(0).getStack());
    }

    @Override
    public void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack) {
        if (slotId == 0) {
            this.nameField.setText(itemStack.isEmpty() ? "" : itemStack.getName().getString());
            this.nameField.setEditable(!itemStack.isEmpty());
        }
    }

    @Override
    public void onContainerPropertyUpdate(Container container, int propertyId, int i) {
    }
}

