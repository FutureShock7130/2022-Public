package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.TurrentConstants;

public class Turret extends SubsystemBase {

    private final CANSparkMax masterFlyWheel = new CANSparkMax(TurrentConstants.kMasterFlyWheelID, MotorType.kBrushless);
    private final CANSparkMax slaveFlyWheel = new CANSparkMax(TurrentConstants.kSlaveFlyWheelID, MotorType.kBrushless); 
    private final WPI_TalonSRX spinner = new WPI_TalonSRX(TurrentConstants.kSpinnerID);

    /** Creates a new TurrentSubystem. */
    public Turret() {
        slaveFlyWheel.follow(masterFlyWheel);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }

    public void flywheelsRun(double speed) {
        masterFlyWheel.set(speed);
    }

    public void spinnerRun(double speed) {
        spinner.set(speed);
    }

    public void flywheelsStop() {
        masterFlyWheel.set(0);
    }

    public void spinnerStop() {
        spinner.set(0);
    }


}