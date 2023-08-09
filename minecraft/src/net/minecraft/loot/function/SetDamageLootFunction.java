package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class SetDamageLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<SetDamageLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> method_53344(instance)
				.<LootNumberProvider, boolean>and(
					instance.group(
						LootNumberProviderTypes.CODEC.fieldOf("damage").forGetter(setDamageLootFunction -> setDamageLootFunction.durabilityRange),
						Codec.BOOL.fieldOf("add").orElse(false).forGetter(setDamageLootFunction -> setDamageLootFunction.add)
					)
				)
				.apply(instance, SetDamageLootFunction::new)
	);
	private final LootNumberProvider durabilityRange;
	private final boolean add;

	private SetDamageLootFunction(List<LootCondition> conditions, LootNumberProvider durabilityRange, boolean add) {
		super(conditions);
		this.durabilityRange = durabilityRange;
		this.add = add;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_DAMAGE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.durabilityRange.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isDamageable()) {
			int i = stack.getMaxDamage();
			float f = this.add ? 1.0F - (float)stack.getDamage() / (float)i : 0.0F;
			float g = 1.0F - MathHelper.clamp(this.durabilityRange.nextFloat(context) + f, 0.0F, 1.0F);
			stack.setDamage(MathHelper.floor(g * (float)i));
		} else {
			LOGGER.warn("Couldn't set damage of loot item {}", stack);
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider durabilityRange) {
		return builder(conditions -> new SetDamageLootFunction(conditions, durabilityRange, false));
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider durabilityRange, boolean add) {
		return builder(conditions -> new SetDamageLootFunction(conditions, durabilityRange, add));
	}
}
