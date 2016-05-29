package patrick96.friendlyfier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

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

        if(target instanceof EntityLiving) {
            EntityLiving entity = (EntityLiving) target;

            if(Utils.canFriendlyfy(entity, false)) {
                player.swingItem();
            }

            int num = Utils.getNumFriendlyfied(player);
            int limit = ConfigHandler.friendlyLimit.getInt();
            if((limit == 0 || limit > num) && Utils.friendlyfy((EntityLiving) target)) {

                target.getEntityData().setString("friendlyfiedPlayer", player.getPersistentID().toString());

                IChatComponent msg = Utils.generateSuccessMessage(entity, player);

                List<EntityPlayer> players = new ArrayList<>();

                if(ConfigHandler.dimensionalSuccessMessage.getBoolean()) {
                    players.addAll(player.worldObj.playerEntities);
                }
                else if(ConfigHandler.radiusSuccessMessage.getInt() > 0) {
                    int radius = ConfigHandler.radiusSuccessMessage.getInt();
                    players.addAll(player.worldObj.getEntitiesWithinAABB(EntityPlayer.class
                            , AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ)
                                    .expand(radius, radius, radius)));
                }
                else {
                    players.add(player);
                }

                for(EntityPlayer pl : players) {
                    if(pl != null) {
                        pl.addChatComponentMessage(msg);
                    }
                }

                if(!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }
            }
        }

        return super.itemInteractionForEntity(stack, player, target);
    }

}
