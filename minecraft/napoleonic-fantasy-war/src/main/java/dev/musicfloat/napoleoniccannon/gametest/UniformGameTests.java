package dev.musicfloat.napoleonicfantasy.gametest;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(NapoleonicFantasy.MOD_ID)
@PrefixGameTestTemplate(false)
public final class UniformGameTests {
    @GameTest(template="empty", timeoutTicks=20)
    public static void allFourPiecesRegisterAsWearable(GameTestHelper helper) {
        ItemStack shako = new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_SHAKO.get());
        ItemStack jacket = new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_JACKET.get());
        ItemStack breeches = new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_BREECHES.get());
        ItemStack boots = new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_BOOTS.get());
        helper.assertTrue(shako.getItem().canEquip(shako, EquipmentSlot.HEAD, null), "Shako must target the head slot");
        helper.assertTrue(jacket.getItem().canEquip(jacket, EquipmentSlot.CHEST, null), "Jacket must target the chest slot");
        helper.assertTrue(breeches.getItem().canEquip(breeches, EquipmentSlot.LEGS, null), "Breeches must target the legs slot");
        helper.assertTrue(boots.getItem().canEquip(boots, EquipmentSlot.FEET, null), "Boots must target the feet slot");
        helper.succeed();
    }

    @GameTest(template="empty", timeoutTicks=20)
    public static void uniformEquipsToPlayerSlots(GameTestHelper helper) {
        Player player = FakePlayerFactory.getMinecraft(helper.getLevel());
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_SHAKO.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_JACKET.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_BREECHES.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(NapoleonicFantasy.SKULL_HUSSAR_BOOTS.get()));
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).is(NapoleonicFantasy.SKULL_HUSSAR_SHAKO.get()), "Shako did not remain equipped");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.CHEST).is(NapoleonicFantasy.SKULL_HUSSAR_JACKET.get()), "Jacket did not remain equipped");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.LEGS).is(NapoleonicFantasy.SKULL_HUSSAR_BREECHES.get()), "Breeches did not remain equipped");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.FEET).is(NapoleonicFantasy.SKULL_HUSSAR_BOOTS.get()), "Boots did not remain equipped");
        helper.succeed();
    }
}
