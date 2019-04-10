package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.client.gui.WrittenBookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.LecternContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;

@Environment(EnvType.CLIENT)
public class LecternScreen extends WrittenBookScreen implements ContainerProvider<LecternContainer> {
	private final LecternContainer lecternContainer;
	private final ContainerListener listener = new ContainerListener() {
		@Override
		public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onContainerPropertyUpdate(Container container, int i, int j) {
			if (i == 0) {
				LecternScreen.this.updatePage();
			}
		}
	};

	public LecternScreen(LecternContainer lecternContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		this.lecternContainer = lecternContainer;
	}

	public LecternContainer method_17573() {
		return this.lecternContainer;
	}

	@Override
	protected void init() {
		super.init();
		this.lecternContainer.addListener(this.listener);
	}

	@Override
	public void onClose() {
		this.minecraft.player.closeGui();
		super.onClose();
	}

	@Override
	public void removed() {
		super.removed();
		this.lecternContainer.removeListener(this.listener);
	}

	@Override
	protected void addCloseButton() {
		if (this.minecraft.player.canModifyWorld()) {
			this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(null)));
			this.addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("lectern.take_book"), buttonWidget -> this.sendButtonPressPacket(3)));
		} else {
			super.addCloseButton();
		}
	}

	@Override
	protected void goToPreviousPage() {
		this.sendButtonPressPacket(1);
	}

	@Override
	protected void goToNextPage() {
		this.sendButtonPressPacket(2);
	}

	@Override
	protected boolean jumpToPage(int i) {
		if (i != this.lecternContainer.getPage()) {
			this.sendButtonPressPacket(100 + i);
			return true;
		} else {
			return false;
		}
	}

	private void sendButtonPressPacket(int i) {
		this.minecraft.interactionManager.clickButton(this.lecternContainer.syncId, i);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void updatePageProvider() {
		ItemStack itemStack = this.lecternContainer.getBookItem();
		this.setPageProvider(WrittenBookScreen.Contents.create(itemStack));
	}

	private void updatePage() {
		this.setPage(this.lecternContainer.getPage());
	}
}
