package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record TradedItem(RegistryEntry<Item> item, int count, ComponentPredicate components, ItemStack itemStack) {
	public static final Codec<TradedItem> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ItemStack.ITEM_CODEC.fieldOf("id").forGetter(TradedItem::item),
					Codecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(TradedItem::count),
					ComponentPredicate.CODEC.optionalFieldOf("components", ComponentPredicate.EMPTY).forGetter(TradedItem::components)
				)
				.apply(instance, TradedItem::new)
	);
	public static final PacketCodec<RegistryByteBuf, TradedItem> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.ITEM),
		TradedItem::item,
		PacketCodecs.VAR_INT,
		TradedItem::count,
		ComponentPredicate.PACKET_CODEC,
		TradedItem::components,
		TradedItem::new
	);
	public static final PacketCodec<RegistryByteBuf, Optional<TradedItem>> OPTIONAL_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs::optional);

	public TradedItem(ItemConvertible item) {
		this(item, 1);
	}

	public TradedItem(ItemConvertible item, int count) {
		this(item.asItem().getRegistryEntry(), count, ComponentPredicate.EMPTY);
	}

	public TradedItem(RegistryEntry<Item> item, int count, ComponentPredicate components) {
		this(item, count, components, createDisplayStack(item, count, components));
	}

	public TradedItem withComponents(UnaryOperator<ComponentPredicate.Builder> builderCallback) {
		return new TradedItem(this.item, this.count, ((ComponentPredicate.Builder)builderCallback.apply(ComponentPredicate.builder())).build());
	}

	private static ItemStack createDisplayStack(RegistryEntry<Item> item, int count, ComponentPredicate components) {
		return new ItemStack(item, count, components.toChanges());
	}

	public boolean matches(ItemStack stack) {
		return stack.itemMatches(this.item) && this.components.test((ComponentHolder)stack);
	}
}
