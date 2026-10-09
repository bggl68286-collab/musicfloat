package dev.musicfloat.napoleonicfantasy.saber.item;

import dev.musicfloat.napoleonicfantasy.saber.client.SabreItemExtensions;
import dev.musicfloat.napoleonicfantasy.saber.combat.SabreCombat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.List;
import java.util.function.Consumer;

public final class ImperialSabreItem extends SwordItem {
    public ImperialSabreItem() {
        super(Tiers.NETHERITE, 23, -2.2f, new Item.Properties().fireResistant().rarity(Rarity.EPIC));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    // NONE lets Forge reach the custom arm pose instead of returning vanilla BLOCK early.
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.NONE; }
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
        if (level.isClientSide || !(entity instanceof Player player) || player.getCooldowns().isOnCooldown(this)) return;
        if (getUseDuration(stack) - remaining >= 20) SabreCombat.nova(player);
        else SabreCombat.wave(player);
        player.swing(player.getUsedItemHand(), true);
        if (!player.getAbilities().instabuild) stack.hurtAndBreak(3, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) SabreCombat.melee(player);
        return super.hurtEnemy(stack, target, attacker);
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.napoleonic_fantasy.wave").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.napoleonic_fantasy.nova").withStyle(ChatFormatting.YELLOW));
        lines.add(Component.translatable("tooltip.napoleonic_fantasy.style").withStyle(ChatFormatting.GRAY));
    }
    @Override public void initializeClient(Consumer<IClientItemExtensions> consumer) { consumer.accept(new SabreItemExtensions()); }
}
