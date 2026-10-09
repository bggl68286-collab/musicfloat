package dev.musicfloat.napoleonicfantasy.uniform;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

public final class UniformArmorItem extends ArmorItem {
    public UniformArmorItem(Type type) { super(UniformArmorMaterial.PRUSSIAN_SKULL_HUSSAR, type, new Properties().stacksTo(1)); }
    @Override public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "napoleonic_fantasy:textures/models/armor/prussian_skull_hussar_" + (slot == EquipmentSlot.LEGS ? "layer_2" : "layer_1") + ".png";
    }
    @Override public void initializeClient(Consumer<IClientItemExtensions> consumer) { consumer.accept(new UniformClientExtensions()); }
}
