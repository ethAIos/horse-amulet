package br.net.ethyios.mountamulet;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(
        Registries.DATA_COMPONENT_TYPE,
        MountAmuletMod.MOD_ID
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StoredMount>> STORED_MOUNT =
        COMPONENTS.registerComponentType("stored_mount", builder -> builder
            .persistent(StoredMount.CODEC)
            .networkSynchronized(StoredMount.STREAM_CODEC)
            .cacheEncoding());

    private ModDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}
