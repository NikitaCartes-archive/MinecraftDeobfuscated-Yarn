/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.util.NarratorManager;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsScreen
extends Screen {
    protected static final int field_33055 = 17;
    protected static final int field_33056 = 20;
    protected static final int field_33057 = 7;
    protected static final long field_33058 = 0x140000000L;
    public static final int field_33059 = 0xFFFFFF;
    public static final int field_33060 = 0xA0A0A0;
    protected static final int field_33061 = 0x4C4C4C;
    protected static final int field_33062 = 0x6C6C6C;
    protected static final int field_33063 = 0x7FFF7F;
    protected static final int field_33064 = 6077788;
    protected static final int field_33065 = 0xFF0000;
    protected static final int field_33036 = 15553363;
    protected static final int field_33037 = -1073741824;
    protected static final int field_33038 = 0xCCAC5C;
    protected static final int field_33039 = -256;
    protected static final int field_33040 = 0x3366BB;
    protected static final int field_33041 = 7107012;
    protected static final int field_33042 = 8226750;
    protected static final int field_33043 = 0xFFFFA0;
    protected static final String field_33044 = "https://www.minecraft.net/realms/adventure-maps-in-1-9";
    protected static final int field_33045 = 8;
    protected static final int field_33046 = 8;
    protected static final int field_33047 = 8;
    protected static final int field_33048 = 8;
    protected static final int field_33049 = 40;
    protected static final int field_33050 = 8;
    protected static final int field_33051 = 8;
    protected static final int field_33052 = 8;
    protected static final int field_33053 = 64;
    protected static final int field_33054 = 64;

    public RealmsScreen() {
        super(NarratorManager.EMPTY);
    }

    /**
     * Moved from RealmsConstants in 20w10a
     */
    protected static int row(int index) {
        return 40 + index * 13;
    }

    @Override
    public void tick() {
        for (ClickableWidget clickableWidget : this.buttons) {
            if (!(clickableWidget instanceof TickableElement)) continue;
            ((TickableElement)((Object)clickableWidget)).tick();
        }
    }

    public void narrateLabels() {
        List<String> list = this.children.stream().filter(RealmsLabel.class::isInstance).map(RealmsLabel.class::cast).map(RealmsLabel::getText).collect(Collectors.toList());
        Realms.narrateNow(list);
    }
}

