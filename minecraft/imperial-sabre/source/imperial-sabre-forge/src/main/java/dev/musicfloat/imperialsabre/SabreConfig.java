package dev.musicfloat.imperialsabre;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SabreConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue WAVE_DAMAGE, NOVA_DAMAGE, WAVE_RANGE, NOVA_RADIUS;
    public static final ForgeConfigSpec.IntValue WAVE_COOLDOWN, NOVA_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue ABILITIES_DAMAGE_PLAYERS;
    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("combat");
        WAVE_DAMAGE = b.defineInRange("waveDamage", 36.0, 1.0, 500.0);
        NOVA_DAMAGE = b.defineInRange("novaDamage", 90.0, 1.0, 1000.0);
        WAVE_RANGE = b.defineInRange("waveRange", 18.0, 3.0, 32.0);
        NOVA_RADIUS = b.defineInRange("novaRadius", 12.0, 3.0, 24.0);
        WAVE_COOLDOWN = b.defineInRange("waveCooldownTicks", 60, 10, 1200);
        NOVA_COOLDOWN = b.defineInRange("novaCooldownTicks", 160, 20, 2400);
        ABILITIES_DAMAGE_PLAYERS = b.comment("Area skills exclude players by default; ordinary sword hits follow vanilla PvP rules.")
            .define("abilitiesDamagePlayers", false);
        b.pop(); SPEC = b.build();
    }
    private SabreConfig() {}
}
