/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5481;
import net.minecraft.class_5492;
import net.minecraft.text.StringRenderable;

@Environment(value=EnvType.CLIENT)
public class class_5491 {
    public static class_5481 method_30922(StringRenderable stringRenderable, boolean bl) {
        class_5492 lv = class_5492.method_30943(stringRenderable, UCharacter::getMirror, class_5491::method_30921);
        Bidi bidi = new Bidi(lv.method_30939(), bl ? 127 : 126);
        bidi.setReorderingMode(0);
        ArrayList<class_5481> list = Lists.newArrayList();
        int i = bidi.countRuns();
        for (int j = 0; j < i; ++j) {
            BidiRun bidiRun = bidi.getVisualRun(j);
            list.addAll(lv.method_30940(bidiRun.getStart(), bidiRun.getLength(), bidiRun.isOddRun()));
        }
        return class_5481.method_30749(list);
    }

    private static String method_30921(String string) {
        try {
            return new ArabicShaping(8).shape(string);
        } catch (Exception exception) {
            return string;
        }
    }
}

