package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
	private final Map<Item, ItemCooldownManager.Entry> entries = Maps.<Item, ItemCooldownManager.Entry>newHashMap();
	private int tick;

	public boolean isCoolingDown(Item item) {
		return this.getCooldownProgress(item, 0.0F) > 0.0F;
	}

	public float getCooldownProgress(Item item, float tickDelta) {
		ItemCooldownManager.Entry entry = (ItemCooldownManager.Entry)this.entries.get(item);
		if (entry != null) {
			float f = (float)(entry.endTick - entry.startTick);
			float g = (float)entry.endTick - ((float)this.tick + tickDelta);
			return MathHelper.clamp(g / f, 0.0F, 1.0F);
		} else {
			return 0.0F;
		}
	}

	public void update() {
		this.tick++;
		if (!this.entries.isEmpty()) {
			Iterator<java.util.Map.Entry<Item, ItemCooldownManager.Entry>> iterator = this.entries.entrySet().iterator();

			while (iterator.hasNext()) {
				java.util.Map.Entry<Item, ItemCooldownManager.Entry> entry = (java.util.Map.Entry<Item, ItemCooldownManager.Entry>)iterator.next();
				if (((ItemCooldownManager.Entry)entry.getValue()).endTick <= this.tick) {
					iterator.remove();
					this.onCooldownUpdate((Item)entry.getKey());
				}
			}
		}
	}

	public void set(Item item, int duration) {
		this.entries.put(item, new ItemCooldownManager.Entry(this.tick, this.tick + duration));
		this.onCooldownUpdate(item, duration);
	}

	public void remove(Item item) {
		this.entries.remove(item);
		this.onCooldownUpdate(item);
	}

	protected void onCooldownUpdate(Item item, int duration) {
	}

	protected void onCooldownUpdate(Item item) {
	}

	static class Entry {
		final int startTick;
		final int endTick;

		Entry(int startTick, int endTick) {
			this.startTick = startTick;
			this.endTick = endTick;
		}
	}
}
