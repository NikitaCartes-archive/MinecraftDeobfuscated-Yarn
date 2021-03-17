package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_5969 {
	private final String field_29612;
	private final class_5965[] field_29613;

	public class_5969(String string, class_5965... args) {
		this.field_29612 = string;
		this.field_29613 = args;
	}

	public class_5969(String string, List<class_5965> list) {
		this.field_29612 = string;
		this.field_29613 = (class_5965[])list.toArray(new class_5965[0]);
	}

	public void method_34793() {
		for (class_5965 lv : this.field_29613) {
			lv.method_34781();
		}
	}

	public void method_34794() {
		for (class_5965 lv : this.field_29613) {
			lv.method_34780();
		}
	}

	public void method_34795() {
		for (class_5965 lv : this.field_29613) {
			lv.method_34782();
		}
	}

	public String method_34796() {
		return this.field_29612;
	}

	public List<class_5965> method_34797() {
		return ImmutableList.copyOf(this.field_29613);
	}
}
