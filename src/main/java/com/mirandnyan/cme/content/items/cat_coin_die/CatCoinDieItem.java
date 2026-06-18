package com.mirandnyan.cme.content.items.cat_coin_die;

import com.mirandnyan.cme.CMEDataComponents;
import com.mirandnyan.cme.recipes.coin_minting.CatCoinDieMintingRecipe;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemRenderer;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.mixin.accessor.LivingEntityAccessor;
import net.createmod.catnip.data.TriState;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CatCoinDieItem extends Item implements CustomUseEffectsItem {
    // TODO: change item component over to custom one (just for ingame readability / future proofing)

    public CatCoinDieItem(Properties properties) {
        super(properties.durability(128));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        InteractionResultHolder<ItemStack> FAIL = new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);

        if (itemstack.has(CMEDataComponents.COIN_MINTING_ITEM)) {
            playerIn.startUsingItem(handIn);
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        }

        InteractionHand otherHand =
                handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack itemInOtherHand = playerIn.getItemInHand(otherHand);
        if (CatCoinDieMintingRecipe.canMint(worldIn, itemInOtherHand)) {
            ItemStack item = itemInOtherHand.copy();
            ItemStack toMint = item.split(1);
            playerIn.startUsingItem(handIn);
            itemstack.set(CMEDataComponents.COIN_MINTING_ITEM, new CoinMintingItemComponent(toMint));
            playerIn.setItemInHand(otherHand, item);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }

        BlockHitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.NONE);
        Vec3 hitVec = raytraceresult.getLocation();

        AABB bb = new AABB(hitVec, hitVec).inflate(1f);
        ItemEntity pickUp = null;
        for (ItemEntity itemEntity : worldIn.getEntitiesOfClass(ItemEntity.class, bb)) {
            if (!itemEntity.isAlive())
                continue;
            if (itemEntity.position()
                    .distanceTo(playerIn.position()) > 3)
                continue;
            ItemStack stack = itemEntity.getItem();
            if (!CatCoinDieMintingRecipe.canMint(worldIn, stack))
                continue;
            pickUp = itemEntity;
            break;
        }

        if (pickUp == null)
            return FAIL;

        ItemStack item = pickUp.getItem()
                .copy();
        ItemStack toPolish = item.split(1);

        playerIn.startUsingItem(handIn);

        if (!worldIn.isClientSide) {
            itemstack.set(CMEDataComponents.COIN_MINTING_ITEM, new CoinMintingItemComponent(toPolish));
            if (item.isEmpty())
                pickUp.discard();
            else
                pickUp.setItem(item);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving) {
        if (!(entityLiving instanceof Player player))
            return stack;
        if (stack.has(CMEDataComponents.COIN_MINTING_ITEM)) {
            @SuppressWarnings("DataFlowIssue") // can't be null, has guard above
            ItemStack toMint = stack.get(CMEDataComponents.COIN_MINTING_ITEM).item();
            ItemStack minted = CatCoinDieMintingRecipe.applyMint(level, toMint);

            if (level.isClientSide) {
                spawnParticles(entityLiving.getEyePosition(1)
                        .add(entityLiving.getLookAngle().scale(.5f)), toMint, level);
                return stack;
            }

            Inventory playerInv = player.getInventory();
            if (!minted.isEmpty()) {
                playerInv.placeItemBackInInventory(minted);
            }

            if (toMint.hasCraftingRemainingItem()) {
                playerInv.placeItemBackInInventory(toMint.getCraftingRemainingItem());
            }

            stack.remove(CMEDataComponents.COIN_MINTING_ITEM);
            stack.hurtAndBreak(1, entityLiving, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
        }

        return stack;
    }

    public static void spawnParticles(Vec3 location, ItemStack polishedStack, Level world) {
        // TODO: maybe fix particles
        for (int i = 0; i < 20; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, world.random, 1 / 8f);
            world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, polishedStack), location.x, location.y,
                    location.z, motion.x, motion.y, motion.z);
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player))
            return;
        if (stack.has(CMEDataComponents.COIN_MINTING_ITEM)) {
            @SuppressWarnings("DataFlowIssue") // can't be null, has guard above
            ItemStack toMint = stack.get(CMEDataComponents.COIN_MINTING_ITEM).item();
            player.getInventory().placeItemBackInInventory(toMint);
            stack.remove(CMEDataComponents.COIN_MINTING_ITEM);
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        // TODO sounds
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        BlockState newState = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false);
        if (newState != null) {
            AllSoundEvents.SANDING_LONG.play(level, player, pos, 1, 1 + (level.random.nextFloat() * 0.5f - 1f) / 5f);
            level.levelEvent(player, LevelEvent.PARTICLES_SCRAPE, pos, 0); // Spawn particles
        } else {
            newState = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false);
            if (newState != null) {
                AllSoundEvents.SANDING_LONG.play(level, player, pos, 1,
                        1 + (level.random.nextFloat() * 0.5f - 1f) / 5f);
                level.levelEvent(player, LevelEvent.PARTICLES_WAX_OFF, pos, 0); // Spawn particles
            }
        }

        if (newState != null) {
            level.setBlockAndUpdate(pos, newState);
            if (player != null)
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        // TODO
        return itemAbility == ItemAbilities.AXE_SCRAPE || itemAbility == ItemAbilities.AXE_WAX_OFF;
    }

    @Override
    public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        // Trigger every tick so that we have more fine grain control over the animation
        return TriState.TRUE;
    }

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        if (stack.has(CMEDataComponents.COIN_MINTING_ITEM)) {
            @SuppressWarnings("DataFlowIssue") // can't be null, has guard above
            ItemStack polishing = stack.get(CMEDataComponents.COIN_MINTING_ITEM).item();
            if (!polishing.isEmpty())
                ((LivingEntityAccessor) entity).create$callSpawnItemParticles(polishing, 1);
        }

        // After 6 ticks play the sound every 7th
        if ((entity.getTicksUsingItem() - 6) % 7 == 0)
            entity.playSound(entity.getEatingSound(stack), 0.9F + 0.2F * random.nextFloat(),
                    random.nextFloat() * 0.2F + 0.9F);

        return true;
    }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return AllSoundEvents.SANDING_SHORT.getMainEvent(); // TODO some anvil sound maybe
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 32;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @SuppressWarnings("removal")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SandPaperItemRenderer()));
    }
}
