/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4325;
import net.minecraft.class_4398;
import net.minecraft.class_4410;
import net.minecraft.class_4434;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class class_4390
extends RealmsScreen {
    private final RealmsServer field_19829;
    private final class_4325 field_19830;
    private RealmsEditBox field_19831;
    private RealmsEditBox field_19832;
    private RealmsButton field_19833;
    private RealmsLabel field_19834;

    public class_4390(RealmsServer realmsServer, class_4325 arg) {
        this.field_19829 = realmsServer;
        this.field_19830 = arg;
    }

    @Override
    public void tick() {
        if (this.field_19831 != null) {
            this.field_19831.tick();
        }
        if (this.field_19832 != null) {
            this.field_19832.tick();
        }
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.field_19833 = new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 17, 97, 20, class_4390.getLocalizedString("mco.create.world")){

            @Override
            public void onPress() {
                class_4390.this.method_21245();
            }
        };
        this.buttonsAdd(this.field_19833);
        this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, this.height() / 4 + 120 + 17, 95, 20, class_4390.getLocalizedString("gui.cancel")){

            @Override
            public void onPress() {
                Realms.setScreen(class_4390.this.field_19830);
            }
        });
        this.field_19833.active(false);
        this.field_19831 = this.newEditBox(3, this.width() / 2 - 100, 65, 200, 20, class_4390.getLocalizedString("mco.configure.world.name"));
        this.addWidget(this.field_19831);
        this.focusOn(this.field_19831);
        this.field_19832 = this.newEditBox(4, this.width() / 2 - 100, 115, 200, 20, class_4390.getLocalizedString("mco.configure.world.description"));
        this.addWidget(this.field_19832);
        this.field_19834 = new RealmsLabel(class_4390.getLocalizedString("mco.selectServer.create"), this.width() / 2, 11, 0xFFFFFF);
        this.addWidget(this.field_19834);
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    @Override
    public boolean charTyped(char c, int i) {
        this.field_19833.active(this.method_21247());
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        switch (i) {
            case 256: {
                Realms.setScreen(this.field_19830);
                return true;
            }
        }
        this.field_19833.active(this.method_21247());
        return false;
    }

    private void method_21245() {
        if (this.method_21247()) {
            class_4410 lv = new class_4410(this.field_19830, this.field_19829, this.field_19830.method_20902(), class_4390.getLocalizedString("mco.selectServer.create"), class_4390.getLocalizedString("mco.create.world.subtitle"), 0xA0A0A0, class_4390.getLocalizedString("mco.create.world.skip"));
            lv.method_21376(class_4390.getLocalizedString("mco.create.world.reset.title"));
            class_4434.class_4445 lv2 = new class_4434.class_4445(this.field_19829.id, this.field_19831.getValue(), this.field_19832.getValue(), lv);
            class_4398 lv3 = new class_4398(this.field_19830, lv2);
            lv3.method_21288();
            Realms.setScreen(lv3);
        }
    }

    private boolean method_21247() {
        return this.field_19831.getValue() != null && !this.field_19831.getValue().trim().isEmpty();
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.field_19834.render(this);
        this.drawString(class_4390.getLocalizedString("mco.configure.world.name"), this.width() / 2 - 100, 52, 0xA0A0A0);
        this.drawString(class_4390.getLocalizedString("mco.configure.world.description"), this.width() / 2 - 100, 102, 0xA0A0A0);
        if (this.field_19831 != null) {
            this.field_19831.render(i, j, f);
        }
        if (this.field_19832 != null) {
            this.field_19832.render(i, j, f);
        }
        super.render(i, j, f);
    }
}

