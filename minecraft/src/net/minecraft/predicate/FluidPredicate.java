package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class FluidPredicate {
	public static final FluidPredicate ANY = new FluidPredicate(null, null, StatePredicate.ANY);
	@Nullable
	private final TagKey<Fluid> tag;
	@Nullable
	private final Fluid fluid;
	private final StatePredicate state;

	public FluidPredicate(@Nullable TagKey<Fluid> tag, @Nullable Fluid fluid, StatePredicate state) {
		this.tag = tag;
		this.fluid = fluid;
		this.state = state;
	}

	public boolean test(ServerWorld world, BlockPos pos) {
		if (this == ANY) {
			return true;
		} else if (!world.canSetBlock(pos)) {
			return false;
		} else {
			FluidState fluidState = world.getFluidState(pos);
			if (this.tag != null && !fluidState.isIn(this.tag)) {
				return false;
			} else {
				return this.fluid != null && !fluidState.isOf(this.fluid) ? false : this.state.test(fluidState);
			}
		}
	}

	public static FluidPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "fluid");
			Fluid fluid = null;
			if (jsonObject.has("fluid")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "fluid"));
				fluid = Registries.FLUID.get(identifier);
			}

			TagKey<Fluid> tagKey = null;
			if (jsonObject.has("tag")) {
				Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
				tagKey = TagKey.of(RegistryKeys.FLUID, identifier2);
			}

			StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
			return new FluidPredicate(tagKey, fluid, statePredicate);
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.fluid != null) {
				jsonObject.addProperty("fluid", Registries.FLUID.getId(this.fluid).toString());
			}

			if (this.tag != null) {
				jsonObject.addProperty("tag", this.tag.id().toString());
			}

			jsonObject.add("state", this.state.toJson());
			return jsonObject;
		}
	}

	public static class Builder {
		@Nullable
		private Fluid fluid;
		@Nullable
		private TagKey<Fluid> tag;
		private StatePredicate state = StatePredicate.ANY;

		private Builder() {
		}

		public static FluidPredicate.Builder create() {
			return new FluidPredicate.Builder();
		}

		public FluidPredicate.Builder fluid(Fluid fluid) {
			this.fluid = fluid;
			return this;
		}

		public FluidPredicate.Builder tag(TagKey<Fluid> tag) {
			this.tag = tag;
			return this;
		}

		public FluidPredicate.Builder state(StatePredicate state) {
			this.state = state;
			return this;
		}

		public FluidPredicate build() {
			return new FluidPredicate(this.tag, this.fluid, this.state);
		}
	}
}
