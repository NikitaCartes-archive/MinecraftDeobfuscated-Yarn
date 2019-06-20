package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class class_2554 implements class_2561 {
	protected final List<class_2561> field_11729 = Lists.<class_2561>newArrayList();
	private class_2583 field_11730;

	@Override
	public class_2561 method_10852(class_2561 arg) {
		arg.method_10866().method_10985(this.method_10866());
		this.field_11729.add(arg);
		return this;
	}

	@Override
	public List<class_2561> method_10855() {
		return this.field_11729;
	}

	@Override
	public class_2561 method_10862(class_2583 arg) {
		this.field_11730 = arg;

		for (class_2561 lv : this.field_11729) {
			lv.method_10866().method_10985(this.method_10866());
		}

		return this;
	}

	@Override
	public class_2583 method_10866() {
		if (this.field_11730 == null) {
			this.field_11730 = new class_2583();

			for (class_2561 lv : this.field_11729) {
				lv.method_10866().method_10985(this.field_11730);
			}
		}

		return this.field_11730;
	}

	@Override
	public Stream<class_2561> method_10865() {
		return Streams.concat(Stream.of(this), this.field_11729.stream().flatMap(class_2561::method_10865));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2554)) {
			return false;
		} else {
			class_2554 lv = (class_2554)object;
			return this.field_11729.equals(lv.field_11729) && this.method_10866().equals(lv.method_10866());
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.method_10866(), this.field_11729});
	}

	public String toString() {
		return "BaseComponent{style=" + this.field_11730 + ", siblings=" + this.field_11729 + '}';
	}
}
