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
import edu.wpi.first.wpilibj.AnalogGyro;

public class Robot extends IterativeRobot {
	private DifferentialDrive driveTrain;	// Create objects
	private TalonSRX verticalArm;
	private Joystick driveStick;
	private Timer robotTimer;
	private DoubleSolenoid solenoid;
	private Solenoid armSolenoid;
	private DigitalInput topLimitSwitch;
	private DigitalInput bottomLimitSwitch;
	private AnalogGyro gyro;
	
	boolean button3Pressed = false;	// Create variables to store joystick and limit switches values
	boolean button5Pressed = false;
	boolean button6Pressed = false;
	boolean button4Pressed = false;
	boolean limitSwitchTop = false;
	boolean limitSwitchBot = false;
	boolean button12Pressed = false;
	
	double Kp = 0.03;
	double angle;
	
	String gameData;

	@Override
	public void robotInit() {
		Spark m_rearLeft = new Spark(0);	// Initialize right Spark objects
		Spark m_frontLeft = new Spark(1);
		
		SpeedControllerGroup m_left = new SpeedControllerGroup(m_rearLeft, m_frontLeft);	// Join right Sparks into a group
		
		Spark m_frontRight = new Spark(2);	// Initialize left Spark objects
		Spark m_rearRight = new Spark(3);
		SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);
		
		driveTrain = new DifferentialDrive(m_left, m_right);
		verticalArm = new TalonSRX(0);
		driveStick = new Joystick(0);
		robotTimer = new Timer();
		solenoid = new DoubleSolenoid(0,1);
		topLimitSwitch = new DigitalInput(0);
		bottomLimitSwitch = new DigitalInput(1);
		armSolenoid = new Solenoid(2);
		gyro = new AnalogGyro(1);
		
		CameraServer.getInstance().startAutomaticCapture();
	}
	//
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		robotTimer.reset();
		robotTimer.start();
		
		gyro.reset();
	}
	
	@Override
	public void autonomousPeriodic() {
		if(gameData.length() > 0) {
        	if(gameData.charAt(0) == 'L') {
        		// Left Auto Code
        	} else {
        		// Right Auto Code
        	}
        }
		
		if (robotTimer.get() < 1.0) {
			armSolenoid.set(true);
		} else {
			armSolenoid.set(false);
		}
		
		if (robotTimer.get() > 1.0 && robotTimer.get() < 11.0) {
			angle = gyro.getAngle();
			driveTrain.arcadeDrive(0.5, angle*Kp);	// Drive Straight
		} else if (robotTimer.get() > 11.0 && robotTimer.get() < 11.2) {
			gyro.reset();	// Reset the angle of the gyro
		} else if (robotTimer.get() > 11.2 && robotTimer.get() < 12.0) {
			driveTrain.arcadeDrive(0.0, 0.0);	// Stop the robot
		} else if (robotTimer.get() > 12.0 && gyro.getAngle() < 80) {
			driveTrain.arcadeDrive(0.0, 0.5);	// Turn to 80deg (momentum to 90?)
		} else {
			driveTrain.arcadeDrive(0.0, 0.0);	// Stop at (90deg?)
		}
		
		
	}
	
	@Override
	public void teleopPeriodic() {
		double speed = driveStick.getRawAxis(3)+1.1;
		//System.out.println(speed);
		
	 	driveTrain.arcadeDrive(-driveStick.getY()/ speed, driveStick.getZ()/1.75);
	 	
	 	
		button3Pressed = driveStick.getRawButton(3);
		button5Pressed = driveStick.getRawButton(5);
		button6Pressed = driveStick.getRawButton(6);
		button4Pressed = driveStick.getRawButton(4);
		button12Pressed = driveStick.getRawButton(12);
		
		//
		limitSwitchTop = topLimitSwitch.get();
		limitSwitchBot = bottomLimitSwitch.get();
		
		if(button12Pressed) 
		{
			armSolenoid.set(true);
		}
		else 
		{
			armSolenoid.set(false);
		}
		
		if (button5Pressed && !button3Pressed && limitSwitchTop) {	// If button 5 is pressed and top limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, 0.5);	// Move the arm up
				
		} else if (!button5Pressed && button3Pressed && limitSwitchBot) {	// If button 3 is pressed and bottom limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, -0.5);	// Move the arm down
				
		} else {	// If nothing is activated or a limit switch is activated,
			verticalArm.set(ControlMode.PercentOutput, 0.0);	// Stop the arm
		} 
		
		if(button6Pressed && !button4Pressed) {
			solenoid.set(DoubleSolenoid.Value.kForward);
		} else if(button4Pressed && !button6Pressed) {
			solenoid.set(DoubleSolenoid.Value.kReverse);
		} else {
			solenoid.set(DoubleSolenoid.Value.kOff);
		}
		
	}
}
