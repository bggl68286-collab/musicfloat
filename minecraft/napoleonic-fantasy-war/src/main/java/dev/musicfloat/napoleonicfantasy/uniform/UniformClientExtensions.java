package dev.musicfloat.napoleonicfantasy.uniform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.HashMap;
import java.util.Map;

public final class UniformClientExtensions implements IClientItemExtensions {
    private final Map<String, SkullHussarArmorModel> models = new HashMap<>();
    @Override public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
        // Forge's armor extension does not expose the player's skin model name here;
        // use the classic layer as the safe default and keep a slim layer available
        // for future renderer integrations.
        boolean slim = false;
        String key = slot.getName() + "_classic";
        return models.computeIfAbsent(key, ignored -> new SkullHussarArmorModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(SkullHussarArmorModel.layer(slot, slim))));
    }
}
