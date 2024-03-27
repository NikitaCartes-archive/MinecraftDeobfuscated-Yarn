package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class ItemEnchantmentsComponent implements TooltipAppender {
	public static final ItemEnchantmentsComponent DEFAULT = new ItemEnchantmentsComponent(new Object2IntLinkedOpenHashMap<>(), true);
	public static final int MAX_ENCHANTMENT_LEVEL = 255;
	private static final Codec<Integer> ENCHANTMENT_LEVEL_CODEC = Codec.intRange(0, 255);
	private static final Codec<Object2IntLinkedOpenHashMap<RegistryEntry<Enchantment>>> INLINE_CODEC = Codec.unboundedMap(
			Registries.ENCHANTMENT.getEntryCodec(), ENCHANTMENT_LEVEL_CODEC
		)
		.xmap(Object2IntLinkedOpenHashMap::new, Function.identity());
	private static final Codec<ItemEnchantmentsComponent> BASE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					INLINE_CODEC.fieldOf("levels").forGetter(component -> component.enchantments),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(component -> component.showInTooltip)
				)
				.apply(instance, ItemEnchantmentsComponent::new)
	);
	public static final Codec<ItemEnchantmentsComponent> CODEC = Codec.withAlternative(BASE_CODEC, INLINE_CODEC, map -> new ItemEnchantmentsComponent(map, true));
	public static final PacketCodec<RegistryByteBuf, ItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.map(Object2IntLinkedOpenHashMap::new, PacketCodecs.registryEntry(RegistryKeys.ENCHANTMENT), PacketCodecs.VAR_INT),
		component -> component.enchantments,
		PacketCodecs.BOOL,
		component -> component.showInTooltip,
		ItemEnchantmentsComponent::new
	);
	final Object2IntLinkedOpenHashMap<RegistryEntry<Enchantment>> enchantments;
	final boolean showInTooltip;

	ItemEnchantmentsComponent(Object2IntLinkedOpenHashMap<RegistryEntry<Enchantment>> enchantments, boolean showInTooltip) {
		this.enchantments = enchantments;
		this.showInTooltip = showInTooltip;
	}

	public int getLevel(Enchantment enchantment) {
		return this.enchantments.getInt(enchantment.getRegistryEntry());
	}

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		if (this.showInTooltip) {
			for (Entry<RegistryEntry<Enchantment>> entry : this.enchantments.object2IntEntrySet()) {
				textConsumer.accept(((Enchantment)((RegistryEntry)entry.getKey()).value()).getName(entry.getIntValue()));
			}
		}
	}

	public ItemEnchantmentsComponent withShowInTooltip(boolean showInTooltip) {
		return new ItemEnchantmentsComponent(this.enchantments, showInTooltip);
	}

	public Set<RegistryEntry<Enchantment>> getEnchantments() {
		return Collections.unmodifiableSet(this.enchantments.keySet());
	}

	public Set<Entry<RegistryEntry<Enchantment>>> getEnchantmentsMap() {
		return Collections.unmodifiableSet(this.enchantments.object2IntEntrySet());
	}

	public int getSize() {
		return this.enchantments.size();
	}

	public boolean isEmpty() {
		return this.enchantments.isEmpty();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof ItemEnchantmentsComponent itemEnchantmentsComponent)
				? false
				: this.showInTooltip == itemEnchantmentsComponent.showInTooltip && this.enchantments.equals(itemEnchantmentsComponent.enchantments);
		}
	}

	public int hashCode() {
		int i = this.enchantments.hashCode();
		return 31 * i + (this.showInTooltip ? 1 : 0);
	}

	public String toString() {
		return "ItemEnchantments{enchantments=" + this.enchantments + ", showInTooltip=" + this.showInTooltip + "}";
	}

	public static class Builder {
		private final Object2IntLinkedOpenHashMap<RegistryEntry<Enchantment>> enchantments = new Object2IntLinkedOpenHashMap<>();
		private final boolean showInTooltip;

		public Builder(ItemEnchantmentsComponent enchantmentsComponent) {
			this.enchantments.putAll(enchantmentsComponent.enchantments);
			this.showInTooltip = enchantmentsComponent.showInTooltip;
		}

		public void set(Enchantment enchantment, int level) {
			if (level <= 0) {
				this.enchantments.removeInt(enchantment.getRegistryEntry());
			} else {
				this.enchantments.put(enchantment.getRegistryEntry(), level);
			}
		}

		public void add(Enchantment enchantment, int level) {
			if (level > 0) {
				this.enchantments.merge(enchantment.getRegistryEntry(), level, Integer::max);
			}
		}

		public void remove(Predicate<RegistryEntry<Enchantment>> predicate) {
			this.enchantments.keySet().removeIf(predicate);
		}

		public int getLevel(Enchantment enchantment) {
			return this.enchantments.getOrDefault(enchantment.getRegistryEntry(), 0);
		}

		public Set<RegistryEntry<Enchantment>> getEnchantments() {
			return this.enchantments.keySet();
		}

		public ItemEnchantmentsComponent build() {
			return new ItemEnchantmentsComponent(this.enchantments, this.showInTooltip);
		}
	}
}
