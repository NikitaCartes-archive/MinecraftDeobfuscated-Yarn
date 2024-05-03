package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;

public class ItemEnchantmentsComponent implements TooltipAppender {
	public static final ItemEnchantmentsComponent DEFAULT = new ItemEnchantmentsComponent(new Object2IntOpenHashMap<>(), true);
	public static final int MAX_ENCHANTMENT_LEVEL = 255;
	private static final Codec<Integer> ENCHANTMENT_LEVEL_CODEC = Codec.intRange(0, 255);
	private static final Codec<Object2IntOpenHashMap<RegistryEntry<Enchantment>>> INLINE_CODEC = Codec.unboundedMap(
			Enchantment.ENTRY_CODEC, ENCHANTMENT_LEVEL_CODEC
		)
		.xmap(Object2IntOpenHashMap::new, Function.identity());
	private static final Codec<ItemEnchantmentsComponent> BASE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					INLINE_CODEC.fieldOf("levels").forGetter(component -> component.enchantments),
					Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(component -> component.showInTooltip)
				)
				.apply(instance, ItemEnchantmentsComponent::new)
	);
	public static final Codec<ItemEnchantmentsComponent> CODEC = Codec.withAlternative(BASE_CODEC, INLINE_CODEC, map -> new ItemEnchantmentsComponent(map, true));
	public static final PacketCodec<RegistryByteBuf, ItemEnchantmentsComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.map(Object2IntOpenHashMap::new, PacketCodecs.registryEntry(RegistryKeys.ENCHANTMENT), PacketCodecs.VAR_INT),
		component -> component.enchantments,
		PacketCodecs.BOOL,
		component -> component.showInTooltip,
		ItemEnchantmentsComponent::new
	);
	final Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments;
	final boolean showInTooltip;

	ItemEnchantmentsComponent(Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments, boolean showInTooltip) {
		this.enchantments = enchantments;
		this.showInTooltip = showInTooltip;

		for (Entry<RegistryEntry<Enchantment>> entry : enchantments.object2IntEntrySet()) {
			int i = entry.getIntValue();
			if (i < 0 || i > 255) {
				throw new IllegalArgumentException("Enchantment " + entry.getKey() + " has invalid level " + i);
			}
		}
	}

	public int getLevel(RegistryEntry<Enchantment> enchantment) {
		return this.enchantments.getInt(enchantment);
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		if (this.showInTooltip) {
			RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
			RegistryEntryList<Enchantment> registryEntryList = getTooltipOrderList(wrapperLookup, RegistryKeys.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);

			for (RegistryEntry<Enchantment> registryEntry : registryEntryList) {
				int i = this.enchantments.getInt(registryEntry);
				if (i > 0) {
					tooltip.accept(Enchantment.getName(registryEntry, i));
				}
			}

			for (Entry<RegistryEntry<Enchantment>> entry : this.enchantments.object2IntEntrySet()) {
				RegistryEntry<Enchantment> registryEntry2 = (RegistryEntry<Enchantment>)entry.getKey();
				if (!registryEntryList.contains(registryEntry2)) {
					tooltip.accept(Enchantment.getName((RegistryEntry<Enchantment>)entry.getKey(), entry.getIntValue()));
				}
			}
		}
	}

	private static <T> RegistryEntryList<T> getTooltipOrderList(
		@Nullable RegistryWrapper.WrapperLookup registryLookup, RegistryKey<Registry<T>> registryRef, TagKey<T> tooltipOrderTag
	) {
		if (registryLookup != null) {
			Optional<RegistryEntryList.Named<T>> optional = registryLookup.getWrapperOrThrow(registryRef).getOptional(tooltipOrderTag);
			if (optional.isPresent()) {
				return (RegistryEntryList<T>)optional.get();
			}
		}

		return RegistryEntryList.of();
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
		private final Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments = new Object2IntOpenHashMap<>();
		private final boolean showInTooltip;

		public Builder(ItemEnchantmentsComponent enchantmentsComponent) {
			this.enchantments.putAll(enchantmentsComponent.enchantments);
			this.showInTooltip = enchantmentsComponent.showInTooltip;
		}

		public void set(RegistryEntry<Enchantment> enchantment, int level) {
			if (level <= 0) {
				this.enchantments.removeInt(enchantment);
			} else {
				this.enchantments.put(enchantment, Math.min(level, 255));
			}
		}

		public void add(RegistryEntry<Enchantment> enchantment, int level) {
			if (level > 0) {
				this.enchantments.merge(enchantment, Math.min(level, 255), Integer::max);
			}
		}

		public void remove(Predicate<RegistryEntry<Enchantment>> predicate) {
			this.enchantments.keySet().removeIf(predicate);
		}

		public int getLevel(RegistryEntry<Enchantment> enchantment) {
			return this.enchantments.getOrDefault(enchantment, 0);
		}

		public Set<RegistryEntry<Enchantment>> getEnchantments() {
			return this.enchantments.keySet();
		}

		public ItemEnchantmentsComponent build() {
			return new ItemEnchantmentsComponent(this.enchantments, this.showInTooltip);
		}
	}
}
