package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPendingInvitesScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier field_22702 = new Identifier("realms", "textures/gui/realms/accept_icon.png");
	private static final Identifier field_22703 = new Identifier("realms", "textures/gui/realms/reject_icon.png");
	private final Screen lastScreen;
	private String toolTip;
	private boolean loaded;
	private RealmsPendingInvitesScreen.PendingInvitationSelectionList pendingInvitationSelectionList;
	private RealmsLabel titleLabel;
	private int selectedInvite = -1;
	private ButtonWidget acceptButton;
	private ButtonWidget rejectButton;

	public RealmsPendingInvitesScreen(Screen screen) {
		this.lastScreen = screen;
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.pendingInvitationSelectionList = new RealmsPendingInvitesScreen.PendingInvitationSelectionList();
		(new Thread("Realms-pending-invitations-fetcher") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					try {
						List<PendingInvite> list = realmsClient.pendingInvites().pendingInvites;
						List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> list2 = (List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry>)list.stream()
							.map(pendingInvite -> RealmsPendingInvitesScreen.this.new PendingInvitationSelectionListEntry(pendingInvite))
							.collect(Collectors.toList());
						RealmsPendingInvitesScreen.this.client.execute(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list2));
					} catch (RealmsServiceException var7) {
						RealmsPendingInvitesScreen.LOGGER.error("Couldn't list invites");
					} finally {
						RealmsPendingInvitesScreen.this.loaded = true;
					}
				}
			})
			.start();
		this.addChild(this.pendingInvitationSelectionList);
		this.acceptButton = this.addButton(
			new ButtonWidget(this.width / 2 - 174, this.height - 32, 100, 20, I18n.translate("mco.invites.button.accept"), buttonWidget -> {
				this.accept(this.selectedInvite);
				this.selectedInvite = -1;
				this.updateButtonStates();
			})
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 50, this.height - 32, 100, 20, I18n.translate("gui.done"), buttonWidget -> this.client.openScreen(new RealmsMainScreen(this.lastScreen))
			)
		);
		this.rejectButton = this.addButton(
			new ButtonWidget(this.width / 2 + 74, this.height - 32, 100, 20, I18n.translate("mco.invites.button.reject"), buttonWidget -> {
				this.reject(this.selectedInvite);
				this.selectedInvite = -1;
				this.updateButtonStates();
			})
		);
		this.titleLabel = new RealmsLabel(I18n.translate("mco.invites.title"), this.width / 2, 12, 16777215);
		this.addChild(this.titleLabel);
		this.narrateLabels();
		this.updateButtonStates();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(new RealmsMainScreen(this.lastScreen));
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void updateList(int slot) {
		this.pendingInvitationSelectionList.removeAtIndex(slot);
	}

	private void reject(int slot) {
		if (slot < this.pendingInvitationSelectionList.getItemCount()) {
			(new Thread("Realms-reject-invitation") {
					public void run() {
						try {
							RealmsClient realmsClient = RealmsClient.createRealmsClient();
							realmsClient.rejectInvitation(
								((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(slot))
									.mPendingInvite
									.invitationId
							);
							RealmsPendingInvitesScreen.this.client.execute(() -> RealmsPendingInvitesScreen.this.updateList(slot));
						} catch (RealmsServiceException var2) {
							RealmsPendingInvitesScreen.LOGGER.error("Couldn't reject invite");
						}
					}
				})
				.start();
		}
	}

	private void accept(int slot) {
		if (slot < this.pendingInvitationSelectionList.getItemCount()) {
			(new Thread("Realms-accept-invitation") {
					public void run() {
						try {
							RealmsClient realmsClient = RealmsClient.createRealmsClient();
							realmsClient.acceptInvitation(
								((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(slot))
									.mPendingInvite
									.invitationId
							);
							RealmsPendingInvitesScreen.this.client.execute(() -> RealmsPendingInvitesScreen.this.updateList(slot));
						} catch (RealmsServiceException var2) {
							RealmsPendingInvitesScreen.LOGGER.error("Couldn't accept invite");
						}
					}
				})
				.start();
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.toolTip = null;
		this.renderBackground();
		this.pendingInvitationSelectionList.render(mouseX, mouseY, delta);
		this.titleLabel.render(this);
		if (this.toolTip != null) {
			this.renderMousehoverTooltip(this.toolTip, mouseX, mouseY);
		}

		if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
			this.drawCenteredString(this.textRenderer, I18n.translate("mco.invites.nopending"), this.width / 2, this.height / 2 - 20, 16777215);
		}

		super.render(mouseX, mouseY, delta);
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

	private void updateButtonStates() {
		this.acceptButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
		this.rejectButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
	}

	private boolean shouldAcceptAndRejectButtonBeVisible(int invite) {
		return invite != -1;
	}

	@Environment(EnvType.CLIENT)
	class PendingInvitationSelectionList extends RealmsObjectSelectionList<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> {
		public PendingInvitationSelectionList() {
			super(RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height, 32, RealmsPendingInvitesScreen.this.height - 40, 36);
		}

		public void removeAtIndex(int index) {
			this.remove(index);
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 36;
		}

		@Override
		public int getRowWidth() {
			return 260;
		}

		@Override
		public boolean isFocused() {
			return RealmsPendingInvitesScreen.this.getFocused() == this;
		}

		@Override
		public void renderBackground() {
			RealmsPendingInvitesScreen.this.renderBackground();
		}

		@Override
		public void setSelected(int i) {
			this.setSelectedItem(i);
			if (i != -1) {
				List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
				PendingInvite pendingInvite = ((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)list.get(i)).mPendingInvite;
				String string = I18n.translate("narrator.select.list.position", i + 1, list.size());
				String string2 = Realms.joinNarrations(
					Arrays.asList(pendingInvite.worldName, pendingInvite.worldOwnerName, RealmsUtil.method_25282(pendingInvite.date), string)
				);
				Realms.narrateNow(I18n.translate("narrator.select", string2));
			}

			this.selectInviteListItem(i);
		}

		public void selectInviteListItem(int item) {
			RealmsPendingInvitesScreen.this.selectedInvite = item;
			RealmsPendingInvitesScreen.this.updateButtonStates();
		}

		public void setSelected(@Nullable RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry pendingInvitationSelectionListEntry) {
			super.setSelected(pendingInvitationSelectionListEntry);
			RealmsPendingInvitesScreen.this.selectedInvite = this.children().indexOf(pendingInvitationSelectionListEntry);
			RealmsPendingInvitesScreen.this.updateButtonStates();
		}
	}

	@Environment(EnvType.CLIENT)
	class PendingInvitationSelectionListEntry extends AlwaysSelectedEntryListWidget.Entry<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> {
		private final PendingInvite mPendingInvite;
		private final List<RealmsAcceptRejectButton> buttons;

		PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
			this.mPendingInvite = pendingInvite;
			this.buttons = Arrays.asList(
				new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.AcceptButton(),
				new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.RejectButton()
			);
		}

		@Override
		public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
			this.renderPendingInvitationItem(this.mPendingInvite, x, y, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsAcceptRejectButton.handleClick(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.buttons, button, mouseX, mouseY);
			return true;
		}

		private void renderPendingInvitationItem(PendingInvite invite, int x, int y, int mouseX, int mouseY) {
			RealmsPendingInvitesScreen.this.textRenderer.draw(invite.worldName, (float)(x + 38), (float)(y + 1), 16777215);
			RealmsPendingInvitesScreen.this.textRenderer.draw(invite.worldOwnerName, (float)(x + 38), (float)(y + 12), 7105644);
			RealmsPendingInvitesScreen.this.textRenderer.draw(RealmsUtil.method_25282(invite.date), (float)(x + 38), (float)(y + 24), 7105644);
			RealmsAcceptRejectButton.render(this.buttons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, x, y, mouseX, mouseY);
			RealmsTextureManager.withBoundFace(invite.worldOwnerUuid, () -> {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				DrawableHelper.blit(x, y, 32, 32, 8.0F, 8.0F, 8, 8, 64, 64);
				DrawableHelper.blit(x, y, 32, 32, 40.0F, 8.0F, 8, 8, 64, 64);
			});
		}

		@Environment(EnvType.CLIENT)
		class AcceptButton extends RealmsAcceptRejectButton {
			AcceptButton() {
				super(15, 15, 215, 5);
			}

			@Override
			protected void render(int x, int y, boolean hovered) {
				RealmsPendingInvitesScreen.this.client.getTextureManager().bindTexture(RealmsPendingInvitesScreen.field_22702);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				float f = hovered ? 19.0F : 0.0F;
				DrawableHelper.blit(x, y, f, 0.0F, 18, 18, 37, 18);
				if (hovered) {
					RealmsPendingInvitesScreen.this.toolTip = I18n.translate("mco.invites.button.accept");
				}
			}

			@Override
			public void handleClick(int index) {
				RealmsPendingInvitesScreen.this.accept(index);
			}
		}

		@Environment(EnvType.CLIENT)
		class RejectButton extends RealmsAcceptRejectButton {
			RejectButton() {
				super(15, 15, 235, 5);
			}

			@Override
			protected void render(int x, int y, boolean hovered) {
				RealmsPendingInvitesScreen.this.client.getTextureManager().bindTexture(RealmsPendingInvitesScreen.field_22703);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				float f = hovered ? 19.0F : 0.0F;
				DrawableHelper.blit(x, y, f, 0.0F, 18, 18, 37, 18);
				if (hovered) {
					RealmsPendingInvitesScreen.this.toolTip = I18n.translate("mco.invites.button.reject");
				}
			}

			@Override
			public void handleClick(int index) {
				RealmsPendingInvitesScreen.this.reject(index);
			}
		}
	}
}
