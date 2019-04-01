package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1110 {
	private final List<class_1111> field_5460;
	private final boolean field_5462;
	private final String field_5461;

	public class_1110(List<class_1111> list, boolean bl, String string) {
		this.field_5460 = list;
		this.field_5462 = bl;
		this.field_5461 = string;
	}

	public List<class_1111> method_4761() {
		return this.field_5460;
	}

	public boolean method_4763() {
		return this.field_5462;
	}

	@Nullable
	public String method_4762() {
		return this.field_5461;
	}
}
