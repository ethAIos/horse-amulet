package br.net.ethyios.mountamulet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record StoredMount(ResourceLocation entityTypeId, CompoundTag entityTag) {
    public static final Codec<StoredMount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("entity_type").forGetter(StoredMount::entityTypeId),
        CompoundTag.CODEC.fieldOf("entity").forGetter(StoredMount::entityTag)
    ).apply(instance, StoredMount::from));

    public static final StreamCodec<RegistryFriendlyByteBuf, StoredMount> STREAM_CODEC =
        ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public StoredMount {
        if (entityTypeId == null) {
            throw malformed("null", "non-null ResourceLocation");
        }

        if (entityTag == null || entityTag.isEmpty()) {
            throw malformed(String.valueOf(entityTag), "non-empty CompoundTag");
        }

        entityTag = entityTag.copy();
    }

    public static StoredMount from(ResourceLocation entityTypeId, CompoundTag entityTag) {
        return new StoredMount(entityTypeId, entityTag);
    }

    @Override
    public CompoundTag entityTag() {
        return entityTag.copy();
    }

    public CompoundTag entityTagWithId() {
        CompoundTag loadTag = this.entityTag();
        loadTag.putString("id", this.entityTypeId().toString());
        return loadTag;
    }

    private static IllegalArgumentException malformed(String offendingValue, String expectedShape) {
        return new IllegalArgumentException(
            "Invalid stored mount value " + offendingValue + "; expected " + expectedShape
        );
    }
}
