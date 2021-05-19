package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class LecternScreen extends BookScreen implements ScreenHandlerProvider<LecternScreenHandler> {
	private final LecternScreenHandler handler;
	private final ScreenHandlerListener listener = new ScreenHandlerListener() {
		@Override
		public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
			LecternScreen.this.updatePageProvider();
		}

		@Override
		public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
			if (property == 0) {
				LecternScreen.this.updatePage();
			}
		}
	};

	public LecternScreen(LecternScreenHandler handler, PlayerInventory inventory, Text title) {
		this.handler = handler;
	}

	public LecternScreenHandler getScreenHandler() {
		return this.handler;
	}

	@Override
	protected void init() {
		super.init();
		this.handler.addListener(this.listener);
	}

	@Override
	public void onClose() {
		this.client.player.closeHandledScreen();
		super.onClose();
	}

	@Override
	public void removed() {
		super.removed();
		this.handler.removeListener(this.listener);
	}

	@Override
	protected void addCloseButton() {
		if (this.client.player.canModifyBlocks()) {
			this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, ScreenTexts.DONE, button -> this.onClose()));
			this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, new TranslatableText("lectern.take_book"), button -> this.sendButtonPressPacket(3)));
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
		if (page != this.handler.getPage()) {
			this.sendButtonPressPacket(100 + page);
			return true;
		} else {
			return false;
		}
	}

	private void sendButtonPressPacket(int id) {
		this.client.interactionManager.clickButton(this.handler.syncId, id);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	void updatePageProvider() {
		ItemStack itemStack = this.handler.getBookItem();
		this.setPageProvider(BookScreen.Contents.create(itemStack));
	}

	void updatePage() {
		this.setPage(this.handler.getPage());
	}

	@Override
	protected void closeScreen() {
		this.client.player.closeHandledScreen();
	}
}
