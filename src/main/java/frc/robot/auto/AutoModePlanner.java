package frc.robot.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.auto.paths.FourCargoFromOne;
import frc.robot.auto.paths.FourCargoFromThreeB;
import frc.robot.auto.paths.ThreeCargoFromOne;
import frc.robot.auto.paths.ThreeCargoFromThree;
import frc.robot.auto.paths.ThreeCargoFromTwo;
import frc.robot.auto.paths.TwoCargoFromOne;
import frc.robot.auto.paths.TwoCargoFromThree;
import frc.robot.auto.paths.TwoCargoFromTwo;

public class AutoModePlanner implements AutoModes {
    private RobotContainer robot;

    public AutoModePlanner(
        RobotContainer robotContainer
    ) {
        robot = robotContainer;
    }

    public SequentialCommandGroup handleAutoMode(AutoModes.StartingPosition startingPos, AutoModes.DriveStrategy driveStrategy) {
        switch (startingPos) {
            case ONE:
                switch (driveStrategy) {
                    case ONE_CARGO:
                        break;
                    case TWO_CARGO:
                        return new TwoCargoFromOne(robot).getCommand();
                    case THREE_CARGO:
                        return new ThreeCargoFromOne(robot).getCommand();
                    case FOUR_CARGO:
                        return new FourCargoFromOne(robot).getCommand();
                    case FIVE_CARGO:
                        break;
                }
            case TWO:
                switch (driveStrategy) {
                    case ONE_CARGO:
                        break;
                    case TWO_CARGO:
                        return new TwoCargoFromTwo(robot).getCommand();
                    case THREE_CARGO:
                        return new ThreeCargoFromTwo(robot).getCommand();
                    case FOUR_CARGO:
                        // return new FourCargoFromTwo(robot).getCommand();
                    case FIVE_CARGO:
                        break;
                }
            case THREE:
                switch (driveStrategy) {
                    case ONE_CARGO:
                        break;
                    case TWO_CARGO:
                        return new TwoCargoFromThree(robot).getCommand();
                    case THREE_CARGO:
                        return new ThreeCargoFromThree(robot).getCommand();
                    case FOUR_CARGO:
                        return new FourCargoFromThreeB(robot).getCommand();
                    case FIVE_CARGO:
                        break;
                }

            default:
                // return a generic shoot command here
                return new TwoCargoFromOne(robot).getCommand();
        }
    }
}