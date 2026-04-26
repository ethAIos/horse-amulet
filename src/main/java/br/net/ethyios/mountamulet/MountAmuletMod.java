package br.net.ethyios.mountamulet;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(MountAmuletMod.MOD_ID)
public final class MountAmuletMod {
    public static final String MOD_ID = "mount_amulet";

    public MountAmuletMod(IEventBus modEventBus) {
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        modEventBus.addListener(this::addCreativeItems);
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.MOUNT_AMULET.get());
        }
    }
}
