package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

public record BlockPredicate(Optional<TagKey<Block>> tag, Optional<RegistryEntryList<Block>> blocks, Optional<StatePredicate> state, Optional<NbtPredicate> nbt) {
	private static final Codec<RegistryEntryList<Block>> BLOCK_ENTRY_LIST_CODEC = Registries.BLOCK
		.createEntryCodec()
		.listOf()
		.xmap(RegistryEntryList::of, blocks -> blocks.stream().toList());
	public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(TagKey.unprefixedCodec(RegistryKeys.BLOCK), "tag").forGetter(BlockPredicate::tag),
					Codecs.createStrictOptionalFieldCodec(BLOCK_ENTRY_LIST_CODEC, "blocks").forGetter(BlockPredicate::blocks),
					Codecs.createStrictOptionalFieldCodec(StatePredicate.CODEC, "state").forGetter(BlockPredicate::state),
					Codecs.createStrictOptionalFieldCodec(NbtPredicate.CODEC, "nbt").forGetter(BlockPredicate::nbt)
				)
				.apply(instance, BlockPredicate::new)
	);

	public boolean test(ServerWorld world, BlockPos pos) {
		if (!world.canSetBlock(pos)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			if (this.tag.isPresent() && !blockState.isIn((TagKey<Block>)this.tag.get())) {
				return false;
			} else if (this.blocks.isPresent() && !blockState.isIn((RegistryEntryList<Block>)this.blocks.get())) {
				return false;
			} else if (this.state.isPresent() && !((StatePredicate)this.state.get()).test(blockState)) {
				return false;
			} else {
				if (this.nbt.isPresent()) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity == null || !((NbtPredicate)this.nbt.get()).test(blockEntity.createNbtWithIdentifyingData())) {
						return false;
					}
				}

				return true;
			}
		}
	}

	public static class Builder {
		private Optional<RegistryEntryList<Block>> blocks = Optional.empty();
		private Optional<TagKey<Block>> tag = Optional.empty();
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
			this.tag = Optional.of(tag);
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
			return new BlockPredicate(this.tag, this.blocks, this.state, this.nbt);
		}
	}
}
