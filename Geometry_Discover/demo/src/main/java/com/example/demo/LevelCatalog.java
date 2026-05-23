package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Server-side mirror of the level list rendered on the search page.
 * Keep in sync with the levels array in templates/search.html.
 */
public final class LevelCatalog {

    public record Level(String key, String name, String creator, String image) {}

    private static final List<Level> LEVELS;
    private static final Map<String, Level> LEVELS_BY_KEY;

    static {
        List<Level> levels = new ArrayList<>();
        add(levels, "0 Exit", "MiNaY", "0 Exit.jpg");
        add(levels, "Aeternus", "Riot", "Aeternus.jpg");
        add(levels, "B", "Motleyorc", "B.jpg");
        add(levels, "Basic 3rr0r", "HanStor", "Basic 3rr0r.jpg");
        add(levels, "Bloodbath", "Riot", "Bloodbath.jpg");
        add(levels, "BUSSIN", "connot", "BUSSIN.jpg");
        add(levels, "Button Masher", "Viprin", "Button Masher.jpg");
        add(levels, "Change of Scene", "Bli", "Change of Scene.jpg");
        add(levels, "Crazy", "DavJT", "Crazy.jpg");
        add(levels, "Crazy II", "DavJT", "Crazy II.jpg");
        add(levels, "Crazy III", "DavJT", "Crazy III.jpg");
        add(levels, "Death Corridor", "KaotikJumper", "Death Corridor.jpg");
        add(levels, "DEEPDIVE", "Vealt", "DEEPDIVE.jpg");
        add(levels, "Dronification", "IvyTeal", "Dronification.jpg");
        add(levels, "Eternity", "Viprin", "Eternity.jpg");
        add(levels, "Freestyle", "Vitto912", "Freestyle.jpg");
        add(levels, "Funhouse", "Rafer", "Funhouse.jpg");
        add(levels, "Future Funk", "JonathanGD", "Future Funk.jpg");
        add(levels, "Ghost Ship", "ThanatosGMD", "Ghost Ship.jpg");
        add(levels, "Grief", "Icedcave", "Grief.jpg");
        add(levels, "Hypersonic", "Viprin", "Hypersonic.jpg");
        add(levels, "ISPYWITHMYLITTLEEYE", "Voxicat", "ISPYWITHMYLITTLEEYE.jpg");
        add(levels, "Kyouki", "Demishio", "Kyouki.jpg");
        add(levels, "LIMBO", "MindCap", "LIMBO.jpg");
        add(levels, "Magma Bound", "ScorchVX", "Magma Bound.jpg");
        add(levels, "Mayhem", "CherryTeam", "Mayhem.jpg");
        add(levels, "Nantendo", "Im Fernando", "Nantendo.jpg");
        add(levels, "Nomad", "ReeseVT", "Nomad.jpg");
        add(levels, "ORBIT", "MindCap", "ORBIT.jpg");
        add(levels, "Psychosis", "Hinds", "Psychosis.jpg");
        add(levels, "ReTraY", "DiMaViKuLov26", "ReTraY.jpg");
        add(levels, "Reanimation", "Terron", "Reanimation.jpg");
        add(levels, "Sakupen End", "Nick24", "Sakupen End.jpg");
        add(levels, "Shock", "Danolex", "Shock.jpg");
        add(levels, "Silent Silhouette", "Hhuuko", "Silent Silhouette.jpg");
        add(levels, "Skeletal Shenanigans", "YoReid", "Skeletal Shenanigans.jpg");
        add(levels, "The Nightmare", "Jax", "The Nightmare.jpg");
        add(levels, "The Towerverse", "16Lord", "The Towerverse.jpg");
        add(levels, "Tidal Wave", "Onilink", "Tidal Wave.jpg");
        add(levels, "Ultra Drivers", "Lazerblitz", "Ultra Drivers.jpg");
        add(levels, "Update Disorder", "DangerousROBOT", "Update Disorder.jpg");
        add(levels, "White Space", "XenderGame", "White Space.jpg");
        add(levels, "Windy Landscape", "Woogi1411", "Windy Landscape.jpg");
        add(levels, "Yatagarasu", "Trusta", "Yatagarasu.jpg");
        add(levels, "youwhenbeecrusher", "Akunakun", "youwhenbeecrusher.jpg");
        add(levels, "Sunshine", "Unzor", "Sunshine.jpg");
        add(levels, "Heartbeat", "Krmal", "Heartbeat.jpg");
        add(levels, "The Yatagarasu", "Manix648", "The Yatagarasu.jpg");
        add(levels, "Future Funk II", "JonathanGD", "Future Funk II.jpg");
        add(levels, "Space Invaders", "DeeperSpace", "Space Invaders.jpg");
        add(levels, "CICADA3302", "Darwin", "CICADA3302.jpg");
        add(levels, "WANNACRY", "kira9999", "WANNACRY.jpg");
        add(levels, "Tornado", "AbstractDark", "Tornado.jpg");
        add(levels, "Unknown Seas", "CapaXL", "Unknown Seas.jpg");
        add(levels, "Rage Quit", "Bli", "Rage Quit.jpg");
        add(levels, "Bloodlust", "Knobbelboy", "Bloodlust.jpg");
        add(levels, "End of line", "PMK", "End of line.jpg");
        add(levels, "Auto Play Area", "Jax", "Auto Play Area.jpg");
        add(levels, "Anubis", "Adiale", "Anubis.jpg");
        add(levels, "Game Over", "Optation", "Game Over.jpg");
        add(levels, "Stereo Madness", "RobTop", "Stereo Madness.jpg");
        add(levels, "Back On Track", "RobTop", "Back On Track.jpg");
        add(levels, "Polargeist", "RobTop", "Polargeist.jpg");
        add(levels, "Dry Out", "RobTop", "Dry Out.jpg");
        add(levels, "Base After Base", "RobTop", "Base After Base.jpg");
        add(levels, "Cant Let Go", "RobTop", "Cant Let Go.jpg");

        LEVELS = Collections.unmodifiableList(levels);

        Map<String, Level> byKey = new HashMap<>();
        for (Level level : levels) {
            byKey.put(level.key(), level);
        }
        LEVELS_BY_KEY = Collections.unmodifiableMap(byKey);
    }

    private LevelCatalog() {}

    public static List<Level> all() {
        return LEVELS;
    }

    public static Optional<Level> find(String key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(LEVELS_BY_KEY.get(key));
    }

    /** Mirror of the JS slug logic in templates/search.html. */
    public static String slug(String name, String creator) {
        String input = (name + "__" + creator).toLowerCase();
        String slugged = input.replaceAll("[^a-z0-9]+", "-");
        return slugged.replaceAll("(^-+|-+$)", "");
    }

    private static void add(List<Level> list, String name, String creator, String image) {
        list.add(new Level(slug(name, creator), name, creator, image));
    }
}
