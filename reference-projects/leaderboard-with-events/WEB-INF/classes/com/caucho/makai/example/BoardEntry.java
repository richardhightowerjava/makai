package com.caucho.makai.example;

public class BoardEntry {

    float score;

    String participant;

    BoardEntry() {

    }

    BoardEntry(String participant, float score) {
        this.score = score;
        this.participant = participant;
    }

    float getScore() {
        return score;
    }

    void setScore(float score) {
        this.score = score;
    }

    String getParticipant() {
        return participant;
    }

    void setParticipant(String participant) {
        this.participant = participant;
    }

    @Override
    public String toString() {
        return "BoardEntry{" +
                "score=" + score +
                ", participant='" + participant + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardEntry that = (BoardEntry) o;

        if (participant != null ? !participant.equals(that.participant) : that.participant != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (participant != null ? participant.hashCode() : 0);
        return result;
    }
}