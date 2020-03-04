package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_22704 = new Identifier("realms", "textures/gui/realms/op_icon.png");
	private static final Identifier field_22705 = new Identifier("realms", "textures/gui/realms/user_icon.png");
	private static final Identifier field_22706 = new Identifier("realms", "textures/gui/realms/cross_player_icon.png");
	private static final Identifier field_22707 = new Identifier("minecraft", "textures/gui/options_background.png");
	private String toolTip;
	private final RealmsConfigureWorldScreen lastScreen;
	private final RealmsServer serverData;
	private RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
	private int column1_x;
	private int column_width;
	private int column2_x;
	private ButtonWidget removeButton;
	private ButtonWidget opdeopButton;
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
	public void init() {
		this.column1_x = this.width / 2 - 160;
		this.column_width = 150;
		this.column2_x = this.width / 2 + 12;
		this.client.keyboard.enableRepeatEvents(true);
		this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
		this.invitedObjectSelectionList.setLeftPos(this.column1_x);
		this.addChild(this.invitedObjectSelectionList);

		for (PlayerInfo playerInfo : this.serverData.players) {
			this.invitedObjectSelectionList.addEntry(playerInfo);
		}

		this.addButton(
			new ButtonWidget(
				this.column2_x,
				row(1),
				this.column_width + 10,
				20,
				I18n.translate("mco.configure.world.buttons.invite"),
				buttonWidget -> this.client.openScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData))
			)
		);
		this.removeButton = this.addButton(
			new ButtonWidget(
				this.column2_x,
				row(7),
				this.column_width + 10,
				20,
				I18n.translate("mco.configure.world.invites.remove.tooltip"),
				buttonWidget -> this.uninvite(this.player)
			)
		);
		this.opdeopButton = this.addButton(
			new ButtonWidget(this.column2_x, row(9), this.column_width + 10, 20, I18n.translate("mco.configure.world.invites.ops.tooltip"), buttonWidget -> {
				if (((PlayerInfo)this.serverData.players.get(this.player)).isOperator()) {
					this.deop(this.player);
				} else {
					this.op(this.player);
				}
			})
		);
		this.addButton(
			new ButtonWidget(
				this.column2_x + this.column_width / 2 + 2,
				row(12),
				this.column_width / 2 + 10 - 2,
				20,
				I18n.translate("gui.back"),
				buttonWidget -> this.backButtonClicked()
			)
		);
		this.titleLabel = this.addChild(new RealmsLabel(I18n.translate("mco.configure.world.players.title"), this.width / 2, 17, 16777215));
		this.narrateLabels();
		this.updateButtonStates();
	}

	private void updateButtonStates() {
		this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
		this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
	}

	private boolean shouldRemoveAndOpdeopButtonBeVisible(int player) {
		return player != -1;
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.backButtonClicked();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void backButtonClicked() {
		if (this.stateChanged) {
			this.client.openScreen(this.lastScreen.getNewScreen());
		} else {
			this.client.openScreen(this.lastScreen);
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
			RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(bl -> {
				if (bl) {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					try {
						realmsClient.uninvite(this.serverData.id, this.selectedInvited);
					} catch (RealmsServiceException var4) {
						LOGGER.error("Couldn't uninvite user");
					}

					this.deleteFromInvitedList(this.selectedInvitedIndex);
					this.player = -1;
					this.updateButtonStates();
				}

				this.stateChanged = true;
				this.client.openScreen(this);
			}, "Question", I18n.translate("mco.configure.world.uninvite.question") + " '" + playerInfo.getName() + "' ?");
			this.client.openScreen(realmsConfirmScreen);
		}
	}

	private void deleteFromInvitedList(int selectedInvitedIndex) {
		this.serverData.players.remove(selectedInvitedIndex);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.toolTip = null;
		this.renderBackground();
		if (this.invitedObjectSelectionList != null) {
			this.invitedObjectSelectionList.render(mouseX, mouseY, delta);
		}

		int i = row(12) + 20;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(field_22707);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0.0, (double)this.height, 0.0).texture(0.0F, (float)(this.height - i) / 32.0F + 0.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex((double)this.width, (double)this.height, 0.0)
			.texture((float)this.width / 32.0F, (float)(this.height - i) / 32.0F + 0.0F)
			.color(64, 64, 64, 255)
			.next();
		bufferBuilder.vertex((double)this.width, (double)i, 0.0).texture((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0.0, (double)i, 0.0).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
		tessellator.draw();
		this.titleLabel.render(this);
		if (this.serverData != null && this.serverData.players != null) {
			this.textRenderer
				.draw(I18n.translate("mco.configure.world.invited") + " (" + this.serverData.players.size() + ")", (float)this.column1_x, (float)row(0), 10526880);
		} else {
			this.textRenderer.draw(I18n.translate("mco.configure.world.invited"), (float)this.column1_x, (float)row(0), 10526880);
		}

		super.render(mouseX, mouseY, delta);
		if (this.serverData != null) {
			if (this.toolTip != null) {
				this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
			}
		}
	}

	protected void renderMousehoverTooltip(String msg, int x, int y) {
		if (msg != null) {
			int i = x + 12;
			int j = y - 12;
			int k = this.textRenderer.getStringWidth(msg);
			this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(msg, (float)i, (float)j, 16777215);
		}
	}

	private void drawRemoveIcon(int x, int y, int xm, int ym) {
		boolean bl = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < row(12) + 20 && ym > row(1);
		this.client.getTextureManager().bindTexture(field_22706);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = bl ? 7.0F : 0.0F;
		DrawableHelper.blit(x, y, 0.0F, f, 8, 7, 8, 14);
		if (bl) {
			this.toolTip = I18n.translate("mco.configure.world.invites.remove.tooltip");
		}
	}

	private void drawOpped(int x, int y, int xm, int ym) {
		boolean bl = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < row(12) + 20 && ym > row(1);
		this.client.getTextureManager().bindTexture(field_22704);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = bl ? 8.0F : 0.0F;
		DrawableHelper.blit(x, y, 0.0F, f, 8, 8, 8, 16);
		if (bl) {
			this.toolTip = I18n.translate("mco.configure.world.invites.ops.tooltip");
		}
	}

	private void drawNormal(int x, int y, int xm, int ym) {
		boolean bl = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < row(12) + 20 && ym > row(1);
		this.client.getTextureManager().bindTexture(field_22705);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = bl ? 8.0F : 0.0F;
		DrawableHelper.blit(x, y, 0.0F, f, 8, 8, 8, 16);
		if (bl) {
			this.toolTip = I18n.translate("mco.configure.world.invites.normal.tooltip");
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionList extends RealmsObjectSelectionList<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		public InvitedObjectSelectionList() {
			super(RealmsPlayerScreen.this.column_width + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
		}

		public void addEntry(PlayerInfo playerInfo) {
			this.addEntry(RealmsPlayerScreen.this.new InvitedObjectSelectionListEntry(playerInfo));
		}

		@Override
		public int getRowWidth() {
			return (int)((double)this.width * 1.0);
		}

		@Override
		public boolean isFocused() {
			return RealmsPlayerScreen.this.getFocused() == this;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0 && mouseX < (double)this.getScrollbarPositionX() && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
				int i = RealmsPlayerScreen.this.column1_x;
				int j = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
				int k = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
				int l = k / this.itemHeight;
				if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
					this.setSelected(l);
					this.itemClicked(k, l, mouseX, mouseY, this.width);
				}

				return true;
			} else {
				return super.mouseClicked(mouseX, mouseY, button);
			}
		}

		@Override
		public void itemClicked(int i, int j, double d, double e, int k) {
			if (j >= 0 && j <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.toolTip != null) {
				if (!RealmsPlayerScreen.this.toolTip.equals(I18n.translate("mco.configure.world.invites.ops.tooltip"))
					&& !RealmsPlayerScreen.this.toolTip.equals(I18n.translate("mco.configure.world.invites.normal.tooltip"))) {
					if (RealmsPlayerScreen.this.toolTip.equals(I18n.translate("mco.configure.world.invites.remove.tooltip"))) {
						RealmsPlayerScreen.this.uninvite(j);
					}
				} else if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(j)).isOperator()) {
					RealmsPlayerScreen.this.deop(j);
				} else {
					RealmsPlayerScreen.this.op(j);
				}
			}
		}

		@Override
		public void setSelected(int i) {
			this.setSelectedItem(i);
			if (i != -1) {
				Realms.narrateNow(I18n.translate("narrator.select", ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(i)).getName()));
			}

			this.selectInviteListItem(i);
		}

		public void selectInviteListItem(int item) {
			RealmsPlayerScreen.this.player = item;
			RealmsPlayerScreen.this.updateButtonStates();
		}

		public void setSelected(@Nullable RealmsPlayerScreen.InvitedObjectSelectionListEntry invitedObjectSelectionListEntry) {
			super.setSelected(invitedObjectSelectionListEntry);
			RealmsPlayerScreen.this.player = this.children().indexOf(invitedObjectSelectionListEntry);
			RealmsPlayerScreen.this.updateButtonStates();
		}

		@Override
		public void renderBackground() {
			RealmsPlayerScreen.this.renderBackground();
		}

		@Override
		public int getScrollbarPositionX() {
			return RealmsPlayerScreen.this.column1_x + this.width - 5;
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 13;
		}
	}

	@Environment(EnvType.CLIENT)
	class InvitedObjectSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsPlayerScreen.InvitedObjectSelectionListEntry> {
		private final PlayerInfo mPlayerInfo;

		public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
			this.mPlayerInfo = playerInfo;
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			this.renderInvitedItem(this.mPlayerInfo, x, y, mouseX, mouseY);
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

			RealmsPlayerScreen.this.textRenderer.draw(invited.getName(), (float)(RealmsPlayerScreen.this.column1_x + 3 + 12), (float)(y + 1), i);
			if (invited.isOperator()) {
				RealmsPlayerScreen.this.drawOpped(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
			} else {
				RealmsPlayerScreen.this.drawNormal(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, mouseX, mouseY);
			}

			RealmsPlayerScreen.this.drawRemoveIcon(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, y + 2, mouseX, mouseY);
			RealmsPlayerScreen.this.textRenderer
				.draw(I18n.translate("mco.configure.world.activityfeed.disabled"), (float)RealmsPlayerScreen.this.column2_x, (float)RealmsPlayerScreen.row(5), 10526880);
			RealmsTextureManager.withBoundFace(invited.getUuid(), () -> {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
				DrawableHelper.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
			});
		}
	}
}
