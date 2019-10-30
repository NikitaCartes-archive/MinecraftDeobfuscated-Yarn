package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private String toolTip;
	private final RealmsConfigureWorldScreen lastScreen;
	private final RealmsServer serverData;
	private RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
	private int column1_x;
	private int column_width;
	private int column2_x;
	private RealmsButton removeButton;
	private RealmsButton opdeopButton;
	private int selectedInvitedIndex = -1;
	private String selectedInvited;
	private int player = -1;
	private boolean stateChanged;
	private RealmsLabel titleLabel;

	public RealmsPlayerScreen(RealmsConfigureWorldScreen lastScreen, RealmsServer serverData) {
		this.lastScreen = lastScreen;
		this.serverData = serverData;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void init() {
		this.column1_x = this.width() / 2 - 160;
		this.column_width = 150;
		this.column2_x = this.width() / 2 + 12;
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(
			new RealmsButton(1, this.column2_x, RealmsConstants.row(1), this.column_width + 10, 20, getLocalizedString("mco.configure.world.buttons.invite")) {
				@Override
				public void onPress() {
					Realms.setScreen(new RealmsInviteScreen(RealmsPlayerScreen.this.lastScreen, RealmsPlayerScreen.this, RealmsPlayerScreen.this.serverData));
				}
			}
		);
		this.buttonsAdd(
			this.removeButton = new RealmsButton(
				4, this.column2_x, RealmsConstants.row(7), this.column_width + 10, 20, getLocalizedString("mco.configure.world.invites.remove.tooltip")
			) {
				@Override
				public void onPress() {
					RealmsPlayerScreen.this.uninvite(RealmsPlayerScreen.this.player);
				}
			}
		);
		this.buttonsAdd(
			this.opdeopButton = new RealmsButton(
				5, this.column2_x, RealmsConstants.row(9), this.column_width + 10, 20, getLocalizedString("mco.configure.world.invites.ops.tooltip")
			) {
				@Override
				public void onPress() {
					if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(RealmsPlayerScreen.this.player)).isOperator()) {
						RealmsPlayerScreen.this.deop(RealmsPlayerScreen.this.player);
					} else {
						RealmsPlayerScreen.this.op(RealmsPlayerScreen.this.player);
					}
				}
			}
		);
		this.buttonsAdd(
			new RealmsButton(0, this.column2_x + this.column_width / 2 + 2, RealmsConstants.row(12), this.column_width / 2 + 10 - 2, 20, getLocalizedString("gui.back")) {
				@Override
				public void onPress() {
					RealmsPlayerScreen.this.backButtonClicked();
				}
			}
		);
		this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
		this.invitedObjectSelectionList.setLeftPos(this.column1_x);
		this.addWidget(this.invitedObjectSelectionList);

		for (PlayerInfo playerInfo : this.serverData.players) {
			this.invitedObjectSelectionList.addEntry(playerInfo);
		}

		this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.players.title"), this.width() / 2, 17, 16777215));
		this.narrateLabels();
		this.updateButtonStates();
	}

	private void updateButtonStates() {
		this.removeButton.setVisible(this.shouldRemoveAndOpdeopButtonBeVisible(this.player));
		this.opdeopButton.setVisible(this.shouldRemoveAndOpdeopButtonBeVisible(this.player));
	}

	private boolean shouldRemoveAndOpdeopButtonBeVisible(int player) {
		return player != -1;
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			Realms.setScreen(this.lastScreen.getNewScreen());
		} else {
			Realms.setScreen(this.lastScreen);
		}
	}

	private void op(int index) {
		this.updateButtonStates();
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		String string = ((PlayerInfo)this.serverData.players.get(index)).getUuid();

		try {
			this.updateOps(realmsClient.op(this.serverData.id, string));
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't op the user");
		}
	}

	private void deop(int index) {
		this.updateButtonStates();
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		String string = ((PlayerInfo)this.serverData.players.get(index)).getUuid();

		try {
			this.updateOps(realmsClient.deop(this.serverData.id, string));
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't deop the user");
		}
	}

	private void updateOps(Ops ops) {
		for (PlayerInfo playerInfo : this.serverData.players) {
			playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
		}
	}

	private void uninvite(int index) {
		this.updateButtonStates();
		if (index >= 0 && index < this.serverData.players.size()) {
			PlayerInfo playerInfo = (PlayerInfo)this.serverData.players.get(index);
			this.selectedInvited = playerInfo.getUuid();
			this.selectedInvitedIndex = index;
			RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(
				this, "Question", getLocalizedString("mco.configure.world.uninvite.question") + " '" + playerInfo.getName() + "' ?", 2
			);
			Realms.setScreen(realmsConfirmScreen);
		}
	}

	@Override
	public void confirmResult(boolean result, int id) {
		if (id == 2) {
			if (result) {
				RealmsClient realmsClient = RealmsClient.createRealmsClient();

				try {
					realmsClient.uninvite(this.serverData.id, this.selectedInvited);
				} catch (RealmsServiceException var5) {
					LOGGER.error("Couldn't uninvite user");
				}

				this.deleteFromInvitedList(this.selectedInvitedIndex);
				this.player = -1;
				this.updateButtonStates();
			}

			this.stateChanged = true;
			Realms.setScreen(this);
		}
	}

	private void deleteFromInvitedList(int selectedInvitedIndex) {
		this.serverData.players.remove(selectedInvitedIndex);
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.toolTip = null;
		this.renderBackground();
		if (this.invitedObjectSelectionList != null) {
			this.invitedObjectSelectionList.render(xm, ym, a);
		}

		int i = RealmsConstants.row(12) + 20;
		RenderSystem.disableFog();
		Tezzelator tezzelator = Tezzelator.instance;
		bind("textures/gui/options_background.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
		tezzelator.vertex(0.0, (double)this.height(), 0.0).tex(0.0F, (float)(this.height() - i) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
		tezzelator.vertex((double)this.width(), (double)this.height(), 0.0)
			.tex((float)this.width() / 32.0F, (float)(this.height() - i) / 32.0F + 0.0F)
			.color(64, 64, 64, 255)
			.endVertex();
		tezzelator.vertex((double)this.width(), (double)i, 0.0).tex((float)this.width() / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
		tezzelator.vertex(0.0, (double)i, 0.0).tex(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
		tezzelator.end();
		this.titleLabel.render(this);
		if (this.serverData != null && this.serverData.players != null) {
			this.drawString(
				getLocalizedString("mco.configure.world.invited") + " (" + this.serverData.players.size() + ")", this.column1_x, RealmsConstants.row(0), 10526880
			);
		} else {
			this.drawString(getLocalizedString("mco.configure.world.invited"), this.column1_x, RealmsConstants.row(0), 10526880);
		}

		super.render(xm, ym, a);
		if (this.serverData != null) {
			if (this.toolTip != null) {
				this.renderMousehoverTooltip(this.toolTip, xm, ym);
			}
		}
	}

	protected void renderMousehoverTooltip(String msg, int x, int y) {
		if (msg != null) {
			int i = x + 12;
			int j = y - 12;
			int k = this.fontWidth(msg);
			this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(msg, i, j, 16777215);
		}
	}

	private void drawRemoveIcon(int x, int y, int xm, int ym) {
		boolean bl = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < RealmsConstants.row(12) + 20 && ym > RealmsConstants.row(1);
		bind("realms:textures/gui/realms/cross_player_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		RealmsScreen.blit(x, y, 0.0F, bl ? 7.0F : 0.0F, 8, 7, 8, 14);
		RenderSystem.popMatrix();
		if (bl) {
			this.toolTip = getLocalizedString("mco.configure.world.invites.remove.tooltip");
		}
	}

	private void drawOpped(int x, int y, int xm, int ym) {
		boolean bl = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < RealmsConstants.row(12) + 20 && ym > RealmsConstants.row(1);
		bind("realms:textures/gui/realms/op_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		RealmsScreen.blit(x, y, 0.0F, bl ? 8.0F : 0.0F, 8, 8, 8, 16);
		RenderSystem.popMatrix();
		if (bl) {
			this.toolTip = getLocalizedString("mco.configure.world.invites.ops.tooltip");
		}
	}

	private void drawNormal(int x, int y, int xm, int ym) {
		boolean bl = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < RealmsConstants.row(12) + 20 && ym > RealmsConstants.row(1);
		bind("realms:textures/gui/realms/user_icon.png");
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.pushMatrix();
		RealmsScreen.blit(x, y, 0.0F, bl ? 8.0F : 0.0F, 8, 8, 8, 16);
		RenderSystem.popMatrix();
		if (bl) {
			this.toolTip = getLocalizedString("mco.configure.world.invites.normal.tooltip");
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionList extends RealmsObjectSelectionList {
		public InvitedObjectSelectionList() {
			super(RealmsPlayerScreen.this.column_width + 10, RealmsConstants.row(12) + 20, RealmsConstants.row(1), RealmsConstants.row(12) + 20, 13);
		}

		public void addEntry(PlayerInfo playerInfo) {
			this.addEntry(RealmsPlayerScreen.this.new InvitedObjectSelectionListEntry(playerInfo));
		}

		@Override
		public int getRowWidth() {
			return (int)((double)this.width() * 1.0);
		}

		@Override
		public boolean isFocused() {
			return RealmsPlayerScreen.this.isFocused(this);
		}

		@Override
		public boolean mouseClicked(double xm, double ym, int buttonNum) {
			if (buttonNum == 0 && xm < (double)this.getScrollbarPosition() && ym >= (double)this.y0() && ym <= (double)this.y1()) {
				int i = RealmsPlayerScreen.this.column1_x;
				int j = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
				int k = (int)Math.floor(ym - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
				int l = k / this.itemHeight();
				if (xm >= (double)i && xm <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
					this.selectItem(l);
					this.itemClicked(k, l, xm, ym, this.width());
				}

				return true;
			} else {
				return super.mouseClicked(xm, ym, buttonNum);
			}
		}

		@Override
		public void itemClicked(int clickSlotPos, int slot, double xm, double ym, int width) {
			if (slot >= 0 && slot <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.toolTip != null) {
				if (!RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip"))
					&& !RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip"))) {
					if (RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
						RealmsPlayerScreen.this.uninvite(slot);
					}
				} else if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(slot)).isOperator()) {
					RealmsPlayerScreen.this.deop(slot);
				} else {
					RealmsPlayerScreen.this.op(slot);
				}
			}
		}

		@Override
		public void selectItem(int item) {
			this.setSelected(item);
			if (item != -1) {
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(item)).getName()));
			}

			this.selectInviteListItem(item);
		}

		public void selectInviteListItem(int item) {
			RealmsPlayerScreen.this.player = item;
			RealmsPlayerScreen.this.updateButtonStates();
		}

		@Override
		public void renderBackground() {
			RealmsPlayerScreen.this.renderBackground();
		}

		@Override
		public int getScrollbarPosition() {
			return RealmsPlayerScreen.this.column1_x + this.width() - 5;
		}

		@Override
		public int getItemCount() {
			return RealmsPlayerScreen.this.serverData == null ? 1 : RealmsPlayerScreen.this.serverData.players.size();
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 13;
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionListEntry extends RealmListEntry {
		final PlayerInfo mPlayerInfo;

		public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
			this.mPlayerInfo = playerInfo;
		}

		@Override
		public void render(int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float a) {
			this.renderInvitedItem(this.mPlayerInfo, rowLeft, rowTop, mouseX, mouseY);
		}

		private void renderInvitedItem(PlayerInfo invited, int x, int y, int mouseX, int mouseY) {
			int i;
			if (!invited.getAccepted()) {
				i = 10526880;
			} else if (invited.getOnline()) {
				i = 8388479;
			} else {
				i = 16777215;
			}

			RealmsPlayerScreen.this.drawString(invited.getName(), RealmsPlayerScreen.this.column1_x + 3 + 12, y + 1, i);
			if (invited.isOperator()) {
				RealmsPlayerScreen.this.drawOpped(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
			} else {
				RealmsPlayerScreen.this.drawNormal(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
			}

			RealmsPlayerScreen.this.drawRemoveIcon(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, y + 2, mouseX, mouseY);
			RealmsPlayerScreen.this.drawString(
				RealmsScreen.getLocalizedString("mco.configure.world.activityfeed.disabled"), RealmsPlayerScreen.this.column2_x, RealmsConstants.row(5), 10526880
			);
			RealmsTextureManager.withBoundFace(invited.getUuid(), () -> {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8.0F, 8.0F, 8, 8, 8, 8, 64, 64);
				RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 40.0F, 8.0F, 8, 8, 8, 8, 64, 64);
			});
		}
	}
}
