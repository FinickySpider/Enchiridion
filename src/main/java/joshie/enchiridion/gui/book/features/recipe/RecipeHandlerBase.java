package joshie.enchiridion.gui.book.features.recipe;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.recipe.IItemStack;
import joshie.enchiridion.api.recipe.IRecipeHandler;
import joshie.enchiridion.util.ELocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;

public abstract class RecipeHandlerBase implements IRecipeHandler {
    protected static final ResourceLocation LOCATION = new ELocation("guide_elements");
    protected NonNullList<IItemStack> stackList = NonNullList.create();
    private String unique;

    public RecipeHandlerBase() {
    }

    public void addToUnique(Object o) {
        String string = "" + o;
        if (unique == null) {
            unique = string;
        } else unique += ":" + string;
    }

    @Override
    public void addTooltip(List<String> list) {
        for (IItemStack stack : stackList) {
            if (stack == null || stack.getItemStack().isEmpty()) continue;
            if (EnchiridionAPI.draw.isMouseOverIItemStack(stack)) {
                list.addAll(stack.getItemStack().getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL));
                break; //Only permit one item to display
            }
        }
    }

    protected final Object getObject(List<Object> input, int i) {
        if (i >= input.size()) return ItemStack.EMPTY;
        input.stream().filter(o -> o instanceof ItemStack).forEach(o -> ((ItemStack) o).setCount(1));

        return input.get(i);
    }

    protected final String getMostCommonName(List<ItemStack> stacks) {
        HashMap<String, Integer> map = new HashMap<>();
        for (ItemStack stack : stacks) {
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i : ids) {
                String name = OreDictionary.getOreName(i);
                if (map.containsKey(name)) {
                    int value = map.get(name) + 1;
                    map.put(name, value);
                } else map.put(name, 1);
            }
        }

        String highest = "";
        int highest_value = 0;
        for (String string : map.keySet()) {
            int value = map.get(string);
            if (value > highest_value) {
                highest_value = value;
                highest = string;
            }
        }
        return highest;
    }

    @Override
    public String getUniqueName() {
        return unique;
    }

    @Override
    public void draw() {
        drawBackground();
        for (IItemStack stack : stackList) {
            EnchiridionAPI.draw.drawIItemStack(stack);
        }
    }

    protected abstract void drawBackground();
}