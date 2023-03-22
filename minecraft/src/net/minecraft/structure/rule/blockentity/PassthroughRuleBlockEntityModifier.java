package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;

public class PassthroughRuleBlockEntityModifier implements RuleBlockEntityModifier {
	public static final PassthroughRuleBlockEntityModifier INSTANCE = new PassthroughRuleBlockEntityModifier();
	public static final Codec<PassthroughRuleBlockEntityModifier> CODEC = Codec.unit(INSTANCE);

	@Nullable
	@Override
	public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
		return nbt;
	}

	@Override
	public RuleBlockEntityModifierType<?> getType() {
		return RuleBlockEntityModifierType.PASSTHROUGH;
	}
}
