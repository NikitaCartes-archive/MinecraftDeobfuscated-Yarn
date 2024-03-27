package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;

public class AppendStaticRuleBlockEntityModifier implements RuleBlockEntityModifier {
	public static final MapCodec<AppendStaticRuleBlockEntityModifier> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(NbtCompound.CODEC.fieldOf("data").forGetter(modifier -> modifier.nbt)).apply(instance, AppendStaticRuleBlockEntityModifier::new)
	);
	private final NbtCompound nbt;

	public AppendStaticRuleBlockEntityModifier(NbtCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
		return nbt == null ? this.nbt.copy() : nbt.copyFrom(this.nbt);
	}

	@Override
	public RuleBlockEntityModifierType<?> getType() {
		return RuleBlockEntityModifierType.APPEND_STATIC;
	}
}
