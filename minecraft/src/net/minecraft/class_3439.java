package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3439 {
	protected final Set<class_2960> field_15300 = Sets.<class_2960>newHashSet();
	protected final Set<class_2960> field_15295 = Sets.<class_2960>newHashSet();
	protected boolean field_15299;
	protected boolean field_15298;
	protected boolean field_15297;
	protected boolean field_15296;
	protected boolean field_17267;
	protected boolean field_17268;
	protected boolean field_17269;
	protected boolean field_17270;

	public void method_14875(class_3439 arg) {
		this.field_15300.clear();
		this.field_15295.clear();
		this.field_15300.addAll(arg.field_15300);
		this.field_15295.addAll(arg.field_15295);
	}

	public void method_14876(class_1860<?> arg) {
		if (!arg.method_8118()) {
			this.method_14881(arg.method_8114());
		}
	}

	protected void method_14881(class_2960 arg) {
		this.field_15300.add(arg);
	}

	public boolean method_14878(@Nullable class_1860<?> arg) {
		return arg == null ? false : this.field_15300.contains(arg.method_8114());
	}

	@Environment(EnvType.CLIENT)
	public void method_14893(class_1860<?> arg) {
		this.method_14879(arg.method_8114());
	}

	protected void method_14879(class_2960 arg) {
		this.field_15300.remove(arg);
		this.field_15295.remove(arg);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14883(class_1860<?> arg) {
		return this.field_15295.contains(arg.method_8114());
	}

	public void method_14886(class_1860<?> arg) {
		this.field_15295.remove(arg.method_8114());
	}

	public void method_14885(class_1860<?> arg) {
		this.method_14877(arg.method_8114());
	}

	protected void method_14877(class_2960 arg) {
		this.field_15295.add(arg);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14887() {
		return this.field_15299;
	}

	public void method_14884(boolean bl) {
		this.field_15299 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14880(class_1729<?> arg) {
		if (arg instanceof class_3858) {
			return this.field_15296;
		} else if (arg instanceof class_3705) {
			return this.field_17268;
		} else {
			return arg instanceof class_3706 ? this.field_17270 : this.field_15298;
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14890() {
		return this.field_15298;
	}

	public void method_14889(boolean bl) {
		this.field_15298 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14891() {
		return this.field_15297;
	}

	public void method_14882(boolean bl) {
		this.field_15297 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14892() {
		return this.field_15296;
	}

	public void method_14888(boolean bl) {
		this.field_15296 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17317() {
		return this.field_17267;
	}

	public void method_17318(boolean bl) {
		this.field_17267 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17319() {
		return this.field_17268;
	}

	public void method_17320(boolean bl) {
		this.field_17268 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17321() {
		return this.field_17269;
	}

	public void method_17322(boolean bl) {
		this.field_17269 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17323() {
		return this.field_17270;
	}

	public void method_17324(boolean bl) {
		this.field_17270 = bl;
	}
}
