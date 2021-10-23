package name.soy.notex.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface MaterialAccessor {
	@Accessor
	Material getMaterial();
}
