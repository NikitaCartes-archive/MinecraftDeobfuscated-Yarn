package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class class_4156 implements class_4213 {
	private final BlockPos field_18492;
	private final class_4158 field_18493;
	private int field_18494;
	private final Runnable field_18495;

	private class_4156(BlockPos blockPos, class_4158 arg, int i, Runnable runnable) {
		this.field_18492 = blockPos;
		this.field_18493 = arg;
		this.field_18494 = i;
		this.field_18495 = runnable;
	}

	public class_4156(BlockPos blockPos, class_4158 arg, Runnable runnable) {
		this(blockPos, arg, arg.method_19161(), runnable);
	}

	public <T> class_4156(Dynamic<T> dynamic, Runnable runnable) {
		this(
			(BlockPos)dynamic.get("pos").map(BlockPos::method_19438).orElse(new BlockPos(0, 0, 0)),
			Registry.field_18792.method_10223(new Identifier(dynamic.get("type").asString(""))),
			dynamic.get("free_tickets").asInt(0),
			runnable
		);
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("pos"),
				this.field_18492.method_19508(dynamicOps),
				dynamicOps.createString("type"),
				dynamicOps.createString(Registry.field_18792.method_10221(this.field_18493).toString()),
				dynamicOps.createString("free_tickets"),
				dynamicOps.createInt(this.field_18494)
			)
		);
	}

	protected boolean method_19137() {
		if (this.field_18494 <= 0) {
			return false;
		} else {
			this.field_18494--;
			this.field_18495.run();
			return true;
		}
	}

	protected boolean method_19138() {
		if (this.field_18494 >= this.field_18493.method_19161()) {
			return false;
		} else {
			this.field_18494++;
			this.field_18495.run();
			return true;
		}
	}

	public boolean method_19139() {
		return this.field_18494 > 0;
	}

	public boolean method_19140() {
		return this.field_18494 != this.field_18493.method_19161();
	}

	public BlockPos method_19141() {
		return this.field_18492;
	}

	public class_4158 method_19142() {
		return this.field_18493;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object != null && this.getClass() == object.getClass() ? Objects.equals(this.field_18492, ((class_4156)object).field_18492) : false;
		}
	}

	public int hashCode() {
		return this.field_18492.hashCode();
	}
}
