package dev.musicfloat.napoleonicfantasy.uniform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class UniformClientExtensions implements IClientItemExtensions {
    private SkullHussarArmorModel model;
    @Override public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
        if (model == null) model = new SkullHussarArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(SkullHussarArmorModel.LAYER));
        return model;
    }
}
