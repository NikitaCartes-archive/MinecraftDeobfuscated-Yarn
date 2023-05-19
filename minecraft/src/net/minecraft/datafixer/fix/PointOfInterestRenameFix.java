package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import java.util.stream.Stream;

public class PointOfInterestRenameFix extends PointOfInterestFix {
	private final Function<String, String> renamer;

	public PointOfInterestRenameFix(Schema outputSchema, String name, Function<String, String> renamer) {
		super(outputSchema, name);
		this.renamer = renamer;
	}

	@Override
	protected <T> Stream<Dynamic<T>> update(Stream<Dynamic<T>> dynamics) {
		return dynamics.map(
			dynamic -> dynamic.update("type", dynamicx -> DataFixUtils.orElse(dynamicx.asString().map(this.renamer).map(dynamicx::createString).result(), dynamicx))
		);
	}
}
