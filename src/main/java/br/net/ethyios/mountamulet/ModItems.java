package br.net.ethyios.mountamulet;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MountAmuletMod.MOD_ID);

    public static final DeferredItem<MountAmuletItem> MOUNT_AMULET = ITEMS.register(
        "mount_amulet",
        () -> new MountAmuletItem(new Item.Properties().stacksTo(1))
    );

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
