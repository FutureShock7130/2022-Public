// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.Constants.SuperstructureConstants;
import frc.robot.commands.Intake.IntakeCmd;
import frc.robot.commands.Intake.IntakeReverse;
import frc.robot.commands.Superstructure.AutoClimb;
import frc.robot.commands.Superstructure.SwingBack;
import frc.robot.commands.Superstructure.SwingForward;
import frc.robot.commands.Turret.LimelightAim;
import frc.robot.commands.Turret.TurretSeek;
import frc.robot.commands.Turret.TurretShoot;
import frc.robot.statemachines.DriveFSM;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Turret;
import frc.robot.vision.Limelight;
import frc.robot.subsystems.Transporter;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

public class RobotContainer {
    // The robot's subsystems
    private final Drive m_robotDrive = new Drive();
    private final Transporter m_robotTransport = new Transporter();
    private final Turret m_robotTurret = new Turret();
    private final Intake m_robotIntake = new Intake();
    private final Limelight m_vision = new Limelight();
    private final Superstructure m_SuperStructure = new Superstructure();

    AutoClimb autoClimb = new AutoClimb(m_SuperStructure);
    TurretShoot shoot = new TurretShoot(m_robotTurret);

    BooleanSupplier targetNotIn = new BooleanSupplier() {
        @Override
        public boolean getAsBoolean(){
            if (m_vision.getV() == 0.0d) return true;
            return false; 
        }
    };

    // The driver's controller
    Joystick m_driverController = new Joystick(OIConstants.kDriveTrainJoystickPort);
    Joystick m_operatorController = new Joystick(OIConstants.kOthersJoystickPort);

    // The container for the robot. Contains subsystems, OI devices, and commands.
    public RobotContainer(DriveFSM driveFSM) {
        // Configure the button bindings
        configureButtonBindings();
        m_robotDrive.setDriveFSM(driveFSM);

        m_robotDrive.setDefaultCommand(
                new RunCommand(
                        () -> {
                            m_robotDrive.drive(
                                    -m_driverController.getRawAxis(OIConstants.leftStick_Y),
                                    m_driverController.getRawAxis(OIConstants.leftStick_X),
                                    m_driverController.getRawAxis(OIConstants.rightStick_X),
                                    false);

                            SwingForward swingForward = new SwingForward(m_SuperStructure);
                            SwingBack swingBack = new SwingBack(m_SuperStructure);

                            if (m_operatorController.getPOV() == OIConstants.POV_UP)
                                swingForward.schedule();
                            if (m_operatorController.getPOV() == OIConstants.POV_DOWN)
                                swingBack.schedule();
                            if (m_operatorController.getPOV() == -1) {
                                swingBack.end(true);
                                swingForward.end(true);
                                swingBack.cancel();
                                swingForward.cancel();
                            }

                            IntakeCmd intake = new IntakeCmd(m_robotIntake);
                            IntakeReverse eject = new IntakeReverse(m_robotIntake);

                            if (m_driverController.getRawAxis(OIConstants.trigger_R) >= 0.5) {
                                intake.schedule();
                            } else {
                                intake.cancel();
                            }

                            if (m_driverController.getRawAxis(OIConstants.trigger_L) >= 0.5) {
                                eject.schedule();
                            } else {
                                eject.cancel();
                            }

                            m_SuperStructure.liftHangerRun(
                                    -m_operatorController.getRawAxis(OIConstants.leftStick_Y)
                                            * SuperstructureConstants.hangerSpeed,
                                    -m_operatorController.getRawAxis(OIConstants.rightStick_Y)
                                            * SuperstructureConstants.hangerSpeed);

                        }, m_robotDrive));

    }

    private void configureButtonBindings() {

        new JoystickButton(m_driverController, OIConstants.Btn_RB)
                .whileHeld(() -> m_robotDrive.setMaxOutput(DriveConstants.DriveSpeedScaler * 0.7))
                .whenReleased(() -> m_robotDrive.setMaxOutput(DriveConstants.DriveSpeedScaler));

        new JoystickButton(m_driverController, OIConstants.Btn_Y)
                .whenHeld(new ConditionalCommand(new TurretSeek(m_robotTurret), new LimelightAim(m_robotTurret, m_vision), targetNotIn));

        new JoystickButton(m_driverController, OIConstants.Btn_B)
                .whileHeld(new RunCommand(() -> {
                    m_robotTurret.spinnerRun(0.3);
                }, m_robotTurret))
                .whenReleased(new RunCommand(() -> {
                    m_robotTurret.spinnerRun(0.0);
                }, m_robotTurret));

        new JoystickButton(m_driverController, OIConstants.Btn_X)
                .whileHeld(new RunCommand(() -> {
                    m_robotTurret.spinnerRun(-0.3);
                }, m_robotTurret))
                .whenReleased(new RunCommand(() -> {
                    m_robotTurret.spinnerRun(0.0);
                }, m_robotTurret));

        new JoystickButton(m_operatorController, OIConstants.Btn_RB)
                .whenHeld(shoot)
                .whenReleased(() -> shoot.cancel());

        // new JoystickButton(m_operatorController,
        // OIConstants.Btn_RB).whenPressed(autoClimb);
        // new JoystickButton(m_operatorController, OIConstants.Btn_LB).whenPressed(()
        // -> autoClimb.cancel());
    }

    // Use this to pass the autonomous command to the main {@link Robot} class.
    public void testDrive() {
        m_robotDrive.testMotor();
    }

    public Pose2d getPose() {
        return m_robotDrive.getPose();
    }

    public Command getAutonomousCommand() {

        String trajectoryJSON = "paths/straightTest.wpilib.json";
        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(trajectoryJSON);
            Trajectory Trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);

            // Run path following command, then stop at the end.
            // return mecanumControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0,
            // false));
        } catch (IOException e) {
            DriverStation.reportError("Unable to open JSON file", e.getStackTrace());
        }
        return null;
    }
}
