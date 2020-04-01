package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class LecternScreen extends BookScreen implements ScreenHandlerProvider<LecternScreenHandler> {
	private final LecternScreenHandler container;
	private final ScreenHandlerListener listener = new ScreenHandlerListener() {
		@Override
		public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onPropertyUpdate(ScreenHandler handler, int propertyId, int value) {
			if (propertyId == 0) {
				LecternScreen.this.updatePage();
			}
		}
	};

	public LecternScreen(LecternScreenHandler container, PlayerInventory inventory, Text title) {
		this.container = container;
	}

	public LecternScreenHandler getScreenHandler() {
		return this.container;
	}

	@Override
	protected void init() {
		super.init();
		this.container.addListener(this.listener);
	}

	@Override
	public void onClose() {
		this.client.player.closeHandledScreen();
		super.onClose();
	}

	@Override
	public void removed() {
		super.removed();
		this.container.removeListener(this.listener);
	}

	@Override
	protected void addCloseButton() {
		if (this.client.player.canModifyWorld()) {
			this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("gui.done"), buttonWidget -> this.client.openScreen(null)));
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
	protected boolean jumpToPage(int page) {
		if (page != this.container.getPage()) {
			this.sendButtonPressPacket(100 + page);
			return true;
		} else {
			return false;
		}
	}

	private void sendButtonPressPacket(int id) {
		this.client.interactionManager.clickButton(this.container.syncId, id);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void updatePageProvider() {
		ItemStack itemStack = this.container.getBookItem();
		this.setPageProvider(BookContent.fromStack(itemStack));
	}

	private void updatePage() {
		this.setPage(this.container.getPage());
	}
}
