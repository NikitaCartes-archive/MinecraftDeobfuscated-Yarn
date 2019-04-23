/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
    private final Map<Item, Entry> entries = Maps.newHashMap();
    private int tick;

    public boolean isCoolingDown(Item item) {
        return this.getCooldownProgress(item, 0.0f) > 0.0f;
    }

    public float getCooldownProgress(Item item, float f) {
        Entry entry = this.entries.get(item);
        if (entry != null) {
            float g = entry.endTick - entry.startTick;
            float h = (float)entry.endTick - ((float)this.tick + f);
            return MathHelper.clamp(h / g, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void update() {
        ++this.tick;
        if (!this.entries.isEmpty()) {
            Iterator<Map.Entry<Item, Entry>> iterator = this.entries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Item, Entry> entry = iterator.next();
                if (entry.getValue().endTick > this.tick) continue;
                iterator.remove();
                this.onCooldownUpdate(entry.getKey());
            }
        }
    }

    public void set(Item item, int i) {
        this.entries.put(item, new Entry(this.tick, this.tick + i));
        this.onCooldownUpdate(item, i);
    }

    @Environment(value=EnvType.CLIENT)
    public void remove(Item item) {
        this.entries.remove(item);
        this.onCooldownUpdate(item);
    }

    protected void onCooldownUpdate(Item item, int i) {
    }

    protected void onCooldownUpdate(Item item) {
    }

    class Entry {
        private final int startTick;
        private final int endTick;

        private Entry(int i, int j) {
            this.startTick = i;
            this.endTick = j;
        }
    }
}

