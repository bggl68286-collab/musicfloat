package dev.musicfloat.napoleonicfantasy.uniform;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum UniformArmorMaterial implements ArmorMaterial {
    PRUSSIAN_SKULL_HUSSAR;
    private static final int[] DURABILITY = {13, 15, 16, 11};
    private static final int[] DEFENSE = {2, 5, 6, 2};
    @Override public int getDurabilityForType(ArmorItem.Type type) { return DURABILITY[type.getSlot().getIndex()]; }
    @Override public int getDefenseForType(ArmorItem.Type type) { return DEFENSE[type.getSlot().getIndex()]; }
    @Override public int getEnchantmentValue() { return 18; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_LEATHER; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.LEATHER); }
    @Override public String getName() { return NapoleonicFantasy.MOD_ID + ":prussian_skull_hussar"; }
    @Override public float getToughness() { return 1.0f; }
    @Override public float getKnockbackResistance() { return .03f; }
}
