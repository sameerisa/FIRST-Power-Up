/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7127.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {
	private DifferentialDrive m_drive;
	private Joystick m_Stick;
	//private Joystick m_rightStick;

	@Override
	public void robotInit() {
		Spark m_frontLeft = new Spark(0);
		Spark m_rearLeft = new Spark(1);
		SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
		
		Spark m_frontRight = new Spark(2);
		Spark m_rearRight = new Spark(3);
		SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);
		
		m_drive = new DifferentialDrive(m_left, m_right);
		//m_myRobot = new DifferentialDrive(new Spark(0), new Spark(1));
		m_Stick = new Joystick(0);
		//m_rightStick = new Joystick(1);
	}

	@Override
	public void teleopPeriodic() {
		m_drive.arcadeDrive(m_Stick.getY(), m_Stick.getZ());
		//m_myRobot.tankDrive(m_leftStick.getY(), m_rightStick.getY());
		
	}
}
