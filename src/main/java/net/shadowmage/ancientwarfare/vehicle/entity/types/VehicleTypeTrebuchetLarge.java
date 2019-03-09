package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeTrebuchetLarge extends VehicleType {
	public VehicleTypeTrebuchetLarge(int typeNum) {
		super(typeNum);
		this.configName = "trebuchet_giant";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 20;
		this.maxMissileWeight = 30.f;
		this.baseHealth = 175;
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot10);
		this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot15);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive10);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive15);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE10);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE15);
		this.validAmmoTypes.add(AmmoRegistry.ammoNapalm10);
		this.validAmmoTypes.add(AmmoRegistry.ammoNapalm15);

		this.validAmmoTypes.add(AmmoRegistry.ammoArrow);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowFlame);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowIron);
		this.validAmmoTypes.add(AmmoRegistry.ammoArrowIronFlame);

		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot30);
		this.validAmmoTypes.add(AmmoRegistry.ammoStoneShot45);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot30);
		this.validAmmoTypes.add(AmmoRegistry.ammoFireShot45);
		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot30);
		this.validAmmoTypes.add(AmmoRegistry.ammoPebbleShot45);
		this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot30);
		this.validAmmoTypes.add(AmmoRegistry.ammoClusterShot45);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive30);
		this.validAmmoTypes.add(AmmoRegistry.ammoExplosive45);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE30);
		this.validAmmoTypes.add(AmmoRegistry.ammoHE45);

		this.ammoBySoldierRank.put(0, AmmoRegistry.ammoStoneShot30);
		this.ammoBySoldierRank.put(1, AmmoRegistry.ammoStoneShot30);
		this.ammoBySoldierRank.put(2, AmmoRegistry.ammoStoneShot30);

		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validArmors.add(ArmorRegistry.armorObsidian);

		this.validUpgrades.add(UpgradeRegistry.pitchDownUpgrade);
		this.validUpgrades.add(UpgradeRegistry.pitchUpUpgrade);
		this.validUpgrades.add(UpgradeRegistry.powerUpgrade);
		this.validUpgrades.add(UpgradeRegistry.reloadUpgrade);
		this.validUpgrades.add(UpgradeRegistry.aimUpgrade);

		this.displayName = "item.vehicleSpawner.16";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.weight");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.large");

		this.pitchAdjustable = false;
		this.powerAdjustable = true;
		this.yawAdjustable = false;
		this.mountable = true;
		this.combatEngine = true;
		this.drivable = true;
		this.riderSits = false;
		this.riderMovesWithTurret = false;

		this.width = 2 * 2.5f;
		this.height = 2 * 2.5f;
		this.riderForwardsOffset = 1.425f * 2.5f;
		this.riderVerticalOffset = 0.35f;
		this.baseMissileVelocityMax = 50.f;
		this.turretVerticalOffset = (34.f + 67.5f + 24.0f) * 0.0625f * 2.5f;

		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = 0.5f;
		this.ammoBaySize = 6;
		this.armorBaySize = 6;
		this.upgradeBaySize = 6;
		this.storageBaySize = 0;
		this.accuracy = 0.85f;

		this.basePitchMax = 70.f;
		this.basePitchMin = 70.f;
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new TrebuchetLargeVarHelper(veh);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/trebuchet_1.png");
		}
	}

	public class TrebuchetLargeVarHelper extends VehicleFiringVarsHelper {
		private float armAngle = -27.f;
		private float armSpeed = 0.f;
		private float stringAngle = -64.f;
		private float stringSpeed = 0.f;

		private TrebuchetLargeVarHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setFloat("aA", this.armAngle);
			tag.setFloat("aS", armSpeed);
			tag.setFloat("sA", stringAngle);
			tag.setFloat("sS", stringSpeed);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			this.armAngle = tag.getFloat("aA");
			this.armSpeed = tag.getFloat("aS");
			this.stringAngle = tag.getFloat("sA");
			this.stringSpeed = tag.getFloat("sS");
		}

		@Override
		public void onFiringUpdate() {
			float increment = (90.f + 27.f) / 20.f;
			armAngle += increment;
			armSpeed = increment;
			stringAngle += 1.3162316f * increment;
			stringSpeed = 1.3162316f * increment;
			if (armAngle >= 90) {
				armSpeed = 0;
				armAngle = 90.f;
				stringAngle = 90.f;
				stringSpeed = 0.f;
				vehicle.firingHelper.startLaunching();
			}
		}

		@Override
		public void onReloadUpdate() {
			float increment = (90.f + 27.f) / (float) vehicle.currentReloadTicks;
			if (armAngle > -27) {
				armAngle -= increment;
				armSpeed = -increment;
				stringAngle -= 1.3162316f * increment;
				stringSpeed = -1.3162316f * increment;
			} else {
				armAngle = -27;
				armSpeed = 0;
				stringAngle = -64.f;
				stringSpeed = 0.f;
			}
		}

		@Override
		public void onLaunchingUpdate() {
			vehicle.firingHelper.spawnMissilesByWeightCount(0, 0, 0);
			vehicle.firingHelper.setFinishedLaunching();
		}

		@Override
		public void onReloadingFinished() {
			armAngle = -27;
			armSpeed = 0;
			stringAngle = -64.f;
			stringSpeed = 0.f;
		}

		@Override
		public float getVar1() {
			return armAngle;
		}

		@Override
		public float getVar2() {
			return armSpeed;
		}

		@Override
		public float getVar3() {
			return stringAngle;
		}

		@Override
		public float getVar4() {
			return stringSpeed;
		}

		@Override
		public float getVar5() {
			return 0;
		}

		@Override
		public float getVar6() {
			return 0;
		}

		@Override
		public float getVar7() {
			return 0;
		}

		@Override
		public float getVar8() {
			return 0;
		}
	}
}
