package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class class_4550 {
	public static final class_4550 field_20692 = new class_4550(null, null, class_4559.field_20736, NbtPredicate.ANY);
	@Nullable
	private final Tag<Block> field_20693;
	@Nullable
	private final Block field_20694;
	private final class_4559 field_20695;
	private final NbtPredicate field_20696;

	public class_4550(@Nullable Tag<Block> tag, @Nullable Block block, class_4559 arg, NbtPredicate nbtPredicate) {
		this.field_20693 = tag;
		this.field_20694 = block;
		this.field_20695 = arg;
		this.field_20696 = nbtPredicate;
	}

	public boolean method_22454(ServerWorld serverWorld, BlockPos blockPos) {
		if (this == field_20692) {
			return true;
		} else {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (this.field_20693 != null && !this.field_20693.contains(block)) {
				return false;
			} else if (this.field_20694 != null && block != this.field_20694) {
				return false;
			} else if (!this.field_20695.method_22514(blockState)) {
				return false;
			} else {
				if (this.field_20696 != NbtPredicate.ANY) {
					BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
					if (blockEntity == null || !this.field_20696.test(blockEntity.toTag(new CompoundTag()))) {
						return false;
					}
				}

				return true;
			}
		}
	}

	public static class_4550 method_22453(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "block");
			NbtPredicate nbtPredicate = NbtPredicate.deserialize(jsonObject.get("nbt"));
			Block block = null;
			if (jsonObject.has("block")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
				block = Registry.BLOCK.get(identifier);
			}

			Tag<Block> tag = null;
			if (jsonObject.has("tag")) {
				Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
				tag = BlockTags.getContainer().get(identifier2);
				if (tag == null) {
					throw new JsonSyntaxException("Unknown block tag '" + identifier2 + "'");
				}
			}

			class_4559 lv = class_4559.method_22519(jsonObject.get("state"));
			return new class_4550(tag, block, lv, nbtPredicate);
		} else {
			return field_20692;
		}
	}

	public JsonElement method_22452() {
		if (this == field_20692) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_20694 != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.field_20694).toString());
			}

			if (this.field_20693 != null) {
				jsonObject.addProperty("tag", this.field_20693.getId().toString());
			}

			jsonObject.add("nbt", this.field_20696.serialize());
			jsonObject.add("state", this.field_20695.method_22513());
			return jsonObject;
		}
	}
}
