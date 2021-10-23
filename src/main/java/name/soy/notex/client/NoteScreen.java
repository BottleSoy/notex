package name.soy.notex.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;

public class NoteScreen extends Screen {
	public NoteScreen(NoteInfo info) {
		super(NarratorManager.EMPTY);

	}

	@Override
	protected void init() {

	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
