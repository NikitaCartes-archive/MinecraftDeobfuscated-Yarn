package net.minecraft.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class BaseText implements Text {
	protected final List<Text> siblings = Lists.<Text>newArrayList();
	private Style field_11730;

	@Override
	public Text append(Text text) {
		text.method_10866().setParent(this.method_10866());
		this.siblings.add(text);
		return this;
	}

	@Override
	public List<Text> getSiblings() {
		return this.siblings;
	}

	@Override
	public Text method_10862(Style style) {
		this.field_11730 = style;

		for (Text text : this.siblings) {
			text.method_10866().setParent(this.method_10866());
		}

		return this;
	}

	@Override
	public Style method_10866() {
		if (this.field_11730 == null) {
			this.field_11730 = new Style();

			for (Text text : this.siblings) {
				text.method_10866().setParent(this.field_11730);
			}
		}

		return this.field_11730;
	}

	@Override
	public Stream<Text> stream() {
		return Streams.concat(Stream.of(this), this.siblings.stream().flatMap(Text::stream));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof BaseText)) {
			return false;
		} else {
			BaseText baseText = (BaseText)object;
			return this.siblings.equals(baseText.siblings) && this.method_10866().equals(baseText.method_10866());
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.method_10866(), this.siblings});
	}

	public String toString() {
		return "BaseComponent{style=" + this.field_11730 + ", siblings=" + this.siblings + '}';
	}
}
