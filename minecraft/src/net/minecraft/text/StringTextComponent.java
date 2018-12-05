package net.minecraft.text;

public class StringTextComponent extends AbstractTextComponent {
	private final String text;

	public StringTextComponent(String string) {
		this.text = string;
	}

	public String getTextField() {
		return this.text;
	}

	@Override
	public String getText() {
		return this.text;
	}

	public StringTextComponent getTextComponent() {
		return new StringTextComponent(this.text);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof StringTextComponent)) {
			return false;
		} else {
			StringTextComponent stringTextComponent = (StringTextComponent)object;
			return this.text.equals(stringTextComponent.getTextField()) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.children + ", style=" + this.getStyle() + '}';
	}
}
