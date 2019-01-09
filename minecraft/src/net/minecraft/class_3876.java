package net.minecraft;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3876 extends class_517 {
	@Override
	protected boolean method_17061() {
		return this.field_3096.method_14892();
	}

	@Override
	protected void method_17060(boolean bl) {
		this.field_3096.method_14888(bl);
	}

	@Override
	protected boolean method_17062() {
		return this.field_3096.method_14891();
	}

	@Override
	protected void method_17063(boolean bl) {
		this.field_3096.method_14882(bl);
	}

	@Override
	protected String method_17064() {
		return "gui.recipebook.toggleRecipes.smeltable";
	}

	@Override
	protected Set<class_1792> method_17065() {
		return class_3866.method_11196().keySet();
	}
}
