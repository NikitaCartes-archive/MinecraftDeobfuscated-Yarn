package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;

public class ClearRuleBlockEntityModifier implements RuleBlockEntityModifier {
	private static final ClearRuleBlockEntityModifier INSTANCE = new ClearRuleBlockEntityModifier();
	public static final MapCodec<ClearRuleBlockEntityModifier> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
		return new NbtCompound();
	}

	@Override
	public RuleBlockEntityModifierType<?> getType() {
		return RuleBlockEntityModifierType.CLEAR;
	}
}
