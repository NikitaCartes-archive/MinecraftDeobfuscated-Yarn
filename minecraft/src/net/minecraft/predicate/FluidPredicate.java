package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class FluidPredicate {
	public static final FluidPredicate ANY = new FluidPredicate(null, null, StatePredicate.ANY);
	@Nullable
	private final Tag<Fluid> tag;
	@Nullable
	private final Fluid fluid;
	private final StatePredicate state;

	public FluidPredicate(@Nullable Tag<Fluid> tag, @Nullable Fluid fluid, StatePredicate state) {
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
			Fluid fluid = fluidState.getFluid();
			if (this.tag != null && !fluid.isIn(this.tag)) {
				return false;
			} else {
				return this.fluid != null && fluid != this.fluid ? false : this.state.test(fluidState);
			}
		}
	}

	public static FluidPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "fluid");
			Fluid fluid = null;
			if (jsonObject.has("fluid")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "fluid"));
				fluid = Registry.FLUID.get(identifier);
			}

			Tag<Fluid> tag = null;
			if (jsonObject.has("tag")) {
				Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
				tag = ServerTagManagerHolder.getTagManager().getTag(Registry.FLUID_KEY, identifier2, id -> new JsonSyntaxException("Unknown fluid tag '" + id + "'"));
			}

			StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
			return new FluidPredicate(tag, fluid, statePredicate);
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
				jsonObject.addProperty("fluid", Registry.FLUID.getId(this.fluid).toString());
			}

			if (this.tag != null) {
				jsonObject.addProperty(
					"tag", ServerTagManagerHolder.getTagManager().getTagId(Registry.FLUID_KEY, this.tag, () -> new IllegalStateException("Unknown fluid tag")).toString()
				);
			}

			jsonObject.add("state", this.state.toJson());
			return jsonObject;
		}
	}

	public static class Builder {
		@Nullable
		private Fluid fluid;
		@Nullable
		private Tag<Fluid> tag;
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

		public FluidPredicate.Builder tag(Tag<Fluid> tag) {
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
