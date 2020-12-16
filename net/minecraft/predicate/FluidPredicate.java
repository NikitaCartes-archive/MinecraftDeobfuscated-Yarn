/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

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
        }
        if (!world.canSetBlock(pos)) {
            return false;
        }
        FluidState fluidState = world.getFluidState(pos);
        Fluid fluid = fluidState.getFluid();
        if (this.tag != null && !fluid.isIn(this.tag)) {
            return false;
        }
        if (this.fluid != null && fluid != this.fluid) {
            return false;
        }
        return this.state.test(fluidState);
    }

    public static FluidPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "fluid");
        Fluid fluid = null;
        if (jsonObject.has("fluid")) {
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "fluid"));
            fluid = Registry.FLUID.get(identifier2);
        }
        Tag<Fluid> tag = null;
        if (jsonObject.has("tag")) {
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
            tag = ServerTagManagerHolder.getTagManager().getTag(Registry.FLUID_KEY, identifier2, identifier -> new JsonSyntaxException("Unknown fluid tag '" + identifier + "'"));
        }
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        return new FluidPredicate(tag, fluid, statePredicate);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.fluid != null) {
            jsonObject.addProperty("fluid", Registry.FLUID.getId(this.fluid).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", ServerTagManagerHolder.getTagManager().getTagId(Registry.FLUID_KEY, this.tag, () -> new IllegalStateException("Unknown fluid tag")).toString());
        }
        jsonObject.add("state", this.state.toJson());
        return jsonObject;
    }
}

