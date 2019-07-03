/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4341;
import net.minecraft.class_4355;
import net.minecraft.class_4359;
import net.minecraft.class_4372;
import net.minecraft.class_4388;
import net.minecraft.class_4389;
import net.minecraft.class_4395;
import net.minecraft.class_4446;
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

@Environment(value=EnvType.CLIENT)
public class class_4406
extends RealmsScreen {
    private static final Logger field_19958 = LogManager.getLogger();
    private String field_19959;
    private final class_4388 field_19960;
    private final RealmsServer field_19961;
    private class_4407 field_19962;
    private int field_19963;
    private int field_19964;
    private int field_19965;
    private RealmsButton field_19966;
    private RealmsButton field_19967;
    private int field_19968 = -1;
    private String field_19969;
    private int field_19970 = -1;
    private boolean field_19971;
    private RealmsLabel field_19972;

    public class_4406(class_4388 arg, RealmsServer realmsServer) {
        this.field_19960 = arg;
        this.field_19961 = realmsServer;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void init() {
        this.field_19963 = this.width() / 2 - 160;
        this.field_19964 = 150;
        this.field_19965 = this.width() / 2 + 12;
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(new RealmsButton(1, this.field_19965, class_4359.method_21072(1), this.field_19964 + 10, 20, class_4406.getLocalizedString("mco.configure.world.buttons.invite")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4395(class_4406.this.field_19960, class_4406.this, class_4406.this.field_19961));
            }
        });
        RealmsButton realmsButton = new RealmsButton(3, this.field_19965, class_4359.method_21072(3), this.field_19964 + 10, 20, class_4406.getLocalizedString("mco.configure.world.buttons.activity")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4372(class_4406.this, class_4406.this.field_19961));
            }
        };
        realmsButton.active(false);
        this.buttonsAdd(realmsButton);
        this.field_19966 = new RealmsButton(4, this.field_19965, class_4359.method_21072(7), this.field_19964 + 10, 20, class_4406.getLocalizedString("mco.configure.world.invites.remove.tooltip")){

            @Override
            public void onPress() {
                class_4406.this.method_21344(class_4406.this.field_19970);
            }
        };
        this.buttonsAdd(this.field_19966);
        this.field_19967 = new RealmsButton(5, this.field_19965, class_4359.method_21072(9), this.field_19964 + 10, 20, class_4406.getLocalizedString("mco.configure.world.invites.ops.tooltip")){

            @Override
            public void onPress() {
                if (((class_4406)class_4406.this).field_19961.players.get(class_4406.this.field_19970).isOperator()) {
                    class_4406.this.method_21339(class_4406.this.field_19970);
                } else {
                    class_4406.this.method_21334(class_4406.this.field_19970);
                }
            }
        };
        this.buttonsAdd(this.field_19967);
        this.buttonsAdd(new RealmsButton(0, this.field_19965 + this.field_19964 / 2 + 2, class_4359.method_21072(12), this.field_19964 / 2 + 10 - 2, 20, class_4406.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                class_4406.this.method_21333();
            }
        });
        this.field_19962 = new class_4407();
        this.field_19962.setLeftPos(this.field_19963);
        this.addWidget(this.field_19962);
        for (PlayerInfo playerInfo : this.field_19961.players) {
            this.field_19962.method_21354(playerInfo);
        }
        this.field_19972 = new RealmsLabel(class_4406.getLocalizedString("mco.configure.world.players.title"), this.width() / 2, 17, 0xFFFFFF);
        this.addWidget(this.field_19972);
        this.narrateLabels();
        this.method_21325();
    }

    private void method_21325() {
        this.field_19966.setVisible(this.method_21326(this.field_19970));
        this.field_19967.setVisible(this.method_21326(this.field_19970));
    }

    private boolean method_21326(int i) {
        return i != -1;
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.method_21333();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void method_21333() {
        if (this.field_19971) {
            Realms.setScreen(this.field_19960.method_21219());
        } else {
            Realms.setScreen(this.field_19960);
        }
    }

    private void method_21334(int i) {
        this.method_21325();
        class_4341 lv = class_4341.method_20989();
        String string = this.field_19961.players.get(i).getUuid();
        try {
            this.method_21328(lv.method_21017(this.field_19961.id, string));
        } catch (class_4355 lv2) {
            field_19958.error("Couldn't op the user");
        }
    }

    private void method_21339(int i) {
        this.method_21325();
        class_4341 lv = class_4341.method_20989();
        String string = this.field_19961.players.get(i).getUuid();
        try {
            this.method_21328(lv.method_21020(this.field_19961.id, string));
        } catch (class_4355 lv2) {
            field_19958.error("Couldn't deop the user");
        }
    }

    private void method_21328(Ops ops) {
        for (PlayerInfo playerInfo : this.field_19961.players) {
            playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
        }
    }

    private void method_21344(int i) {
        this.method_21325();
        if (i >= 0 && i < this.field_19961.players.size()) {
            PlayerInfo playerInfo = this.field_19961.players.get(i);
            this.field_19969 = playerInfo.getUuid();
            this.field_19968 = i;
            class_4389 lv = new class_4389(this, "Question", class_4406.getLocalizedString("mco.configure.world.uninvite.question") + " '" + playerInfo.getName() + "' ?", 2);
            Realms.setScreen(lv);
        }
    }

    @Override
    public void confirmResult(boolean bl, int i) {
        if (i == 2) {
            if (bl) {
                class_4341 lv = class_4341.method_20989();
                try {
                    lv.method_20994(this.field_19961.id, this.field_19969);
                } catch (class_4355 lv2) {
                    field_19958.error("Couldn't uninvite user");
                }
                this.method_21347(this.field_19968);
                this.field_19970 = -1;
                this.method_21325();
            }
            this.field_19971 = true;
            Realms.setScreen(this);
        }
    }

    private void method_21347(int i) {
        this.field_19961.players.remove(i);
    }

    @Override
    public void render(int i, int j, float f) {
        this.field_19959 = null;
        this.renderBackground();
        if (this.field_19962 != null) {
            this.field_19962.render(i, j, f);
        }
        int k = class_4359.method_21072(12) + 20;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tezzelator tezzelator = Tezzelator.instance;
        class_4406.bind("textures/gui/options_background.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 32.0f;
        tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
        tezzelator.vertex(0.0, this.height(), 0.0).tex(0.0, (float)(this.height() - k) / 32.0f + 0.0f).color(64, 64, 64, 255).endVertex();
        tezzelator.vertex(this.width(), this.height(), 0.0).tex((float)this.width() / 32.0f, (float)(this.height() - k) / 32.0f + 0.0f).color(64, 64, 64, 255).endVertex();
        tezzelator.vertex(this.width(), k, 0.0).tex((float)this.width() / 32.0f, 0.0).color(64, 64, 64, 255).endVertex();
        tezzelator.vertex(0.0, k, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
        tezzelator.end();
        this.field_19972.render(this);
        if (this.field_19961 != null && this.field_19961.players != null) {
            this.drawString(class_4406.getLocalizedString("mco.configure.world.invited") + " (" + this.field_19961.players.size() + ")", this.field_19963, class_4359.method_21072(0), 0xA0A0A0);
        } else {
            this.drawString(class_4406.getLocalizedString("mco.configure.world.invited"), this.field_19963, class_4359.method_21072(0), 0xA0A0A0);
        }
        super.render(i, j, f);
        if (this.field_19961 == null) {
            return;
        }
        if (this.field_19959 != null) {
            this.method_21332(this.field_19959, i, j);
        }
    }

    protected void method_21332(String string, int i, int j) {
        if (string == null) {
            return;
        }
        int k = i + 12;
        int l = j - 12;
        int m = this.fontWidth(string);
        this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, k, l, 0xFFFFFF);
    }

    private void method_21327(int i, int j, int k, int l) {
        boolean bl = k >= i && k <= i + 9 && l >= j && l <= j + 9 && l < class_4359.method_21072(12) + 20 && l > class_4359.method_21072(1);
        class_4406.bind("realms:textures/gui/realms/cross_player_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(i, j, 0.0f, bl ? 7.0f : 0.0f, 8, 7, 8, 14);
        GlStateManager.popMatrix();
        if (bl) {
            this.field_19959 = class_4406.getLocalizedString("mco.configure.world.invites.remove.tooltip");
        }
    }

    private void method_21335(int i, int j, int k, int l) {
        boolean bl = k >= i && k <= i + 9 && l >= j && l <= j + 9 && l < class_4359.method_21072(12) + 20 && l > class_4359.method_21072(1);
        class_4406.bind("realms:textures/gui/realms/op_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(i, j, 0.0f, bl ? 8.0f : 0.0f, 8, 8, 8, 16);
        GlStateManager.popMatrix();
        if (bl) {
            this.field_19959 = class_4406.getLocalizedString("mco.configure.world.invites.ops.tooltip");
        }
    }

    private void method_21340(int i, int j, int k, int l) {
        boolean bl = k >= i && k <= i + 9 && l >= j && l <= j + 9 && l < class_4359.method_21072(12) + 20 && l > class_4359.method_21072(1);
        class_4406.bind("realms:textures/gui/realms/user_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(i, j, 0.0f, bl ? 8.0f : 0.0f, 8, 8, 8, 16);
        GlStateManager.popMatrix();
        if (bl) {
            this.field_19959 = class_4406.getLocalizedString("mco.configure.world.invites.normal.tooltip");
        }
    }

    @Environment(value=EnvType.CLIENT)
    class class_4408
    extends RealmListEntry {
        final PlayerInfo field_19979;

        public class_4408(PlayerInfo playerInfo) {
            this.field_19979 = playerInfo;
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.method_21356(this.field_19979, k, j, n, o);
        }

        private void method_21356(PlayerInfo playerInfo, int i, int j, int k, int l) {
            int m = !playerInfo.getAccepted() ? 0xA0A0A0 : (playerInfo.getOnline() ? 0x7FFF7F : 0xFFFFFF);
            class_4406.this.drawString(playerInfo.getName(), class_4406.this.field_19963 + 3 + 12, j + 1, m);
            if (playerInfo.isOperator()) {
                class_4406.this.method_21335(class_4406.this.field_19963 + class_4406.this.field_19964 - 10, j + 1, k, l);
            } else {
                class_4406.this.method_21340(class_4406.this.field_19963 + class_4406.this.field_19964 - 10, j + 1, k, l);
            }
            class_4406.this.method_21327(class_4406.this.field_19963 + class_4406.this.field_19964 - 22, j + 2, k, l);
            class_4406.this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.activityfeed.disabled"), class_4406.this.field_19965, class_4359.method_21072(5), 0xA0A0A0);
            class_4446.method_21559(playerInfo.getUuid(), () -> {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(class_4406.this.field_19963 + 2 + 2, j + 1, 8.0f, 8.0f, 8, 8, 8, 8, 64, 64);
                RealmsScreen.blit(class_4406.this.field_19963 + 2 + 2, j + 1, 40.0f, 8.0f, 8, 8, 8, 8, 64, 64);
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    class class_4407
    extends RealmsObjectSelectionList {
        public class_4407() {
            super(class_4406.this.field_19964 + 10, class_4359.method_21072(12) + 20, class_4359.method_21072(1), class_4359.method_21072(12) + 20, 13);
        }

        public void method_21354(PlayerInfo playerInfo) {
            this.addEntry(new class_4408(playerInfo));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width() * 1.0);
        }

        @Override
        public boolean isFocused() {
            return class_4406.this.isFocused(this);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (i == 0 && d < (double)this.getScrollbarPosition() && e >= (double)this.y0() && e <= (double)this.y1()) {
                int j = class_4406.this.field_19963;
                int k = class_4406.this.field_19963 + class_4406.this.field_19964;
                int l = (int)Math.floor(e - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
                int m = l / this.itemHeight();
                if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
                    this.selectItem(m);
                    this.itemClicked(l, m, d, e, this.width());
                }
                return true;
            }
            return super.mouseClicked(d, e, i);
        }

        @Override
        public void itemClicked(int i, int j, double d, double e, int k) {
            if (j < 0 || j > ((class_4406)class_4406.this).field_19961.players.size() || class_4406.this.field_19959 == null) {
                return;
            }
            if (class_4406.this.field_19959.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip")) || class_4406.this.field_19959.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip"))) {
                if (((class_4406)class_4406.this).field_19961.players.get(j).isOperator()) {
                    class_4406.this.method_21339(j);
                } else {
                    class_4406.this.method_21334(j);
                }
            } else if (class_4406.this.field_19959.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
                class_4406.this.method_21344(j);
            }
        }

        @Override
        public void selectItem(int i) {
            this.setSelected(i);
            if (i != -1) {
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((class_4406)class_4406.this).field_19961.players.get(i).getName()));
            }
            this.method_21353(i);
        }

        public void method_21353(int i) {
            class_4406.this.field_19970 = i;
            class_4406.this.method_21325();
        }

        @Override
        public void renderBackground() {
            class_4406.this.renderBackground();
        }

        @Override
        public int getScrollbarPosition() {
            return class_4406.this.field_19963 + this.width() - 5;
        }

        @Override
        public int getItemCount() {
            return class_4406.this.field_19961 == null ? 1 : ((class_4406)class_4406.this).field_19961.players.size();
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 13;
        }
    }
}

