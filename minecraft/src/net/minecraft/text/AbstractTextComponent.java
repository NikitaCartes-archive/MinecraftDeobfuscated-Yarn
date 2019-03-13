package net.minecraft.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractTextComponent implements TextComponent {
	protected final List<TextComponent> children = Lists.<TextComponent>newArrayList();
	private Style field_11730;

	@Override
	public TextComponent append(TextComponent textComponent) {
		textComponent.method_10866().setParent(this.method_10866());
		this.children.add(textComponent);
		return this;
	}

	@Override
	public List<TextComponent> getChildren() {
		return this.children;
	}

	@Override
	public TextComponent method_10862(Style style) {
		this.field_11730 = style;

		for (TextComponent textComponent : this.children) {
			textComponent.method_10866().setParent(this.method_10866());
		}

		return this;
	}

	@Override
	public Style method_10866() {
		if (this.field_11730 == null) {
			this.field_11730 = new Style();

			for (TextComponent textComponent : this.children) {
				textComponent.method_10866().setParent(this.field_11730);
			}
		}

		return this.field_11730;
	}

	@Override
	public Stream<TextComponent> stream() {
		return Streams.concat(Stream.of(this), this.children.stream().flatMap(TextComponent::stream));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof AbstractTextComponent)) {
			return false;
		} else {
			AbstractTextComponent abstractTextComponent = (AbstractTextComponent)object;
			return this.children.equals(abstractTextComponent.children) && this.method_10866().equals(abstractTextComponent.method_10866());
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.method_10866(), this.children});
	}

	public String toString() {
		return "BaseComponent{style=" + this.field_11730 + ", siblings=" + this.children + '}';
	}
}
