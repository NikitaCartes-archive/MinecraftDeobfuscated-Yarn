package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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

@Environment(EnvType.CLIENT)
public class AnvilScreen extends AbstractContainerScreen<AnvilContainer> implements ContainerListener {
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
		this.nameField = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, I18n.translate("container.repair"));
		this.nameField.setFocusUnlocked(false);
		this.nameField.changeFocus(true);
		this.nameField.setEditableColor(-1);
		this.nameField.setUneditableColor(-1);
		this.nameField.setHasBorder(false);
		this.nameField.setMaxLength(35);
		this.nameField.setChangedListener(this::onRenamed);
		this.children.add(this.nameField);
		this.container.addListener(this);
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
		this.container.removeListener(this);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.nameField.keyPressed(keyCode, scanCode, modifiers) && !this.nameField.isActive() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		RenderSystem.disableBlend();
		this.font.draw(this.title.asFormattedString(), 60.0F, 6.0F, 4210752);
		int i = this.container.getLevelCost();
		if (i > 0) {
			int j = 8453920;
			boolean bl = true;
			String string = I18n.translate("container.repair.cost", i);
			if (i >= 40 && !this.minecraft.player.abilities.creativeMode) {
				string = I18n.translate("container.repair.expensive");
				j = 16736352;
			} else if (!this.container.getSlot(2).hasStack()) {
				bl = false;
			} else if (!this.container.getSlot(2).canTakeItems(this.playerInventory.player)) {
				j = 16736352;
			}

			if (bl) {
				int k = this.containerWidth - 8 - this.font.getStringWidth(string) - 2;
				int l = 69;
				fill(k - 2, 67, this.containerWidth - 8, 79, 1325400064);
				this.font.drawWithShadow(string, (float)k, 69.0F, j);
			}
		}
	}

	private void onRenamed(String name) {
		if (!name.isEmpty()) {
			String string = name;
			Slot slot = this.container.getSlot(0);
			if (slot != null && slot.hasStack() && !slot.getStack().hasCustomName() && name.equals(slot.getStack().getName().getString())) {
				string = "";
			}

			this.container.setNewItemName(string);
			this.minecraft.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
		}
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
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BG_TEX);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		this.blit(i + 59, j + 20, 0, this.containerHeight + (this.container.getSlot(0).hasStack() ? 0 : 16), 110, 16);
		if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
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
