package org.hartlandrobotics.echelon2.blueAlliance.fragments;

import org.hartlandrobotics.echelon2.database.entities.Match;

public class MatchListViewModel {
    private int matchNumber;
    private String matchKey;
    private String red1;
    private String red2;
    private String blue1;
    private String blue2;
    private boolean isSelected;

    public MatchListViewModel(Match match){
        this.matchNumber = match.getMatchNumber();
        this.matchKey = match.getMatchKey();
        this.red1 = match.getRed1TeamKey();
        this.red2 = match.getRed2TeamKey();
        this.blue1 = match.getBlue1TeamKey();
        this.blue2 = match.getBlue2TeamKey();
        this.isSelected = false;
    }


    public int getMatchNumber() {
        return matchNumber;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public String getRed1() {
        return red1;
    }

    public String getRed2() {
        return red2;
    }

    public String getBlue1() {
        return blue1;
    }

    public String getBlue2() {
        return blue2;
    }

    public void setIsSelected(boolean isSelected){this.isSelected = isSelected;}
}
