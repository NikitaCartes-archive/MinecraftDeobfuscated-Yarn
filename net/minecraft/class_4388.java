/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4325;
import net.minecraft.class_4341;
import net.minecraft.class_4355;
import net.minecraft.class_4359;
import net.minecraft.class_4367;
import net.minecraft.class_4381;
import net.minecraft.class_4394;
import net.minecraft.class_4396;
import net.minecraft.class_4398;
import net.minecraft.class_4406;
import net.minecraft.class_4410;
import net.minecraft.class_4415;
import net.minecraft.class_4419;
import net.minecraft.class_4422;
import net.minecraft.class_4423;
import net.minecraft.class_4425;
import net.minecraft.class_4434;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Environment(value=EnvType.CLIENT)
public class class_4388
extends class_4415<WorldTemplate>
implements class_4367.class_4369 {
    private static final Logger field_19790 = LogManager.getLogger();
    private String field_19791;
    private final class_4325 field_19792;
    private RealmsServer field_19793;
    private final long field_19794;
    private int field_19795;
    private int field_19796;
    private final int field_19797 = 80;
    private final int field_19798 = 5;
    private RealmsButton field_19799;
    private RealmsButton field_19800;
    private RealmsButton field_19801;
    private RealmsButton field_19802;
    private RealmsButton field_19803;
    private RealmsButton field_19804;
    private RealmsButton field_19805;
    private boolean field_19806;
    private int field_19807;
    private int field_19808;

    public class_4388(class_4325 arg, long l) {
        this.field_19792 = arg;
        this.field_19794 = l;
    }

    @Override
    public void init() {
        if (this.field_19793 == null) {
            this.method_21204(this.field_19794);
        }
        this.field_19795 = this.width() / 2 - 187;
        this.field_19796 = this.width() / 2 + 190;
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.field_19799 = new RealmsButton(2, this.method_21200(0, 3), class_4359.method_21072(0), 100, 20, class_4388.getLocalizedString("mco.configure.world.buttons.players")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4406(class_4388.this, class_4388.this.field_19793));
            }
        };
        this.buttonsAdd(this.field_19799);
        this.field_19800 = new RealmsButton(3, this.method_21200(1, 3), class_4359.method_21072(0), 100, 20, class_4388.getLocalizedString("mco.configure.world.buttons.settings")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4422(class_4388.this, class_4388.this.field_19793.clone()));
            }
        };
        this.buttonsAdd(this.field_19800);
        this.field_19801 = new RealmsButton(4, this.method_21200(2, 3), class_4359.method_21072(0), 100, 20, class_4388.getLocalizedString("mco.configure.world.buttons.subscription")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4425(class_4388.this, class_4388.this.field_19793.clone(), class_4388.this.field_19792));
            }
        };
        this.buttonsAdd(this.field_19801);
        for (int i = 1; i < 5; ++i) {
            this.method_21199(i);
        }
        this.field_19805 = new RealmsButton(8, this.method_21220(0), class_4359.method_21072(13) - 5, 100, 20, class_4388.getLocalizedString("mco.configure.world.buttons.switchminigame")){

            @Override
            public void onPress() {
                class_4419 lv = new class_4419(class_4388.this, RealmsServer.class_4321.MINIGAME);
                lv.method_21423(RealmsScreen.getLocalizedString("mco.template.title.minigame"));
                Realms.setScreen(lv);
            }
        };
        this.buttonsAdd(this.field_19805);
        this.field_19802 = new RealmsButton(5, this.method_21220(0), class_4359.method_21072(13) - 5, 90, 20, class_4388.getLocalizedString("mco.configure.world.buttons.options")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4423(class_4388.this, ((class_4388)class_4388.this).field_19793.slots.get(((class_4388)class_4388.this).field_19793.activeSlot).clone(), ((class_4388)class_4388.this).field_19793.worldType, ((class_4388)class_4388.this).field_19793.activeSlot));
            }
        };
        this.buttonsAdd(this.field_19802);
        this.field_19803 = new RealmsButton(6, this.method_21220(1), class_4359.method_21072(13) - 5, 90, 20, class_4388.getLocalizedString("mco.configure.world.backup")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4381(class_4388.this, class_4388.this.field_19793.clone(), ((class_4388)class_4388.this).field_19793.activeSlot));
            }
        };
        this.buttonsAdd(this.field_19803);
        this.field_19804 = new RealmsButton(7, this.method_21220(2), class_4359.method_21072(13) - 5, 90, 20, class_4388.getLocalizedString("mco.configure.world.buttons.resetworld")){

            @Override
            public void onPress() {
                Realms.setScreen(new class_4410(class_4388.this, class_4388.this.field_19793.clone(), class_4388.this.method_21219()));
            }
        };
        this.buttonsAdd(this.field_19804);
        this.buttonsAdd(new RealmsButton(0, this.field_19796 - 80 + 8, class_4359.method_21072(13) - 5, 70, 20, class_4388.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                class_4388.this.method_21231();
            }
        });
        this.field_19803.active(true);
        if (this.field_19793 == null) {
            this.method_21242();
            this.method_21240();
            this.field_19799.active(false);
            this.field_19800.active(false);
            this.field_19801.active(false);
        } else {
            this.method_21234();
            if (this.method_21238()) {
                this.method_21240();
            } else {
                this.method_21242();
            }
        }
    }

    private void method_21199(int i) {
        int j = this.method_21228(i);
        int k = class_4359.method_21072(5) + 5;
        int l = 100 + i;
        class_4367 lv = new class_4367(j, k, 80, 80, () -> this.field_19793, string -> {
            this.field_19791 = string;
        }, l, i, this);
        this.getProxy().buttonsAdd(lv);
    }

    private int method_21220(int i) {
        return this.field_19795 + i * 95;
    }

    private int method_21200(int i, int j) {
        return this.width() / 2 - (j * 105 - 5) / 2 + i * 105;
    }

    @Override
    public void tick() {
        this.tickButtons();
        ++this.field_19807;
        --this.field_19808;
        if (this.field_19808 < 0) {
            this.field_19808 = 0;
        }
    }

    @Override
    public void render(int i, int j, float f) {
        this.field_19791 = null;
        this.renderBackground();
        this.drawCenteredString(class_4388.getLocalizedString("mco.configure.worlds.title"), this.width() / 2, class_4359.method_21072(4), 0xFFFFFF);
        super.render(i, j, f);
        if (this.field_19793 == null) {
            this.drawCenteredString(class_4388.getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 0xFFFFFF);
            return;
        }
        String string = this.field_19793.getName();
        int k = this.fontWidth(string);
        int l = this.field_19793.state == RealmsServer.class_4320.CLOSED ? 0xA0A0A0 : 0x7FFF7F;
        int m = this.fontWidth(class_4388.getLocalizedString("mco.configure.world.title"));
        this.drawCenteredString(class_4388.getLocalizedString("mco.configure.world.title"), this.width() / 2, 12, 0xFFFFFF);
        this.drawCenteredString(string, this.width() / 2, 24, l);
        int n = Math.min(this.method_21200(2, 3) + 80 - 11, this.width() / 2 + k / 2 + m / 2 + 10);
        this.method_21201(n, 7, i, j);
        if (this.method_21238()) {
            this.drawString(class_4388.getLocalizedString("mco.configure.current.minigame") + ": " + this.field_19793.getMinigameName(), this.field_19795 + 80 + 20 + 10, class_4359.method_21072(13), 0xFFFFFF);
        }
        if (this.field_19791 != null) {
            this.method_21214(this.field_19791, i, j);
        }
    }

    private int method_21228(int i) {
        return this.field_19795 + (i - 1) * 98;
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.method_21231();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void method_21231() {
        if (this.field_19806) {
            this.field_19792.method_20897();
        }
        Realms.setScreen(this.field_19792);
    }

    private void method_21204(final long l) {
        new Thread(){

            @Override
            public void run() {
                class_4341 lv = class_4341.method_20989();
                try {
                    class_4388.this.field_19793 = lv.method_20991(l);
                    class_4388.this.method_21234();
                    if (class_4388.this.method_21238()) {
                        class_4388.this.method_21243();
                    } else {
                        class_4388.this.method_21241();
                    }
                } catch (class_4355 lv2) {
                    field_19790.error("Couldn't get own world");
                    Realms.setScreen(new class_4394(lv2.getMessage(), (RealmsScreen)class_4388.this.field_19792));
                } catch (IOException iOException) {
                    field_19790.error("Couldn't parse response getting own world");
                }
            }
        }.start();
    }

    private void method_21234() {
        this.field_19799.active(!this.field_19793.expired);
        this.field_19800.active(!this.field_19793.expired);
        this.field_19801.active(true);
        this.field_19805.active(!this.field_19793.expired);
        this.field_19802.active(!this.field_19793.expired);
        this.field_19804.active(!this.field_19793.expired);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return super.mouseClicked(d, e, i);
    }

    private void method_21206(RealmsServer realmsServer) {
        if (this.field_19793.state == RealmsServer.class_4320.OPEN) {
            this.field_19792.method_20853(realmsServer, new class_4388(this.field_19792.method_20902(), this.field_19794));
        } else {
            this.method_21218(true, new class_4388(this.field_19792.method_20902(), this.field_19794));
        }
    }

    @Override
    public void method_21108(int i, @NotNull class_4367.class_4368 arg, boolean bl, boolean bl2) {
        switch (arg) {
            case NOTHING: {
                break;
            }
            case JOIN: {
                this.method_21206(this.field_19793);
                break;
            }
            case SWITCH_SLOT: {
                if (bl) {
                    this.method_21236();
                    break;
                }
                if (bl2) {
                    this.method_21222(i, this.field_19793);
                    break;
                }
                this.method_21203(i, this.field_19793);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown action " + (Object)((Object)arg));
            }
        }
    }

    private void method_21236() {
        class_4419 lv = new class_4419(this, RealmsServer.class_4321.MINIGAME);
        lv.method_21423(class_4388.getLocalizedString("mco.template.title.minigame"));
        lv.method_21429(class_4388.getLocalizedString("mco.minigame.world.info.line1") + "\\n" + class_4388.getLocalizedString("mco.minigame.world.info.line2"));
        Realms.setScreen(lv);
    }

    private void method_21203(int i, RealmsServer realmsServer) {
        String string = class_4388.getLocalizedString("mco.configure.world.slot.switch.question.line1");
        String string2 = class_4388.getLocalizedString("mco.configure.world.slot.switch.question.line2");
        Realms.setScreen(new class_4396((bl, j) -> {
            if (bl) {
                this.method_21205(realmsServer.id, i);
            } else {
                Realms.setScreen(this);
            }
        }, class_4396.class_4397.INFO, string, string2, true, 9));
    }

    private void method_21222(int i, RealmsServer realmsServer) {
        String string = class_4388.getLocalizedString("mco.configure.world.slot.switch.question.line1");
        String string2 = class_4388.getLocalizedString("mco.configure.world.slot.switch.question.line2");
        Realms.setScreen(new class_4396((bl, j) -> {
            if (bl) {
                class_4410 lv = new class_4410(this, realmsServer, this.method_21219(), class_4388.getLocalizedString("mco.configure.world.switch.slot"), class_4388.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 0xA0A0A0, class_4388.getLocalizedString("gui.cancel"));
                lv.method_21379(i);
                lv.method_21376(class_4388.getLocalizedString("mco.create.world.reset.title"));
                Realms.setScreen(lv);
            } else {
                Realms.setScreen(this);
            }
        }, class_4396.class_4397.INFO, string, string2, true, 10));
    }

    protected void method_21214(String string, int i, int j) {
        if (string == null) {
            return;
        }
        int k = i + 12;
        int l = j - 12;
        int m = this.fontWidth(string);
        if (k + m + 3 > this.field_19796) {
            k = k - m - 20;
        }
        this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(string, k, l, 0xFFFFFF);
    }

    private void method_21201(int i, int j, int k, int l) {
        if (this.field_19793.expired) {
            this.method_21221(i, j, k, l);
        } else if (this.field_19793.state == RealmsServer.class_4320.CLOSED) {
            this.method_21232(i, j, k, l);
        } else if (this.field_19793.state == RealmsServer.class_4320.OPEN) {
            if (this.field_19793.daysLeft < 7) {
                this.method_21202(i, j, k, l, this.field_19793.daysLeft);
            } else {
                this.method_21229(i, j, k, l);
            }
        }
    }

    private void method_21221(int i, int j, int k, int l) {
        RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.field_19791 = class_4388.getLocalizedString("mco.selectServer.expired");
        }
    }

    private void method_21202(int i, int j, int k, int l, int m) {
        RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        if (this.field_19807 % 20 < 10) {
            RealmsScreen.blit(i, j, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            RealmsScreen.blit(i, j, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        GlStateManager.popMatrix();
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.field_19791 = m <= 0 ? class_4388.getLocalizedString("mco.selectServer.expires.soon") : (m == 1 ? class_4388.getLocalizedString("mco.selectServer.expires.day") : class_4388.getLocalizedString("mco.selectServer.expires.days", m));
        }
    }

    private void method_21229(int i, int j, int k, int l) {
        RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.field_19791 = class_4388.getLocalizedString("mco.selectServer.open");
        }
    }

    private void method_21232(int i, int j, int k, int l) {
        RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        RealmsScreen.blit(i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        GlStateManager.popMatrix();
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27) {
            this.field_19791 = class_4388.getLocalizedString("mco.selectServer.closed");
        }
    }

    private boolean method_21238() {
        return this.field_19793 != null && this.field_19793.worldType.equals((Object)RealmsServer.class_4321.MINIGAME);
    }

    private void method_21240() {
        this.method_21216(this.field_19802);
        this.method_21216(this.field_19803);
        this.method_21216(this.field_19804);
    }

    private void method_21216(RealmsButton realmsButton) {
        realmsButton.setVisible(false);
        this.removeButton(realmsButton);
    }

    private void method_21241() {
        this.method_21226(this.field_19802);
        this.method_21226(this.field_19803);
        this.method_21226(this.field_19804);
    }

    private void method_21226(RealmsButton realmsButton) {
        realmsButton.setVisible(true);
        this.buttonsAdd(realmsButton);
    }

    private void method_21242() {
        this.method_21216(this.field_19805);
    }

    private void method_21243() {
        this.method_21226(this.field_19805);
    }

    public void method_21208(RealmsWorldOptions realmsWorldOptions) {
        RealmsWorldOptions realmsWorldOptions2 = this.field_19793.slots.get(this.field_19793.activeSlot);
        realmsWorldOptions.templateId = realmsWorldOptions2.templateId;
        realmsWorldOptions.templateImage = realmsWorldOptions2.templateImage;
        class_4341 lv = class_4341.method_20989();
        try {
            lv.method_20993(this.field_19793.id, this.field_19793.activeSlot, realmsWorldOptions);
            this.field_19793.slots.put(this.field_19793.activeSlot, realmsWorldOptions);
        } catch (class_4355 lv2) {
            field_19790.error("Couldn't save slot settings");
            Realms.setScreen(new class_4394(lv2, (RealmsScreen)this));
            return;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            field_19790.error("Couldn't save slot settings");
        }
        Realms.setScreen(this);
    }

    public void method_21215(String string, String string2) {
        String string3 = string2 == null || string2.trim().isEmpty() ? null : string2;
        class_4341 lv = class_4341.method_20989();
        try {
            lv.method_21005(this.field_19793.id, string, string3);
            this.field_19793.setName(string);
            this.field_19793.setDescription(string3);
        } catch (class_4355 lv2) {
            field_19790.error("Couldn't save settings");
            Realms.setScreen(new class_4394(lv2, (RealmsScreen)this));
            return;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            field_19790.error("Couldn't save settings");
        }
        Realms.setScreen(this);
    }

    public void method_21218(boolean bl, RealmsScreen realmsScreen) {
        class_4434.class_4437 lv = new class_4434.class_4437(this.field_19793, this, this.field_19792, bl);
        class_4398 lv2 = new class_4398(realmsScreen, lv);
        lv2.method_21288();
        Realms.setScreen(lv2);
    }

    public void method_21217(RealmsScreen realmsScreen) {
        class_4434.class_4435 lv = new class_4434.class_4435(this.field_19793, this);
        class_4398 lv2 = new class_4398(realmsScreen, lv);
        lv2.method_21288();
        Realms.setScreen(lv2);
    }

    public void method_21198() {
        this.field_19806 = true;
    }

    void method_21209(WorldTemplate worldTemplate) {
        if (worldTemplate == null) {
            return;
        }
        if (WorldTemplate.Type.MINIGAME.equals((Object)worldTemplate.type)) {
            this.method_21224(worldTemplate);
        }
    }

    private void method_21205(long l, int i2) {
        class_4388 lv = this.method_21219();
        class_4434.class_4443 lv2 = new class_4434.class_4443(l, i2, (bl, i) -> Realms.setScreen(lv), 11);
        class_4398 lv3 = new class_4398(this.field_19792, lv2);
        lv3.method_21288();
        Realms.setScreen(lv3);
    }

    private void method_21224(WorldTemplate worldTemplate) {
        class_4434.class_4442 lv = new class_4434.class_4442(this.field_19793.id, worldTemplate, this.method_21219());
        class_4398 lv2 = new class_4398(this.field_19792, lv);
        lv2.method_21288();
        Realms.setScreen(lv2);
    }

    public class_4388 method_21219() {
        return new class_4388(this.field_19792, this.field_19794);
    }
}

