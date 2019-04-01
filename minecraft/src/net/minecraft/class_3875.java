package net.minecraft;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3875 extends class_517 {
	@Override
	protected boolean method_17061() {
		return this.field_3096.method_17319();
	}

	@Override
	protected void method_17060(boolean bl) {
		this.field_3096.method_17320(bl);
	}

	@Override
	protected boolean method_17062() {
		return this.field_3096.method_17317();
	}

	@Override
	protected void method_17063(boolean bl) {
		this.field_3096.method_17318(bl);
	}

	@Override
	protected String method_17064() {
		return "gui.recipebook.toggleRecipes.blastable";
	}

	@Override
	protected Set<class_1792> method_17065() {
		return class_2609.method_11196().keySet();
	}
}
