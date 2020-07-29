/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextCollector {
    private final List<StringVisitable> field_25260 = Lists.newArrayList();

    public void add(StringVisitable stringVisitable) {
        this.field_25260.add(stringVisitable);
    }

    @Nullable
    public StringVisitable getRawCombined() {
        if (this.field_25260.isEmpty()) {
            return null;
        }
        if (this.field_25260.size() == 1) {
            return this.field_25260.get(0);
        }
        return StringVisitable.concat(this.field_25260);
    }

    public StringVisitable getCombined() {
        StringVisitable stringVisitable = this.getRawCombined();
        return stringVisitable != null ? stringVisitable : StringVisitable.EMPTY;
    }
}

