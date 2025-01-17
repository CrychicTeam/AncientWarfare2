package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAmmo extends ItemBaseVehicle {
	private String tooltipName;
	private String tooltipVehicleList;

	public ItemAmmo(ResourceLocation registryName, IAmmo ammo) {
		super(registryName.getPath());
		if (!ammo.isAvailableAsItem())
			setCreativeTab(null);
		tooltipName = "item." + registryName.getPath() + ".tooltip";
		tooltipVehicleList = "item." + registryName.getPath() + ".tooltipVehicleList";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(tooltipName));
		tooltip.add(I18n.format(tooltipVehicleList));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, (i, m) -> new ModelResourceLocation(new ResourceLocation(AncientWarfareCore.MOD_ID, "vehicle/ammo"), "variant=" + getRegistryName().getPath()));
	}
}
