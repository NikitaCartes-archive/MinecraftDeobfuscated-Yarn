package net.minecraft.structure.rule.blockentity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class AppendLootRuleBlockEntityModifier implements RuleBlockEntityModifier {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<AppendLootRuleBlockEntityModifier> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Identifier.CODEC.fieldOf("loot_table").forGetter(modifier -> modifier.lootTable))
				.apply(instance, AppendLootRuleBlockEntityModifier::new)
	);
	private final Identifier lootTable;

	public AppendLootRuleBlockEntityModifier(Identifier lootTable) {
		this.lootTable = lootTable;
	}

	@Override
	public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
		NbtCompound nbtCompound = nbt == null ? new NbtCompound() : nbt.copy();
		Identifier.CODEC.encodeStart(NbtOps.INSTANCE, this.lootTable).resultOrPartial(LOGGER::error).ifPresent(nbtx -> nbtCompound.put("LootTable", nbtx));
		nbtCompound.putLong("LootTableSeed", random.nextLong());
		return nbtCompound;
	}

	@Override
	public RuleBlockEntityModifierType<?> getType() {
		return RuleBlockEntityModifierType.APPEND_LOOT;
	}
}
