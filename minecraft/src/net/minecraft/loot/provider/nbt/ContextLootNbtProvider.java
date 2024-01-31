package net.minecraft.loot.provider.nbt;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;

public class ContextLootNbtProvider implements LootNbtProvider {
	private static final String BLOCK_ENTITY_TARGET_NAME = "block_entity";
	private static final ContextLootNbtProvider.Target BLOCK_ENTITY_TARGET = new ContextLootNbtProvider.Target() {
		@Override
		public NbtElement getNbt(LootContext context) {
			BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
			return blockEntity != null ? blockEntity.createNbtWithIdentifyingData(blockEntity.getWorld().getRegistryManager()) : null;
		}

		@Override
		public String getName() {
			return "block_entity";
		}

		@Override
		public Set<LootContextParameter<?>> getRequiredParameters() {
			return ImmutableSet.of(LootContextParameters.BLOCK_ENTITY);
		}
	};
	public static final ContextLootNbtProvider BLOCK_ENTITY = new ContextLootNbtProvider(BLOCK_ENTITY_TARGET);
	private static final Codec<ContextLootNbtProvider.Target> TARGET_CODEC = Codec.STRING.xmap(type -> {
		if (type.equals("block_entity")) {
			return BLOCK_ENTITY_TARGET;
		} else {
			LootContext.EntityTarget entityTarget = LootContext.EntityTarget.fromString(type);
			return getTarget(entityTarget);
		}
	}, ContextLootNbtProvider.Target::getName);
	public static final Codec<ContextLootNbtProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(TARGET_CODEC.fieldOf("target").forGetter(provider -> provider.target)).apply(instance, ContextLootNbtProvider::new)
	);
	public static final Codec<ContextLootNbtProvider> INLINE_CODEC = TARGET_CODEC.xmap(ContextLootNbtProvider::new, provider -> provider.target);
	private final ContextLootNbtProvider.Target target;

	private static ContextLootNbtProvider.Target getTarget(LootContext.EntityTarget entityTarget) {
		return new ContextLootNbtProvider.Target() {
			@Nullable
			@Override
			public NbtElement getNbt(LootContext context) {
				Entity entity = context.get(entityTarget.getParameter());
				return entity != null ? NbtPredicate.entityToNbt(entity) : null;
			}

			@Override
			public String getName() {
				return entityTarget.name();
			}

			@Override
			public Set<LootContextParameter<?>> getRequiredParameters() {
				return ImmutableSet.of(entityTarget.getParameter());
			}
		};
	}

	private ContextLootNbtProvider(ContextLootNbtProvider.Target target) {
		this.target = target;
	}

	@Override
	public LootNbtProviderType getType() {
		return LootNbtProviderTypes.CONTEXT;
	}

	@Nullable
	@Override
	public NbtElement getNbt(LootContext context) {
		return this.target.getNbt(context);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.target.getRequiredParameters();
	}

	public static LootNbtProvider fromTarget(LootContext.EntityTarget target) {
		return new ContextLootNbtProvider(getTarget(target));
	}

	interface Target {
		@Nullable
		NbtElement getNbt(LootContext context);

		String getName();

		Set<LootContextParameter<?>> getRequiredParameters();
	}
}
