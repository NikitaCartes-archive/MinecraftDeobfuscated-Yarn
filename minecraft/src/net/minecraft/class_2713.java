package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2713 implements class_2596<class_2602> {
	private class_2713.class_2714 field_12408;
	private List<class_2960> field_12414;
	private List<class_2960> field_12409;
	private boolean field_12413;
	private boolean field_12412;
	private boolean field_12411;
	private boolean field_12410;

	public class_2713() {
	}

	public class_2713(
		class_2713.class_2714 arg, Collection<class_2960> collection, Collection<class_2960> collection2, boolean bl, boolean bl2, boolean bl3, boolean bl4
	) {
		this.field_12408 = arg;
		this.field_12414 = ImmutableList.copyOf(collection);
		this.field_12409 = ImmutableList.copyOf(collection2);
		this.field_12413 = bl;
		this.field_12412 = bl2;
		this.field_12411 = bl3;
		this.field_12410 = bl4;
	}

	public void method_11753(class_2602 arg) {
		arg.method_11115(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12408 = arg.method_10818(class_2713.class_2714.class);
		this.field_12413 = arg.readBoolean();
		this.field_12412 = arg.readBoolean();
		this.field_12411 = arg.readBoolean();
		this.field_12410 = arg.readBoolean();
		int i = arg.method_10816();
		this.field_12414 = Lists.<class_2960>newArrayList();

		for (int j = 0; j < i; j++) {
			this.field_12414.add(arg.method_10810());
		}

		if (this.field_12408 == class_2713.class_2714.field_12416) {
			i = arg.method_10816();
			this.field_12409 = Lists.<class_2960>newArrayList();

			for (int j = 0; j < i; j++) {
				this.field_12409.add(arg.method_10810());
			}
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_12408);
		arg.writeBoolean(this.field_12413);
		arg.writeBoolean(this.field_12412);
		arg.writeBoolean(this.field_12411);
		arg.writeBoolean(this.field_12410);
		arg.method_10804(this.field_12414.size());

		for (class_2960 lv : this.field_12414) {
			arg.method_10812(lv);
		}

		if (this.field_12408 == class_2713.class_2714.field_12416) {
			arg.method_10804(this.field_12409.size());

			for (class_2960 lv : this.field_12409) {
				arg.method_10812(lv);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public List<class_2960> method_11750() {
		return this.field_12414;
	}

	@Environment(EnvType.CLIENT)
	public List<class_2960> method_11757() {
		return this.field_12409;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11754() {
		return this.field_12413;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11752() {
		return this.field_12412;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11755() {
		return this.field_12411;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11756() {
		return this.field_12410;
	}

	@Environment(EnvType.CLIENT)
	public class_2713.class_2714 method_11751() {
		return this.field_12408;
	}

	public static enum class_2714 {
		field_12416,
		field_12415,
		field_12417;
	}
}
