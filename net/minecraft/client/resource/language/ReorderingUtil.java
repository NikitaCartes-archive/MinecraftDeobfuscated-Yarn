/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.TextReorderingProcessor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;

@Environment(value=EnvType.CLIENT)
public class ReorderingUtil {
    public static OrderedText reorder(StringVisitable text, boolean rightToLeft) {
        TextReorderingProcessor textReorderingProcessor = TextReorderingProcessor.create(text, UCharacter::getMirror, ReorderingUtil::shapeArabic);
        Bidi bidi = new Bidi(textReorderingProcessor.getString(), rightToLeft ? 127 : 126);
        bidi.setReorderingMode(0);
        ArrayList<OrderedText> list = Lists.newArrayList();
        int i = bidi.countRuns();
        for (int j = 0; j < i; ++j) {
            BidiRun bidiRun = bidi.getVisualRun(j);
            list.addAll(textReorderingProcessor.process(bidiRun.getStart(), bidiRun.getLength(), bidiRun.isOddRun()));
        }
        return OrderedText.concat(list);
    }

    private static String shapeArabic(String string) {
        try {
            return new ArabicShaping(8).shape(string);
        } catch (Exception exception) {
            return string;
        }
    }
}

