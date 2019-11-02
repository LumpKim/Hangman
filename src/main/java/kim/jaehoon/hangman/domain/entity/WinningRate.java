package kim.jaehoon.hangman.domain.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class WinningRate {
    public int win;
    public int defeat;
    public float percent;

    public void incrementWinCount() {
        this.win += 1;
    }

    public void incrementDefeatCount() {
        this.defeat += 1;
    }

    public float getPercent() {
        if (win == 0 && defeat == 0) {
            this.percent = 0;
        } else {
            this.percent = (float) win / (win + defeat);
        }
        return this.percent;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getDefeat() {
        return defeat;
    }

    public void setDefeat(int defeat) {
        this.defeat = defeat;
    }

}
