# The 1.7.10 Patch
Attempt to fix some exploits in The 1.7.10 Pack technic modpack.
# Current patches:
* BiblioAtlasGive, BiblioFrameGive and BiblioTableGive patched by the ModMixins authors.
* WIP BuildMarkerGive fix, just ignores the packet as I can't see what purpose it has
* MrCrayfish's Furniture Mod CrayfishGive (temporary fix, disabled packages)
* MrCrayfish's Furniture Mod block destruction exploit (disabled packet)
*  Ender IO EIOTP Fix, checks power usage/range/holding the teleportation staff or on a teleport anchor
* GalacticFire fix, disable packet
* Mekanism Digital Miner / Personal Chest / Machine security fix, security was checked on the client for some reason?
* Tinkers' Construct TinkerChest, check that the player has a knapsack in their inventory before opening it
* Tinkers' Construct TinkerGive, check that the stencil table output item is a pattern and the input item is a blank pattern, rather than any input/output
* Thermal Expansion, disable caches (temporary, you could use Mekanism bins in the meantime)

And more to come.