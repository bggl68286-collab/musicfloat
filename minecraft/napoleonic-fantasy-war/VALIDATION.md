# Validation

- `gradle compileJava`: passed.
- `gradle runGameTestServer`: passed, **13 required tests**.
- `gradle runClient`: loaded one mod (`napoleonic_fantasy`) with cannon, sabre and armor resources.
- Client smoke test: equipped all four armor pieces with `/item replace`, switched to third person, and confirmed the skull shako, red collar, silver chest braids, cuffs, belt and fitted trousers render on the player.
- Blockbench source: `art/source/prussian_skull_hussar_uniform.bbmodel` parses as Blockbench 5 format and contains 18 fitted detail meshes in five groups.

The only client log warnings in the isolated smoke test were the vanilla offline OpenAL/Mojang service warnings; no mod model or resource loading errors remained after the unified namespace was applied.
