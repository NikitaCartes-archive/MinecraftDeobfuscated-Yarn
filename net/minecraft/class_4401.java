/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.PendingInvite;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4325;
import net.minecraft.class_4341;
import net.minecraft.class_4355;
import net.minecraft.class_4371;
import net.minecraft.class_4446;
import net.minecraft.class_4448;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class class_4401
extends RealmsScreen {
    private static final Logger field_19935 = LogManager.getLogger();
    private final RealmsScreen field_19936;
    private String field_19937;
    private boolean field_19938;
    private class_4402 field_19939;
    private RealmsLabel field_19940;
    private int field_19941 = -1;
    private RealmsButton field_19942;
    private RealmsButton field_19943;

    public class_4401(RealmsScreen realmsScreen) {
        this.field_19936 = realmsScreen;
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.field_19939 = new class_4402();
        new Thread("Realms-pending-invitations-fetcher"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                class_4341 lv = class_4341.method_20989();
                try {
                    List<PendingInvite> list = lv.method_21030().pendingInvites;
                    List list2 = list.stream().map(pendingInvite -> new class_4403((PendingInvite)pendingInvite)).collect(Collectors.toList());
                    Realms.execute(() -> class_4401.this.field_19939.replaceEntries(list2));
                } catch (class_4355 lv2) {
                    field_19935.error("Couldn't list invites");
                } finally {
                    class_4401.this.field_19938 = true;
                }
            }
        }.start();
        this.field_19942 = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, class_4401.getLocalizedString("mco.invites.button.accept")){

            @Override
            public void onPress() {
                class_4401.this.method_21311(class_4401.this.field_19941);
                class_4401.this.field_19941 = -1;
                class_4401.this.method_21307();
            }
        };
        this.buttonsAdd(this.field_19942);
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, class_4401.getLocalizedString("gui.done")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4325(class_4401.this.field_19936));
            }
        });
        this.field_19943 = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, class_4401.getLocalizedString("mco.invites.button.reject")){

            @Override
            public void onPress() {
                class_4401.this.method_21308(class_4401.this.field_19941);
                class_4401.this.field_19941 = -1;
                class_4401.this.method_21307();
            }
        };
        this.buttonsAdd(this.field_19943);
        this.field_19940 = new RealmsLabel(class_4401.getLocalizedString("mco.invites.title"), this.width() / 2, 12, 0xFFFFFF);
        this.addWidget(this.field_19940);
        this.addWidget(this.field_19939);
        this.narrateLabels();
        this.method_21307();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            Realms.setScreen(new class_4325(this.field_19936));
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void method_21300(int i) {
        this.field_19939.method_21321(i);
    }

    private void method_21308(final int i) {
        if (i < this.field_19939.getItemCount()) {
            new Thread("Realms-reject-invitation"){

                @Override
                public void run() {
                    try {
                        class_4341 lv = class_4341.method_20989();
                        lv.method_21006(((class_4403)((class_4401)class_4401.this).field_19939.children().get((int)i)).field_19953.invitationId);
                        Realms.execute(() -> class_4401.this.method_21300(i));
                    } catch (class_4355 lv2) {
                        field_19935.error("Couldn't reject invite");
                    }
                }
            }.start();
        }
    }

    private void method_21311(final int i) {
        if (i < this.field_19939.getItemCount()) {
            new Thread("Realms-accept-invitation"){

                @Override
                public void run() {
                    try {
                        class_4341 lv = class_4341.method_20989();
                        lv.method_20999(((class_4403)((class_4401)class_4401.this).field_19939.children().get((int)i)).field_19953.invitationId);
                        Realms.execute(() -> class_4401.this.method_21300(i));
                    } catch (class_4355 lv2) {
                        field_19935.error("Couldn't accept invite");
                    }
                }
            }.start();
        }
    }

    @Override
    public void render(int i, int j, float f) {
        this.field_19937 = null;
        this.renderBackground();
        this.field_19939.render(i, j, f);
        this.field_19940.render(this);
        if (this.field_19937 != null) {
            this.method_21306(this.field_19937, i, j);
        }
        if (this.field_19939.getItemCount() == 0 && this.field_19938) {
            this.drawCenteredString(class_4401.getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 0xFFFFFF);
        }
        super.render(i, j, f);
    }

    protected void method_21306(String string, int i, int j) {
        if (string == null) {
            return;
        }
        int k = i + 12;
        int l = j - 12;
        int m = this.fontWidth(string);
        this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, k, l, 0xFFFFFF);
    }

    private void method_21307() {
        this.field_19942.setVisible(this.method_21314(this.field_19941));
        this.field_19943.setVisible(this.method_21314(this.field_19941));
    }

    private boolean method_21314(int i) {
        return i != -1;
    }

    public static String method_21301(PendingInvite pendingInvite) {
        return class_4448.method_21567(System.currentTimeMillis() - pendingInvite.date.getTime());
    }

    @Environment(value=EnvType.CLIENT)
    class class_4403
    extends RealmListEntry {
        final PendingInvite field_19953;
        private final List<class_4371> field_19955;

        class_4403(PendingInvite pendingInvite) {
            this.field_19953 = pendingInvite;
            this.field_19955 = Arrays.asList(new class_4404(), new class_4405());
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.method_21324(this.field_19953, k, j, n, o);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            class_4371.method_21114(class_4401.this.field_19939, this, this.field_19955, i, d, e);
            return true;
        }

        private void method_21324(PendingInvite pendingInvite, int i, int j, int k, int l) {
            class_4401.this.drawString(pendingInvite.worldName, i + 38, j + 1, 0xFFFFFF);
            class_4401.this.drawString(pendingInvite.worldOwnerName, i + 38, j + 12, 0x6C6C6C);
            class_4401.this.drawString(class_4401.method_21301(pendingInvite), i + 38, j + 24, 0x6C6C6C);
            class_4371.method_21113(this.field_19955, class_4401.this.field_19939, i, j, k, l);
            class_4446.method_21559(pendingInvite.worldOwnerUuid, () -> {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(i, j, 8.0f, 8.0f, 8, 8, 32, 32, 64, 64);
                RealmsScreen.blit(i, j, 40.0f, 8.0f, 8, 8, 32, 32, 64, 64);
            });
        }

        @Environment(value=EnvType.CLIENT)
        class class_4405
        extends class_4371 {
            class_4405() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void method_21112(int i, int j, boolean bl) {
                RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.pushMatrix();
                RealmsScreen.blit(i, j, bl ? 19.0f : 0.0f, 0.0f, 18, 18, 37, 18);
                GlStateManager.popMatrix();
                if (bl) {
                    class_4401.this.field_19937 = RealmsScreen.getLocalizedString("mco.invites.button.reject");
                }
            }

            @Override
            public void method_21110(int i) {
                class_4401.this.method_21308(i);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class class_4404
        extends class_4371 {
            class_4404() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void method_21112(int i, int j, boolean bl) {
                RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.pushMatrix();
                RealmsScreen.blit(i, j, bl ? 19.0f : 0.0f, 0.0f, 18, 18, 37, 18);
                GlStateManager.popMatrix();
                if (bl) {
                    class_4401.this.field_19937 = RealmsScreen.getLocalizedString("mco.invites.button.accept");
                }
            }

            @Override
            public void method_21110(int i) {
                class_4401.this.method_21311(i);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class class_4402
    extends RealmsObjectSelectionList<class_4403> {
        public class_4402() {
            super(class_4401.this.width(), class_4401.this.height(), 32, class_4401.this.height() - 40, 36);
        }

        public void method_21321(int i) {
            this.remove(i);
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
            return class_4401.this.isFocused(this);
        }

        @Override
        public void renderBackground() {
            class_4401.this.renderBackground();
        }

        @Override
        public void selectItem(int i) {
            this.setSelected(i);
            if (i != -1) {
                List list = class_4401.this.field_19939.children();
                PendingInvite pendingInvite = ((class_4403)list.get((int)i)).field_19953;
                String string = RealmsScreen.getLocalizedString("narrator.select.list.position", i + 1, list.size());
                String string2 = Realms.joinNarrations(Arrays.asList(pendingInvite.worldName, pendingInvite.worldOwnerName, class_4401.method_21301(pendingInvite), string));
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string2));
            }
            this.method_21322(i);
        }

        public void method_21322(int i) {
            class_4401.this.field_19941 = i;
            class_4401.this.method_21307();
        }
    }
}

