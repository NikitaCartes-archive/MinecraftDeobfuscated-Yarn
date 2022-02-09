package net.minecraft.data.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

public class MultipartBlockStateSupplier implements BlockStateSupplier {
	private final Block block;
	private final List<MultipartBlockStateSupplier.Multipart> multiparts = Lists.<MultipartBlockStateSupplier.Multipart>newArrayList();

	private MultipartBlockStateSupplier(Block block) {
		this.block = block;
	}

	@Override
	public Block getBlock() {
		return this.block;
	}

	public static MultipartBlockStateSupplier create(Block block) {
		return new MultipartBlockStateSupplier(block);
	}

	public MultipartBlockStateSupplier with(List<BlockStateVariant> variants) {
		this.multiparts.add(new MultipartBlockStateSupplier.Multipart(variants));
		return this;
	}

	public MultipartBlockStateSupplier with(BlockStateVariant variant) {
		return this.with(ImmutableList.of(variant));
	}

	public MultipartBlockStateSupplier with(When condition, List<BlockStateVariant> variants) {
		this.multiparts.add(new MultipartBlockStateSupplier.ConditionalMultipart(condition, variants));
		return this;
	}

	public MultipartBlockStateSupplier with(When condition, BlockStateVariant... variants) {
		return this.with(condition, ImmutableList.copyOf(variants));
	}

	public MultipartBlockStateSupplier with(When condition, BlockStateVariant variant) {
		return this.with(condition, ImmutableList.of(variant));
	}

	public JsonElement get() {
		StateManager<Block, BlockState> stateManager = this.block.getStateManager();
		this.multiparts.forEach(multipart -> multipart.validate(stateManager));
		JsonArray jsonArray = new JsonArray();
		this.multiparts.stream().map(MultipartBlockStateSupplier.Multipart::get).forEach(jsonArray::add);
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("multipart", jsonArray);
		return jsonObject;
	}

	static class ConditionalMultipart extends MultipartBlockStateSupplier.Multipart {
		private final When when;

		ConditionalMultipart(When when, List<BlockStateVariant> variants) {
			super(variants);
			this.when = when;
		}

		@Override
		public void validate(StateManager<?, ?> stateManager) {
			this.when.validate(stateManager);
		}

		@Override
		public void extraToJson(JsonObject json) {
			json.add("when", (JsonElement)this.when.get());
		}
	}

	static class Multipart implements Supplier<JsonElement> {
		private final List<BlockStateVariant> variants;

		Multipart(List<BlockStateVariant> variants) {
			this.variants = variants;
		}

		public void validate(StateManager<?, ?> stateManager) {
		}

		public void extraToJson(JsonObject json) {
		}

		public JsonElement get() {
			JsonObject jsonObject = new JsonObject();
			this.extraToJson(jsonObject);
			jsonObject.add("apply", BlockStateVariant.toJson(this.variants));
			return jsonObject;
		}
	}
}
