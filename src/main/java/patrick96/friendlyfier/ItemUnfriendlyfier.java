package patrick96.friendlyfier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.UUID;

import static patrick96.friendlyfier.Friendlyfier.MODID;

public class ItemUnfriendlyfier extends Item {

    public ItemUnfriendlyfier() {
        super();
        setFull3D();
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName(MODID + ".unfriendlyfier");
        setTextureName(MODID + ":unfriendlyfier");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if(target instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) target;

            player.swingItem();

            if(Utils.canUnfriendlyfy(entity)) {

                if(!entity.worldObj.isRemote) {
                    UUID friendlyfiedPlayer = UUID.fromString(entity.getEntityData().getString("friendlyfiedPlayer"));
                    if(player.getPersistentID().equals(friendlyfiedPlayer) || ConfigHandler.unfriendlyfyAll.getBoolean() || (ConfigHandler.unfriendlyfyOp.getBoolean() && MinecraftServer.getServer().getConfigurationManager().func_152603_m().func_152700_a(player.getGameProfile().getName()) != null)) {
                        if(Utils.unfriendlyfy((EntityLiving) target)) {
                            if(!player.capabilities.isCreativeMode) {
                                stack.stackSize--;
                            }

                            player.swingItem();
                            player.addChatComponentMessage(new ChatComponentText("Unfriend"));
                        }
                    }
                    else {
                        player.addChatComponentMessage(new ChatComponentText("You cannot unfriendlyfy an other player's friendly mob"));
                    }
                }
            }
        }

        return super.itemInteractionForEntity(stack, player, target);
    }

}
