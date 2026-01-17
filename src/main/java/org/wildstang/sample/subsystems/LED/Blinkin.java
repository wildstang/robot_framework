package org.wildstang.sample.subsystems.LED;

import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;

public class Blinkin {

    public enum BlinkinValues {
        /*
         * Fixed Palette Pattern
         */
        RAINBOW_RAINBOW_PALETTE(1005),
        RAINBOW_PARTY_PALETTE(1015),
        RAINBOW_OCEAN_PALETTE(1025),
        RAINBOW_LAVA_PALETTE(1035),
        RAINBOW_FOREST_PALETTE(1045),
        RAINBOW_WITH_GLITTER(1055),
        CONFETTI(1065),
        SHOT_RED(1075),
        SHOT_BLUE(1085),
        SHOT_WHITE(1095),
        SINELON_RAINBOW_PALETTE(1105),
        SINELON_PARTY_PALETTE(1115),
        SINELON_OCEAN_PALETTE(1125),
        SINELON_LAVA_PALETTE(1135),
        SINELON_FOREST_PALETTE(1145),
        BEATS_PER_MINUTE_RAINBOW_PALETTE(1155),
        BEATS_PER_MINUTE_PARTY_PALETTE(1165),
        BEATS_PER_MINUTE_OCEAN_PALETTE(1175),
        BEATS_PER_MINUTE_LAVA_PALETTE(1185),
        BEATS_PER_MINUTE_FOREST_PALETTE(1195),
        FIRE_MEDIUM(1205),
        FIRE_LARGE(1215),
        TWINKLES_RAINBOW_PALETTE(1225),
        TWINKLES_PARTY_PALETTE(1235),
        TWINKLES_OCEAN_PALETTE(1245),
        TWINKLES_LAVA_PALETTE(1255),
        TWINKLES_FOREST_PALETTE(1265),
        COLOR_WAVES_RAINBOW_PALETTE(1275),
        COLOR_WAVES_PARTY_PALETTE(1285),
        COLOR_WAVES_OCEAN_PALETTE(1295),
        COLOR_WAVES_LAVA_PALETTE(1305),
        COLOR_WAVES_FOREST_PALETTE(1315),
        LARSON_SCANNER_RED(1325),
        LARSON_SCANNER_GRAY(1335),
        LIGHT_CHASE_RED(1345),
        LIGHT_CHASE_BLUE(1355),
        LIGHT_CHASE_GRAY(1365),
        HEARTBEAT_RED(1375),
        HEARTBEAT_BLUE(1385),
        HEARTBEAT_WHITE(1395),
        HEARTBEAT_GRAY(1405),
        BREATH_RED(1415),
        BREATH_BLUE(1425),
        BREATH_GRAY(1435),
        STROBE_RED(1445),
        STROBE_BLUE(1455),
        STROBE_GOLD(1465),
        STROBE_WHITE(1475),
        /*
         * CP1: Color 1 Pattern
         */
        CP1_END_TO_END_BLEND_TO_BLACK(1485),
        CP1_LARSON_SCANNER(1495),
        CP1_LIGHT_CHASE(1505),
        CP1_HEARTBEAT_SLOW(1515),
        CP1_HEARTBEAT_MEDIUM(1525),
        CP1_HEARTBEAT_FAST(1535),
        CP1_BREATH_SLOW(1545),
        CP1_BREATH_FAST(1555),
        CP1_SHOT(1565),
        CP1_STROBE(1575),
        /*
         * CP2: Color 2 Pattern
         */
        CP2_END_TO_END_BLEND_TO_BLACK(1585),
        CP2_LARSON_SCANNER(1595),
        CP2_LIGHT_CHASE(1605),
        CP2_HEARTBEAT_SLOW(1615),
        CP2_HEARTBEAT_MEDIUM(1625),
        CP2_HEARTBEAT_FAST(1635),
        CP2_BREATH_SLOW(1645),
        CP2_BREATH_FAST(1655),
        CP2_SHOT(1665),
        CP2_STROBE(1675),
        /*
         * CP1_2: Color 1 and 2 Pattern
         */
        CP1_2_SPARKLE_1_ON_2(1685),
        CP1_2_SPARKLE_2_ON_1(1695),
        CP1_2_COLOR_GRADIENT(1705),
        CP1_2_BEATS_PER_MINUTE(1715),
        CP1_2_END_TO_END_BLEND_1_TO_2(1725),
        CP1_2_END_TO_END_BLEND(1735),
        CP1_2_NO_BLENDING(1745),
        CP1_2_TWINKLES(1755),
        CP1_2_COLOR_WAVES(1765),
        CP1_2_SINELON(1775),
        /*
         * Solid color
         */
        HOT_PINK(1785),
        DARK_RED(1795),
        RED(1805),
        RED_ORANGE(1815),
        ORANGE(1825),
        GOLD(1835),
        YELLOW(1845),
        LAWN_GREEN(1855),
        LIME(1865),
        DARK_GREEN(1875),
        GREEN(1885),
        BLUE_GREEN(1895),
        AQUA(1905),
        SKY_BLUE(1915),
        DARK_BLUE(1925),
        BLUE(1935),
        BLUE_VIOLET(1945),
        VIOLET(1955),
        WHITE(1965),
        GRAY(1975),
        DARK_GRAY(1985),
        BLACK(1995);
    
        public final int value;
        private BlinkinValues(int value) {
          this.value = value;
        }
      };
    
    private PWM controller;

    public Blinkin(int PWMID){
        controller = new PWM(PWMID);
    }

    public void setColor(BlinkinValues input){
      controller.setPulseTimeMicroseconds(input.value);
    }


}
