/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4359;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class class_4387
extends RealmsScreen {
    private final RealmsScreen field_19787;
    private final boolean field_19788;

    public class_4387(RealmsScreen realmsScreen, boolean bl) {
        this.field_19787 = realmsScreen;
        this.field_19788 = bl;
    }

    @Override
    public void init() {
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, class_4359.method_21072(12), class_4387.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                Realms.setScreen(class_4387.this.field_19787);
            }
        });
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        String string = class_4387.getLocalizedString(this.field_19788 ? "mco.client.outdated.title" : "mco.client.incompatible.title");
        this.drawCenteredString(string, this.width() / 2, class_4359.method_21072(3), 0xFF0000);
        int k = this.field_19788 ? 2 : 3;
        for (int l = 0; l < k; ++l) {
            String string2 = class_4387.getLocalizedString((this.field_19788 ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (l + 1));
            this.drawCenteredString(string2, this.width() / 2, class_4359.method_21072(5) + l * 12, 0xFFFFFF);
        }
        super.render(i, j, f);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 257 || i == 335 || i == 256) {
            Realms.setScreen(this.field_19787);
            return true;
        }
        return super.keyPressed(i, j, k);
    }
}

