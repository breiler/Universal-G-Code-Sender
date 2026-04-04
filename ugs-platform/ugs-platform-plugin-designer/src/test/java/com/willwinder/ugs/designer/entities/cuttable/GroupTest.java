package com.willwinder.ugs.designer.entities.cuttable;

import com.willwinder.ugs.designer.entities.EntitySetting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.List;

public class GroupTest {
    @Test
    public void setLaserPowerShoouldSetTheLaserPowerOnAllChildren() {
        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle = new com.willwinder.ugs.designer.entities.cuttable.Rectangle(1, 1);

        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();
        group.addChild(rectangle);
        group.setSpindleSpeed(10);

        assertEquals(10, rectangle.getSpindleSpeed(), 0.1);
    }

    @Test
    public void getLaserPowerShouldGetTheHighestValue() {
        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle1 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle(1, 1);
        rectangle1.setSpindleSpeed(11);

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle2 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle(1, 1);
        rectangle2.setSpindleSpeed(10);

        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();
        group.addChild(rectangle1);
        group.addChild(rectangle2);

        assertEquals(11, group.getSpindleSpeed(), 0.1);
    }

    @Test
    public void getSettingsShouldReturnACombinedListOfSettings() {
        com.willwinder.ugs.designer.entities.cuttable.Point point1 = new com.willwinder.ugs.designer.entities.cuttable.Point();
        com.willwinder.ugs.designer.entities.cuttable.Point point2 = new com.willwinder.ugs.designer.entities.cuttable.Point();

        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();
        assertEquals(List.of(), group.getSettings());

        group.addChild(point1);
        assertEquals(List.of(EntitySetting.POSITION_X, EntitySetting.POSITION_Y, EntitySetting.CUT_TYPE, EntitySetting.SPINDLE_SPEED, EntitySetting.START_DEPTH, EntitySetting.TARGET_DEPTH), group.getSettings());

        group.addChild(point2);
        assertEquals(List.of(EntitySetting.POSITION_X, EntitySetting.POSITION_Y, EntitySetting.CUT_TYPE, EntitySetting.SPINDLE_SPEED, EntitySetting.START_DEPTH, EntitySetting.TARGET_DEPTH), group.getSettings());

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        group.addChild(rectangle);
        assertEquals(List.of(EntitySetting.POSITION_X, EntitySetting.POSITION_Y, EntitySetting.CUT_TYPE, EntitySetting.SPINDLE_SPEED, EntitySetting.START_DEPTH, EntitySetting.TARGET_DEPTH), group.getSettings());
    }

    @Test
    public void getSettingsReturnCutTypeIfTheyAreTheSame() {
        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle1 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle1.setCutType(com.willwinder.ugs.designer.entities.cuttable.CutType.LASER_FILL);
        group.addChild(rectangle1);

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle2 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle2.setCutType(com.willwinder.ugs.designer.entities.cuttable.CutType.LASER_FILL);
        group.addChild(rectangle2);

        assertTrue(group.getSettings().contains(EntitySetting.CUT_TYPE));

        rectangle2.setCutType(com.willwinder.ugs.designer.entities.cuttable.CutType.ON_PATH);
        assertFalse(group.getSettings().contains(EntitySetting.CUT_TYPE));
    }

    @Test
    public void getSettingsReturnStartDepthTypeIfTheyAreTheSame() {
        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle1 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle1.setStartDepth(10.1);
        group.addChild(rectangle1);

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle2 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle2.setStartDepth(10.1);
        group.addChild(rectangle2);

        assertTrue(group.getSettings().contains(EntitySetting.START_DEPTH));

        rectangle2.setStartDepth(10.2);
        assertFalse(group.getSettings().contains(EntitySetting.START_DEPTH));
    }

    @Test
    public void getSettingsReturnTargetDepthTypeIfTheyAreTheSame() {
        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle1 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle1.setTargetDepth(10.1);
        group.addChild(rectangle1);

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle2 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle2.setTargetDepth(10.1);
        group.addChild(rectangle2);

        assertTrue(group.getSettings().contains(EntitySetting.TARGET_DEPTH));

        rectangle2.setTargetDepth(10.2);
        assertFalse(group.getSettings().contains(EntitySetting.TARGET_DEPTH));
    }

    @Test
    public void getSettingsReturnSpindleSpeedTypeIfTheyAreTheSame() {
        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle1 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle1.setSpindleSpeed(10);
        group.addChild(rectangle1);

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle2 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle2.setSpindleSpeed(10);
        group.addChild(rectangle2);

        assertTrue(group.getSettings().contains(EntitySetting.SPINDLE_SPEED));

        rectangle2.setSpindleSpeed(11);
        assertFalse(group.getSettings().contains(EntitySetting.SPINDLE_SPEED));
    }

    @Test
    public void getSettingsReturnFeedRateTypeIfTheyAreTheSame() {
        com.willwinder.ugs.designer.entities.cuttable.Group group = new com.willwinder.ugs.designer.entities.cuttable.Group();

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle1 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle1.setFeedRate(10);
        group.addChild(rectangle1);

        com.willwinder.ugs.designer.entities.cuttable.Rectangle rectangle2 = new com.willwinder.ugs.designer.entities.cuttable.Rectangle();
        rectangle2.setFeedRate(10);
        group.addChild(rectangle2);

        assertTrue(group.getSettings().contains(EntitySetting.FEED_RATE));

        rectangle2.setFeedRate(11);
        assertFalse(group.getSettings().contains(EntitySetting.FEED_RATE));
    }
}
