package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

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
		if (stack.isOf(Items.PLAYER_HEAD) && context.get(this.entity.getParameter()) instanceof PlayerEntity playerEntity) {
			GameProfile gameProfile = playerEntity.getGameProfile();
			stack.getOrCreateNbt().put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootContext.EntityTarget target) {
		return builder(conditions -> new FillPlayerHeadLootFunction(conditions, target));
	}
}
