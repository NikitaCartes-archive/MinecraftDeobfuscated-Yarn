package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1090 implements class_1087 {
	private final class_809 field_5404;
	private final class_806 field_5405;
	private final class_1058 field_16594;

	public class_1090(class_809 arg, class_806 arg2, class_1058 arg3) {
		this.field_5404 = arg;
		this.field_5405 = arg2;
		this.field_16594 = arg3;
	}

	@Override
	public List<class_777> method_4707(@Nullable class_2680 arg, @Nullable class_2350 arg2, Random random) {
		return Collections.emptyList();
	}

	@Override
	public boolean method_4708() {
		return false;
	}

	@Override
	public boolean method_4712() {
		return true;
	}

	@Override
	public boolean method_4713() {
		return true;
	}

	@Override
	public class_1058 method_4711() {
		return this.field_16594;
	}

	@Override
	public class_809 method_4709() {
		return this.field_5404;
	}

	@Override
	public class_806 method_4710() {
		return this.field_5405;
	}
}
