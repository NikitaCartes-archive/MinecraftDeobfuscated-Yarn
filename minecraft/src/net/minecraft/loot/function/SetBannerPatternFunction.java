package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

public class SetBannerPatternFunction extends ConditionalLootFunction {
	private static final Codec<Pair<RegistryEntry<BannerPattern>, DyeColor>> BANNER_PATTERN_AND_COLOR_CODEC = Codec.<RegistryEntry<BannerPattern>, DyeColor>mapPair(
			Registries.BANNER_PATTERN.createEntryCodec().fieldOf("pattern"), DyeColor.CODEC.fieldOf("color")
		)
		.codec();
	public static final Codec<SetBannerPatternFunction> CODEC = RecordCodecBuilder.create(
		instance -> method_53344(instance)
				.<List<Pair<RegistryEntry<BannerPattern>, DyeColor>>, boolean>and(
					instance.group(
						BANNER_PATTERN_AND_COLOR_CODEC.listOf().fieldOf("patterns").forGetter(setBannerPatternFunction -> setBannerPatternFunction.patterns),
						Codec.BOOL.fieldOf("append").forGetter(setBannerPatternFunction -> setBannerPatternFunction.append)
					)
				)
				.apply(instance, SetBannerPatternFunction::new)
	);
	private final List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;
	private final boolean append;

	SetBannerPatternFunction(List<LootCondition> conditions, List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns, boolean append) {
		super(conditions);
		this.patterns = patterns;
		this.append = append;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound == null) {
			nbtCompound = new NbtCompound();
		}

		BannerPattern.Patterns patterns = new BannerPattern.Patterns();
		this.patterns.forEach(patterns::add);
		NbtList nbtList = patterns.toNbt();
		NbtList nbtList2;
		if (this.append) {
			nbtList2 = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).copy();
			nbtList2.addAll(nbtList);
		} else {
			nbtList2 = nbtList;
		}

		nbtCompound.put("Patterns", nbtList2);
		BlockItem.setBlockEntityNbt(stack, BlockEntityType.BANNER, nbtCompound);
		return stack;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_BANNER_PATTERN;
	}

	public static SetBannerPatternFunction.Builder builder(boolean append) {
		return new SetBannerPatternFunction.Builder(append);
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetBannerPatternFunction.Builder> {
		private final ImmutableList.Builder<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = ImmutableList.builder();
		private final boolean append;

		Builder(boolean append) {
			this.append = append;
		}

		protected SetBannerPatternFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetBannerPatternFunction(this.getConditions(), this.patterns.build(), this.append);
		}

		public SetBannerPatternFunction.Builder pattern(RegistryKey<BannerPattern> pattern, DyeColor color) {
			return this.pattern(Registries.BANNER_PATTERN.entryOf(pattern), color);
		}

		public SetBannerPatternFunction.Builder pattern(RegistryEntry<BannerPattern> pattern, DyeColor color) {
			this.patterns.add(Pair.of(pattern, color));
			return this;
		}
	}
}
