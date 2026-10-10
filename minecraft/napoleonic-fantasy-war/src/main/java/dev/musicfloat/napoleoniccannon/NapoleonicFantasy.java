package dev.musicfloat.napoleonicfantasy;

import dev.musicfloat.napoleonicfantasy.item.HandCannonItem;
import dev.musicfloat.napoleonicfantasy.entity.Cannonball;
import dev.musicfloat.napoleonicfantasy.network.CannonNetwork;
import dev.musicfloat.napoleonicfantasy.particle.CannonParticleOptions;
import dev.musicfloat.napoleonicfantasy.saber.item.ImperialSabreItem;
import dev.musicfloat.napoleonicfantasy.saber.network.SabreNetwork;
import dev.musicfloat.napoleonicfantasy.saber.particle.ImperialParticleOptions;
import dev.musicfloat.napoleonicfantasy.uniform.UniformArmorItem;
import dev.musicfloat.napoleonicfantasy.musket.item.CharlevilleMusketItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.*;
import java.util.*;

@Mod(NapoleonicFantasy.MOD_ID)
public final class NapoleonicFantasy {
    public static final String MOD_ID="napoleonic_fantasy";
    public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,MOD_ID);
    public static final RegistryObject<Item> CANNON=ITEMS.register("hand_cannon",HandCannonItem::new);
    public static final RegistryObject<Item> ROUND_SHOT=ITEMS.register("round_shot",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> CANISTER_SHOT=ITEMS.register("canister_shot",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> SABRE=ITEMS.register("eagle_sabre",ImperialSabreItem::new);
    public static final RegistryObject<Item> MUSKET=ITEMS.register("charleville_1777",CharlevilleMusketItem::new);
    public static final RegistryObject<Item> SKULL_HUSSAR_SHAKO=ITEMS.register("prussian_skull_hussar_shako",()->new UniformArmorItem(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> SKULL_HUSSAR_JACKET=ITEMS.register("prussian_skull_hussar_jacket",()->new UniformArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> SKULL_HUSSAR_BREECHES=ITEMS.register("prussian_skull_hussar_breeches",()->new UniformArmorItem(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> SKULL_HUSSAR_BOOTS=ITEMS.register("prussian_skull_hussar_boots",()->new UniformArmorItem(ArmorItem.Type.BOOTS));
    public static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,MOD_ID);
    public static final RegistryObject<EntityType<Cannonball>> BALL=ENTITIES.register("cannonball",()->EntityType.Builder.<Cannonball>of(Cannonball::new,MobCategory.MISC).sized(.24f,.24f).clientTrackingRange(96).updateInterval(1).build(MOD_ID+":cannonball"));
    public static final DeferredRegister<ParticleType<?>> PARTICLES=DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,MOD_ID);
    public static final RegistryObject<ParticleType<CannonParticleOptions>> FX=PARTICLES.register("cannon_fx",()->new ParticleType<>(false,CannonParticleOptions.DESERIALIZER){
        @Override public com.mojang.serialization.Codec<CannonParticleOptions> codec(){return CannonParticleOptions.CODEC;}
    });
    public static final RegistryObject<ParticleType<ImperialParticleOptions>> IMPERIAL_FX=PARTICLES.register("imperial_fx",()->new ParticleType<>(false,ImperialParticleOptions.DESERIALIZER){
        @Override public com.mojang.serialization.Codec<ImperialParticleOptions> codec(){return ImperialParticleOptions.CODEC;}
    });
    public static final DeferredRegister<SoundEvent> SOUNDS=DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,MOD_ID);
    public static final Map<String,RegistryObject<SoundEvent>> SOUND=new LinkedHashMap<>();
    static {for(String name:List.of("sponge","powder","insert","ram","aim","ignite","report","return","impact","cartridge","ramrod","flintlock","musket_report","bayonet_mount","bayonet_thrust"))SOUND.put(name,SOUNDS.register(name,()->SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID,name))));}
    public NapoleonicFantasy(){
        var bus=FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);ENTITIES.register(bus);PARTICLES.register(bus);SOUNDS.register(bus);
        bus.addListener(this::tab);ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,CannonConfig.SPEC);CannonNetwork.register();SabreNetwork.register();
    }
    private void tab(BuildCreativeModeTabContentsEvent e){if(e.getTabKey()==CreativeModeTabs.COMBAT){e.accept(CANNON);e.accept(ROUND_SHOT);e.accept(CANISTER_SHOT);e.accept(SABRE);e.accept(MUSKET);} if(e.getTabKey()==CreativeModeTabs.COMBAT || e.getTabKey()==CreativeModeTabs.INGREDIENTS){e.accept(SKULL_HUSSAR_SHAKO);e.accept(SKULL_HUSSAR_JACKET);e.accept(SKULL_HUSSAR_BREECHES);e.accept(SKULL_HUSSAR_BOOTS);}}
}
