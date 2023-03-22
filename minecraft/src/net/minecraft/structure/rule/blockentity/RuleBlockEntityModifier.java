package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

public interface RuleBlockEntityModifier {
	Codec<RuleBlockEntityModifier> TYPE_CODEC = Registries.RULE_BLOCK_ENTITY_MODIFIER
		.getCodec()
		.dispatch(RuleBlockEntityModifier::getType, RuleBlockEntityModifierType::codec);

	@Nullable
	NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt);

	RuleBlockEntityModifierType<?> getType();
}
