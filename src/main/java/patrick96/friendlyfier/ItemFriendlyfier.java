package patrick96.friendlyfier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import static patrick96.friendlyfier.Friendlyfier.MODID;

public class ItemFriendlyfier extends Item {

    public ItemFriendlyfier() {
        super();
        setFull3D();
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName(MODID + ".friendlyfier");
        setTextureName(MODID + ":friendlyfier");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if(target instanceof EntityCreature && target.isCreatureType(EnumCreatureType.monster, false)) {
            if(Utils.friendlyfy((EntityCreature) target)) {
                player.swingItem();
                // TODO config message with parameters for name, and nametag, health,

                EntityCreature entity = (EntityCreature) target;
                String name;

                if(entity.hasCustomNameTag()) {
                    name = entity.getCustomNameTag() + " (" + Utils.getOriginalName(entity) + ")";
                }
                else {
                    name = Utils.getOriginalName(entity);
                }

                player.addChatMessage(new ChatComponentText(name + " is now friendly!"));
                if(!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }
            }
        }

        return super.itemInteractionForEntity(stack, player, target);
    }

}
