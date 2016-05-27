package patrick96.friendlyfier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import static patrick96.friendlyfier.Friendlyfier.MODID;

public class ItemFriendlyfier extends Item {

    public ItemFriendlyfier() {
        super();
        setFull3D();
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("friendlyfier");
        setRegistryName(MODID, "friendlyfier");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if(target instanceof EntityCreature && target.isCreatureType(EnumCreatureType.MONSTER, false)) {
            playerIn.swingArm(hand);

            if(Utils.friendlyfy((EntityCreature) target)) {
                playerIn.addChatMessage(target.getDisplayName().appendText("(" + Utils.getOriginalName(target) + ") is now friendly!"));
                if(!playerIn.isCreative()) {
                    stack.stackSize--;
                }
            }
        }

        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }
}
