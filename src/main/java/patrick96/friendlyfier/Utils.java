package patrick96.friendlyfier;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Utils {

    public static String prettyPrintNumber(double num) {
        return prettyPrintNumber(num, 0, true);
    }

    /**
     * Pretty prints numbers
     *
     * @param num           the number to format
     * @param decimalPlaces the amount of decimal places to be shown
     * @return the formated number as a String
     */
    public static String prettyPrintNumber(double num, int decimalPlaces, boolean trailingZeros) {
        StringBuilder format = new StringBuilder(",##0");
        if(decimalPlaces > 0) {
            format.append(".");
            for(int i = 0; i < decimalPlaces; i++) {
                format.append(trailingZeros ? "0" : "#");
            }
        }
        DecimalFormat form = new DecimalFormat(format.toString());
        return form.format(num);
    }

    public static String getOriginalName(Entity e) {
        String s = EntityList.getEntityString(e);

        if (s == null)
        {
            s = "generic";
        }

        return StatCollector.translateToLocal("entity." + s + ".name");
    }

    public static IChatComponent generateSuccessMessage(EntityLiving entity, EntityPlayer player) {
        ChatComponentText msg = new ChatComponentText("");

        String configMsg = ConfigHandler.successMessage.getString();

        // Loop over every character to avoid replacing stuff inside entity name using the @ sign
        for(int i = 0; i < configMsg.length(); i++) {
            char character = configMsg.charAt(i);

            if(character == '@') {
                IChatComponent toAdd = null;
                switch(configMsg.charAt(i + 1)) {
                    case 'n':
                            toAdd = new ChatComponentText(Utils.getOriginalName(entity));
                        break;

                    case 't':
                            if(entity.hasCustomNameTag()) {
                                toAdd = new ChatComponentText(entity.getCustomNameTag());
                            }
                        break;

                    case 'o':
                            toAdd = new ChatComponentText(entity.getCommandSenderName());
                        break;

                    case 'i':
                            toAdd = new ChatComponentText(EntityList.getEntityString(entity));
                        break;

                    case 'p':
                            toAdd = player.func_145748_c_();
                        break;

                    case 'h':
                            toAdd = new ChatComponentText("" + entity.getHealth());
                        break;

                    case 'f':
                            toAdd = new ChatComponentText(prettyPrintNumber(entity.getHealth() / 2F, 1, false));
                        break;

                    case 'u':
                            toAdd = new ChatComponentText("" + getNumFriendlyfied(player));
                        break;

                    default:
                        // If no variable is found print as normal
                        toAdd = new ChatComponentText("@" + configMsg.charAt(i + 1));
                }

                if(toAdd != null) {
                    msg.appendSibling(toAdd);
                }
                i++;
            }
            else {
                msg.appendText(character + "");
            }

        }

        return msg;
    }

    public static void log(Level l, String msg) {
        FMLLog.log(Friendlyfier.MODID, l, msg);
    }

    public static UUID getFriendlyfiedPlayer(EntityLiving entity) {
        if(entity != null && entity.getEntityData().getBoolean("friendlyfied")) {
            return UUID.fromString(entity.getEntityData().getString("friendlyfiedPlayer"));
        }

        return null;
    }

    public static int getNumFriendlyfied(EntityPlayer player) {
        List entities = player.worldObj.loadedEntityList;

        int num = 0;
        for(Object obj : entities) {
            Entity e = ((Entity) obj);
            NBTTagCompound data = e.getEntityData();
            if(e instanceof EntityLiving
                    && data.getBoolean("friendlyfied")
                    && data.hasKey("friendlyfiedPlayer")
                    && player.getPersistentID().equals(UUID.fromString(data.getString("friendlyfiedPlayer")))) {
                num++;
            }
        }

        return num;
    }

    public static boolean couldTypeBeFriendlyfied(EntityLiving entity) {
        return entity.isCreatureType(EnumCreatureType.monster, false)  && (ConfigHandler.useWhitelist.getBoolean() == Arrays.asList(ConfigHandler.blacklist.getStringList()).contains(EntityList.getEntityString(entity)));
    }

    public static boolean canFriendlyfy(EntityLiving entity, boolean onSpawn) {
        return couldTypeBeFriendlyfied(entity) && onSpawn == entity.getEntityData().getBoolean("friendlyfied");
    }

    public static boolean friendlyfy(EntityLiving entity) {
        return friendlyfy(entity, false);
    }

    public static boolean friendlyfy(EntityLiving entity, boolean onSpawn) {
        if(entity.worldObj.isRemote || !canFriendlyfy(entity, onSpawn)) {
            return false;
        }

        entity.getEntityData().setBoolean("friendlyfied", true);
        List<EntityAIBase> tasks = new ArrayList<>();
        List<EntityAIBase> targetTasks = new ArrayList<>();

        for(Object t : entity.tasks.taskEntries) {
            tasks.add(((EntityAITasks.EntityAITaskEntry) t).action);
        }

        for(Object t : entity.targetTasks.taskEntries) {
            targetTasks.add(((EntityAITasks.EntityAITaskEntry) t).action);
        }

        for(EntityAIBase b : tasks) {
            entity.tasks.removeTask(b);
        }

        for(EntityAIBase b : targetTasks) {
            entity.targetTasks.removeTask(b);
        }

        if(entity instanceof EntityCreeper && ConfigHandler.defuseCreeper.getBoolean()) {
            EntityCreeper creeper = (EntityCreeper) entity;
            try {
                creeper.setCreeperState(-1);
                Field watcher = ReflectionHelper.findField(Entity.class, "dataWatcher", "field_70180_af");
                ((DataWatcher) watcher.get(creeper)).updateObject(18, (byte) 0);
            } catch(IllegalAccessException e) {
                Utils.log(Level.ERROR, "Failed to defuse creeper. It's gonna blow!!!");
                e.printStackTrace();
            }
        }

        if(entity instanceof EntityPigZombie) {
            try {
                ReflectionHelper.findField(EntityPigZombie.class, "angerLevel", "field_70837_d").set(entity, 0);
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        entity.tasks.addTask(0, new EntityAISwimming(entity));
        entity.tasks.addTask(2, new EntityAILookIdle(entity));

        if(ConfigHandler.invulnerable.getBoolean()) {
            try {
                ReflectionHelper.findField(Entity.class, "invulnerable", "field_83001_bt").set(entity, true);
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(entity instanceof EntityCreature) {
            EntityCreature creature = ((EntityCreature) entity);
            entity.tasks.addTask(1, new EntityAIWander(creature, 1.0D));
            creature.setTarget(null);
            creature.setPathToEntity(null);
        }

        if(ConfigHandler.persistence.getBoolean()) {
            entity.func_110163_bv();
        }

        return true;
    }

}
