package net.minecraft.network.chat;

public class TextComponent extends BaseComponent {
	private final String text;

	public TextComponent(String string) {
		this.text = string;
	}

	public String getTextField() {
		return this.text;
	}

	@Override
	public String getText() {
		return this.text;
	}

	public TextComponent method_10992() {
		return new TextComponent(this.text);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof TextComponent)) {
			return false;
		} else {
			TextComponent textComponent = (TextComponent)object;
			return this.text.equals(textComponent.getTextField()) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}
}
