package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

public class SetBannerPatternLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetBannerPatternLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<BannerPatternsComponent, boolean>and(
					instance.group(
						BannerPatternsComponent.CODEC.fieldOf("patterns").forGetter(function -> function.patterns),
						Codec.BOOL.fieldOf("append").forGetter(function -> function.append)
					)
				)
				.apply(instance, SetBannerPatternLootFunction::new)
	);
	private final BannerPatternsComponent patterns;
	private final boolean append;

	SetBannerPatternLootFunction(List<LootCondition> conditions, BannerPatternsComponent patterns, boolean append) {
		super(conditions);
		this.patterns = patterns;
		this.append = append;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		if (this.append) {
			stack.apply(
				DataComponentTypes.BANNER_PATTERNS,
				BannerPatternsComponent.DEFAULT,
				this.patterns,
				(current, newPatterns) -> new BannerPatternsComponent.Builder().addAll(current).addAll(newPatterns).build()
			);
		} else {
			stack.set(DataComponentTypes.BANNER_PATTERNS, this.patterns);
		}

		return stack;
	}

	@Override
	public LootFunctionType<SetBannerPatternLootFunction> getType() {
		return LootFunctionTypes.SET_BANNER_PATTERN;
	}

	public static SetBannerPatternLootFunction.Builder builder(boolean append) {
		return new SetBannerPatternLootFunction.Builder(append);
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetBannerPatternLootFunction.Builder> {
		private final BannerPatternsComponent.Builder patterns = new BannerPatternsComponent.Builder();
		private final boolean append;

		Builder(boolean append) {
			this.append = append;
		}

		protected SetBannerPatternLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetBannerPatternLootFunction(this.getConditions(), this.patterns.build(), this.append);
		}

		public SetBannerPatternLootFunction.Builder pattern(RegistryEntry<BannerPattern> pattern, DyeColor color) {
			this.patterns.add(pattern, color);
			return this;
		}
	}
}
