package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
	private final Map<Item, ItemCooldownManager.Cooldown> cooldowns = Maps.<Item, ItemCooldownManager.Cooldown>newHashMap();
	private int tick;

	public boolean isCoolingDown(Item item) {
		return this.method_7905(item, 0.0F) > 0.0F;
	}

	public float method_7905(Item item, float f) {
		ItemCooldownManager.Cooldown cooldown = (ItemCooldownManager.Cooldown)this.cooldowns.get(item);
		if (cooldown != null) {
			float g = (float)(cooldown.field_8027 - cooldown.field_8028);
			float h = (float)cooldown.field_8027 - ((float)this.tick + f);
			return MathHelper.clamp(h / g, 0.0F, 1.0F);
		} else {
			return 0.0F;
		}
	}

	public void update() {
		this.tick++;
		if (!this.cooldowns.isEmpty()) {
			Iterator<Entry<Item, ItemCooldownManager.Cooldown>> iterator = this.cooldowns.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Item, ItemCooldownManager.Cooldown> entry = (Entry<Item, ItemCooldownManager.Cooldown>)iterator.next();
				if (((ItemCooldownManager.Cooldown)entry.getValue()).field_8027 <= this.tick) {
					iterator.remove();
					this.onCooldownUpdate((Item)entry.getKey());
				}
			}
		}
	}

	public void set(Item item, int i) {
		this.cooldowns.put(item, new ItemCooldownManager.Cooldown(this.tick, this.tick + i));
		this.onCooldownUpdate(item, i);
	}

	@Environment(EnvType.CLIENT)
	public void remove(Item item) {
		this.cooldowns.remove(item);
		this.onCooldownUpdate(item);
	}

	protected void onCooldownUpdate(Item item, int i) {
	}

	protected void onCooldownUpdate(Item item) {
	}

	class Cooldown {
		private final int field_8028;
		private final int field_8027;

		private Cooldown(int i, int j) {
			this.field_8028 = i;
			this.field_8027 = j;
		}
	}
}
