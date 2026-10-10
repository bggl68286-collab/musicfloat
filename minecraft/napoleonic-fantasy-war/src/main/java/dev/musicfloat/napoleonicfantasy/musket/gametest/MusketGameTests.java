package dev.musicfloat.napoleonicfantasy.musket.gametest;

import com.mojang.authlib.GameProfile;
import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.musket.combat.MusketCombat;
import dev.musicfloat.napoleonicfantasy.musket.item.CharlevilleMusketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GameTestHolder(NapoleonicFantasy.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MusketGameTests {
    private static FakePlayer player(GameTestHelper helper, String name) {
        var p = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
        var at = helper.absolutePos(new BlockPos(2, 1, 1));
        p.setPos(at.getX() + .5, at.getY(), at.getZ() + .5);
        p.setYRot(0); p.setXRot(0); p.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(NapoleonicFantasy.MUSKET.get()));
        p.getCooldowns().removeCooldown(NapoleonicFantasy.MUSKET.get());
        return p;
    }
    @GameTest(template="empty", timeoutTicks=30)
    public static void muzzleLoadSingleShotAndCooldown(GameTestHelper helper) {
        var p = player(helper, "musket_cycle");
        var stack = p.getMainHandItem();
        var item = (CharlevilleMusketItem) stack.getItem();
        item.use(helper.getLevel(), p, InteractionHand.MAIN_HAND);
        item.onUseTick(helper.getLevel(), p, stack, item.getUseDuration(stack) - CharlevilleMusketItem.LOAD_TICKS);
        helper.assertTrue(CharlevilleMusketItem.loaded(stack), "Full muzzle-loading cycle must arm the musket");
        Zombie target = helper.spawn(EntityType.ZOMBIE, new BlockPos(2, 1, 7));
        target.setNoAi(true); target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); target.setHealth(200);
        item.releaseUsing(stack, helper.getLevel(), p, item.getUseDuration(stack) - CharlevilleMusketItem.LOAD_TICKS);
        p.stopUsingItem();
        helper.assertTrue(!CharlevilleMusketItem.loaded(stack), "Firing must consume the loaded round");
        helper.assertTrue(target.getHealth() < 200, "The single ball must damage a forward target");
        helper.assertTrue(p.getCooldowns().isOnCooldown(item), "Firing must impose a cooldown");
        helper.assertTrue(stack.getDamageValue() == 1, "Firing must consume durability");
        helper.succeed();
    }
    @GameTest(template="empty", timeoutTicks=20)
    public static void bayonetToggleAndThrustState(GameTestHelper helper) {
        var p = player(helper, "musket_bayonet");
        var stack = p.getMainHandItem(); var item = (CharlevilleMusketItem) stack.getItem();
        p.setShiftKeyDown(true); item.use(helper.getLevel(), p, InteractionHand.MAIN_HAND); p.setShiftKeyDown(false);
        helper.assertTrue(CharlevilleMusketItem.bayonet(stack), "Shift-use must mount the socket bayonet");
        item.use(helper.getLevel(), p, InteractionHand.MAIN_HAND); item.releaseUsing(stack, helper.getLevel(), p, item.getUseDuration(stack) - 1);
        helper.assertTrue(p.getCooldowns().isOnCooldown(item), "Bayonet thrust must have a melee cooldown");
        helper.succeed();
    }
}
