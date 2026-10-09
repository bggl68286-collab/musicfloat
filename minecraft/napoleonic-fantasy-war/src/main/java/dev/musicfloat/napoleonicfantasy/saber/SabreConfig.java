package dev.musicfloat.napoleonicfantasy.saber;

import net.minecraftforge.common.ForgeConfigSpec;
import dev.musicfloat.napoleonicfantasy.CannonConfig;

public final class SabreConfig {
    public static final ForgeConfigSpec SPEC = CannonConfig.SPEC;
    public static final ForgeConfigSpec.DoubleValue WAVE_DAMAGE = CannonConfig.WAVE_DAMAGE, NOVA_DAMAGE = CannonConfig.NOVA_DAMAGE, WAVE_RANGE = CannonConfig.WAVE_RANGE, NOVA_RADIUS = CannonConfig.NOVA_RADIUS;
    public static final ForgeConfigSpec.IntValue WAVE_COOLDOWN = CannonConfig.WAVE_COOLDOWN, NOVA_COOLDOWN = CannonConfig.NOVA_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue ABILITIES_DAMAGE_PLAYERS = CannonConfig.ABILITIES_DAMAGE_PLAYERS;
    private SabreConfig() {}
}
