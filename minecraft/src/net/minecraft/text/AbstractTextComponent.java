package net.minecraft.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractTextComponent implements TextComponent {
	protected List<TextComponent> children = Lists.<TextComponent>newArrayList();
	private Style style;

	@Override
	public TextComponent append(TextComponent textComponent) {
		textComponent.getStyle().setParent(this.getStyle());
		this.children.add(textComponent);
		return this;
	}

	@Override
	public List<TextComponent> getChildren() {
		return this.children;
	}

	@Override
	public TextComponent setStyle(Style style) {
		this.style = style;

		for (TextComponent textComponent : this.children) {
			textComponent.getStyle().setParent(this.getStyle());
		}

		return this;
	}

	@Override
	public Style getStyle() {
		if (this.style == null) {
			this.style = new Style();

			for (TextComponent textComponent : this.children) {
				textComponent.getStyle().setParent(this.style);
			}
		}

		return this.style;
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
			return this.children.equals(abstractTextComponent.children) && this.getStyle().equals(abstractTextComponent.getStyle());
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.getStyle(), this.children});
	}

	public String toString() {
		return "BaseComponent{style=" + this.style + ", siblings=" + this.children + '}';
	}
}
