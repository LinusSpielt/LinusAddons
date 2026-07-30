package net.linusaddons.mod.config.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import net.linusaddons.mod.features.kuudra.StunWaypointsFeature;
import net.linusaddons.mod.features.kuudra.TentacleDetectFeature;
import net.linusaddons.mod.utils.render.WorldRenderUtils;

import java.awt.*;

@Category(
        value = "Linus Addons"
)
public class LinusAddonsConfig {

    @ConfigOption.Separator("General")

    @ConfigEntry(
            id = "HollowAndRendFix",
            translation = "Hollow & Rend FIX"
    )
    @Comment("Fixes a bug with Hollow and Rend not working correctly.")
    public static boolean HollowAndRendFix = true;

    @ConfigEntry(
            id = "PickobulusBlocker",
            translation = "Pickobulus Blocker"
    )
    @Comment("Blocks using the Pickobulus ability while not in stun Phase.")
    public static boolean PickobulusBlocker = true;

    @ConfigEntry(
            id = "AutoGFS",
            translation = "Auto GFS"
    )
    @Comment("Automatically refills Pearls while in Kuudra.")
    public static boolean AutoGFS = true;

    @ConfigEntry(
            id = "pearlThreshold",
            translation = "Pearl GFS Threshold"
    )
    @Comment("Ammount of pearls left to trigger a refill.")
    @ConfigOption.Range(min = 1, max = 6)
    @ConfigOption.Slider
    public static int pearlTreshold = 1;

    @ConfigEntry(
            id = "toxicAmmount",
            translation = "Toxic Arrow Ammount"
    )
    @Comment("Ammount of TAP to get for dps.")
    @ConfigOption.Range(min = 0, max = 32)
    @ConfigOption.Slider
    public static int toxicAmmount = 20;

    @ConfigOption.Separator("Supply Phase")

    @ConfigEntry(
            id = "supplyRodCircle",
            translation = "Supply Rod Circle"
    )
    @Comment("Displays the range you can pull a supply with your rod for.")
    public static boolean supplyRodCircle = true;

    @ConfigEntry(
            id = "supplyRodCircleColor",
            translation = "Supply Rod Circle Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Change the color of the circle.")
    public static int supplyRodColor = new Color(255, 255, 255, 52).getRGB();

    @ConfigEntry(
            id = "TentacleScanner",
            translation = "Tentacle Scanner"
    )
    @Comment("Tentacle scanning (required for Dynamic Waypoints).")
    public static boolean TentDetector = true;

    @ConfigEntry(
            id = "ConditionalBoxes",
            translation = "Dynamic Waypoints"
    )
    @Comment("")
    public static boolean ConditionalBoxes = true;

    @ConfigEntry(id = "ConditionalBoxesRenderDistance", translation = "Conditional Boxes Render Distance")
    @ConfigOption.Range(min = 0, max = 25)
    @ConfigOption.Slider
    @Comment("Render Distance of Conditional Boxes (0 Renders All Boxes).")
    public static double ConditionalBoxesRenderDist = 22.5;

    @ConfigEntry(
            id = "tentacle",
            translation = "Render Tentacles."
    )
    @Comment("What Tentacles to mark.")
    public static TentacleDetectFeature.TentacleSize[] renderTentacleSizes = new TentacleDetectFeature.TentacleSize[]{
            TentacleDetectFeature.TentacleSize.MINI,
            TentacleDetectFeature.TentacleSize.MEDIUM,
            TentacleDetectFeature.TentacleSize.BIG,
            TentacleDetectFeature.TentacleSize.BOX
    };

    @ConfigEntry(
            id = "tentacleStyle",
            translation = "Tentacle Style"
    )
    @ConfigOption.Select
    @Comment("Choose how tentacles are rendered.")
    public static WorldRenderUtils.RenderStyle tentStyle = WorldRenderUtils.RenderStyle.OUTLINE;

    @ConfigEntry(
            id = "miniTentacleColor",
            translation = "Mini Tentacle Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Choose how tentacles are rendered.")
    public static int miniTentColor = new Color(0, 245, 255, 200).getRGB();

    @ConfigEntry(
            id = "mediumTentacleColor",
            translation = "Medium Tentacle Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Choose how tentacles are rendered.")
    public static int mediumTentColor = new Color(0, 245, 255, 200).getRGB();

    @ConfigEntry(
            id = "largeTentacleColor",
            translation = "Large Tentacle Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Choose how tentacles are rendered.")
    public static int largeTentColor = new Color(0, 245, 255, 200).getRGB();

    @ConfigOption.Separator("Build Phase")

    @ConfigEntry(
            id = "preSpotWaypoint",
            translation = "Pre Spot Waypoint"
    )
    @Comment("Render a waypoint at your pre spot location during the Build phase.")
    public static boolean preSpotWaypoint = true;

    @ConfigEntry(
            id = "preSpotWaypointColor",
            translation = "Pre Spot Waypoint Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Color of the pre spot waypoint beacon.")
    public static int preSpotWaypointColor = new Color(255, 215, 0, 220).getRGB();

    @ConfigEntry(
            id = "preSpotWaypointStyle",
            translation = "Pre Spot Waypoint Style"
    )
    @ConfigOption.Select
    @Comment("Render style of the pre spot waypoint box (solid fill, outline, or both).")
    public static WorldRenderUtils.RenderStyle preSpotWaypointStyle = WorldRenderUtils.RenderStyle.BOTH;

    @ConfigEntry(
            id = "hideRendCooldown",
            translation = "Hide Rend Cooldown"
    )
    @Comment("Prevent Rend Cooldown spam in chat during build.")
    public static boolean hideRendCooldown = true;

    @ConfigEntry(
            id = "simpleBuildProgressOverlay",
            translation = "Build Progress Overlay"
    )
    @Comment("Display the accurate build percent as a compact single-line widget")
    public static boolean simpleBuildProgressOverlay = false;

    @ConfigEntry(
            id = "buildStartCountdownOverlay",
            translation = "Build Start Countdown"
    )
    @Comment("Show a countdown for when the build starts during the phase animation.")
    public static boolean buildStartCountdownOverlay = true;

    @ConfigOption.Separator("Stun Phase")

    @ConfigEntry(
            id = "stunWaypoints",
            translation = "Stun Waypoints"
    )
    @Comment("Show waypoints for stun positions.")
    public static boolean stunWaypoints = true;

    @ConfigEntry(
            id = "stunWaypointColor",
            translation = "Stun Waypoints Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Change the color of stun waypoints.")
    public static int stunWaypointColor = new Color(0, 245, 255, 200).getRGB();

    @ConfigEntry(
            id = "stunWaypointStyle",
            translation = "Stun Waypoint Style"
    )
    @ConfigOption.Select
    @Comment("Choose how stun waypoints are drawn (outline, filled, or both).")
    public static WorldRenderUtils.RenderStyle stunWaypointStyle = WorldRenderUtils.RenderStyle.OUTLINE;

    @ConfigEntry(
            id = "stunWaypointBlock",
            translation = "Stun Waypoint Block"
    )
    @ConfigOption.Select
    @Comment("Choose which stun location marker to display.")
    public static StunWaypointsFeature.StunWaypoint stunWaypointBlock = StunWaypointsFeature.StunWaypoint.LEFT_POD;

    @ConfigEntry(
            id = "dpsWaypoints",
            translation = "Dps Waypoints"
    )
    @Comment("Show waypoints for dps positions.")
    public static boolean dpsWaypoints = true;

    @ConfigEntry(
            id = "dpsWaypointColor",
            translation = "Dps Waypoints Color"
    )
    @ConfigOption.Color(alpha = true)
    @Comment("Change the color of stun waypoints.")
    public static int dpsWaypointColor = new Color(0, 245, 255, 200).getRGB();

    @ConfigEntry(
            id = "dpsWaypointStyle",
            translation = "Dps Waypoint Style"
    )
    @ConfigOption.Select
    @Comment("Choose how stun waypoints are drawn (outline, filled, or both).")
    public static WorldRenderUtils.RenderStyle dpsWaypointStyle = WorldRenderUtils.RenderStyle.OUTLINE;

    @ConfigOption.Separator("Boss Phase")

    @ConfigEntry(
            id = "kuudraDirectionAlert",
            translation = "Kuudra Direction Alert"
    )
    @Comment("Show an alert indicating which side Kuudra will spawn on.")
    public static boolean kuudraDirectionAlert = true;

    @ConfigEntry(
            id = "bossBlockWaypoint",
            translation = "Boss Block Waypoint"
    )
    @Comment("Renders a Waypoint for a missplaced Block in the Boss Wall.")
    public static boolean bossBlockWaypoint = true;

    @ConfigEntry(
            id = "boneAimWaypoint",
            translation = "Bone Aim Waypoint"
    )
    @Comment("Renders a dynamic Waypoint to aim your Rend Bone at.")
    public static boolean boneAimWaypoint = true;

    @ConfigEntry(
            id = "backBoneHelper",
            translation = "BackBone Info"
    )
    @Comment("Sends the players Armor and Item used when BackBone hits.")
    public static boolean backBoneInfo = true;
}
