/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsAcceptRejectButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsPendingInvitesScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsScreen lastScreen;
    private String toolTip;
    private boolean loaded;
    private PendingInvitationSelectionList pendingInvitationSelectionList;
    private RealmsLabel titleLabel;
    private int selectedInvite = -1;
    private RealmsButton acceptButton;
    private RealmsButton rejectButton;

    public RealmsPendingInvitesScreen(RealmsScreen lastScreen) {
        this.lastScreen = lastScreen;
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        new Thread("Realms-pending-invitations-fetcher"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                try {
                    List<PendingInvite> list = realmsClient.pendingInvites().pendingInvites;
                    List list2 = list.stream().map(pendingInvite -> new PendingInvitationSelectionListEntry((PendingInvite)pendingInvite)).collect(Collectors.toList());
                    Realms.execute(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list2));
                } catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't list invites");
                } finally {
                    RealmsPendingInvitesScreen.this.loaded = true;
                }
            }
        }.start();
        this.acceptButton = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, RealmsPendingInvitesScreen.getLocalizedString("mco.invites.button.accept")){

            @Override
            public void onPress() {
                RealmsPendingInvitesScreen.this.accept(RealmsPendingInvitesScreen.this.selectedInvite);
                RealmsPendingInvitesScreen.this.selectedInvite = -1;
                RealmsPendingInvitesScreen.this.updateButtonStates();
            }
        };
        this.buttonsAdd(this.acceptButton);
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, RealmsPendingInvitesScreen.getLocalizedString("gui.done")){

            @Override
            public void onPress() {
                Realms.setScreen(new RealmsMainScreen(RealmsPendingInvitesScreen.this.lastScreen));
            }
        });
        this.rejectButton = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, RealmsPendingInvitesScreen.getLocalizedString("mco.invites.button.reject")){

            @Override
            public void onPress() {
                RealmsPendingInvitesScreen.this.reject(RealmsPendingInvitesScreen.this.selectedInvite);
                RealmsPendingInvitesScreen.this.selectedInvite = -1;
                RealmsPendingInvitesScreen.this.updateButtonStates();
            }
        };
        this.buttonsAdd(this.rejectButton);
        this.titleLabel = new RealmsLabel(RealmsPendingInvitesScreen.getLocalizedString("mco.invites.title"), this.width() / 2, 12, 0xFFFFFF);
        this.addWidget(this.titleLabel);
        this.addWidget(this.pendingInvitationSelectionList);
        this.narrateLabels();
        this.updateButtonStates();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        if (eventKey == 256) {
            Realms.setScreen(new RealmsMainScreen(this.lastScreen));
            return true;
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    private void updateList(int slot) {
        this.pendingInvitationSelectionList.removeAtIndex(slot);
    }

    private void reject(final int slot) {
        if (slot < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-reject-invitation"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.createRealmsClient();
                        realmsClient.rejectInvitation(((PendingInvitationSelectionListEntry)((RealmsPendingInvitesScreen)RealmsPendingInvitesScreen.this).pendingInvitationSelectionList.children().get((int)slot)).mPendingInvite.invitationId);
                        Realms.execute(() -> RealmsPendingInvitesScreen.this.updateList(slot));
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't reject invite");
                    }
                }
            }.start();
        }
    }

    private void accept(final int slot) {
        if (slot < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-accept-invitation"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.createRealmsClient();
                        realmsClient.acceptInvitation(((PendingInvitationSelectionListEntry)((RealmsPendingInvitesScreen)RealmsPendingInvitesScreen.this).pendingInvitationSelectionList.children().get((int)slot)).mPendingInvite.invitationId);
                        Realms.execute(() -> RealmsPendingInvitesScreen.this.updateList(slot));
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't accept invite");
                    }
                }
            }.start();
        }
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.toolTip = null;
        this.renderBackground();
        this.pendingInvitationSelectionList.render(xm, ym, a);
        this.titleLabel.render(this);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm, ym);
        }
        if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
            this.drawCenteredString(RealmsPendingInvitesScreen.getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 0xFFFFFF);
        }
        super.render(xm, ym, a);
    }

    protected void renderMousehoverTooltip(String msg, int x, int y) {
        if (msg == null) {
            return;
        }
        int i = x + 12;
        int j = y - 12;
        int k = this.fontWidth(msg);
        this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(msg, i, j, 0xFFFFFF);
    }

    private void updateButtonStates() {
        this.acceptButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
        this.rejectButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
    }

    private boolean shouldAcceptAndRejectButtonBeVisible(int invite) {
        return invite != -1;
    }

    public static String getAgePresentation(PendingInvite invite) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - invite.date.getTime());
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitationSelectionListEntry
    extends RealmListEntry {
        final PendingInvite mPendingInvite;
        private final List<RealmsAcceptRejectButton> buttons;

        PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
            this.mPendingInvite = pendingInvite;
            this.buttons = Arrays.asList(new AcceptButton(), new RejectButton());
        }

        @Override
        public void render(int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float a) {
            this.renderPendingInvitationItem(this.mPendingInvite, rowLeft, rowTop, mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            RealmsAcceptRejectButton.handleClick(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.buttons, i, d, e);
            return true;
        }

        private void renderPendingInvitationItem(PendingInvite invite, int x, int y, int mouseX, int mouseY) {
            RealmsPendingInvitesScreen.this.drawString(invite.worldName, x + 38, y + 1, 0xFFFFFF);
            RealmsPendingInvitesScreen.this.drawString(invite.worldOwnerName, x + 38, y + 12, 0x808080);
            RealmsPendingInvitesScreen.this.drawString(RealmsPendingInvitesScreen.getAgePresentation(invite), x + 38, y + 24, 0x808080);
            RealmsAcceptRejectButton.render(this.buttons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, x, y, mouseX, mouseY);
            RealmsTextureManager.withBoundFace(invite.worldOwnerUuid, () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(x, y, 8.0f, 8.0f, 8, 8, 32, 32, 64, 64);
                RealmsScreen.blit(x, y, 40.0f, 8.0f, 8, 8, 32, 32, 64, 64);
            });
        }

        @Environment(value=EnvType.CLIENT)
        class RejectButton
        extends RealmsAcceptRejectButton {
            RejectButton() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void render(int x, int y, boolean hovered) {
                RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.pushMatrix();
                RealmsScreen.blit(x, y, hovered ? 19.0f : 0.0f, 0.0f, 18, 18, 37, 18);
                RenderSystem.popMatrix();
                if (hovered) {
                    RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.reject");
                }
            }

            @Override
            public void handleClick(int index) {
                RealmsPendingInvitesScreen.this.reject(index);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class AcceptButton
        extends RealmsAcceptRejectButton {
            AcceptButton() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void render(int x, int y, boolean hovered) {
                RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.pushMatrix();
                RealmsScreen.blit(x, y, hovered ? 19.0f : 0.0f, 0.0f, 18, 18, 37, 18);
                RenderSystem.popMatrix();
                if (hovered) {
                    RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.accept");
                }
            }

            @Override
            public void handleClick(int index) {
                RealmsPendingInvitesScreen.this.accept(index);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitationSelectionList
    extends RealmsObjectSelectionList<PendingInvitationSelectionListEntry> {
        public PendingInvitationSelectionList() {
            super(RealmsPendingInvitesScreen.this.width(), RealmsPendingInvitesScreen.this.height(), 32, RealmsPendingInvitesScreen.this.height() - 40, 36);
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
            return RealmsPendingInvitesScreen.this.isFocused(this);
        }

        @Override
        public void renderBackground() {
            RealmsPendingInvitesScreen.this.renderBackground();
        }

        @Override
        public void selectItem(int item) {
            this.setSelected(item);
            if (item != -1) {
                List list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
                PendingInvite pendingInvite = ((PendingInvitationSelectionListEntry)list.get((int)item)).mPendingInvite;
                String string = RealmsScreen.getLocalizedString("narrator.select.list.position", item + 1, list.size());
                String string2 = Realms.joinNarrations(Arrays.asList(pendingInvite.worldName, pendingInvite.worldOwnerName, RealmsPendingInvitesScreen.getAgePresentation(pendingInvite), string));
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string2));
            }
            this.selectInviteListItem(item);
        }

        public void selectInviteListItem(int item) {
            RealmsPendingInvitesScreen.this.selectedInvite = item;
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }
    }
}

