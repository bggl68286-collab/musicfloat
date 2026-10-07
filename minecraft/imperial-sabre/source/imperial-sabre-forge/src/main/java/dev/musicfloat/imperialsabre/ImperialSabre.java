package dev.musicfloat.imperialsabre;

import dev.musicfloat.imperialsabre.item.ImperialSabreItem;
import dev.musicfloat.imperialsabre.network.SabreNetwork;
import dev.musicfloat.imperialsabre.particle.ImperialParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ImperialSabre.MOD_ID)
public final class ImperialSabre {
    public static final String MOD_ID = "imperial_sabre";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> SABRE = ITEMS.register("eagle_sabre", ImperialSabreItem::new);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);
    public static final RegistryObject<ParticleType<ImperialParticleOptions>> IMPERIAL_FX = PARTICLES.register("imperial_fx",
        () -> new ParticleType<>(false, ImperialParticleOptions.DESERIALIZER) {
            @Override public com.mojang.serialization.Codec<ImperialParticleOptions> codec() {
                return ImperialParticleOptions.CODEC;
            }
        });
    public ImperialSabre() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        PARTICLES.register(bus);
        bus.addListener(this::creativeTab);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SabreConfig.SPEC);
        SabreNetwork.register();
    }
    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) event.accept(SABRE);
    }
}
