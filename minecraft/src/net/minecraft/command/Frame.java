package net.minecraft.command;

public record Frame(int depth, ReturnValueConsumer returnValueConsumer, Frame.Control frameControl) {
	public void succeed(int returnValue) {
		this.returnValueConsumer.onSuccess(returnValue);
	}

	public void fail() {
		this.returnValueConsumer.onFailure();
	}

	public void doReturn() {
		this.frameControl.discard();
	}

	@FunctionalInterface
	public interface Control {
		void discard();
	}
}
