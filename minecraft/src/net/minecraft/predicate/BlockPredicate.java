package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockPredicate {
	public static final BlockPredicate ANY = new BlockPredicate(null, null, StatePredicate.ANY, NbtPredicate.ANY);
	@Nullable
	private final Tag<Block> tag;
	@Nullable
	private final Block block;
	private final StatePredicate state;
	private final NbtPredicate nbt;

	public BlockPredicate(@Nullable Tag<Block> tag, @Nullable Block block, StatePredicate state, NbtPredicate nbt) {
		this.tag = tag;
		this.block = block;
		this.state = state;
		this.nbt = nbt;
	}

	public boolean test(ServerWorld world, BlockPos pos) {
		if (this == ANY) {
			return true;
		} else if (!world.canSetBlock(pos)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (this.tag != null && !this.tag.contains(block)) {
				return false;
			} else if (this.block != null && block != this.block) {
				return false;
			} else if (!this.state.test(blockState)) {
				return false;
			} else {
				if (this.nbt != NbtPredicate.ANY) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity == null || !this.nbt.test(blockEntity.toTag(new CompoundTag()))) {
						return false;
					}
				}

				return true;
			}
		}
	}

	public static BlockPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "block");
			NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
			Block block = null;
			if (jsonObject.has("block")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
				block = Registry.BLOCK.get(identifier);
			}

			Tag<Block> tag = null;
			if (jsonObject.has("tag")) {
				Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
				tag = ServerTagManagerHolder.getTagManager().getBlocks().getTag(identifier2);
				if (tag == null) {
					throw new JsonSyntaxException("Unknown block tag '" + identifier2 + "'");
				}
			}

			StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
			return new BlockPredicate(tag, block, statePredicate, nbtPredicate);
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
			}

			if (this.tag != null) {
				jsonObject.addProperty("tag", ServerTagManagerHolder.getTagManager().getBlocks().getTagId(this.tag).toString());
			}

			jsonObject.add("nbt", this.nbt.toJson());
			jsonObject.add("state", this.state.toJson());
			return jsonObject;
		}
	}

	public static class Builder {
		@Nullable
		private Block block;
		@Nullable
		private Tag<Block> tag;
		private StatePredicate state = StatePredicate.ANY;
		private NbtPredicate nbt = NbtPredicate.ANY;

		private Builder() {
		}

		public static BlockPredicate.Builder create() {
			return new BlockPredicate.Builder();
		}

		public BlockPredicate.Builder block(Block block) {
			this.block = block;
			return this;
		}

		public BlockPredicate.Builder method_29233(Tag<Block> tag) {
			this.tag = tag;
			return this;
		}

		public BlockPredicate.Builder state(StatePredicate state) {
			this.state = state;
			return this;
		}

		public BlockPredicate build() {
			return new BlockPredicate(this.tag, this.block, this.state, this.nbt);
		}
	}
}
