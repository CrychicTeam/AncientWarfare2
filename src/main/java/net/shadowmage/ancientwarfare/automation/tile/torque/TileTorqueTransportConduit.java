package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

public TileTorqueTransportConduit()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_conduit_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  maxRpm = AWAutomationStatics.low_rpm_max;;
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from!=orientation;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

}
