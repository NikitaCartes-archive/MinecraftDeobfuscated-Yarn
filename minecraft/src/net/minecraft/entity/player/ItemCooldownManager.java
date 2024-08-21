package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
	private final Map<Identifier, ItemCooldownManager.Entry> entries = Maps.<Identifier, ItemCooldownManager.Entry>newHashMap();
	private int tick;

	public boolean isCoolingDown(ItemStack stack) {
		return this.getCooldownProgress(stack, 0.0F) > 0.0F;
	}

	public float getCooldownProgress(ItemStack stack, float tickDelta) {
		Identifier identifier = this.getGroup(stack);
		ItemCooldownManager.Entry entry = (ItemCooldownManager.Entry)this.entries.get(identifier);
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
			Iterator<java.util.Map.Entry<Identifier, ItemCooldownManager.Entry>> iterator = this.entries.entrySet().iterator();

			while (iterator.hasNext()) {
				java.util.Map.Entry<Identifier, ItemCooldownManager.Entry> entry = (java.util.Map.Entry<Identifier, ItemCooldownManager.Entry>)iterator.next();
				if (((ItemCooldownManager.Entry)entry.getValue()).endTick <= this.tick) {
					iterator.remove();
					this.onCooldownUpdate((Identifier)entry.getKey());
				}
			}
		}
	}

	public Identifier getGroup(ItemStack stack) {
		UseCooldownComponent useCooldownComponent = stack.get(DataComponentTypes.USE_COOLDOWN);
		Identifier identifier = Registries.ITEM.getId(stack.getItem());
		return useCooldownComponent == null ? identifier : (Identifier)useCooldownComponent.cooldownGroup().orElse(identifier);
	}

	public void set(ItemStack stack, int duration) {
		this.set(this.getGroup(stack), duration);
	}

	public void set(Identifier groupId, int duration) {
		this.entries.put(groupId, new ItemCooldownManager.Entry(this.tick, this.tick + duration));
		this.onCooldownUpdate(groupId, duration);
	}

	public void remove(Identifier groupId) {
		this.entries.remove(groupId);
		this.onCooldownUpdate(groupId);
	}

	protected void onCooldownUpdate(Identifier groupId, int duration) {
	}

	protected void onCooldownUpdate(Identifier groupId) {
	}

	static record Entry(int startTick, int endTick) {
	}
}
