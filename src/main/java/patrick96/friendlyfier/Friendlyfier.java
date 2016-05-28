package patrick96.friendlyfier;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.logging.log4j.Level;

import java.io.File;

@Mod(modid = Friendlyfier.MODID, version = Friendlyfier.VERSION)
public class Friendlyfier
{
    public static final String MODID = "friendlyfier";
    public static final String VERSION = "@VERSION@";

    @SidedProxy(clientSide = "patrick96.friendlyfier.ClientProxy", serverSide = "patrick96.friendlyfier.ServerProxy")
    public static CommonProxy proxy;

    public final static Item itemFriendlyfier = new ItemFriendlyfier();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(new File(event.getModConfigurationDirectory(), MODID + ".cfg"));

        proxy.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
        GameRegistry.registerItem(itemFriendlyfier, "friendlyfier");

        GameRegistry.addRecipe(new ItemStack(itemFriendlyfier, 2), " AB", " CA", "D  ",
                'A', Items.gold_nugget,
                'B', Items.lead,
                'C', Items.stick,
                'D', Items.bone);

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        Entity entity = event.entity;
        if(entity instanceof EntityLiving) {
            if(Utils.friendlyfy(((EntityLiving) entity), true)) {

                EntityLiving creature = (EntityLiving) entity;
                String name;

                if(creature.hasCustomNameTag()) {
                    name = creature.getCustomNameTag() + " (" + Utils.getOriginalName(creature) + ")";
                }
                else {
                    name = Utils.getOriginalName(creature);
                }
                Utils.log(Level.INFO, "Friendlyfied " + name + " on world join");
            }
        }
    }
}
