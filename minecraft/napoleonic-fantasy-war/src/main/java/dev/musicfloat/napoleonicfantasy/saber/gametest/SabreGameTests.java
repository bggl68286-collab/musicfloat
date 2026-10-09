package dev.musicfloat.napoleonicfantasy.saber.gametest;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.saber.combat.SabreCombat;
import dev.musicfloat.napoleonicfantasy.saber.network.SabreNetwork;
import dev.musicfloat.napoleonicfantasy.saber.particle.ImperialParticleOptions;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import java.util.UUID;

@GameTestHolder(NapoleonicFantasy.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SabreGameTests {
    @GameTest(template="empty",timeoutTicks=40) public static void chargedReleaseDamagesMobAndStartsCooldown(GameTestHelper helper) {
        var player=FakePlayerFactory.getMinecraft(helper.getLevel());
        var origin=helper.absolutePos(new BlockPos(1,1,1));
        player.setPos(origin.getX()+.5,origin.getY(),origin.getZ()+.5);
        ItemStack sword=new ItemStack(NapoleonicFantasy.SABRE.get());
        player.getCooldowns().removeCooldown(sword.getItem());
        player.setItemInHand(InteractionHand.MAIN_HAND,sword);
        Zombie target=helper.spawn(EntityType.ZOMBIE,new BlockPos(3,1,3));
        target.setNoAi(true);target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);target.setHealth(200);
        sword.getItem().use(helper.getLevel(),player,InteractionHand.MAIN_HAND);
        sword.getItem().releaseUsing(sword,helper.getLevel(),player,72000-20);
        helper.assertTrue(target.getHealth()<150,"Charged release must deal the configured heavy area damage");
        helper.assertTrue(player.getCooldowns().isOnCooldown(sword.getItem()),"Charged ability must start its server cooldown");
        helper.assertTrue(sword.getDamageValue()==3,"Skill must consume durability in survival");
        player.stopUsingItem();
        helper.succeed();
    }
    @GameTest(template="empty",timeoutTicks=20) public static void registeredSwordLoadsOnServer(GameTestHelper helper) {
        ItemStack stack=new ItemStack(NapoleonicFantasy.SABRE.get());
        helper.assertTrue(!stack.isEmpty() && stack.isDamageableItem(),"Sword must register and have durability on a dedicated server");
        helper.assertTrue(stack.getAttributeModifiers(EquipmentSlot.MAINHAND).size()>=2,"Sword combat attributes missing");
        helper.succeed();
    }
    @GameTest(template="empty",timeoutTicks=20) public static void directionalWaveRejectsRearAndOutOfRange(GameTestHelper helper) {
        helper.assertTrue(SabreCombat.insideWave(new Vec3(0,0,10),new Vec3(0,0,1),18),"Forward target should be hit");
        helper.assertTrue(!SabreCombat.insideWave(new Vec3(0,0,-10),new Vec3(0,0,1),18),"Rear target should be excluded");
        helper.assertTrue(!SabreCombat.insideWave(new Vec3(0,0,19),new Vec3(0,0,1),18),"Out-of-range target should be excluded");
        helper.assertTrue(!SabreCombat.insideWave(new Vec3(10,0,0),new Vec3(0,0,1),18),"Side target should be excluded");
        helper.succeed();
    }
    @GameTest(template="empty",timeoutTicks=20) public static void effectAndParticleNetworkRoundTrip(GameTestHelper helper) {
        FriendlyByteBuf b=new FriendlyByteBuf(Unpooled.buffer());
        try {
            var effect=new SabreNetwork.Effect(UUID.randomUUID(),2,new Vec3(1,2,3),new Vec3(0,0,1),12);
            effect.encode(b);helper.assertTrue(effect.equals(SabreNetwork.Effect.decode(b)),"Effect packet lost data");
            b.clear();var particle=new ImperialParticleOptions(3,12,24,.5f);
            particle.writeToNetwork(b);
            helper.assertTrue(particle.equals(ImperialParticleOptions.DESERIALIZER.fromNetwork(NapoleonicFantasy.IMPERIAL_FX.get(),b)),"Particle options lost data");
        } finally {b.release();}
        helper.succeed();
    }
}
