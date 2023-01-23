package uoc.ds.pr.model;

import java.util.Comparator;

public class Enrollment implements Comparable <Enrollment>{
    private final Player player;
    public static final Comparator<Enrollment> CMP_E = (Enrollment e1, Enrollment e2)->e1.getPlayer().getLevel().compareTo(e2.getPlayer().getLevel());


    public Enrollment(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public int compareTo(Enrollment e) {
        return getPlayer().getLevel().compareTo(e.getPlayer().getLevel());}

}
