package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class class_4551 {
	public static final class_4551 field_20708 = new class_4551(null, null, class_4559.field_20736);
	@Nullable
	private final Tag<Fluid> field_20709;
	@Nullable
	private final Fluid field_20710;
	private final class_4559 field_20711;

	public class_4551(@Nullable Tag<Fluid> tag, @Nullable Fluid fluid, class_4559 arg) {
		this.field_20709 = tag;
		this.field_20710 = fluid;
		this.field_20711 = arg;
	}

	public boolean method_22475(ServerWorld serverWorld, BlockPos blockPos) {
		if (this == field_20708) {
			return true;
		} else {
			FluidState fluidState = serverWorld.getFluidState(blockPos);
			Fluid fluid = fluidState.getFluid();
			if (this.field_20709 != null && !this.field_20709.contains(fluid)) {
				return false;
			} else {
				return this.field_20710 != null && fluid != this.field_20710 ? false : this.field_20711.method_22518(fluidState);
			}
		}
	}

	public static class_4551 method_22474(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "fluid");
			Fluid fluid = null;
			if (jsonObject.has("fluid")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "fluid"));
				fluid = Registry.FLUID.get(identifier);
			}

			Tag<Fluid> tag = null;
			if (jsonObject.has("tag")) {
				Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
				tag = FluidTags.method_22448().get(identifier2);
				if (tag == null) {
					throw new JsonSyntaxException("Unknown fluid tag '" + identifier2 + "'");
				}
			}

			class_4559 lv = class_4559.method_22519(jsonObject.get("state"));
			return new class_4551(tag, fluid, lv);
		} else {
			return field_20708;
		}
	}

	public JsonElement method_22473() {
		if (this == field_20708) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_20710 != null) {
				jsonObject.addProperty("fluid", Registry.FLUID.getId(this.field_20710).toString());
			}

			if (this.field_20709 != null) {
				jsonObject.addProperty("tag", this.field_20709.getId().toString());
			}

			jsonObject.add("state", this.field_20711.method_22513());
			return jsonObject;
		}
	}
}
