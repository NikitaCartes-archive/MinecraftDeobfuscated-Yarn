/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4341;
import net.minecraft.class_4359;
import net.minecraft.class_4388;
import net.minecraft.class_4406;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class class_4395
extends RealmsScreen {
    private static final Logger field_19878 = LogManager.getLogger();
    private RealmsEditBox field_19879;
    private final RealmsServer field_19880;
    private final class_4388 field_19881;
    private final RealmsScreen field_19882;
    private final int field_19883 = 0;
    private final int field_19884 = 1;
    private RealmsButton field_19885;
    private final int field_19886 = 2;
    private String field_19887;
    private boolean field_19888;

    public class_4395(class_4388 arg, RealmsScreen realmsScreen, RealmsServer realmsServer) {
        this.field_19881 = arg;
        this.field_19882 = realmsScreen;
        this.field_19880 = realmsServer;
    }

    @Override
    public void tick() {
        this.field_19879.tick();
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.field_19885 = new RealmsButton(0, this.width() / 2 - 100, class_4359.method_21072(10), class_4395.getLocalizedString("mco.configure.world.buttons.invite")){

            @Override
            public void onPress() {
                class_4395.this.method_21284();
            }
        };
        this.buttonsAdd(this.field_19885);
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, class_4359.method_21072(12), class_4395.getLocalizedString("gui.cancel")){

            @Override
            public void onPress() {
                Realms.setScreen(class_4395.this.field_19882);
            }
        });
        this.field_19879 = this.newEditBox(2, this.width() / 2 - 100, class_4359.method_21072(2), 200, 20, class_4395.getLocalizedString("mco.configure.world.invite.profile.name"));
        this.focusOn(this.field_19879);
        this.addWidget(this.field_19879);
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    private void method_21284() {
        class_4341 lv = class_4341.method_20989();
        if (this.field_19879.getValue() == null || this.field_19879.getValue().isEmpty()) {
            this.method_21286(class_4395.getLocalizedString("mco.configure.world.players.error"));
            return;
        }
        try {
            RealmsServer realmsServer = lv.method_21004(this.field_19880.id, this.field_19879.getValue().trim());
            if (realmsServer != null) {
                this.field_19880.players = realmsServer.players;
                Realms.setScreen(new class_4406(this.field_19881, this.field_19880));
            } else {
                this.method_21286(class_4395.getLocalizedString("mco.configure.world.players.error"));
            }
        } catch (Exception exception) {
            field_19878.error("Couldn't invite user");
            this.method_21286(class_4395.getLocalizedString("mco.configure.world.players.error"));
        }
    }

    private void method_21286(String string) {
        this.field_19888 = true;
        this.field_19887 = string;
        Realms.narrateNow(string);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            Realms.setScreen(this.field_19882);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawString(class_4395.getLocalizedString("mco.configure.world.invite.profile.name"), this.width() / 2 - 100, class_4359.method_21072(1), 0xA0A0A0);
        if (this.field_19888) {
            this.drawCenteredString(this.field_19887, this.width() / 2, class_4359.method_21072(5), 0xFF0000);
        }
        this.field_19879.render(i, j, f);
        super.render(i, j, f);
    }
}

