package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AnvilScreen extends ContainerScreen<AnvilContainer> implements ContainerListener {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/anvil.png");
	private TextFieldWidget nameField;

	public AnvilScreen(AnvilContainer anvilContainer, PlayerInventory playerInventory, Component component) {
		super(anvilContainer, playerInventory, component);
	}

	@Override
	protected void init() {
		super.init();
		this.minecraft.keyboard.enableRepeatEvents(true);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.nameField = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, I18n.translate("container.repair"));
		this.nameField.method_1856(false);
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
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.nameField.getText();
		this.init(minecraftClient, i, j);
		this.nameField.setText(string);
	}

	@Override
	public void removed() {
		super.removed();
		this.minecraft.keyboard.enableRepeatEvents(false);
		this.container.removeListener(this);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.nameField.keyPressed(i, j, k) && !this.nameField.method_20315() ? super.keyPressed(i, j, k) : true;
	}

	@Override
	protected void drawForeground(int i, int j) {
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.font.draw(this.title.getFormattedText(), 60.0F, 6.0F, 4210752);
		int k = this.container.getLevelCost();
		if (k > 0) {
			int l = 8453920;
			boolean bl = true;
			String string = I18n.translate("container.repair.cost", k);
			if (k >= 40 && !this.minecraft.player.abilities.creativeMode) {
				string = I18n.translate("container.repair.expensive");
				l = 16736352;
			} else if (!this.container.getSlot(2).hasStack()) {
				bl = false;
			} else if (!this.container.getSlot(2).canTakeItems(this.playerInventory.player)) {
				l = 16736352;
			}

			if (bl) {
				int m = this.containerWidth - 8 - this.font.getStringWidth(string) - 2;
				int n = 69;
				fill(m - 2, 67, this.containerWidth - 8, 79, 1325400064);
				this.font.drawWithShadow(string, (float)m, 69.0F, l);
			}
		}

		GlStateManager.enableLighting();
	}

	private void onRenamed(String string) {
		if (!string.isEmpty()) {
			String string2 = string;
			Slot slot = this.container.getSlot(0);
			if (slot != null && slot.hasStack() && !slot.getStack().hasDisplayName() && string.equals(slot.getStack().getDisplayName().getString())) {
				string2 = "";
			}

			this.container.setNewItemName(string2);
			this.minecraft.player.networkHandler.sendPacket(new RenameItemC2SPacket(string2));
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.nameField.render(i, j, f);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BG_TEX);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		this.blit(k + 59, l + 20, 0, this.containerHeight + (this.container.getSlot(0).hasStack() ? 0 : 16), 110, 16);
		if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
			this.blit(k + 99, l + 45, this.containerWidth, 0, 28, 21);
		}
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
		this.onContainerSlotUpdate(container, 0, container.getSlot(0).getStack());
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
		if (i == 0) {
			this.nameField.setText(itemStack.isEmpty() ? "" : itemStack.getDisplayName().getString());
			this.nameField.setIsEditable(!itemStack.isEmpty());
		}
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
	}
}
