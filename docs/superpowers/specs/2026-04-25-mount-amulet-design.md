# Mount Amulet Design

## Goal

Build a NeoForge 1.21.1 mod named `mount_amulet`.

The mod adds a craftable reusable amulet that stores an owned or tamed mount by
right-clicking it. A filled amulet releases the same mount later, preserving
its saved characteristics and equipped items.

## Approved Approach

Use entity saved data on an item data component.

The amulet `ItemStack` stores a typed component with the captured entity type id
and the mount's persisted entity data. This matches Minecraft 1.21.1 item data
storage, avoids world-level lookup tables, and keeps filled amulet data attached
to the item through inventory moves, drops, and saves.

## Scope

The first version targets broad vanilla mounts with ownership or tame state:

- horses
- donkeys
- mules
- llamas and trader llamas
- camels
- skeleton and zombie horses only when vanilla saved data exposes ownership;
  otherwise they are rejected like unowned mounts

The amulet must not capture arbitrary vehicles, hostile entities, or mounts the
player does not own. Capture and release logic runs on the server.

## Recipe

The amulet is a shaped crafting recipe:

```text
_ S _
S _ S
_ L _
```

`S` is `minecraft:string`. `L` is `minecraft:leather`. Blank slots must stay
empty.

## Components

### `MountAmuletMod`

Main mod entry point. Owns mod id, registers items, data components, and creative
tab placement.

### `ModItems`

Registers the `mount_amulet` item. Keeps item registration separate from the mod
entry point so future items do not grow the main class.

### `ModDataComponents`

Registers the stored mount data component. The component value is `StoredMount`
and supplies both disk and network codecs.

### `StoredMount`

Record containing:

- `ResourceLocation entityTypeId`
- `CompoundTag entityTag`

It validates malformed input with exception messages that include the offending
value and expected shape.

### `MountOwnership`

Answers whether a player may capture a mount. It accepts only tame or owned
vanilla mount entities and rejects entities whose owner does not match the
player.

### `MountCapture`

Serializes a mount into `StoredMount`, writes it to the amulet stack, and removes
the live entity after successful storage. It avoids client-side mutation.

### `MountRelease`

Reads `StoredMount`, recreates the entity in a target position, adds it to the
server level, then clears the component from the amulet. It refuses release when
stored data cannot create a valid entity.

### `MountAmuletItem`

Coordinates player interactions:

- empty amulet + valid owned mount: capture
- filled amulet + block right-click: release above clicked block
- filled amulet + air right-click: release in front of player
- invalid interaction: fail without changing the stack

## Data Flow

Capture:

1. Player right-clicks a candidate mount with an empty amulet.
2. Server verifies ownership through `MountOwnership`.
3. `MountCapture` stores entity type and persisted entity tag.
4. Live mount is removed only after the amulet stack receives the component.

Release:

1. Player right-clicks with a filled amulet.
2. `MountRelease` chooses clicked block position or air target in front of the
   player.
3. Server recreates the entity from the stored entity type and tag.
4. Entity is placed in the world and the amulet becomes empty again.

## Errors And Player Feedback

Invalid actions do not consume or overwrite the amulet. Player-facing messages
are plain text and concise:

- mount is not supported
- mount is not owned by this player
- amulet already contains a mount
- stored mount data is invalid
- release position is blocked

Debug or diagnostic logging, if added, uses structured JSON-style fields.

## Tests

Use focused tests through the project Gradle test or game test setup:

- ownership accepts owned/tamed mounts and rejects unowned mounts
- stored mount component round trips entity type id and tag
- invalid stored data fails with useful exception messages
- recipe JSON matches the approved shape and ingredients

Final verification command is the Gradle build command for this project.

## Out Of Scope

- custom model or texture beyond a basic item asset
- config file for capture rules
- cross-mod mount support beyond vanilla entity inheritance
- storing passengers
- storing mounts while ridden
- releasing into occupied spaces
