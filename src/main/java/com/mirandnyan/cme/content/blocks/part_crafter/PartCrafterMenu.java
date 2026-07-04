package com.mirandnyan.cme.content.blocks.part_crafter;

import com.google.common.collect.Lists;
import com.mirandnyan.cme.CMEBlocks;
import com.mirandnyan.cme.CMEMenus;
import com.mirandnyan.cme.CMERecipeTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PartCrafterMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex;
    private final Level level;
    private List<RecipeHolder<PartCrafterRecipe>> recipes;
    private ItemStack input;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener;
    public final Container container;
    final ResultContainer resultContainer;

    public PartCrafterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public PartCrafterMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, final ContainerLevelAccess access) {
        super(menuType, containerId);
        this.selectedRecipeIndex = DataSlot.standalone();
        this.recipes = Lists.newArrayList();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = () -> { };
        this.container = new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();
                PartCrafterMenu.this.slotsChanged(this);
                PartCrafterMenu.this.slotUpdateListener.run();
            }
        };
        this.resultContainer = new ResultContainer();
        this.access = access;
        this.level = playerInventory.player.level();
        this.inputSlot = this.addSlot(new Slot(this.container, INPUT_SLOT, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 33) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
                PartCrafterMenu.this.resultContainer.awardUsedRecipes(player, this.getRelevantItems());
                ItemStack resultStack = PartCrafterMenu.this.inputSlot.remove(1);
                if (!resultStack.isEmpty()) {
                    PartCrafterMenu.this.setupResultSlot();
                }

                access.execute((level, pos) -> {
                    long l = level.getGameTime();
                    if (PartCrafterMenu.this.lastSoundTime != l) {
                        // TODO: sound
                        level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        PartCrafterMenu.this.lastSoundTime = l;
                    }
                });
                super.onTake(player, stack);
            }

            private List<ItemStack> getRelevantItems() {
                return List.of(PartCrafterMenu.this.inputSlot.getItem());
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<RecipeHolder<PartCrafterRecipe>> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, CMEBlocks.PART_CRAFTER.get());
    }

    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (this.isValidRecipeIndex(id)) {
            this.selectedRecipeIndex.set(id);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < this.recipes.size();
    }

    public void slotsChanged(@NotNull Container inventory) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(inventory, itemstack);
        }

    }

    private static SingleRecipeInput createRecipeInput(Container container) {
        return new SingleRecipeInput(container.getItem(0));
    }

    private void setupRecipeList(Container container, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(CMERecipeTypes.PART_CRAFTING.getType(), createRecipeInput(container), this.level);
        }

    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            RecipeHolder<PartCrafterRecipe> recipeholder = this.recipes.get(this.selectedRecipeIndex.get());
            ItemStack itemstack = recipeholder.value().assemble(createRecipeInput(this.container), this.level.registryAccess());
            if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
                this.resultContainer.setRecipeUsed(recipeholder);
                this.resultSlot.set(itemstack);
            } else {
                this.resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public @NotNull MenuType<?> getType() {
        return CMEMenus.PART_CRAFTER.get();
    }

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            Item item = stack.getItem();
            resultStack = stack.copy();
            if (index == 1) {
                item.onCraftedBy(stack, player.level(), player);
                if (!this.moveItemStackTo(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, resultStack);
            } else if (index == 0) {
                if (!this.moveItemStackTo(stack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(CMERecipeTypes.PART_CRAFTING.getType(), new SingleRecipeInput(stack), this.level).isPresent()) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(stack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38 && !this.moveItemStackTo(stack, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (stack.getCount() == resultStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            this.broadcastChanges();
        }

        return resultStack;
    }

    public void removed(@NotNull Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((p_40313_, p_40314_) -> this.clearContainer(player, this.container));
    }
}
