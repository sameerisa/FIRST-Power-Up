/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7127.robot;

//Import needed classes
import com.ctre.phoenix.motorcontrol.ControlMode;	// Import classes from CTRE
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;	// Import WPILib classes
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {
	// Create objects
	private DifferentialDrive driveTrain;	// Drive train
	private TalonSRX verticalArm;	// Vertical motor
	private Joystick driveStick;	// Joystick
	private Timer robotTimer;	// Timer
	private DoubleSolenoid gripperSolenoid;	// Gripper pneumatic solenoid
	private Solenoid armSolenoid;	// Raising arm solenoid
	private DigitalInput topLimitSwitch;	// top limit switch
	private DigitalInput bottomLimitSwitch;	// bottom limit switch
	private DigitalInput jumpA;	// Position jumper a
	private DigitalInput jumpB;	// Position jumper b
	
	// Create variables to store joystick and limit switches values
	boolean button3Pressed = false;	// Button 3, failsafe false
	boolean button4Pressed = false;	// Button 4, failsafe false
	boolean button5Pressed = false;	// Button 5, failsafe false
	boolean button6Pressed = false;	// Button 6, failsafe false
	boolean button12Pressed = false;	// Button 12, failsafe false
	boolean limitSwitchTop = true;	// Top limit switch, failsafe true
	boolean limitSwitchBot = true;	// Bottom limit switch, failsafe true
	boolean jumperA = false;	// Position jumper A, failsafe false
	boolean jumperB = false;	// Position jumper B, failsafe false
	
	int robotLocation = 0;	// Robot location, failsafe 0
	
	String gameData;	// Game data, failsafe empty

	@Override
	public void robotInit() {
		Spark m_rearLeft = new Spark(0);	// Initialize left Spark objects
		Spark m_frontLeft = new Spark(1);
		
		SpeedControllerGroup m_left = new SpeedControllerGroup(m_rearLeft, m_frontLeft);	// Join left Sparks into a group
		
		Spark m_frontRight = new Spark(2);	// Initialize right Spark objects
		Spark m_rearRight = new Spark(3);
		SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);	// Join right Sparks into a group
		
		driveTrain = new DifferentialDrive(m_left, m_right);	// Create drivetrain with Spark groups
		verticalArm = new TalonSRX(0);	// Initialize vertical arm motor
		driveStick = new Joystick(0);	// Initialize joystick
		robotTimer = new Timer();	// Initialize timer
		gripperSolenoid = new DoubleSolenoid(0,1);	// Initialize gripper solenoids
		armSolenoid = new Solenoid(2);	// Initialize arm raising solenoid
		topLimitSwitch = new DigitalInput(0);	// Initialize top limit switch
		bottomLimitSwitch = new DigitalInput(1);	// Initialize bottom limit switch
		jumpA = new DigitalInput(2);	// Jumpers:
		jumpB = new DigitalInput(3);	// FS: XX	1: A	2: AB	3: B
		
		CameraServer.getInstance().startAutomaticCapture();	// Start camera stream to DS
	}
	//
	@Override
	public void autonomousInit() {	// Initial autonomous code (run once at beginning of autonomous)
		gameData = DriverStation.getInstance().getGameSpecificMessage();	// Get game data (switch/scale positions)
		jumperA = !jumpA.get();	// Get Value of Position jumpers
		jumperB = !jumpB.get();
		
		// Set robotLocation based on jumpers
		if (jumperA && !jumperB) {	// 2
			robotLocation = 1;
		} else if (jumperA && jumperB) {	// 2 & 3
			robotLocation = 2;
		} else if (!jumperA && jumperB) {	// 3
			robotLocation = 3;
		} else {	// None
			robotLocation = 0;	// Failsafe
		}
		
		robotTimer.reset();	// Set timer to 0
		robotTimer.start();	// and start it
	}
	
	@Override
	public void autonomousPeriodic() {	// Periodic autonomous code (run every (10ms?) while autonomous is active)
		limitSwitchBot = bottomLimitSwitch.get();	// Get value of bottom limit switch
		
		/*if (robotTimer.get() < 0.0 && limitSwitchBot) {	// Move arm down
			verticalArm.set(ControlMode.PercentOutput, -0.5);
		} else {
			verticalArm.set(ControlMode.PercentOutput, 0.0);
		}*/
		
		if (robotTimer.get() > 0.0 && robotTimer.get() < 0.4) {	// Deploy arm for 0.3 seconds
			armSolenoid.set(true);
		} else {
			armSolenoid.set(false);
		}
		
		if(gameData.length() > 0 &&  gameData.charAt(0) == 'L') {	// If our switch is to the left...
    		switch (robotLocation) {
    			case 0:	// ...and we do not know where we are...
    				driveTrain.arcadeDrive(0.0, 0.0);	// ...don't drive
    			break;
    			case 1:	// ...and we are to the left...
    				if (robotTimer.get() > 1.3 && robotTimer.get() < 4.0) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				/*} else if (robotTimer.get() > 3.3 && robotTimer.get() < 14.0) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ... correct the drift
    				*/} else if (robotTimer.get() > 4 && robotTimer.get() < 4.9) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, 0.6);	
    				} else if (robotTimer.get() > 5 && robotTimer.get() < 5.4) {
    					driveTrain.arcadeDrive(0.6, 0.0);
    				} else if (robotTimer.get() > 5.4 && robotTimer.get() < 5.5) {	// ... and the time is between 5.3 and 5.4 seconds...
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    			break;
    			case 2:	// ...and we are in the middle...
    				if (robotTimer.get() > 0.5 && robotTimer.get() < 1.2) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.6, 0.0);	// ...drive straight at 0.7 speed
    				} else if (robotTimer.get() > 1.2 && robotTimer.get() < 2.4) {	// ...and the time is between 1 and 1.4 seconds...
    					driveTrain.arcadeDrive(0.0, -0.5);	// ...correct the drift
    				} else if (robotTimer.get() > 2.4 && robotTimer.get() < 4.0) {	// ...and the time is between 1.4 and 4 seconds...
    					driveTrain.arcadeDrive(0.6, 0.0);	// ...drive straight at 0.7 speed
    				} else if (robotTimer.get() > 4.0 && robotTimer.get() < 5.4) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, 0.5);	// ... correct the drift
    				} else if (robotTimer.get() > 5.4 && robotTimer.get() < 6.8) {	// ... and the time is between 5.3 and 5.4 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);
    				} else if (robotTimer.get() > 6.8 && robotTimer.get() < 7.0) {
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    			break;
    			case 3:	// ... and we are to the right...
    				if (robotTimer.get() > 1.3 && robotTimer.get() < 3.8) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				/*} else if (robotTimer.get() > 3.3 && robotTimer.get() < 14.0) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ... correct the drift
    				*/} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    			break;
    		}
    	} else {	// If our switch is not to the left (to the right)...
    		switch (robotLocation) {
    			case 0:	// ...and we do not know where we are...
    				driveTrain.arcadeDrive(0.0, 0.0);	// ... do not drive
    			break;
				case 1:	// ...and we are to the left...
					if (robotTimer.get() > 1.3 && robotTimer.get() < 3.8) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				/*} else if (robotTimer.get() > 3.3 && robotTimer.get() < 14.0) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ... correct the drift
    				*/} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
				case 2:	// ...and we are in the middle...
					if (robotTimer.get() > 0.5 && robotTimer.get() < 1.2) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.6, 0.0);	// ...drive straight at 0.7 speed
    				} else if (robotTimer.get() > 1.2 && robotTimer.get() < 2.4) {	// ...and the time is between 1 and 1.4 seconds...
    					driveTrain.arcadeDrive(0.0, 0.5);	// ...correct the drift
    				} else if (robotTimer.get() > 2.4 && robotTimer.get() < 4.0) {	// ...and the time is between 1.4 and 4 seconds...
    					driveTrain.arcadeDrive(0.6, 0.0);	// ...drive straight at 0.7 speed
    				} else if (robotTimer.get() > 4.0 && robotTimer.get() < 5.4) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.5);	// ... correct the drift
    				} else if (robotTimer.get() > 5.4 && robotTimer.get() < 6.8) {	// ... and the time is between 5.3 and 5.4 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);
    				} else if (robotTimer.get() > 6.8 && robotTimer.get() < 7.0) {
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
				case 3:	// ...and we are to the right...
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.0) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				/*} else if (robotTimer.get() > 3.3 && robotTimer.get() < 14.0) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ... correct the drift
    				*/} else if (robotTimer.get() > 4 && robotTimer.get() < 4.9) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	
    				} else if (robotTimer.get() > 5 && robotTimer.get() < 5.4) {
    					driveTrain.arcadeDrive(0.6, 0.0);
    				} else if (robotTimer.get() > 5.4 && robotTimer.get() < 5.5) {	// ... and the time is between 5.3 and 5.4 seconds...
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
    		}
    	}
    }	
	
	@Override
	public void teleopPeriodic() {	// Periodic teleop code (run every (10ms?) while teleop is active)
		double speed = driveStick.getRawAxis(3)+1.1;	// Get value of joystick throttle
		
	 	driveTrain.arcadeDrive(-driveStick.getY()/ speed, driveStick.getZ()/1.75);	// Drive the robot
	 	
	 	// Get the values of the buttons
		button3Pressed = driveStick.getRawButton(3);
		button4Pressed = driveStick.getRawButton(4);
		button5Pressed = driveStick.getRawButton(5);
		button6Pressed = driveStick.getRawButton(6);
		button12Pressed = driveStick.getRawButton(12);
		
		// Get the values of the limit switches
		limitSwitchTop = topLimitSwitch.get();
		limitSwitchBot = bottomLimitSwitch.get();
		
		if (button12Pressed) {	// If button 12 is pressed...
			armSolenoid.set(true);	// ...raise the arm
		} else {	// If button 12 is not pressed...
			armSolenoid.set(false);	// ...do not raise the arm
		}
		
		if (button5Pressed && !button3Pressed && limitSwitchTop) {	// If button 5 is pressed and top limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, 0.5);	// Move the arm up
				
		} else if (!button5Pressed && button3Pressed && limitSwitchBot) {	// If button 3 is pressed and bottom limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, -0.5);	// Move the arm down
				
		} else {	// If nothing is activated or a limit switch is activated,
			verticalArm.set(ControlMode.PercentOutput, 0.0);	// Stop the arm
		} 
		
		if (button6Pressed && !button4Pressed) {	// If button 6 but not button 4 is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...open the gripper
		} else if (button4Pressed && !button6Pressed) {	// If button 4 but not button 6 is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kReverse);	// ...close the gripper
		} else {	// If either both or neither button is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kOff);	// ...stop moving the gripper
		}
		
	}
}
