# Motor Controllers

[Victor and Talon Documentation](https://www.ctr-electronics.com/downloads/api/java/html/classcom_1_1ctre_1_1phoenix_1_1motorcontrol_1_1can_1_1_talon_s_r_x.html)

[Spark Max Documentation](https://www.revrobotics.com/content/sw/max/sw-docs/java/com/revrobotics/CANSparkMax.html)

While not exactly the same WsPhoenix, for Talons and Victors, and WsSparkMax contain the same core functions.

First, the motor controller must be defined in `WSOutputs`.
It requires a name, a `WsPhoenixConfig` or `WsSparkMaxConfig`, and whether to log the motor controller's state.
Both require a CAN constant, `WsPhoenixConfig` requires whether it is a Talon or Victor (true/false) and `WsSparkMaxConfig` requires whether the motor is brushless of not.
Additionally, they both can take whether it should be inverted and a default value.
```
EXAMPLE_MOTOR("Example Motor", new WsPhoenixConfig(CANConstants.EXAMPLE_MOTOR, true), false)
EXAMPLE_MOTOR("Example Motor", new WsSparkMaxConfig(CANConstants.EXAMPLE_MOTOR, true, false, 0.25), false)
```

Once the motor controller is defined it can be pulled from the framework's `InputManager`.
```
WsPhoenix motor = (WsPhoenix) Core.getOutputManager().getOutputs().get(WSOutputs.EXAMPLE_MOTOR);
WsSparkMax motor = (WsSparkMax) Core.getOutputManager().getOutputs().get(WSOutputs.EXAMPLE_MOTOR);
```

To set the speed of the motor and apply it immediately use `setSpeed(value)`.
```
motor.setSpeed(0.5);
```

To pull information from the motor controller and its encoder use the following functions.
Note, encoders are only available on Talon's.
```
double motorPercentSpeed = motor.getOutput();
double encoderPosition   = motor.getPosition();
double encoderVelocity   = motor.getVelocity();
motor.resetEncoder();
```

Use the following functions to enable brake or coast modes.
```
motor.setBrake();
motor.setCoast();
```

To add a following motor controller, just add it to `WSOutputs`.
It requires a name, a `WsPhoenixFollowerConfig` or `WsSparkMaxFollowerConfig`, and whether to log the motor controller's state.
Both require an `Outputs` to follow and a CAN constant.
`WsPhoenixConfig` requires whether it is a Talon or Victor (true/false) and `WsSparkMaxConfig` require's whether the motor is brushless of not.
Additionally, they both can take whether it should be inverted (relative to the followed motor).
Once defined, the follower will follow the given motor without any other input.
```
EXAMPLE_FOLLOWER("Example Follower", new WsPhoenixFollowerConfig(EXAMPLE_MOTOR, CANConstants.EXAMPLE_FOLLOWER, true), false)
EXAMPLE_FOLLOWER("Example Follower", new WsSparkMaxFollowerConfig(EXAMPLE_MOTOR, CANConstants.EXAMPLE_FOLLOWER, true, false, 0.25), false)
```