package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsPendingInvitesScreen extends RealmsScreen {
	static final Identifier ACCEPT_HIGHLIGHTED_ICON_TEXTURE = new Identifier("pending_invite/accept_highlighted");
	static final Identifier ACCEPT_ICON_TEXTURE = new Identifier("pending_invite/accept");
	static final Identifier REJECT_HIGHLIGHTED_ICON_TEXTURE = new Identifier("pending_invite/reject_highlighted");
	static final Identifier REJECT_ICON_TEXTURE = new Identifier("pending_invite/reject");
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text NO_PENDING_TEXT = Text.translatable("mco.invites.nopending");
	static final Text ACCEPT_TEXT = Text.translatable("mco.invites.button.accept");
	static final Text REJECT_TEXT = Text.translatable("mco.invites.button.reject");
	private final Screen parent;
	private final CompletableFuture<List<PendingInvite>> pendingInvites = CompletableFuture.supplyAsync(() -> {
		try {
			return RealmsClient.create().pendingInvites().pendingInvites;
		} catch (RealmsServiceException var1) {
			LOGGER.error("Couldn't list invites", (Throwable)var1);
			return List.of();
		}
	}, Util.getIoWorkerExecutor());
	@Nullable
	Text tooltip;
	RealmsPendingInvitesScreen.PendingInvitationSelectionList pendingInvitationSelectionList;
	int selectedInvite = -1;
	private ButtonWidget acceptButton;
	private ButtonWidget rejectButton;

	public RealmsPendingInvitesScreen(Screen parent, Text title) {
		super(title);
		this.parent = parent;
	}

	@Override
	public void init() {
		RealmsMainScreen.resetPendingInvitesCount();
		this.pendingInvitationSelectionList = new RealmsPendingInvitesScreen.PendingInvitationSelectionList();
		this.pendingInvites
			.thenAcceptAsync(
				pendingInvites -> {
					List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> list = pendingInvites.stream()
						.map(invite -> new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry(invite))
						.toList();
					this.pendingInvitationSelectionList.replaceEntries(list);
					if (list.isEmpty()) {
						this.client.getNarratorManager().narrateSystemMessage(NO_PENDING_TEXT);
					}
				},
				this.executor
			);
		this.addDrawableChild(this.pendingInvitationSelectionList);
		this.acceptButton = this.addDrawableChild(ButtonWidget.builder(ACCEPT_TEXT, button -> {
			this.handle(this.selectedInvite, true);
			this.selectedInvite = -1;
			this.updateButtonStates();
		}).dimensions(this.width / 2 - 174, this.height - 32, 100, 20).build());
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 50, this.height - 32, 100, 20).build());
		this.rejectButton = this.addDrawableChild(ButtonWidget.builder(REJECT_TEXT, button -> {
			this.handle(this.selectedInvite, false);
			this.selectedInvite = -1;
			this.updateButtonStates();
		}).dimensions(this.width / 2 + 74, this.height - 32, 100, 20).build());
		this.updateButtonStates();
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	void handle(int index, boolean accepted) {
		if (index < this.pendingInvitationSelectionList.getEntryCount()) {
			String string = ((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)this.pendingInvitationSelectionList.children().get(index)).mPendingInvite.invitationId;
			CompletableFuture.supplyAsync(() -> {
				try {
					RealmsClient realmsClient = RealmsClient.create();
					if (accepted) {
						realmsClient.acceptInvitation(string);
					} else {
						realmsClient.rejectInvitation(string);
					}

					return true;
				} catch (RealmsServiceException var3x) {
					LOGGER.error("Couldn't handle invite", (Throwable)var3x);
					return false;
				}
			}, Util.getIoWorkerExecutor()).thenAcceptAsync(result -> {
				if (result) {
					this.pendingInvitationSelectionList.removeAtIndex(index);
					RealmsPeriodicCheckers realmsPeriodicCheckers = this.client.getRealmsPeriodicCheckers();
					if (accepted) {
						realmsPeriodicCheckers.serverList.reset();
					}

					realmsPeriodicCheckers.pendingInvitesCount.reset();
				}
			}, this.executor);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.tooltip = null;
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, Colors.WHITE);
		if (this.tooltip != null) {
			context.drawTooltip(this.textRenderer, this.tooltip, mouseX, mouseY);
		}

		if (this.pendingInvites.isDone() && this.pendingInvitationSelectionList.getEntryCount() == 0) {
			context.drawCenteredTextWithShadow(this.textRenderer, NO_PENDING_TEXT, this.width / 2, this.height / 2 - 20, Colors.WHITE);
		}
	}

	void updateButtonStates() {
		this.acceptButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
		this.rejectButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
	}

	private boolean shouldAcceptAndRejectButtonBeVisible(int invite) {
		return invite != -1;
	}

	@Environment(EnvType.CLIENT)
	class PendingInvitationSelectionList extends RealmsObjectSelectionList<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> {
		public PendingInvitationSelectionList() {
			super(RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height - 72, 32, 36);
		}

		public void removeAtIndex(int index) {
			this.remove(index);
		}

		@Override
		public int getMaxPosition() {
			return this.getEntryCount() * 36;
		}

		@Override
		public int getRowWidth() {
			return 260;
		}

		@Override
		public void setSelected(int index) {
			super.setSelected(index);
			this.selectInviteListItem(index);
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
		private static final int field_32123 = 38;
		final PendingInvite mPendingInvite;
		private final List<RealmsAcceptRejectButton> buttons;

		PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
			this.mPendingInvite = pendingInvite;
			this.buttons = Arrays.asList(
				new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.AcceptButton(),
				new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.RejectButton()
			);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderPendingInvitationItem(context, this.mPendingInvite, x, y, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsAcceptRejectButton.handleClick(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.buttons, button, mouseX, mouseY);
			return true;
		}

		private void renderPendingInvitationItem(DrawContext context, PendingInvite invite, int x, int y, int mouseX, int mouseY) {
			context.drawText(RealmsPendingInvitesScreen.this.textRenderer, invite.worldName, x + 38, y + 1, Colors.WHITE, false);
			context.drawText(RealmsPendingInvitesScreen.this.textRenderer, invite.worldOwnerName, x + 38, y + 12, 7105644, false);
			context.drawText(RealmsPendingInvitesScreen.this.textRenderer, RealmsUtil.convertToAgePresentation(invite.date), x + 38, y + 24, 7105644, false);
			RealmsAcceptRejectButton.render(context, this.buttons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, x, y, mouseX, mouseY);
			RealmsUtil.drawPlayerHead(context, x, y, 32, invite.worldOwnerUuid);
		}

		@Override
		public Text getNarration() {
			Text text = ScreenTexts.joinLines(
				Text.literal(this.mPendingInvite.worldName),
				Text.literal(this.mPendingInvite.worldOwnerName),
				RealmsUtil.convertToAgePresentation(this.mPendingInvite.date)
			);
			return Text.translatable("narrator.select", text);
		}

		@Environment(EnvType.CLIENT)
		class AcceptButton extends RealmsAcceptRejectButton {
			AcceptButton() {
				super(15, 15, 215, 5);
			}

			@Override
			protected void render(DrawContext context, int x, int y, boolean showTooltip) {
				context.drawGuiTexture(
					showTooltip ? RealmsPendingInvitesScreen.ACCEPT_HIGHLIGHTED_ICON_TEXTURE : RealmsPendingInvitesScreen.ACCEPT_ICON_TEXTURE, x, y, 18, 18
				);
				if (showTooltip) {
					RealmsPendingInvitesScreen.this.tooltip = RealmsPendingInvitesScreen.ACCEPT_TEXT;
				}
			}

			@Override
			public void handleClick(int index) {
				RealmsPendingInvitesScreen.this.handle(index, true);
			}
		}

		@Environment(EnvType.CLIENT)
		class RejectButton extends RealmsAcceptRejectButton {
			RejectButton() {
				super(15, 15, 235, 5);
			}

			@Override
			protected void render(DrawContext context, int x, int y, boolean showTooltip) {
				context.drawGuiTexture(
					showTooltip ? RealmsPendingInvitesScreen.REJECT_HIGHLIGHTED_ICON_TEXTURE : RealmsPendingInvitesScreen.REJECT_ICON_TEXTURE, x, y, 18, 18
				);
				if (showTooltip) {
					RealmsPendingInvitesScreen.this.tooltip = RealmsPendingInvitesScreen.REJECT_TEXT;
				}
			}

			@Override
			public void handleClick(int index) {
				RealmsPendingInvitesScreen.this.handle(index, false);
			}
		}
	}
}
