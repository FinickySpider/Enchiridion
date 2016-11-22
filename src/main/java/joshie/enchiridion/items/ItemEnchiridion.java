package joshie.enchiridion.items;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.lib.GuiIDs;
import joshie.enchiridion.library.LibraryHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.RESET;

@Optional.Interface(modid = "guideapi", iface = "amerifrance.guideapi.api.IGuideItem")
public class ItemEnchiridion extends Item /*implements IGuideItem*/ {
    public static final String IS_LIBRARY = "IsELibrary";
    public ItemEnchiridion() {
        setHasSubtypes(true);
    }

    /*@Optional.Method(modid = "guideapi")
    @Override
    public Book getBook(ItemStack stack) {
        return null;
    }*/

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || oldStack.getItemDamage() != newStack.getItemDamage();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.getItemDamage() == 1 ? 1 : 16;
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        if (stack.getItemDamage() == 1) {
            return TextFormatting.GOLD + Enchiridion.translate("library");
        }

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            return Enchiridion.format("new", DARK_GREEN, RESET);
        }

        IBook book = BookRegistry.INSTANCE.getBook(stack);
        return book == null ? Enchiridion.format("new", DARK_GREEN, RESET) : book.getDisplayName();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack.getItemDamage() == 1) {
            ItemStack internal = LibraryHelper.getLibraryContents(playerIn).getCurrentBookItem();
            if (!internal.isEmpty() && (stack.getItem() != this || (stack.getItem() == this && getDamage(stack) == 0))) {
                tooltip.addAll(internal.getTooltip(playerIn, advanced));
            }
        } else {
            IBook book = BookRegistry.INSTANCE.getBook(stack);
            if (book != null) {
                book.addInformation(tooltip);
            }
        }
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == 1) {
            if (player.isSneaking()) player.openGui(Enchiridion.instance, GuiIDs.LIBRARY, world, 0, hand.ordinal(), 0);
            else {
                /* int currentBook = LibraryHelper.getLibraryContents(player).getCurrentBook();
                ItemStack book = LibraryHelper.getLibraryContents(player).getStackInSlot(currentBook);
                if (!book.isEmpty()) {
                    IBookHandler handler = EnchiridionAPI.library.getBookHandlerForStack(book);
                    if (handler != null) {
                        handler.handle(book, player, hand, currentBook, player.isSneaking());
                    }
                } else */player.openGui(Enchiridion.instance, GuiIDs.LIBRARY, world, 0, hand.ordinal(), 0);
            }

        } else player.openGui(Enchiridion.instance, GuiIDs.BOOK, world, 0, hand.ordinal(), 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        if (EConfig.libraryAsItem) list.add(new ItemStack(item, 1, 1));

        list.add(new ItemStack(item));

        for (String book : BookRegistry.INSTANCE.getUniqueNames()) {
            ItemStack stack = new ItemStack(item);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString("identifier", book);
            list.add(stack);
        }
    }

    @Override
    @Nonnull
    public Item setUnlocalizedName(@Nonnull String unlocalizedName) {
        super.setUnlocalizedName(unlocalizedName);
        setRegistryName(unlocalizedName);
        GameRegistry.register(this);
        return this;
    }
}