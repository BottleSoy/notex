package name.soy.notex.mixin;

import net.minecraft.server.command.PlaySoundCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
@Mixin(PlaySoundCommand.class)
public class PlaySoundMixin {
	@ModifyConstant(method = "makeArgumentsForCategory", constant = @Constant(floatValue = 2.0F))
	private static float pitch(float prev) {
		return Float.MAX_VALUE;
	}
}
