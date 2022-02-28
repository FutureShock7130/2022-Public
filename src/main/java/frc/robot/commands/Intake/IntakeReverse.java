// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;

public class IntakeReverse extends CommandBase {

  Intake intake;

  /** Creates a new intakeCmd. */
  public IntakeReverse(Intake m_robotIntake) {
    intake = m_robotIntake;
    
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("IntakeReverse started");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    intake.intakeReverse();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
      System.out.println("IntakeReverse ended");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }

  public void interrupted(){
    intake.intakeStop();
  }
}