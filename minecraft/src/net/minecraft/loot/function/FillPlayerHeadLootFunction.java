package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;

public class FillPlayerHeadLootFunction extends ConditionalLootFunction {
	public static final Codec<FillPlayerHeadLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(function -> function.entity))
				.apply(instance, FillPlayerHeadLootFunction::new)
	);
	private final LootContext.EntityTarget entity;

	public FillPlayerHeadLootFunction(List<LootCondition> conditions, LootContext.EntityTarget entity) {
		super(conditions);
		this.entity = entity;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.FILL_PLAYER_HEAD;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.entity.getParameter());
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isOf(Items.PLAYER_HEAD)) {
			Object var4 = context.get(this.entity.getParameter());
			if (var4 instanceof PlayerEntity playerEntity) {
				stack.set(DataComponentTypes.PROFILE, new ProfileComponent(playerEntity.getGameProfile()));
			}
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootContext.EntityTarget target) {
		return builder(conditions -> new FillPlayerHeadLootFunction(conditions, target));
	}
}
