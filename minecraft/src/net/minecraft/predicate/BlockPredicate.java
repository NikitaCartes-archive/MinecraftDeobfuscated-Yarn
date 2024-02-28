package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public record BlockPredicate(Optional<RegistryEntryList<Block>> blocks, Optional<StatePredicate> state, Optional<NbtPredicate> nbt) {
	public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(RegistryCodecs.entryList(RegistryKeys.BLOCK), "blocks").forGetter(BlockPredicate::blocks),
					Codecs.createStrictOptionalFieldCodec(StatePredicate.CODEC, "state").forGetter(BlockPredicate::state),
					Codecs.createStrictOptionalFieldCodec(NbtPredicate.CODEC, "nbt").forGetter(BlockPredicate::nbt)
				)
				.apply(instance, BlockPredicate::new)
	);
	public static final PacketCodec<RegistryByteBuf, BlockPredicate> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(PacketCodecs.registryEntryList(RegistryKeys.BLOCK)),
		BlockPredicate::blocks,
		PacketCodecs.optional(StatePredicate.PACKET_CODEC),
		BlockPredicate::state,
		PacketCodecs.optional(NbtPredicate.PACKET_CODEC),
		BlockPredicate::nbt,
		BlockPredicate::new
	);

	public boolean test(ServerWorld world, BlockPos pos) {
		if (!world.canSetBlock(pos)) {
			return false;
		} else {
			return !this.testState(world.getBlockState(pos))
				? false
				: !this.nbt.isPresent() || testBlockEntity(world, world.getBlockEntity(pos), (NbtPredicate)this.nbt.get());
		}
	}

	public boolean test(CachedBlockPosition pos) {
		return !this.testState(pos.getBlockState())
			? false
			: !this.nbt.isPresent() || testBlockEntity(pos.getWorld(), pos.getBlockEntity(), (NbtPredicate)this.nbt.get());
	}

	private boolean testState(BlockState state) {
		return this.blocks.isPresent() && !state.isIn((RegistryEntryList<Block>)this.blocks.get())
			? false
			: !this.state.isPresent() || ((StatePredicate)this.state.get()).test(state);
	}

	private static boolean testBlockEntity(WorldView world, @Nullable BlockEntity blockEntity, NbtPredicate nbtPredicate) {
		return blockEntity != null && nbtPredicate.test(blockEntity.createNbtWithIdentifyingData(world.getRegistryManager()));
	}

	public boolean hasNbt() {
		return this.nbt.isPresent();
	}

	public static class Builder {
		private Optional<RegistryEntryList<Block>> blocks = Optional.empty();
		private Optional<StatePredicate> state = Optional.empty();
		private Optional<NbtPredicate> nbt = Optional.empty();

		private Builder() {
		}

		public static BlockPredicate.Builder create() {
			return new BlockPredicate.Builder();
		}

		public BlockPredicate.Builder blocks(Block... blocks) {
			this.blocks = Optional.of(RegistryEntryList.of(Block::getRegistryEntry, blocks));
			return this;
		}

		public BlockPredicate.Builder blocks(Collection<Block> blocks) {
			this.blocks = Optional.of(RegistryEntryList.of(Block::getRegistryEntry, blocks));
			return this;
		}

		public BlockPredicate.Builder tag(TagKey<Block> tag) {
			this.blocks = Optional.of(Registries.BLOCK.getOrCreateEntryList(tag));
			return this;
		}

		public BlockPredicate.Builder nbt(NbtCompound nbt) {
			this.nbt = Optional.of(new NbtPredicate(nbt));
			return this;
		}

		public BlockPredicate.Builder state(StatePredicate.Builder state) {
			this.state = state.build();
			return this;
		}

		public BlockPredicate build() {
			return new BlockPredicate(this.blocks, this.state, this.nbt);
		}
	}
}
